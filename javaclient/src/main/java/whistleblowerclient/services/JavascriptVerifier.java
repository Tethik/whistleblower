package whistleblowerclient.services;

import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.util.encoders.Base64;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import whistleblowerclient.Log;

import java.io.*;
import java.net.*;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by tethik on 06/01/16.
 */
public class JavascriptVerifier implements Runnable {

    private static String GIT_URI = "/home/tethik/code/thesis/whistleblower/code/javascript_client/";
    private static final String SERVICE_URL = "http://jzkelb5g73g6q3wn.onion";

    private Proxy proxy;

    public JavascriptVerifier() {
        setUpTorProxy();
    }

    private void setUpTorProxy() {
        // Connect to tor
        SocketAddress addr = new InetSocketAddress("127.0.0.1", 9050);
        proxy = new Proxy(Proxy.Type.SOCKS, addr);
    }

    private String checksum(InputStream is) throws IOException {
        SHA256.Digest hash = new SHA256.Digest();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            hash.update(data, 0, nRead);
        }
        return Base64.toBase64String(hash.digest());
    }

    private InputStream fetch(String path) throws IOException {
        URL url = new URL(SERVICE_URL+"/"+path);
        URLConnection connection = url.openConnection(proxy);
        connection.setDoOutput(true);

        InputStream is = connection.getInputStream();
        return is;
    }

    private void traverse(File directory, Queue<File> fetchQueue) {
        for(File file : directory.listFiles()) {
            if(file.getName().equals(".git"))
                continue;
            if(file.isDirectory()) {
                traverse(file, fetchQueue);
                continue;
            }
            fetchQueue.add(file);
        }
    }

    private Map<String, String> validChecksums = new HashMap<>();

    private void buildChecksums(Queue<File> filesToFetch) throws IOException {
        for (File file : filesToFetch) {
            buildChecksum(file);
        }
    }

    private void buildChecksum(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        String path = getRelativeGitPath(file);
        validChecksums.put(getRelativeGitPath(file), checksum(is));
        Log.i(path + " " + validChecksums.get(path));
        is.close();
    }

    private String getRelativeGitPath(File file) {
        String path = file.getPath();
        return path.replace("/home/tethik/code/thesis/whistleblower/code/javascript_client/", "");
    }

    public void fetchAndCompare(File file, ValidationResult result) {
        String path = getRelativeGitPath(file);
        try {
            InputStream is = fetch(path);
            String hash = checksum(is);
            Log.i(path + " " + hash + " " + hash.equals(validChecksums.get(path)));
            is.close();
            if(!validChecksums.get(path).equals(hash)) {
                result.errors.add("Invalid checksum for file " + path);
            }
        } catch(IOException ex) {
            result.errors.add("Could not fetch " + path);
        }
    }

    /**
     * Validates Integrity of served static content.
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();

        try {
            Repository repo = FileRepositoryBuilder.create(new File(GIT_URI));

            // Todo: validate signatures.
            File gitdir = repo.getDirectory();
            Queue<File> filesToFetch = new ArrayDeque<>();
            traverse(gitdir, filesToFetch);
            filesToFetch.parallelStream().forEach(file1 -> {
                try {
                    buildChecksum(file1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            buildChecksums(filesToFetch);

            // Download each file and compare bytes.
            filesToFetch.parallelStream().forEach(file -> fetchAndCompare(file, result));

        } catch (IOException e) {
            e.printStackTrace();
            result.errors.add("Unknown IOException occured " + e.toString());
        } finally {
            return result;
        }
    }

    @Override
    public void run() {


    }
}
