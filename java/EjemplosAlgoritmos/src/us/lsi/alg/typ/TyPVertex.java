package us.lsi.alg.typ;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jgrapht.GraphPath;

import us.lsi.common.IntPair;
import us.lsi.common.List2;
import us.lsi.graphs.virtual.ActionSimpleEdge;
import us.lsi.graphs.virtual.ActionVirtualVertex;
import us.lsi.tareasyprocesadores.datos.Tarea;

public record TyPVertex(Integer index, List<Double> cargas) 
      implements ActionVirtualVertex<TyPVertex, ActionSimpleEdge<TyPVertex,Integer>, Integer>{

	
	public static Integer numeroDeProcesadores;
	public static Integer numeroDeTareas;
	public static TyPVertex inicial;
	public static Integer n;
	public static Integer m;
	
	public static void datos(String fichero, Integer np) {
		Tarea.leeTareas(fichero);
		numeroDeProcesadores = np;
		numeroDeTareas = Tarea.tareas.size();
		n = numeroDeTareas;
		m = numeroDeProcesadores;
	}
	
	public static TyPVertex first() {
		return new TyPVertex(0,List2.copy(0.,m));
	}
	
	public static TyPVertex last() {
		n = numeroDeTareas;
		m = numeroDeProcesadores;
		return new TyPVertex(n,List2.copy(0.,m));
	}
	
	public static TyPVertex of(Integer index, List<Double> cargas) {
		List<Double> cargasC = List.copyOf(cargas);
		return new TyPVertex(index,cargasC);
	}
	
	public Boolean goal() {
		return this.index()==TyPVertex.n;
	}
	
	public Integer npMax() {
		return IntStream.range(0,m)
		.boxed()
		.max(Comparator.comparing(i->this.cargas.get(i)))
		.get();
	}
	
	public Integer npMin() {
		return IntStream.range(0,m)
				.boxed()
				.min(Comparator.comparing(i->this.cargas.get(i)))
				.get();
	}

	public Double maxCarga() {
		return this.cargas().get(this.npMax());
	}

	@Override
	public Boolean isValid() {
		return true;
	}

	@Override
	public List<Integer> actions() {
		if(this.goal()) return List2.of();
		return List2.rangeList(0,m);
	}
	
	public List<Double> cargasDespues(Integer a){
		Double d = Tarea.getDuracion(this.index) + this.cargas().get(a);
        return List2.setElement(this.cargas(), a, d);
	}
	
	public Integer greadyAction() {
		return IntStream.range(0,m)
				.boxed()
				.min(Comparator.comparing(a->cargasDespues(a).get(a)))
				.get();
	}
	
	public  ActionSimpleEdge<TyPVertex, Integer> greadyEdge() {
		return this.edge(this.greadyAction());
	}

	@Override
	public TyPVertex neighbor(Integer a) {
		return TyPVertex.of(index+1, cargasDespues(a));
	}
	
	@Override
	public ActionSimpleEdge<TyPVertex, Integer> edge(Integer a) {
		return ActionSimpleEdge.of(this,this.neighbor(a), a, 1.);
	}
	
	public static TyPVertex copy(TyPVertex vertex) {
		return new TyPVertex(vertex.index,vertex.cargas);
	}
	
	
	public static SolucionTyP getSolucion(GraphPath<TyPVertex, ActionSimpleEdge<TyPVertex,Integer>> path){	
		Map<Integer,List<Tarea>> carga = path.getEdgeList().stream()
				.map(e->IntPair.of(e.action(),e.source().index()))
				.collect(Collectors.groupingBy(p->p.first(),Collectors.mapping(p->Tarea.getTarea(p.second()), Collectors.toList())));
		
		TyPVertex v = List2.last(path.getVertexList());
		return SolucionTyP.of(carga, v.maxCarga(), v.npMin(), v.npMax());
	}

}
