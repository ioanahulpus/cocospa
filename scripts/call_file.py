'''
Script to call the API with default parameters 
to get the complexity score for a text file.
'''


import requests
import json
import sys

#HOSTNAME = 'localhost'
HOSTNAME = 'demaq3.informatik.uni-mannheim.de'

defaultURL = "http://" + HOSTNAME + ":8080/complexity"

header = {"Content-Type": "application/json"}
try:
    file_path = sys.argv[1]
except:
    print ("Please provide a file as a first arg")

try:
    URL = sys.argv[2]
except:
    URL = defaultURL
    print ("No URL provided as arg2, using:", URL)


with open(file_path, 'r', encoding='utf-8') as fin:
    text = fin.read()

data = {"text": text}
json_data = json.dumps(data)

response = requests.post(URL, headers = header, data = json_data)
print (response.text)
