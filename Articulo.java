import  java.io.Serializable;

public class Articulo implements Serializable{
	
	int id, exist;
	String nombre, desc, clasif;
	byte[] imagen;
	double precio;
	
	/*transient
		primitivos = 0;
		objetos = null;
	*/
	
	public Articulo(int id, String nombre, String desc, String clasif, double precio, int exist, byte[] imagen){
		this.id = id;
		this.nombre = nombre;
		this.desc = desc;
		this.clasif = clasif;
		this.precio = precio;
		this.exist = exist;
		this.imagen = imagen;
	}
	
	//GETers
	
	int getid(){
		return this.id;
	}
	
	String getnombre(){
		return this.nombre;
	}
	
	String getdesc(){
		return this.desc;
	}
	
	String getclasif(){
		return this.clasif;
	}
	
	double getprecio(){
		return this.precio;
	}
	int getexist(){
		return this.exist;
	}
	
	byte[] getimagen(){
		return this.imagen;
	}
	
	//SETeer
	
	void setexist(int exist){
		this.exist = exist;
	}

}