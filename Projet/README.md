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
tous les résultats requis par cette tache sont disponibles on peut alors
l'executer et la returer de la liste des taches restantes.


## Exercice 4

### Question 1

J'avais initialement fait une synchro uniquement basée sur un spinlock au
niveau de la table des résultats. Mais je me suis finalement dit que ça serait
tout de même un peu plus propre de faire attendre les threads.

J'ai donc ajouté un `ExecutorService` qui me permet de submit des `ExecutorThread`,
ma classe qui éxécute une tache. ceux ci remplissent la table `futureResults`
avec des `Futur<Object>` lesquels permettent au thread qui en ont besoin
d'attendre que les résultats soient disponibles.

Une fois sortis de la boucle on copies tous ces futurs résultats dans la table
de résultats finaux ce qui nous permet d'attendre que tous les résultats soient
bien disponibles.