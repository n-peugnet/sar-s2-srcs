# Moteur d’exécution de workflows

Nicolas Peugnet  
Mai 2020

## 1. Définition de graphes

_Pas de commentaires._

## 2. Définition de Jobs

### 2.1. Identifiant de tâche

Je pense qu'à cause de la surcharge de méthodes il est possible d'avoir
plusieurs méthodes avec le même nom. Mais le `JobValidator` aurait pu vérifier
que deux méthodes ne portent pas le même nom. Cependant cela laisse moins de
liberté pour l'utilisateur. L'utilisation d'annotation pour définir l'id des
taches permet donc plus de flexibilité.

## 3. Exécution séquentielle en local

### 3.1. Algorithme de la méthode `execute`

Tant qu'il reste des taches à exécuter on parcourt les taches restantes. Et si
tous les résultats requis par cette tache sont disponibles on peut alors
l’exécuter et la retirer de la liste des taches restantes.

## 4. Exécution parallèle en local

### 4.1. Parallélisme des tâches indépendantes

J'avais initialement fait une synchro uniquement basée sur un _spinlock_ au
niveau de la table des résultats. Mais je me suis finalement dit que ça serait
tout de même un peu plus propre de faire attendre les threads.

J'ai donc ajouté un `ExecutorService` qui me permet de _submit_ des
`ExecutorThread`, ma classe qui exécute une tache. Ceux ci remplissent la
table `futureResults` avec des `Futur<Object>` lesquels permettent au thread
qui en ont besoin d'attendre que les résultats soient disponibles.

Une fois sortis de la boucle on copies tous ces futurs résultats dans la table
de résultats finaux ce qui nous permet d'attendre que tous les résultats soient
bien disponibles.

## 5. Exécution à distance sur un serveur central

### 5.1. Protocole de communication

Le protocole de communication est basé sur Java RMI. Il y a un `Host` avec la
méthode `submitJob`. Dans un premier temps elle prenait simplement un `Job` en
paramètre. J'ai modifié la classe abstraite `Job` afin qu'elle implémente
`Serializable` pour pouvoir l'envoyer via RMI. La classe `HostImpl` instancie
ensuite un `JobExecutorParallel` et renvoie le résultat de `execute` via RMI.

Dans un second temps j'ai ajouté le package `notifications` contenant le code
nécessaire à l'envoi de notifications au client. Les notifications sont
également basées sur RMI : la fonction `submitJob` prend maintenant une
instance de `Notifiable`, une interface `JobExecutorPluggable` est également
créée afin d'ajouter une version de `execute` prenant un paramètre `Notifiable`
et le client en envoie donc une implémentation concrète : `NotifSdtout`, au
préalable exportée, sur laquelle le job exécutera alors la fonction `notify`
à distance.

### 5.2. Justifier votre choix de l’API

RMI me paraissait bien pratique dans le cas présent en raison de sa simplicité
de mise en oeuvre, son intégration à Java et sa cohérence avec les
problématiques posées par le sujet.

En effet, c'est bien plus efficace et rapide à mettre en œuvre qu'une
communication à base de *socket* pure. Ce n'est toutefois pas tout à fait prévu
pour envoyer des notifications, et, pour ne pas avoir à mettre en place un
second canal de communication, j'ai fait en sorte que le client soit, dans
une certaine mesure, également un serveur.

Aucune interopérabilité n'étant demandée, je n'ai pas vu d'inconvénient à
utiliser une solution _Java-only_ telle que Java RMI.

### 5.3. Hypothèse sur le type des objets du contexte

On suppose bien évidemment que les objets du contexte d'un `Job` sont
`Serializable`. autrement il ne sera pas possible de soumettre le job à
travers RMI.

### 5.4. Mécanisme de la notification

Comme expliqué ci-dessus, Java RMI ne prévoit rien pour les événements
_ServerSide_. J'ai donc eu recours à une ruse de sioux (dénichée sur
StackOverflow) qui consiste à exporter un _remote object_ du côté du client et
de l'envoyer au serveur en même temps que le job à exécuter. Celui-ci
implémente l'interface inaugurée pour l'occasion : `Notifiable` et ne fait
qu'écrire sur la sortie standard le contenu de la notification.

## 6. Exécution à distance sur un cluster de machines

### 5.1. Protocole de communications

Les esclaves ne communiquent pas entre aux. Un esclave, au démarrage, appelle la
fonction `registerTaskTracker` du maître puis attends qu'il appelle sa fonction
`submitTask` pour l’exécuter et en rendre le résultat.

### 5.2. Affectation des tâches équitable

Le maître a en mémoire une `BlockingQueue` de `TaskExecutor`. Le `TaskExecutor`
est un objet contenant une instance de `TaskHost` (le serveur distant), un
nombre de tâche maximales exécutables en parallèle et un compteur de taches
affectées. Lorsque le `JobExecutorDistributed` demande un `TaskExecutor` pour
exécuter sa tâche celui-ci est pris de la queue, cette action est donc bloquante
si il n'y en a pas de disponible. Au même moment, si il reste des tâches
disponibles dans ce `TaskExecutor` on le remet alors en bout de Queue.
Ce mécanisme permet donc d'affecter les tâches équitablement sur chaque machine
tout en respectant le nombre de taches disponibles qu'il leur reste.

### 5.3. Respect de le borne de tâche courante

Cette question a déjà partiellement été répondue à la question précédente.
On ajoutera juste qu'un `TaskExecutor` n'est remis dans la queue que quand il a
a nouveau des taches disponibles. Le maître garanti donc de na pas dépasser la
limite de tâches courantes.

### 5.4. Communication d’un résultat entre deux tâches dépendantes

Lorsqu'un esclave a fini d’exécuter une tâche il renvoie le résultat au maître,
lequel pourra rendre disponible ce résultat à toutes les autres tâches qui en
ont besoin. De plus une tâche n'est soumise à un esclave que quand tous ses
paramètres sont disponibles. Cette solution permet de ne pas avoir à ajouter de
communication en esclaves. Ils sont donc tous indépendants et n'ont pas de
conscience de leurs pairs. De cette manière il est également très simple de
palier à une faute, il suffit pour le maître d'affecter la tâche à une autre
machine. Les inconvénients principaux sont la centralisation très forte sur la
machine maître mais elle était déjà un SPOF dans le protocole demandé, je ne me
suis donc pas embêté à distribuer le protocole plus que ça.

### 5.5. Plusieurs jobs en cours d’exécution sur le cluster

Je n'ai eu en quelque sorte rien eu à faire de particulier pour que plusieurs
jobs puissent être en cours d'exécutions sur le cluster. RMI lance un thread par
requête lequel va instancier un `JobExecutorDistributed`. Celui-ci va ensuite
lancer un thread par tâche qui _wait_ chacun sur l'ensemble des résultats qu'ils
attendent grâce aux `Future<Object>`. Une fois tous ces résultats obtenus ils
sont tous synchronisés au niveau de la `BlockingQueue` ce qui permet à chaque
thread de chaque job d'attendre qu'un `TaskExecutor` soit disponible.

### 5.6. Mécanisme de détection de panne et réaffectation des tâches perdues

Le maître attend de chaque `TaskExecutor` qu'il lui renvoie un résultat via RMI.
Si celui-ci ne le fait pas ou renvoie un résultat incorrect, RMI lève une
exception facile à identifier car elle est spécifique à ce problème : la
`UnmarshalException`. Le `JobExecutor` _catche_ donc cette exception en
particulier et, dans son traitement, signale le nœud défaillant pour qu'il soit
retiré de la queue des `TaskExecutor` disponibles, puis demande un nouveau
`TaskExecutor` pour lui soumettre à nouveau la tâche.
Je n'ai pas mis ce traitement dans une boucle car je trouvais que 2 essais
étaient suffisants dans la plupart des cas. Si le `JobExecutor` tombe sur deux
nœuds défaillants à la suite alors l'exécution du _job_ échoue et renvoie une
`RemoteException` que l'utilisateur pourra catcher afin de pouvoir lui-même
retenter l'exécution de son _job_, peut-être avec une politique
d'_exponential backoff_.

*[API]: Application Programming Interface
*[RMI]: Remote Method Invocation
*[SPOF]: Single Point Of Failure
