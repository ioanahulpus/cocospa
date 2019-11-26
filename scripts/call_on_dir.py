import requests
import json
import sys
import os
import time

HOSTNAME = 'localhost'
#HOSTNAME = 'demaq3.informatik.uni-mannheim.de'

URL = "http://" + HOSTNAME + ":8080/complexity"


header = {"Content-Type": "application/json"}
try:
    dir_path = sys.argv[1]
except:
    print ("Please provide a directory of files as a first arg")

def files_in_folder(mypath):
    fisiere = []
    for f in os.listdir(mypath):
        if os.path.isfile(os.path.join(mypath, f)):
            fisiere.append(os.path.join(mypath, f))
    return sorted(fisiere)

for file in files_in_folder(dir_path):
    if '-spanish' in os.path.basename(file):
        continue
    with open(file, 'r', encoding='utf-8') as fin:
        text = fin.read()

    data = {"text": text}
    json_data = json.dumps(data)

    response = requests.post(URL, headers = header, data = json_data)
    js_response = json.loads(response.text)   
    print (os.path.basename(file),'\t', js_response['complexityScore'])
    time.sleep(1)
