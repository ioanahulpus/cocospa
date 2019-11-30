'''
Script to call the API with default parameters 
to get the complexity score for every text file in
the standard and simplified wikipedia corpus.

    Download wikipedia from here: https://cs.pomona.edu/~dkauchak/simplification/data.v2/document-aligned.v2.tar.gz
    https://cs.pomona.edu/~dkauchak/simplification/

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

URL = "http://" + HOSTNAME + ":8080/complexity"


header = {"Content-Type": "application/json"}
try:
    simple_wiki = sys.argv[1]
except:
    print ("Please provide simple wikipedia as a first arg")

try:
    complecs_wiki = sys.argv[2]
except:
    print ("Please provide simple wikipedia as a second arg")

def split_into_articles(wiki_text):
    articles = defaultdict(str)
    current_par = '0'
    for line in wiki_text:
        article_name, paragraph, text = line.split('\t')
        if current_par == paragraph:
            articles[article_name] += ' ' + text.strip()
        else:
            articles[article_name] += '\n' + text.strip()
            current_par = paragraph
    return articles

with open(simple_wiki, 'r', encoding='utf-8') as fin:
    simple = fin.readlines()

with open(complecs_wiki, 'r', encoding='utf-8') as fin:
    complecs = fin.readlines()


articles_simple_wikipedia = split_into_articles(simple)
articles_complecs_wikipedia = split_into_articles(complecs)

def get_complexity_from_api(text):
    data = {"text": text}
    json_data = json.dumps(data)
    time.sleep(1)
    response = requests.post(URL, headers = header, data = json_data)
    js_response = json.loads(response.text)
    complexity_score = js_response['complexityScore']
    return complexity_score

def clean(text):
    text = text.replace('-LRB-', '(')
    text = text.replace('-RRB-', ')')
    text = text.strip()
    return text

with open('wikipedia_eval_clean.csv', 'w') as fout:
    fout.write('\t'.join(['article', 'simple', 'complex', 'simple_size', 'complex_size']) + '\n')
    for (article_name, simple_text), (_, complecs_text) in zip(articles_simple_wikipedia.items(), articles_complecs_wikipedia.items()):
        simp_score = get_complexity_from_api(clean(simple_text))
        comp_score = get_complexity_from_api(clean(complecs_text))
        
        simp_size = len(simple_text.split(' '))
        comp_size = len(complecs_text.split(' '))

        line = '\t'.join([article_name, str(simp_score), str(comp_score), str(simp_size), str(comp_size)])
        print (line)
        fout.write(line + '\n')


