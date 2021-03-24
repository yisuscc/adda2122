package us.lsi.graphs.alg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import us.lsi.flujossecuenciales.Iterators;
import us.lsi.graphs.Graphs2;
import us.lsi.graphs.virtual.EGraph;
import us.lsi.path.EGraphPath.PathType;

public class DephtSearch<V, E> implements GraphAlg<V,E>, Iterator<V>, Iterable<V> {


	protected Map<V,E> edgeToOrigin;
	public Graph<V,E> graph;
	protected Stack<V> stack;
	protected V startVertex; 
	public Map<V,Integer> position;
	private Integer n = 0;

	DephtSearch(Graph<V, E> g, V startVertex) {
		this.graph = g;
		this.startVertex = startVertex;
		this.edgeToOrigin = new HashMap<>();
		this.edgeToOrigin.put(startVertex, null);
		this.stack = new Stack<>();
		this.stack.add(startVertex);
		this.position = new HashMap<>();
	}
	
	@Override
	public Stream<V> stream() {
		return Iterators.asStream(this.iterator());
	}
	
	@Override
	public DephtSearch<V, E> copy() {
		return GraphAlg.depth(this.graph, this.startVertex);
	}
	
	public Iterator<V> iterator() {
		return this;
	}
	
	public boolean isSeenVertex(V v) {
		return this.edgeToOrigin.containsKey(v);
	}
	
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	public V next() {
		V actual = stack.pop();
		for(V v:Graphs.neighborListOf(graph, actual)) {
			if(!this.edgeToOrigin.containsKey(v)) {
				stack.add(v);
				this.edgeToOrigin.put(v,graph.getEdge(actual, v));
			}
		}
		this.position.put(actual,n);
		n++;
		return actual;
	}

	public E getEdgeToOrigin(V v) {
		return this.edgeToOrigin.get(v);
	}

	@Override
	public EGraph<V, E> getGraph() {
		return Graphs2.eGraph(this.graph,startVertex(),PathType.Sum);
	}
	
	@Override
	public V startVertex() {
		return this.startVertex;
	}
	
	public Set<E> edges() {
		return this.edgeToOrigin.values().stream().collect(Collectors.toSet());
	}

}
