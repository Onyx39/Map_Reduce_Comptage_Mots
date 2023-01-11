# Map_Reduce_Comptage_Mots
Projet de 'Flux de données et accès concurrents'

Valentin Richard

Emma Orset


## Introduction

Le but de ce projet est d'implémenter un algorithme de comptage de mots en Java sur un modèle Map_reduce.

## Choix

Nous avons fait le choix de réaliser ce projet en multithreadé.

Nous aurions pu le faire en RMI ou en Socket par exemple, mais cela nous a semblé plus accessible en multithreadé.

De plus, nous n'avions jamais fait de multithreadé donc nous avons appris beaucoup de choses. 

## Lancement du code

Pour lancer le code, vous pouvez ajouter un fichier .txt dans le dossier data et modifier le chemain dans l'objet Moniteur de la classe Main.
Sinon, vous pouvez utiliser les textes déjà présents, comme celui de la Bible.
Vous pouvez également adapter le nombre de Mappers et de Reducers à votre convenance.
Si vous voulez avoir un affichage du dictionnaire et non seulement le temps d'execution, vous pouvez ajouter l'argument "true".

## Tests

Nous avons fait plusieurs testes sur notre code pour vérifier son fonctionnement.
Nous avons pris le fichier bible.txt comme exemple.
Nous avons regardé les temps d'execution pour chaque test.

Voici les différents tests et résultats obtenus :
- Reducer = 1 / Mapper = 1 : 1min
- Reducer = 5 / Mapper = 5 : 18sec
- Reducer = 10 / Mapper = 10 : 10sec
- Reducer = 20 / Mapper = 20 : 8sec
- Reducer = 100 / Mapper = 100 : 7,5sec
- Reducer = 10 / Mapper = 20 : 8sec

Sachant que ces résultats dépendent beaucoup de la machine, nous les avons traités sur une machine assez ancienne (2014) ayant deux coeurs.
Les résultats peuvent donc être plus performants et plus flagrants sur une machine plus puissante.

Nous avons remarqué que plus nous augmentions le nombre de Mappers et de Réducers, moins le temps d'éxecution avait une grosse différence, car la création des Mappers et des Reducers prend aussi du temps.
De plus, nous avons remarqué qu'il est plus efficace d'augmenter le nombre de Mappers que le nombre de Reducers.

## Difficulté

Nous avons principalement fait face à une difficulté. En multithreadé, étant donné que l'ont fait écrire les mappers et les reducers dans une variable partagée, on a utilisé la fonction synchronise pour éviter les problèmes de concurrences. Nous avons mis un peu de temps à comprendre d'où venait le problème, que nous avons détécté en comptant le nombre de mots que nous devions obtenir et le nombre de mots que nous avions réellement. 

## Conclusion

Nous avons donc atteint notre objectif. 
