import java.net.*;
import java.io.*;
import java.io.File;
import java.awt.image.*;
import java.util.*;
import javax.imageio.*;
import com.opencsv.*;

public class ServidorO{
	private ServerSocket s;
	private Socket cl;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String[][] catalogo = new String[5][7], saveCarrito = null;
	private int nf = 0, cuenta;
	private int[][] carrito =  null;
	private Articulo a1;
	private Pedido p1;
	private File carritoF = null;
	private String user;
	
	public ServidorO(){
		ConexionServ();
	}

	public static void main(String[] arg)
	{
		new ServidorO();
		/**/
	}
	
	public void ConexionServ(){
		try
		{
			int pto=1234;
			s = new ServerSocket(pto);
			System.out.println("servicio iniciado... Esperando cliente...");
			s.setReuseAddress(true);//
			
			for(;;)
			{
				cl = s.accept();
				cl.setSoLinger(true,5000);//Se habilita una vez que se invoca el metodo close()
				System.out.println("Cliente conectado desde:"+cl.getInetAddress()+":"+cl.getPort());
				
				ois = new ObjectInputStream(cl.getInputStream());
				oos = new ObjectOutputStream(cl.getOutputStream());
				
				
				
				String opc=(String)ois.readObject();				
				System.out.println("Instruccion recibida: "+opc);
				switch(opc.toUpperCase())
				{
					case "CATA":
						nf = 0;
						EnviarCatalogo();
						break;
						
					case "PEDI":
						RecibirPedido();
						break;
						
					case "USER":
						EvaluarUsuario();
						break;
						
					default:
						System.out.println("---OPCION INVALIDA");
				}
				
				oos.close();
				ois.close();
				cl.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();//Vaciado de pila. Metodos que se ejecutaron hasta la excepcion
		}
	}
	
	public void EnviarCatalogo(){
		System.out.println("leyendo cvs...");
		try{
			String productos = "Productos.csv";
			CSVReader csvReader = new CSVReader(new FileReader(productos));
			String[] columnas = null;
			while((columnas = csvReader.readNext()) != null) {
				for(int i=0;i<7;i++){
					catalogo[nf][i] = columnas[i];
				}
				nf++;
			}
			csvReader.close();
			oos.writeObject(nf);
			oos.flush();
			
			for(int i = 0; i < nf; i++){
				System.out.println("Enviando producto: " + catalogo[i][0]);
				
				a1 = new Articulo(Integer.parseInt(catalogo[i][0]), catalogo[i][1], catalogo[i][2], catalogo[i][3],
											Double.parseDouble(catalogo[i][4]), Integer.parseInt(catalogo[i][5]), ConvertirImagen(catalogo[i][6]));

				oos.writeObject(a1);
				oos.flush();

				System.out.println("Enviado." + catalogo[i][0]);
			}
		}catch(Exception e){
		}
	}
	
	public void RecibirPedido(){
		try{
			p1 = (Pedido)ois.readObject();
			System.out.println("Pedido recibido.");
			if(p1.getVog() == false){
				guardarCarrito();
			}else{
				actualizarExist();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void actualizarExist(){
		System.out.println("Actualizando esxistencias...");
		
		File catalogoV = new File("Productos.csv");
		catalogoV.delete();
		
		try{
			CSVWriter writer = new CSVWriter(new FileWriter("Productos.csv"), ',');
			int arti = 0;
			for(int i = 0; i < nf; i++){
				if(Integer.parseInt(catalogo[i][0]) == p1.getarticulos()[arti][0]){
					catalogo[i][5] = "" + (Integer.parseInt(catalogo[i][5]) - p1.getarticulos()[arti][1]);
					arti++;
				}
				writer.writeNext(catalogo[i]);
			}
			writer.close();
			
			System.out.println("Existencias actualizadas.");
		}catch(Exception e){
		}
	}
	
	public void guardarCarrito(){
		System.out.println("Guardando carrito...");
		
		saveCarrito = new String[p1.getNumArticulos()][2];
		for(int i = 0; i < p1.getNumArticulos(); i++){
			saveCarrito[i][0] = "" + p1.getarticulos()[i][0];
			saveCarrito[i][1] = "" + p1.getarticulos()[i][1];
			System.out.println(saveCarrito[i][0]);
		}
		
		carritoF = new File("Carrito-"+p1.getUser()+".csv");
		
		if(carritoF.exists()){
			carritoF.delete();
			System.out.println("Carrito eliminado.");
		}
				
		try{
			CSVWriter writer = new CSVWriter(new FileWriter("Carrito-"+p1.getUser()+".csv"), ',');
			for(int i = 0; i < p1.getNumArticulos(); i++){
					writer.writeNext(saveCarrito[i]);
			}
			writer.close();
			
			System.out.println("Carrito guardado.");
		}catch(Exception e){
		}
	}
	
	public void EvaluarUsuario(){
		try{
			System.out.println("Recibiendo usuario.");
			user = (String)ois.readObject();
			System.out.println("Usuario recibido.");
			
			carritoF = new File("Carrito-"+user+".csv");
			boolean valid = false;
			if(carritoF.exists()){
				valid = true;
				oos.writeObject(valid);
				oos.flush();
				cargarCarrito();
				oos.writeObject(carrito);
				oos.flush();
				System.out.println("Carrito enviado.");
			}
			else{
				valid = false;
				oos.writeObject(valid);
				oos.flush();
				System.out.println("Usuario no encontrado.");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void cargarCarrito(){
		System.out.println("Cargando carrito...");
		
		try{
			String nombreFC = "Carrito-"+user+".csv";
			CSVReader csvReader1 = new CSVReader(new FileReader(nombreFC));
			String[] carritoCarg1 = null;
			cuenta = 0;
			while((carritoCarg1 = csvReader1.readNext()) != null) {
				cuenta++;
			}
			System.out.println("Productos cargados: "+cuenta);
			csvReader1.close();
			
			//ENVIAR 'cuenta'
			
			carrito = new int[nf][2];
			
			CSVReader csvReader2 = new CSVReader(new FileReader(nombreFC));
			String[] carritoCarg2 = null;
			int la = 0;
			while((carritoCarg2 = csvReader2.readNext()) != null) {
				System.out.println(carritoCarg2[0]);
					carrito[la][0] = Integer.parseInt(carritoCarg2[0]);
					carrito[la][1] = Integer.parseInt(carritoCarg2[1]);
					System.out.println("Produto "+la+": " + carrito[la][0] + "\t" + carrito[la][1]);
				la++;
			}
			csvReader2.close();
			System.out.println("Carrito cargado.");
		}catch(Exception e){
		}
		//carritoF.delete();
	}
	
	public byte[] ConvertirImagen(String imag){
		ByteArrayOutputStream outputStream = null;
		try{
			BufferedImage imagen = ImageIO.read(new File("Imagenes/"+imag));
			outputStream = new ByteArrayOutputStream();
			ImageIO.write(imagen, "jpg", outputStream);
		}catch(Exception e){}
		return outputStream.toByteArray();
	}
}