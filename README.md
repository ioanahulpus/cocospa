# Conceptual Complexity using Spreading Activation ![](https://github.com/ioanahulpus/cocospa/workflows/Java%20CI/badge.svg)

This is the code to run and call an HTTP endpoint that returns a score of conceptual complexity for a text. We are currently running a Swagger API component a server at the [University of Mannheim](http://demaq3.informatik.uni-mannheim.de:8080/swagger-ui.html)


```json
{
  "firingThreshold": 0.01,
  "graphDecay": 0.25,
  "linkerThreshold": 0.35,
  "paragraphDecay": 0.5,
  "phiTo1": true,
  "sentenceDecay": 0.7,
  "text": "Some towns on the Eyre Highway in the south-east corner of Western Australia, between the South Australian border almost as far as Caiguna, do not follow official Western Australian time. \nEugowra is said to be named after the Indigenous Australian word meaning \"The place where the sand washes down the hill \"",
  "tokenDecay": 0.85,
  "useExclusivity": true,
  "useImportance": true
}
```


## Calling the API

