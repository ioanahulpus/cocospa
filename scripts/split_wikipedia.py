'''
Script to split the wikipedia corpus into separate
text files.

    Download wikipedia from here: https://cs.pomona.edu/~dkauchak/simplification/data.v2/document-aligned.v2.tar.gz
    https://cs.pomona.edu/~dkauchak/simplification/

'''
import sys
import os
from collections import defaultdict

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

def make_dir(outp):
    if not os.path.exists(outp):
        os.makedirs(outp)

OUT_DIR = 'split'
make_dir(OUT_DIR)

for (article_name, simple_text), (_, complecs_text) in zip(articles_simple_wikipedia.items(), articles_complecs_wikipedia.items()):
    article_name = article_name.replace('/', ' ')
    article_name = article_name.replace('\\', ' ')
    with open(os.path.join(OUT_DIR, article_name + '.sim'), 'w') as fout:
        fout.write(simple_text.replace('-LRB-', '(').replace('-RRB-', ')'))
    with open(os.path.join(OUT_DIR, article_name + '.com'), 'w') as fout:
        fout.write(complecs_text.replace('-LRB-', '(').replace('-RRB-', ')'))
