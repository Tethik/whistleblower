#!/usr/bin/python
import os
import subprocess

os.chdir('client/')
try:
    result = subprocess.check_output(["git", "pull", "origin", "master", "--verify-signatures"], stderr=subprocess.STDOUT)
    result = str(result)
    if("Already" in result):
        print("No new changes.")
    else:
        print("Updated:")
        print(result)
except subprocess.CalledProcessError as ex:
    result = str(ex.output)
    if("not have a GPG" in result):
        print("Untrusted code: Missing GPG signature")
    else:
        print(ex.output)

# `git pull --verify-signatures`
