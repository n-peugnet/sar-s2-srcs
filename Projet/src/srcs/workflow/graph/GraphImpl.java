package srcs.workflow.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GraphImpl<T> implements Graph<T> {
	
	protected Set<T> nodes = new HashSet<>();
	protected Set<Edge<T>> edges = new HashSet<>();

	/** {@inheritDoc} */
	@Override
	public Iterator<T> iterator() {
		return nodes.iterator();
	}

	/** {@inheritDoc} */
	@Override
	public void addNode(T n) throws IllegalArgumentException {
		if (nodes.contains(n)) {
			throw new IllegalArgumentException("The node " + n + " already exists");
		}
		nodes.add(n);
		
	}

	/** {@inheritDoc} */
	@Override
	public void addEdge(T from, T to) throws IllegalArgumentException {
		if (!nodes.contains(from)) {
			throw new IllegalArgumentException("The node 'from' " + from + " does not exists");
		}
		if (!nodes.contains(to)) {
			throw new IllegalArgumentException("The node 'to' " + to + " does not exists");
		}
		Edge<T> e = new Edge<T>(from, to);
		if (edges.contains(e)) {
			throw new IllegalArgumentException("The edge " + e + " already exists");
		}
		edges.add(e);
		
	}

	/** {@inheritDoc} */
	@Override
	public boolean existEdge(T from, T to) {
		return edges.contains(new Edge<T>(from, to));
	}

	/** {@inheritDoc} */
	@Override
	public boolean existNode(T n) {
		return nodes.contains(n);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return nodes.size();
	}

	/** {@inheritDoc} */
	@Override
	public List<T> getNeighborsOut(T from) throws IllegalArgumentException {
		if (!nodes.contains(from)) {
			throw new IllegalArgumentException("The node " + from + " does not exists");
		}
		List<T> neighbours = new ArrayList<>();
		for (Edge<T> edge : edges) {
			if (edge.from.equals(from)) {
				neighbours.add(edge.to);
			}
		}
		return neighbours;
	}

	/** {@inheritDoc} */
	@Override
	public List<T> getNeighborsIn(T to) throws IllegalArgumentException {
		if (!nodes.contains(to)) {
			throw new IllegalArgumentException("The node " + to + " does not exists");
		}
		List<T> neighbours = new ArrayList<>();
		for (Edge<T> edge : edges) {
			if (edge.to.equals(to)) {
				neighbours.add(edge.from);
			}
		}
		return neighbours;
	}

	/** {@inheritDoc} */
	@Override
	public Set<T> accessible(T from) throws IllegalArgumentException {
		Set<T> visited = new HashSet<>();
		visited.add(from);
		return accessible(from, visited);
	}
	
	/**
	 * fonction récursive renvoyant la liste des noeuds accessibles à partir de from
	 * en excluant les noeuds déjà visités.
	 * @param from
	 * @param visited
	 * @return la liste des noeuds accessibles.
	 */
	protected Set<T> accessible(T from, Set<T> visited) {
		Set<T> list = new HashSet<>(getNeighborsOut(from));
		Set<T> toVisit = new HashSet<>(list);
		toVisit.removeAll(visited);
		visited.addAll(list);
		for (T n : toVisit) {
			list.addAll(this.accessible(n, visited));
		}
		return list;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDAG() {
		for (T n : nodes) {
			if (accessible(n).contains(n)) {
				return false;
			}
		}
		return true;
	}
	
	protected static class Edge<T> {
		T from;
		T to;
		
		public Edge(T from, T to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.from, this.to);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this.getClass().isInstance(obj)) {
				Edge<?> edge = this.getClass().cast(obj);
				return from.equals(edge.from) && to.equals(edge.to);
			}
			return false;
		}
		
		@Override
		public String toString() {
			return from.toString() + "->" + to.toString();
		}
	}

}
