package us.lsi.alg.subconjuntos;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import us.lsi.graphs.Graphs2;
import us.lsi.graphs.alg.AStar;
import us.lsi.graphs.alg.GraphAlg;
import us.lsi.graphs.virtual.EGraph;

public class TestAStar {
	
	public static void main(String[] args) {

	// Set up
	Locale.setDefault(new Locale("en", "US"));

	for (Integer id_fichero = 1; id_fichero < 3; id_fichero++) {

		DatosSubconjuntos.iniDatos("ficheros/subconjuntos" + id_fichero + ".txt");
		System.out.println("\n\n>\tResultados para el test " + id_fichero + "\n");
//		DatosSubconjuntos.toConsole();
		

		// V�rtices clave

		SubconjuntosVertex start = SubconjuntosVertex.initial();

		// Grafo

		EGraph<SubconjuntosVertex, SubconjuntosEdge> graph = 
				Graphs2.simpleVirtualGraphSum(start,SubconjuntosVertex.goal(),null,v->true, x-> x.weight());

		System.out.println("\n\n#### PI-7 Ej3 Algoritmo Astar ####");
		
		AStar<SubconjuntosVertex, SubconjuntosEdge> aStar = GraphAlg.aStar(graph, 
				SubconjuntosHeuristic::heuristic);
		
		List<Integer> gp_as = aStar.search().get().getEdgeList().stream().map(x -> x.action())
				.collect(Collectors.toList()); // getEdgeList();
		SolucionSubconjuntos s_as = SolucionSubconjuntos.of(gp_as);
		System.out.println(s_as);
	}

	}
}
