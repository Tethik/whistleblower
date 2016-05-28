import os
import shutil

from stem.control import Controller
from webapp import app

print(' * Connecting to tor')

try:
    with Controller.from_port() as controller:
        controller.authenticate()

        # Create a hidden service where visitors of port 80 get redirected to local
        # port 5000 (this is where Flask runs by default).
        response = controller.create_ephemeral_hidden_service({80: 5000}, await_publication = True)
        print(" * Our service is available at %s.onion, press ctrl+c to quit" % response.service_id)

        try:
            app.run(debug=True)
        finally:
            print(" * Shutting down our hidden service")
except Exception as ex:
    print("Failed to communicate with the Tor process. Have you started the Tor daemon?")
    print(ex)
