import requests
import json
import sys

URL = "http://localhost:8080/complexity"

header = {"Content-Type": "application/json"}
try:
    file_path = sys.argv[1]
except:
    print ("Please provide a file as a first arg")


with open(file_path, 'r', encoding='utf-8') as fin:
    text = fin.read()

data = {"text": text}
json_data = json.dumps(data)

response = requests.post(URL, headers = header, data = json_data)
print (response.text)