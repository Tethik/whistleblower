import stem
from stem import CircStatus, Signal
from stem.control import Controller
import pycurl
from datetime import datetime


with Controller.from_port(port = 9051) as controller:
    controller.authenticate()

    def doRequest(url):
        if not controller.is_newnym_available():
            # print(str(controller.get_newnym_wait()) + "until new circuit")
            return False
        controller.signal(Signal.NEWNYM) # Requests new circuit

        query = pycurl.Curl()
        query.setopt(pycurl.URL, url)
        query.setopt(pycurl.PROXY, 'localhost')
        query.setopt(pycurl.PROXYPORT, 9050)
        query.setopt(pycurl.PROXYTYPE, pycurl.PROXYTYPE_SOCKS5_HOSTNAME)
        f = open('/dev/null', 'wb')
        query.setopt(query.WRITEDATA, f)

        try:
            query.perform()
            return True
        except pycurl.error as exc:
            print(exc)
            return False
        finally:
            f.close()

    url = "jzkelb5g73g6q3wn.onion"

    c = 0
    start_time = datetime.now()
    diff = start_time - start_time
    while diff.seconds < 600:
        if doRequest(url):
            c += 1
            print(diff, c)
            # print(controller.get_circuit())
        diff = (datetime.now() - start_time)
    print(c)
