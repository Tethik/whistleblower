package whistleblowerclient.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.symmetric.AES;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.interfaces.ElGamalKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.jcajce.*;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.Streams;
import whistleblowerclient.Log;
import whistleblowerclient.services.Submission;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author tethik
 */
public class CryptographyHandler {

    private PGPSecretKey secretKey = null;

    // todo: allow user to set this.
    private String passphrase = "derp";

    private static final CryptographyHandler instance = new CryptographyHandler();
    public static CryptographyHandler getInstance()
    {
        return instance;
    }

    private CryptographyHandler() {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public boolean loadKeys() throws IOException, PGPException {
        secretKey = readSecretKey(new ArmoredInputStream(new FileInputStream("secret.pgp")));


        PGPDigestCalculatorProvider digestCalculatorProvider = new JcaPGPDigestCalculatorProviderBuilder().build();
        PBESecretKeyDecryptor decryptor = new JcePBESecretKeyDecryptorBuilder(digestCalculatorProvider)
                .build(passphrase.toCharArray());

        PGPPrivateKey privateKey = secretKey.extractPrivateKey(decryptor);

        return secretKey != null;
    }

    private PGPSecretKey readSecretKey(InputStream input) throws IOException, PGPException
    {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());

        //
        // we just loop through the collection till we find a key suitable for encryption, in the real
        // world you would probably want to be a bit smarter about this.
        //

        Iterator keyRingIter = pgpSec.getKeyRings();
        while (keyRingIter.hasNext())
        {
            PGPSecretKeyRing keyRing = (PGPSecretKeyRing)keyRingIter.next();

            Iterator keyIter = keyRing.getSecretKeys();
            while (keyIter.hasNext())
            {
                PGPSecretKey key = (PGPSecretKey)keyIter.next();

                if (key.isSigningKey())
                {
                    return key;
                }
            }
        }

        throw new IllegalArgumentException("Can't find signing key in key ring.");
    }
    
       
    public PGPDecryptionResult decrypt(InputStream in) throws IOException {

        PGPDecryptionResult result = new PGPDecryptionResult();
//        List<String> receivers = new ArrayList<>();

        in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
        PGPObjectFactory pgpF = new PGPObjectFactory(in, new JcaKeyFingerprintCalculator());
        PGPEncryptedDataList enc;

        Object o = pgpF.nextObject();

        if (o instanceof  PGPEncryptedDataList) {
            enc = (PGPEncryptedDataList) o;
        } else {
            enc = (PGPEncryptedDataList) pgpF.nextObject();
        }

        Iterator it = enc.getEncryptedDataObjects();

        PGPPublicKeyEncryptedData pbe = null;

        while (it.hasNext()) {

            PGPPublicKeyEncryptedData current = (PGPPublicKeyEncryptedData) it.next();

            if(current.getKeyID() == secretKey.getKeyID()) {
                pbe = current;
            }

//            receivers.add(Long.toString(current.getKeyID()));
        }

        if(pbe == null)
            return result;


        try {
            PGPDigestCalculatorProvider digestCalculatorProvider = new JcaPGPDigestCalculatorProviderBuilder().build();

            PBESecretKeyDecryptor decryptor = new JcePBESecretKeyDecryptorBuilder(digestCalculatorProvider)
                    .build(passphrase.toCharArray());

            PGPPrivateKey privateKey = secretKey.extractPrivateKey(decryptor);

            InputStream clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(privateKey));


            JcaPGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);
            Object message = plainFact.nextObject();

            if(message instanceof PGPCompressedData) {
                PGPCompressedData   cData = (PGPCompressedData)plainFact.nextObject();

                InputStream         compressedStream = new BufferedInputStream(cData.getDataStream());
                JcaPGPObjectFactory    pgpFact = new JcaPGPObjectFactory(compressedStream);

                message = pgpFact.nextObject();
            }

            if (message instanceof PGPLiteralData)
            {
                PGPLiteralData ld = (PGPLiteralData)message;

                InputStream unc = ld.getInputStream();

                ObjectMapper mapper = new ObjectMapper();
                result.submission = mapper.readValue(unc, Submission.class);
                unc.close();
                Log.i(result.submission.reply_to);
                Log.i(Integer.toString(result.submission.receivers.size()));

                result.decryptedSuccessfully = !pbe.isIntegrityProtected() ||  pbe.verify();
                return result;
            }
        } catch (PGPException e) {
            e.printStackTrace();
        }
        return result;
    }


    
    public void keygen(
        String identity)
        throws IOException, InvalidKeyException, NoSuchProviderException, SignatureException, PGPException, NoSuchAlgorithmException
    {    
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
        kpg.initialize(1024);

        KeyPair kp = kpg.generateKeyPair();
        
        
        OutputStream secretOut = new ArmoredOutputStream(new FileOutputStream("secret.pgp"));    
        OutputStream publicOut = new ArmoredOutputStream(new FileOutputStream("public.pgp"));


        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyPair keyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, kp, new Date());
        secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, keyPair, identity, sha1Calc, null, null,
                new JcaPGPContentSignerBuilder(keyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
                new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, sha1Calc).setProvider("BC").build(passphrase.toCharArray()));

        secretKey.encode(secretOut);        
        secretOut.close();
        
        PGPPublicKey key = secretKey.getPublicKey();
        
        key.encode(publicOut);        
        publicOut.close();
    }


    public EncryptedResponsePacket encryptReply(String aesKey, InputStream plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException {
        byte[] keyBytes = Base64.decode(aesKey);

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CCM/NoPadding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key, new SecureRandom());
        byte[] iv = cipher.getIV();

        ByteArrayOutputStream encrypted = new ByteArrayOutputStream();


        CipherOutputStream cOut = new CipherOutputStream(encrypted, cipher);
        Streams.pipeAll(plaintext, cOut);
        cOut.close();

        EncryptedResponsePacket packet = new EncryptedResponsePacket();
        packet.iv = Base64.toBase64String(iv);
        packet.mode = "ccm";
        packet.ct = Base64.toBase64String(encrypted.toByteArray());

        return packet;
    }

    public String getKeyID() {
        return Long.toString(secretKey.getKeyID());
    }

    public String getUserID() {
        return (String) secretKey.getUserIDs().next();
    }

    public InnerResponsePacket decryptReply(String aesKey, InputStream in) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        ObjectMapper mapper = new ObjectMapper();
        EncryptedResponsePacket packet = mapper.readValue(in, EncryptedResponsePacket.class);

        byte[] keyBytes = Base64.decode(aesKey);
        byte[] iv = Base64.decode(packet.iv);

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CCM/NoPadding", "BC");


        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        ByteArrayOutputStream decrypted = new ByteArrayOutputStream();
        ByteArrayInputStream encrypted = new ByteArrayInputStream(Base64.decode(packet.ct));

        CipherOutputStream cOut = new CipherOutputStream(decrypted, cipher);
        Streams.pipeAll(encrypted, cOut);
        cOut.close();

        InnerResponsePacket ipacket = mapper.readValue(new String(decrypted.toByteArray(), "UTF8"), InnerResponsePacket.class);
        return ipacket;
    }

//    public void keygenEcc(String identity) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
//        String curveid = "secp256k1";
//
//        ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec(curveid);
//        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "BC");
//        kpg.initialize(new SecureRandom());
//        KeyFactory fact = KeyFactory.getInstance("EC", "BC");
//        ECCurve curve = params.getCurve();
//        java.security.spec.EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, params.getSeed());
//        curve.
//        java.security.spec.ECPoint point = ECPointUtil.decodePoint(ellipticCurve, encoded);
//        java.security.spec.ECParameterSpec params2 = EC5Util.convertSpec(ellipticCurve, params);
//        java.security.spec.ECPublicKeySpec keySpec = new java.security.spec.ECPublicKeySpec(point,params2);
//        ECPrivateKey sk =  (ECPrivateKey) fact.generatePrivate(keySpec);
//        ECPublicKey pb = (ECPublicKey) fact.generatePublic(keySpec);
//        byte[] encodedKey = pb.getEncoded();
//        byte[] encodedPrivateKey = sk.getEncoded();
//
//        OutputStream out = new FileOutputStream("ecsecretkey");
//        out.write(encodedPrivateKey);
//        out.close();
//
//
//        PublicKey key = new PublicKey();
//        key.identity = identity;
//        key.curve = curveid;
//        key.encodedPublicKey = Base64.toBase64String(encodedKey);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.writeValue(new File("ecpublickey"), key);
//        mapper.writeValue(System.out, key);
//    }
//
//
//    public void loadecc() {
//
//    }
       
}
