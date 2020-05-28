package srcs.workflow.graph;

import java.util.List;
import java.util.Set;

public interface Graph<T> extends Iterable<T> {

	/**
	 * ajoute un nouveau noeud dans le graphe. 
	 * @param n le noeud à ajouter
	 * @throws IllegalArgumentException Si le noeud existe déjà dans le graphe.
	 */
	void addNode(T n) throws IllegalArgumentException;
	
	/**
	 * ajoute un nouveau lien unidirectionnel partant du noeud from et arrivant au noeud to.
	 * @param from
	 * @param to
	 * @throws IllegalArgumentException si ni from, ni to n’existent ou bien si le lien existe déjà.
	 */
	void addEdge(T from, T to) throws IllegalArgumentException;
	
	/**
	 * teste l’existence d’un arc entre from et to dans le graphe
	 * @param from
	 * @param to
	 * @return vrai si l'arc existe
	 */
	boolean existEdge(T from, T to);
	
	/**
	 * teste l’existence d’un noeud dans le graphe.
	 * @param n
	 * @return vrai si le noeud existe.
	 */
	boolean existNode(T n);
	
	/**
	 * teste si le graphe ne contient aucun noeud.
	 */
	boolean isEmpty();
	
	/**
	 * renvoie le nombre de noeuds présents dans le graphe.
	 * @return le nombre de noeuds.
	 */
	int size();
	
	/**
	 * renvoie la liste des noeuds voisins du noeud from via les arcs sortants.
	 * @param from
	 * @return une liste de noeuds.
	 * @throws IllegalArgumentException si from n’existe pas dans le graphe.
	 */
	List<T> getNeighborsOut(T from) throws IllegalArgumentException;
	
	/**
	 * renvoie la liste des noeuds voisins du noeud to via les arcs entrants.
	 * @param to
	 * @return
	 * @throws IllegalArgumentException si to n’existe pas dans le graphe.
	 */
	List<T> getNeighborsIn(T to) throws IllegalArgumentException;
	
	/**
	 * renvoie la liste des noeuds accessibles à partir de from.
	 * @param from
	 * @return
	 * @throws IllegalArgumentException si from n’existe pas dans le graphe.
	 */
	Set<T> accessible(T from) throws IllegalArgumentException;
	
	/**
	 * teste si le graphe est acyclique.
	 * 
	 * on notera que ceci est vrai si aucun des noeuds ne peut être accessible à partir de lui-même.
	 * @return
	 */
	boolean isDAG();
}
