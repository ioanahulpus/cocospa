'''
Script to call the API with default parameters 
to get the complexity score for every text file in
the newsela corpus.
'''

import requests
import json
import sys
import os
import time
from collections import defaultdict
import pandas as pd

HOSTNAME = 'localhost'
#HOSTNAME = 'demaq3.informatik.uni-mannheim.de'

defaultURL = "http://" + HOSTNAME + ":8080/complexity"

header = {"Content-Type": "application/json"}
try:
    dir_path = sys.argv[1]
except:
    print ("Please provide a directory of files as a first arg")

try:
    URL = sys.argv[2]
except:
    URL = defaultURL
    print ("No URL provided as arg2, using:", URL)


def files_in_folder(mypath):
    fisiere = []
    for f in os.listdir(mypath):
        if os.path.isfile(os.path.join(mypath, f)):
            fisiere.append(os.path.join(mypath, f))
    return sorted(fisiere)


complexity = defaultdict(dict)
#3d-indoormap.en.0.txt
for file in files_in_folder(dir_path):
    basefile = os.path.basename(file)
    if '-spanish' in basefile:
        continue
    with open(file, 'r', encoding='utf-8') as fin:
        text = fin.read()

    article_title, _, level, _ = basefile.split('.')

    data = {"text": text}
    json_data = json.dumps(data)

    response = requests.post(URL, headers = header, data = json_data)
    js_response = json.loads(response.text)
    complexity_score = js_response['complexityScore']
    complexity[article_title][level] = complexity_score
    print (basefile, '\t', complexity_score)
    time.sleep(1)

dataframe = pd.DataFrame(complexity).T
dataframe.to_csv('results.csv', sep = '\t')