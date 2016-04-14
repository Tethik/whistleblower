This directory contains two pcaps or txt files containing raw data sent to the Tor hidden service by my verifier script (using a Selenium controlled Tor Browser) 
and a Tor Browser controlled by me.

The data was captured by Wireshark running on the loopback interface, meaning the raw data that was proxied from the Tor process to my webserver socket (Flask).

The purpose was to see if behaviour and data sent by either instance was indistinguishable from each other. The result was: not quite, since 
every time the verifier checked the source code it would send an additional favicon.ico requests. (But there was away around this)
