import  java.io.Serializable;

public class Pedido implements Serializable{
	
	int[][] articulos;
	Double total;
	int numArticulos;
	boolean vog;
	String user;
	
	
	/*transient
		primitivos = 0;
		objetos = null;
	*/
	
	public Pedido(int[][] articulos, Double total, int numArticulos, boolean vog, String user){
		this.articulos = articulos;
		this.total = total;
		this.numArticulos = numArticulos;
		this.vog = vog;
		this.user = user;
	}
	
	//GETers
	
	int[][] getarticulos(){
		return this.articulos;
	}
	
	Double gettotal(){
		return this.total;
	}
	
	int getNumArticulos(){
		return this.numArticulos;
	}
	
	boolean getVog(){
		return this.vog;
	}
	
	String getUser(){
		return this.user;
	}

}