package us.lsi.iterativorecursivos;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import us.lsi.common.IntPair;
import us.lsi.common.IntQuartet;
import us.lsi.common.List2;
import us.lsi.common.Matrix;
import us.lsi.common.Pair;
import us.lsi.common.Preconditions;
import us.lsi.common.String2;
import us.lsi.common.View2;
import us.lsi.common.View4;
import us.lsi.streams.Stream2;

import org.apache.commons.math3.fraction.BigFraction;

public class RecursivosMultiples {

// fib(n) = fib(n-1)+fib(n-2), fib(0) = 0, fib(1) = 1
	
	
	/**
	 * Adaptaci�n del algoritmo anterior pot(b,n) para el c�lculo de los n�mero de Fibonacci
	 * 
	 * @param n T�rmino
	 * @return Valor del n-esimo n�mero de Fibonacci calculado de forma iterativa
	 */
	public static long fib(int n){
			int i = 0;
		    int a = 1;
			int b = 0;
			while(i < n){
				i = i+1;
				int a0 = a;
				a = a0+b;
				b = a0;
			}
			return b;
	}
	
	
	/**
	 * C�lculo de la funci�n f definida por:
	 * 
	 * f(n) = d1 si n=1
	 * f(n) = d0 si n=0
	 * f(n) = a*f(n-1)+b*f(n-2) si n &gt; 1
	 * 
	 * @param n Par�metro de entrada
	 * @param a Par�metro de entrada
	 * @param b Par�metro de entrada
	 * @param d1 Par�metro de entrada
	 * @param d0 Par�metro de entrada
	 * @return El valor de f(n,a,b,d1,d0) calculado de forma recursiva sin memoria
	 */
	public static BigInteger fbsm(Integer n,Integer a, Integer b, Integer d1,Integer d0){
		BigInteger r;
		if(n==0) {
			r = BigInteger.ZERO;
		} else if(n==1) {
			r = BigInteger.ONE;
		} else {
			BigInteger aa = new BigInteger(a.toString());
			BigInteger bb = new BigInteger(b.toString());
			r = aa.multiply(fbsm(n-1,a,b,d1,d0)).add(bb.multiply(fbsm(n-2,a,b,d1,d0)));
		}
		return r;
	}
	
	/**
	 * C�lculo de la funci�n f definida por:
	 * 
	 * f(n) = d1 si n=1
	 * f(n) = d0 si n=0
	 * f(n) = a*f(n-1)+b*f(n-2) si n &gt; 1
	 * 
	 * @param n Par�metro de entrada
	 * @param a Par�metro de entrada
	 * @param b Par�metro de entrada
	 * @param d1 Par�metro de entrada
	 * @param d0 Par�metro de entrada
	 * @return El valor de f(n,a,b,d1,d0) calculado de forma recursiva con memoria
	 */
	public static BigInteger fbm(Integer n,Integer a, Integer b, Integer d1,Integer d0){
		Map<Integer,BigInteger> m = new HashMap<>();
		BigInteger aa = new BigInteger(a.toString());
		BigInteger bb = new BigInteger(b.toString());
		BigInteger dd1 = new BigInteger(d1.toString());
		BigInteger dd0 = new BigInteger(d0.toString());
		return fbm(m,n,aa,bb,dd1,dd0);
	}
	
	public static BigInteger fbm(Map<Integer,BigInteger> m, Integer n,BigInteger a, BigInteger b, BigInteger d1,BigInteger d0){
		BigInteger r;
		if(m.containsKey(n)) {
			r = m.get(n);
		} else if(n==0) {
			r = d0;
			m.put(n,r);
		} else if(n==1) {
			r = d1;
			m.put(n,r);
		} else {
			r = a.multiply(fbm(m,n-1,a,b,d1,d0)).add(b.multiply(fbm(m,n-2,a,b,d1,d0)));
			m.put(n,r);
		}
		return r;
	}
	
	public static BigInteger fblin(Integer n,Integer a, Integer b, Integer d1,Integer d0){
		Map<Integer,BigInteger> m = new HashMap<>();
		BigInteger aa = new BigInteger(a.toString());
		BigInteger bb = new BigInteger(b.toString());
		BigInteger dd1 = new BigInteger(d1.toString());
		BigInteger dd0 = new BigInteger(d0.toString());
		for(int i = 0;i<=n;i++) {
			BigInteger r;
			if(i==0) r = dd0;
			else if(i==1) r = dd1;
			else r = aa.multiply(m.get(i-1)).add(bb.multiply(m.get(i-2)));
			m.put(i, r);
			m.remove(i-3);
		}
		return m.get(n);
	}
	
	public static BigInteger fbMatrix(Integer n,Integer a, Integer b, Integer d1,Integer d0){
		Integer[] bb = {a,b,1,0};
		Integer[] cl = {d1,d0};
		Matrix<BigFraction> base = Matrix.of(bb, 2, 2).map(e->new BigFraction(e));
		Matrix<BigFraction> colum = Matrix.of(cl, 2, 1).map(e->new BigFraction(e));
		Matrix<BigFraction> m = Matrix.pow(base, n);
		Matrix<BigFraction> r = Matrix.multiply(m,colum);
		return r.get(1, 0).getNumerator();
	}
	
	

	public static record Bn(Long a, Long b) {
		public static Bn of(Long a, Long b) {
			return new Bn(a,b);
		}
	}
		
	public static BigInteger binom(Long n, Long k) {
		Map<Bn,BigInteger> m = new HashMap<>();
		for(Long i=0L; i<=n; i++) {
			for(Long j=0L; j<=i;j++) {
				if(j==0) m.put(Bn.of(i,0L),BigInteger.ONE);
				else if(j==1 || i-j ==1) m.put(Bn.of(i,j),new BigInteger(i.toString()));
				else {
					BigInteger r = m.get(Bn.of(i-1, j-1)).add(m.get(Bn.of(i-1, j-1)));
					m.put(Bn.of(i,j),r);
				}
				m.remove(Bn.of(i-2,k));
			}				
		}
		return m.get(Bn.of(n, k));
	}

	
	
	public static record Rectangle(Integer fMin, Integer cMin, Integer fMax, Integer cMax) implements Comparable<Rectangle>{
		public static Rectangle of(Integer fMin,Integer cMin,Integer fMax, Integer cMax) {
			Preconditions.checkArgument(fMax-fMin>=0 && cMax-cMin>=0,String.format("fMin = %d,cMin=%d, fMax=%d, cMax=%d",fMin,cMin,fMax,cMax));
			return new Rectangle(fMin,cMin,fMax,cMax);
		}
		public static Rectangle of(IntPair min,IntPair max) {
			return new Rectangle(min.first(),min.second(),max.first(),max.second());
		}
		public Integer area() {
			return (fMax-fMin)*(cMax-cMin);
		}
		@Override
		public int compareTo(Rectangle other) {
			return this.area().compareTo(other.area());
		}
	}
	
	public static <E> Matrix<E> largeRectangle1(Matrix<E> m, Predicate<E> pd){	
		return Matrix.allSubMatrix(m)
				.filter(mx->Matrix.allElements(mx,pd))
				.max(Comparator.comparing(my->my.area()))
				.orElse(Matrix.empty());	
	}
	
	

	public static <E> Matrix<E> largeRectangle4(Matrix<E> m, Predicate<E> pd){
		Matrix<E> r;
		if(m.nf()>2 && m.nc()>2) {
			View4<Matrix<E>> vx = m.views4();
			Matrix<E> ma = largeRectangle4(vx.a(),pd);
			Matrix<E> mb = largeRectangle4(vx.b(),pd);
			Matrix<E> mc = largeRectangle4(vx.c(),pd);
			Matrix<E> md = largeRectangle4(vx.d(),pd);
			Matrix<E> ct = center4(m,pd);
			r = List.of(ma,mb,mc,md,ct).stream().max(Comparator.comparing(my->my.area())).get();
		} else {
			r = largeRectangle1(m,pd);
		}
		return r;
	}
	
	public static <E> Matrix<E> center4(Matrix<E> m, Predicate<E> pd) {
		Integer fc = m.nf()/2;
		List<IntPair> fBlocks = fileBlocks(fc,m,pd);
		Integer cc = m.nc()/2;
		List<IntPair> cBlocks = columBlocks(cc,m,pd);
		Matrix<E> ctf = fBlocks.stream()
				.map(p->IntPair.of(p.center(),cc))
				.map(c->center(c,m,pd))
				.max(Comparator.comparing(my->my.area())).orElse(Matrix.empty());
		Matrix<E> ctc = cBlocks.stream()
				.map(p->IntPair.of(fc,p.center()))
				.map(c->center(c,m,pd))
				.max(Comparator.comparing(my->my.area())).orElse(Matrix.empty());
		return List.of(ctf,ctc).stream().max(Comparator.comparing(my->my.area())).orElse(Matrix.empty());
	}
	
	public static <E> Matrix<E> largeRectangle2(Matrix<E> m, Predicate<E> pd){
		Matrix<E> r;
		if(m.nf()>2 && m.nc()>2) {
			View2<Matrix<E>> vx = m.views2F();
			Matrix<E> ma = largeRectangle2(vx.left(),pd);
			Matrix<E> mb = largeRectangle2(vx.right(),pd);
		    Matrix<E> ct = center2(m,pd);
			r = List.of(ma,mb,ct).stream().max(Comparator.comparing(my->my.area())).get();
		} else {
			r = largeRectangle1(m,pd);
		}
		return r;
	}
	
	public static <E> Matrix<E> center2(Matrix<E> m, Predicate<E> pd){
		Integer fc = m.nf()/2;
	    List<IntPair> blocks = fileBlocks(fc,m,pd);
		Matrix<E> ct = blocks.stream()
				.map(p->IntPair.of(p.center(),m.nc()/2))
				.map(c->center(c,m,pd))
				.max(Comparator.comparing(my->my.area()))
				.orElse(Matrix.empty());
		return ct;
	}
	
	public static <E> List<IntPair> columBlocks(Integer c, Matrix<E> m, Predicate<E> pd) {
		List<IntPair> r = new ArrayList<>();
		Integer f = 0;
		while(f<m.nf()) {
			while(f<m.nf() && !pd.test(m.get(f,c))) f++;
			Integer fi = f;
			while(f<m.nf() && pd.test(m.get(f,c))) f++;
			Integer fd = c;
			if (fd>fi) r.add(IntPair.of(fi, fd));
		}
		return r;
	}
	
	public static <E> List<IntPair> fileBlocks(Integer f, Matrix<E> m, Predicate<E> pd) {
		List<IntPair> r = new ArrayList<>();
		Integer c = 0;
		while(c<m.nc()) {
			while(c<m.nc() && !pd.test(m.get(f,c))) c++;
			Integer ci = c;
			while(c<m.nc() && pd.test(m.get(f,c))) c++;
			Integer cd = c;
			if (cd>ci) r.add(IntPair.of(ci, cd));
		}
		return r;
	}
	
	
	public static <E> Integer filesDown(Integer f, Integer c, Integer stopF, Matrix<E> m, Predicate<E> pd) {
		while (f>=stopF && pd.test(m.get(f, c))) f--;
		return f+1;
	}
	
	public static <E> Integer filesUp(Integer f, Integer c, Integer stopF, Matrix<E> m, Predicate<E> pd) {
		while (f<stopF && pd.test(m.get(f, c))) f++;
		return f;
	}
	
	public static <E> Matrix<E> center(IntPair center, Matrix<E> m, Predicate<E> pd) {
		Integer f = center.first();;
		Integer c = center.second();
		if (!pd.test(m.get(f,c))) return Matrix.empty();
		Integer ci = center.second();
		Integer cd = center.second()+1;
		Integer fMin = 0;
		Integer fMax = m.nf();
		Rectangle rMax = Rectangle.of(fMin,ci,fMax,ci);
		do { 
			Integer fd = filesDown(center.first(),c,0,m,pd);
			Integer fu = filesUp(center.first(),c,m.nf(),m,pd);
			Preconditions.checkArgument(fu>=fd,String.format("%d,%d",fd,fu));
			if(fd > fMin) fMin = fd;
			if(fu < fMax) fMax = fu;
			Rectangle r = Rectangle.of(fMin,ci,fMax,cd);
			if(r.compareTo(rMax)>0) rMax = r;
			if(c%2==0) {
				ci--;
				c = ci;
			} else {
				cd++;
				c = cd;
			}
		} while	(ci >= 0 && pd.test(m.get(f,ci)) && cd < m.nc() && pd.test(m.get(f,cd)));
		return m.view(rMax.fMin(),rMax.cMin(),rMax.fMax()-rMax.fMin(),rMax.cMax()-rMax.cMin()+1);
	}
	
	public static <E> Boolean cumplePropiedadRecursiva(Matrix<E> m, Predicate<Matrix<E>> pd) {
		Boolean r;
		if(m.nf()>2 && m.nc()>2) {
			View4<Matrix<E>> vx = m.views4();
			r =	cumplePropiedadRecursiva(vx.a(),pd) &&
			    cumplePropiedadRecursiva(vx.b(),pd) &&
			    cumplePropiedadRecursiva(vx.c(),pd) &&
			    cumplePropiedadRecursiva(vx.d(),pd) &&
			    pd.test(m);
		} else {
			r =  pd.test(m);
		}
		return r;
	}
	
	public static record SolH(Integer from, Integer to,Integer minI, Integer a, List<Integer> ls)
			implements Comparable<SolH> {

		public static SolH of(Integer from, Integer to, Integer minI, List<Integer> ls) {
			Integer h = ls.get(minI);
			Integer a = (to - from) * h;
			return new SolH(from, to, minI, a, ls);
		}

		public static SolH zero() {
			return of(-1, -1, -1, null);
		}

		@Override
		public int compareTo(SolH other) {
			return this.a().compareTo(other.a());
		}

		@Override
		public String toString() {
			return String.format("(inf=%d,sup=%d,minIndex=%d,area=%d)", this.from(), this.to(), this.minI(),
					this.a());
		}
	}
	
	public static Integer minIndex(List<Integer> ls, Integer i1, Integer i2) {
		return ls.get(i1) < ls.get(i2) ?i1:i2;
	}
	
	public static Integer minIndex(List<Integer> ls, Integer i1, Integer i2, Integer i3) {
		return minIndex(ls,i1,minIndex(ls,i2,i3));
	}


	public static SolH histograma0(List<Integer> ls) {
		Integer n = ls.size();
		SolH amax = SolH.zero();
		for (Integer i = 0; i < n; i++) {
			for (Integer j = i+1; j < n; j++) {
				Integer m = i;
				for(Integer k=i+1;k<j;k++) {
					m = minIndex(ls,m,k);
				}
				SolH e = SolH.of(i,j,m,ls);
				if (e.compareTo(amax) > 0) amax = e;
			}
		}
		return amax;
	}


	public static SolH histograma1(List<Integer> ls){
		Integer n = ls.size();
		SolH em = SolH.zero();
		for(Integer i = 0; i < n; i++){
			Integer m = i;
			for(Integer j = i+1; j <= n; j++){
				m = minIndex(ls,j-1,m);
				SolH e = SolH.of(i,j,m,ls);
				if (e.compareTo(em) > 0) em = e;
			}
		}
		return em;
	}

	public static SolH histograma2(List<Integer> ls){
		Integer n = ls.size();
		return histograma2(0,n,ls);
	}

	public static SolH histograma2(Integer i, Integer j, List<Integer> ls){
		SolH rm;
		if(j-i>1){
			Integer k = (j+i)/2;
			SolH r1 = histograma2(i,k,ls);
			SolH r2 = histograma2(k,j,ls);
			SolH r3 = centro(i,j,k,ls);
			rm = List.of(r1,r2,r3).stream().max(Comparator.naturalOrder()).get();
		} else if (j-i == 1) {
			rm = SolH.of(i,j,i,ls);
		} else {
			rm = SolH.zero();
		}
		return rm;
	}


	public static SolH centro(Integer i, Integer j, Integer k, List<Integer> ls) {
	        Integer i1 = k-1, j1 = k+1;
	        Integer min_index = minIndex(ls,i1,j1-1);
	        SolH amax = SolH.zero();
	        while(i1 >= i && j1 <= j) {
	            min_index = minIndex(ls,min_index,i1,j1-1);
	            SolH r = SolH.of(i1,j1,min_index,ls);
	            if(r.compareTo(amax) > 0) amax = r;
	            if (i1 == i) j1++;
	            else if (j1 == j) i1--;
	            else {
	                if (ls.get(i1-1) >= ls.get(j1))  i1--;
	                else j1++;
	            }
	        }
	        return amax;
	}
	
	public static <E> Matrix<E> subMatrixConPropiedad(Matrix<E> mt, Predicate<Matrix<E>> pd){
		return mt.allSubMatrix()
				.filter(m->m.nf()>1 && m.nc()>1)
				.filter(pd)
				.findFirst()
				.get();
	}

	public static void test1() {
		Long t0 = System.nanoTime();
		BigInteger r1 = fblin(10000,1,1,1,0);
		Long t1 = System.nanoTime();
		BigInteger r2 = fbm(10000,1,1,1,0);
		Long t2 = System.nanoTime();
		BigInteger r3 = fbMatrix(10000,1,1,1,0);
		Long t3 = System.nanoTime();
		String2.toConsole("%d == %s\n%d == %s\n%d == %s",t1-t0,r1,t2-t1,r2,t3-t2,r3);
	}
	
	public static void test2() {
		Long t0 = System.nanoTime();
		BigInteger r1 = fblin(100000,1,1,1,0);
		Long t1 = System.nanoTime();
//		BigInteger r2 = fbm(10000,1,1,1,0);
//		Long t2 = System.nanoTime();
		BigInteger r3 = fbMatrix(100000,1,1,1,0);
		Long t3 = System.nanoTime();
		String2.toConsole("%d == %s\n%d == %s",t1-t0,r1,t3-t1,r3);
	}
	
	public static void test3() {
		Integer[] d = {0,1,1,0,1,1,1,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,0,1,1,1,0,1,0,0,1,1,1,0,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0};	
		Matrix<Integer> r = Matrix.of(d,12,5);
		String2.toConsole("___________________");
		String2.toConsole(r.toString());
		String2.toConsole("___________________");
		String2.toConsole(largeRectangle1(r,e->e.equals(1)).toString());
		String2.toConsole("___________________");
		String2.toConsole(largeRectangle4(r,e->e.equals(1)).toString());
		String2.toConsole("___________________");
		String2.toConsole(largeRectangle2(r,e->e.equals(1)).toString());
	}
	
	public static void test4() {
		Integer[] d = {0,1,1,0,1,1,1,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,0,1,1,1,0,1,0,0,1,1,1,0,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0};
		Matrix<Integer> r = Matrix.of(d,12,5);
		String2.toConsole("______________\n%s\n",r.toString());
		r.allSubMatrix()
		.filter(mt->mt.nf()==3 && mt.nc()==4)
		.forEach(m->String2.toConsole("______________\n%s\n",m));
//		.forEach(m->String2.toConsole("______________\n"));
	}
	
	public static void test5() {
		Predicate<Integer> pd = e->e.equals(1);
		Integer[] d = {0,1,1,0,1,1,1,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,0,1,1,1,0,1,0,0,1,1,1,0,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0};
		Integer[] d2 = {0,1,1,0,1,1,1,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,0,1,1,1,0,1,0,0,1,1,1,0,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0};
		Matrix<Integer> r = Matrix.of(d,12,5);
		Matrix<Integer> s = r.view(1,0,4,5);
		String2.toConsole("______________\n%s\n",r);
		String2.toConsole("______________\n%s\n",fileBlocks(7,r,pd));
		String2.toConsole("______________\n%s\n",s);
		String2.toConsole("______________\n%s\n",center(s.centerIntPair(),s,pd));
		Integer fc = 3;
		Matrix<Integer> ct = IntStream.range(0,r.nc()).boxed()
				.map(c->center(IntPair.of(fc,c),r,pd))
				.max(Comparator.comparing(my->my.area())).get();
		String2.toConsole("______________");
		String2.toConsole("%s",ct);
	}
	
	
	public static void test6() {
		Integer[][] mat =   { {1,0,0,1,0},{0,0,1,0,1},{0,0,0,1,0},{1,0,1,0,1}};
		Matrix<Integer> mt = Matrix.of(mat);
		Matrix<Integer> r = subMatrixConPropiedad(mt,m->m.corners().stream().allMatch(e->e.equals(1)));
		String2.toConsole("Solucion \n%s",r);
	}
	
	
	
	public static void test7() {
		List<Integer> ls = List.of(6,2,5,4,5,1,6);
		String2.toConsole("0 ___________\n%s\n",histograma0(ls));
		String2.toConsole("1 ___________\n%s\n",histograma1(ls));
		String2.toConsole("2 ___________\n%s\n",histograma2(ls));
	}
	
	public static void test8() {
		Integer[] d = {0,1,1,0,1,1,1,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,0,1,1,1,0,1,0,0,1,1,1,0,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0};	
		Matrix<Integer> r = Matrix.of(d,12,5);
		View2<Matrix<Integer>> vx = r.views2F();
		String2.toConsole("m \n%s",r);
		String2.toConsole("left \n%s",vx.left());
		String2.toConsole("right \n%s",vx.right());
	}
	
	
	public static void main(String[] args) {
		test3();
	}
	

}
