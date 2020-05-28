# Moteur d’exécution de workflows

Nicolas Peugnet
Mai 2020

## Exercice 1

_Pas de commentaires._

## Exercice 2

### Question 1

Je pense qu'à cause de la surcharge de methodes il est posible d'avoir
plusieurs methodes avec le même nom. Mais le JobValidator aurait pu vérifier
que deux méthodes ne portent pas le même nom. Cependant cela laisse maoins de
liberté pour l'utilisateur. L'utilisation d'annotation pour définir l'id des
taches permet donc plus de fléxibilité.

## Exercice 3

### Question 1

Tant qu'il reste des taches à executer on parcourt les taches restantes. Et si
tous les résultats requis par cette tache sont disponnibles on peut alors
l'executer et la returer de la liste des taches restantes.


