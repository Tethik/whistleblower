from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.firefox.firefox_binary import FirefoxBinary
from selenium.webdriver.firefox.firefox_profile import FirefoxProfile
import hashlib

binary = FirefoxBinary(firefox_path="./tor-browser_en-US/Browser/start-tor-browser")
profile = FirefoxProfile(profile_directory="./tor-browser_en-US/Browser/TorBrowser/Data/Browser/profile.default/")

driver = webdriver.Firefox(firefox_binary=binary, firefox_profile=profile)
driver.get("http://jzkelb5g73g6q3wn.onion/")

links = driver.find_elements_by_tag_name("link")
scripts = driver.find_elements_by_tag_name("script")

links = [x.get_attribute("href") for x in links]
scripts = [x.get_attribute("src") for x in scripts]

child_sources = links + scripts

valid_sums = set(['60cccad42e09811544a7be0b0c0e5bf069781beb136bc07f6eacb96db30a0f8a',
'2d8cf9bd4a11fe9fc72c8dfc4595aeffe7f0e8376ba8d29a8b604088e2933057',
'157598c1721102982019dd86b174031f9b03e983aae5d1b8a9a5b5c79f437851',
'cf614b3ed8f53fe1fe4b89023e563fe02eda4e24d544fb03ae57b2acc9a8f1b9',
'a3c6ccd19e5c16faefbef429d042458b82c80af040f450b1ee208dba88d5b0df',
'eece6e0c65b7007ab0eb1b4998d36dafe381449525824349128efc3f86f4c91c'])

def fetch_and_checksum(url):
    h = hashlib.sha256()
    driver.get(url)
    text = driver.find_element_by_tag_name("pre").text.strip()
    h.update(text.encode('utf-8'))
    return h.hexdigest()

for cs in child_sources:
    if "base64" in cs:
        continue
    checksum = fetch_and_checksum(cs)
    status = "OK"
    if checksum not in valid_sums:
        status = "FAIL"
    print(checksum, cs, status)

driver.close()
