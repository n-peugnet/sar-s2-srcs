# Moteur d’exécution de workflows

Nicolas Peugnet  
Mai 2020

## 1. Définition de graphes

_Pas de commentaires._

## 2. Définition de Jobs

### 2.1. Identifiant de tâche

Je pense qu'à cause de la surcharge de methodes il est posible d'avoir
plusieurs methodes avec le même nom. Mais le JobValidator aurait pu vérifier
que deux méthodes ne portent pas le même nom. Cependant cela laisse maoins de
liberté pour l'utilisateur. L'utilisation d'annotation pour définir l'id des
taches permet donc plus de fléxibilité.

## 3. Exécution séquentielle en local

### 3.1. Algorithme de la méthode execute

Tant qu'il reste des taches à executer on parcourt les taches restantes. Et si
tous les résultats requis par cette tache sont disponibles on peut alors
l'executer et la returer de la liste des taches restantes.


## 4. Exécution parallèle en local

### 4.1. Parallélisme des tâches indépendantes

J'avais initialement fait une synchro uniquement basée sur un spinlock au
niveau de la table des résultats. Mais je me suis finalement dit que ça serait
tout de même un peu plus propre de faire attendre les threads.

J'ai donc ajouté un `ExecutorService` qui me permet de submit des
`ExecutorThread`, ma classe qui éxécute une tache. ceux ci remplissent la
table `futureResults` avec des `Futur<Object>` lesquels permettent au thread
qui en ont besoin d'attendre que les résultats soient disponibles.

Une fois sortis de la boucle on copies tous ces futurs résultats dans la table
de résultats finaux ce qui nous permet d'attendre que tous les résultats soient
bien disponibles.

## 5. Exécution à distance sur un serveur central

### 5.1. Protocole de communication

Le protocole de communication est basé sur Java RMI. Il y a un `Host` avec la
methode `submitJob`. Dans un premier temps elle prenait simplement un `Job` en
paramètre. J'ai modifié la classe abstraite `Job` afin qu'elle implémente
`Serializable` pour pouvoir l'envoyer via RMI. La class HostImpl instancie
ensuite un `JobExecutorParallel` et renvoie le résultat de `execute` via RMI.

Dans un second temps j'ai ajouté le package `notifications` contenant le code
nécessaire à l'envoi de notifications au client. Les notifications sont
égalament basées sur RMI : la fonction `submitJob` prend maintenant une
instance de `Notifiable`, une interface `JobExecutorPluggable` est également
créée afin d'ajouter une version de `execute` prenant un paramètre `Notifiable`
et le client en envoie donc une implementation concrète : `NotifSdtout`, au
préalable exportée, sur laquelle le job éxécutera alors la fonction `notify`
à distance.

### 5.2. Justifier votre choix de l’API

RMI me parraissait bien pratique dans le cas présent en raison de sa simplicité
de mise en oeuvre, son intégration à Java et sa cohérence avec les
poblématiques posées par le sujet.

En effet, c'est bien plus efficace et rapide à mettre en oeuvre qu'une
communication à base de socket pure. Ce n'est toutefois pas tout à fait prévu
pour envoyer des notifications, et, pour ne pas avoir à mettre en place un
second canal de communication, j'ai fait en sorte que le client soit, dans
une certaine mesure, également un serveur.

Aucune intéropérabilité n'étant demandée, je n'ai pas vu d'inconvénient à
utiliser une solution Java-only telle que Java RMI.

### 5.3. Hypothèse sur le type des objets du contexte

On suppose bien évidemment que les objets du contexte d'un `Job` sont
`Serializable`. autrement il ne sera pas possible de soumettre le job à
traver RMI.

### 5.4. Mécanisme de la notification

Comme expliqué ci-dessus, Java RMI ne prévoit rien pour les événements
_ServerSide_. J'ai donc eu recours à une ruse de sioux (dénichée sur
StackOverflow) qui consite à exporter un _remote object_ du côté du client et
de l'envoyer au serveur en même temps que le job à executer. Celui-ci
implémente l'interface inaugurée pour l'occasion : `Notifiable` et ne fait
qu'écrire sur la sortie standard le contenu de la notification.

