package us.lsi.common;

import java.lang.reflect.Array;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.fraction.Fraction;


/**
 * @author migueltoro
 *
 * @param <E> El tipo de los elementos de la matriz que deben ser elementos de un campo numérico
 */
public class Matrix<E extends FieldElement<E>> {
	
	public static <T extends FieldElement<T>> Matrix<T> of(Field<T> f, T[][] datos) {
		return new Matrix<>(f,datos);
	}
	
	public static <T extends FieldElement<T>> Matrix<T> ofValue(Field<T> f, int nf, int nc, T value) {
		@SuppressWarnings("unchecked")
		T[][] datos = (T[][]) Array.newInstance(f.getZero().getClass(),nf,nc);
		Matrix<T> r = Matrix.of(f,datos);
		for (int i = 0; i < nf; i++) {
			for (int j = 0; j < nc; j++)
				r.set(i,j,value);
		}
		return r;
	}
	
	public static <T extends FieldElement<T>> Matrix<T> zero(Field<T> f, int nf, int nc) {
		@SuppressWarnings("unchecked")
		T[][] datos = (T[][]) Array.newInstance(f.getZero().getClass(),nf,nc);
		Matrix<T> r = Matrix.of(f,datos);
		for (int i = 0; i < nf; i++) {
			for (int j = 0; j < nc; j++)
				r.set(i,j,f.getZero());
		}
		return r;
	}
	
	public static  <T extends FieldElement<T>> Matrix<T> one(Field<T> f, int nf){
		@SuppressWarnings("unchecked")
		T[][] datos = (T[][]) Array.newInstance(f.getZero().getClass(),nf,nf);
		Matrix<T> r = Matrix.of(f,datos);
		for (int i = 0; i < nf; i++) {
			for (int j = 0; j < nf; j++) {
				if (i == j) {
					r.set(i,j,f.getOne());
				} else {
					r.set(i,j,f.getZero());
				}
			}
		}
		return  r;
	}
	
	private E[][] datos;
	private int nf;
	private int nc;
	private int iv;	
	private int jv;
	private Field<E> field; 
	

	private Matrix(Field<E> f, E[][] datos) {
		super();
		this.datos = datos;		
		this.nf = datos.length;
		this.nc = datos[0].length;
		this.iv = 0;
		this.jv = 0;
		this.field = f;
	}

	private Matrix(Field<E> f, E[][] datos, int nf, int nc, int iv, int jv) {
		super();
		this.datos = datos;
		this.nf = nf;
		this.nc = nc;
		this.iv = iv;
		this.jv = jv;
		this.field = f;
	}

	E get(int i, int j) {
		return this.datos[this.iv+i][this.jv+j];
	}
	void set(int i, int j, E value) {
		this.datos[this.iv+i][this.jv+j] = value;
	}
	
	Matrix<E> view(int nf, int nc, int iv, int jv){
		Matrix<E> r = of(this.field,this.datos);
		r.iv = this.iv +iv; r.nf = nf; r.jv = this.jv +jv; r.nc = nc;
		return r;
	}
	
	Matrix<E> view(int nv){
		int nf = this.nf/2;
		int nc = this.nc/2;
		Matrix<E> r = of(this.field,this.datos);
		switch(nv){
		case 0:  r = view(nf,nc,0,0); break;
		case 1:  r = view(nf,this.nc-nc,0,nc); break;
		case 2:  r = view(this.nf-nf,nc,nf,0); break;
		case 3:  r = view(this.nf-nf,this.nc-nc,nf,nc); break;
		default: Preconditions.checkArgument(false, "Opción no válida");
		}
		return r;
	}
	
	void print(String title) {
		System.out.println(String.format("\n%s = (nf = %d, nc = %d, iv = %d, , jv = %d)\n",
				title,this.nf,this.nc,this.iv,this.jv));
		for (int i = 0; i < this.nf; i++) {
			for (int j = 0; j < this.nc; j++)
				System.out.print(String.format("%s ", this.get(i,j).toString()));
			System.out.println("\n");
		}
	}
	
	Matrix<E> copy(){
		return new Matrix<>(this.field,this.datos, this.nf, this.nc, this.iv,this.jv);
	}
	
	void copy(Matrix<E> r){
		Boolean ch = this.nc==r.nc && this.nf==r.nf;
		Preconditions.checkArgument(ch, "No se cumplen condiciones de copia");
	    for (int i = 0; i < this.nf; i++) {
	        for (int j = 0; j < this.nc; j++) {
	            r.set(i,j,this.get(i, j));
	        }
	    }
	}
	
	public static <T extends FieldElement<T>> void copy(Matrix<T> out, Matrix<T> in){
		Boolean ch = in.nc==out.nc && in.nf==out.nf;
		Preconditions.checkArgument(ch, "No se cumplen condiciones de copia");
	    for (int i = 0; i < in.nf; i++) {
	        for (int j = 0; j < in.nc; j++) {
	            out.set(i,j,in.get(i, j));
	        }
	    }
	}
	
	View4<Matrix<E>,E> views() {
		return MatrixView.of(this);
	}

	Matrix<E> multiply(Matrix<E> in2){
		Matrix<E> r = Matrix.zero(this.field,this.nf,in2.nc);
		Boolean ch = this.nc==in2.nf && r.nf == this.nf && r.nc == in2.nc;
		Preconditions.checkArgument(ch, "No se cumplen condiciones de multiplicación");
	    for (int i = 0; i < this.nf; i++) {
	        for (int j = 0; j < in2.nc; j++) {
	            r.set(i,j,this.field.getZero());
	            for (int k = 0; k < this.nc; k++){
	            	E val = (this.get(i,k).multiply(in2.get(k,j))).add(r.get(i,j));
	            	r.set(i,j,val);
	            }
	        }
	    }
	    return r;
	}
	
	Matrix<E> multiply_r(Matrix<E> m2){
		Matrix<E> r = Matrix.zero(this.field, this.nf, m2.nc);
		Boolean ch = this.nc==m2.nf;
		Preconditions.checkArgument(ch,String.format("No se cumplen condiciones de multiplicación = (in1.nc = %d, in2.nf = %d)",this.nc,m2.nf));
		if(this.nc < 2 || this.nf < 2 || m2.nf < 2 || m2.nc < 2) {
			r = this.multiply(m2);
		} else {
			View4<Matrix<E>, E> v1 = this.views();
			View4<Matrix<E>, E> v2 = m2.views();
			View4<Matrix<E>, E> vr = r.views();
			Matrix.copy(vr.m0(),(v1.m0().multiply_r(v2.m0())).add(v1.m1().multiply_r(v2.m2())));
			Matrix.copy(vr.m1(),(v1.m0().multiply_r(v2.m1())).add(v1.m1().multiply_r(v2.m3())));
			Matrix.copy(vr.m2(),(v1.m2().multiply_r(v2.m0())).add(v1.m3().multiply_r(v2.m2())));	
			Matrix.copy(vr.m3(),(v1.m2().multiply_r(v2.m1())).add(v1.m3().multiply_r(v2.m3())));
		}
		return r;
	}
	
	Matrix<E> add(Matrix<E> m2){
		Matrix<E> r = Matrix.zero(this.field,this.nf,m2.nc);
		Boolean ch = this.nc==m2.nc && this.nf==m2.nf && r.nc == this.nc && r.nf == this.nf;
		Preconditions.checkArgument(ch, "No se cumplen condiciones de suma");
	    for (int i = 0; i < this.nf; i++) {
	        for (int j = 0; j < this.nc; j++) {
	        	E val = this.get(i,j).add(m2.get(i,j));
	            r.set(i,j,val);
	        }
	    }
	    return r;
	}
	
	Matrix<E> add_r(Matrix<E> m2){
		Boolean ch = this.nc==m2.nc && this.nf==m2.nf;
		Preconditions.checkArgument(ch, "No se cumplen condiciones desuma");
		Matrix<E> r = Matrix.zero(this.field, this.nf, this.nc);
		if(this.nc > 1 && this.nf > 1) {
			View4<Matrix<E>, E> v1 = this.views();
			View4<Matrix<E>, E> v2 = m2.views();
			View4<Matrix<E>, E> rv = r.views();
			Matrix.copy(rv.m0(),v1.m0().add_r(v2.m0()));
			Matrix.copy(rv.m1(),v1.m1().add_r(v2.m1()));
			Matrix.copy(rv.m2(),v1.m2().add_r(v2.m2()));
			Matrix.copy(rv.m3(),v1.m3().add_r(v2.m3()));
		} else {
			r = this.add(m2);
		}
		return r;
	}
	
	static class MatrixView<E extends FieldElement<E>> implements View4<Matrix<E>,E>{
		private Matrix<E> m0;
		private Matrix<E> m1;
		private Matrix<E> m2;
		private Matrix<E> m3;
		public static <T extends FieldElement<T>> MatrixView<T> of(Matrix<T> m){
			return new MatrixView<>(m.view(0), m.view(1), m.view(2), m.view(3));
		}
		private MatrixView(Matrix<E> m0, Matrix<E> m1, Matrix<E> m2, Matrix<E> m3) {
			super();
			this.m0 = m0;
			this.m1 = m1;
			this.m2 = m2;
			this.m3 = m3;
		}
		
		public Matrix<E> m0() {
			return m0;
		}
		public Matrix<E> m1() {
			return m1;
		}
		public Matrix<E> m2() {
			return m2;
		}
		public Matrix<E> m3() {
			return m3;
		}
		
	}
	
	public static void main(String[] args) {
			Matrix<Fraction> m1 = Matrix.one(Fraction.ZERO.getField(), 7);
			m1.print("m1");
			Matrix<Fraction> m2 = Matrix.ofValue(Fraction.ZERO.getField(), 7, 7, Fraction.TWO);
			m2.print("m2");			
			Matrix<Fraction> m3 = m1.add(m2);
			m3.print("m3");
			Matrix<Fraction> m4 = m1.add_r(m2);
			m4.print("m4");
			Matrix<Fraction> m5 = m1.multiply(m2);
			m5.print("m5");
			Matrix<Fraction> m6 = m1.multiply_r(m2);
			m6.print("m6");
		}
}
