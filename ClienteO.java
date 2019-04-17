import java.net.*;
import java.io.*;

public class ClienteO{
	private int pto=0;
	private String host="localhost";
	private Socket cl;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Pedido p1;
	private String[] listaPro;
	
	private Articulo[] catalogo;
	private int totalProductos = 0, objCarrito = 0;
	private int[][] carrito = null;
	
	
	public ClienteO(int pto, String host){
		this.pto = pto;
		this.host = host;
		ConexionClie();
	}
	
	public void ConexionClie(){
		try{
			cl = new Socket(host, pto);
			System.out.println("Cliente conectado, recibiendo objeto...");
			
			oos = new ObjectOutputStream(cl.getOutputStream());
			ois = new ObjectInputStream(cl.getInputStream());
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void CerrarConexion(){
		try{
			ois.close();
			oos.close();
			cl.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void EnviarPedido(int[][] articulos, Double total, int numArticulos, boolean vog, String user){
		try{
			oos.writeObject("PEDI");
			oos.flush();
			System.out.println("Enviando pedido.");
				
			p1 = new Pedido(articulos, total, numArticulos, vog, user);
			oos.writeObject(p1);
			oos.flush();
			System.out.println("Pedido enviado.");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void RecibeCatalogo(){
		try{
			oos.writeObject("CATA");
			oos.flush();
			
			totalProductos = (int)ois.readObject();
			
			catalogo = new Articulo[totalProductos];
			listaPro = new String[totalProductos];
			
			for(int i = 0; i< totalProductos; i++){
					catalogo[i] = (Articulo)ois.readObject();
					
					listaPro[i] = catalogo[i].getnombre();
				System.out.println("Recibido producto: "+catalogo[i].getid());
			}
			
		} catch(Exception e){
				e.printStackTrace();
		}
	}
	
	public int[][] validarUser(String user){
		int[][] lalala = null;
		try{
			oos.writeObject("USER");
			oos.flush();
			System.out.println("Enviando usuario.");
			
			oos.writeObject(user);
			oos.flush();
			
			boolean valid = (boolean)ois.readObject();
			
			//FALTA RECIBIR EL NUMERO DE PRODUCTOS QUE SE LEYERON DEL CARRITO.
			
			if(valid==true){
				objCarrito = (int)ois.readObject();
				System.out.println("Llegaron: "+objCarrito);
				carrito = (int[][])ois.readObject();
				lalala = carrito;
				
			} else {
				lalala = null;
			}
			System.out.println("Usuario enviado.");
		} catch(Exception e){
			e.printStackTrace();
		}
		return lalala;
	}
	
	public String[] Catalogo(){
		return listaPro;
	}
	
	public int getTotalProductos(){
		return totalProductos;
	}
	
	public Articulo[] getCatalogo(){
		return catalogo;
	}
	
	public int getObjCarrito(){
		return objCarrito;
	}
}