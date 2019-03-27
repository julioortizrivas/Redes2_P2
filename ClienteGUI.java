import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import com.opencsv.*;

public class ClienteGUI extends JFrame implements ActionListener {
	private JButton btnAgregar, btnCarrito, btnQuitar;
	private JEditorPane edpimagen;
	private JPanel p1 , p2, p3, p2_1, p3_1, p3_2;
	private Image image = null;
	private ImageIcon imagenprod;
	private JLabel lblimagen, lblnombre, lblclasif, lblprecio, lblexist, lbl, lblcat;
	private JTextArea lbldesc;
	private JList lista;
	private int selecMostrar = 0, artisCarrito = 0, mayorcar = 0;
	private ClienteO sCl;
	private int[][] carrito = null, carritoAux = null;
	private String[][] carritoS = null, saveCarrito = null;
	private Double totalAPagar = 0.0;
	private String user = null;
	private boolean uservalid = false;
	
	private DecimalFormat f = new DecimalFormat("###,###.##");
	
	char comilla=34;
	
	private ListSelectionListener listaListener = new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent listSelectionEvent) {
			pantallaInicial();
		}
	};
	
	public ClienteGUI(){
		super("Cliente:");
		
		while(uservalid == false){
			user = "" + JOptionPane.showInputDialog("Ingrese el usuario: ");
			if(user.length() > 0)
				uservalid = true;
		}
		
		sCl = new ClienteO(1234,"127.0.0.1");
		carrito = sCl.validarUser(user);
		sCl.CerrarConexion();
		
		sCl = new ClienteO(1234,"127.0.0.1");
		sCl.RecibeCatalogo();
		sCl.CerrarConexion();
		carritoS = new String[sCl.totalProductos][4];
		
		generarVentana();
	}
	
	//1 Bad log
	//2 Programa del eco - flujo
	//3 Programa del eco - datagrama
	//4 Mandar archivos - datagrama
	//5 Ahorcado en dos lenguajes
	
	public static void main(String[] args){
		new ClienteGUI();
	}
	
	public void pantallaInicial(){
		selecMostrar = lista.getSelectedIndex();
				asignarImagen(selecMostrar);
				lblimagen.setIcon(imagenprod);
				lblnombre.setText("Nombre: " + sCl.catalogo[selecMostrar].getnombre());
				lbldesc.setText("Descripci\u00F3n: " + sCl.catalogo[selecMostrar].getdesc());
				lblclasif.setText("Clasificaci\u00F3n: " + sCl.catalogo[selecMostrar].getclasif());
				lblprecio.setText("Precio: $" + f.format(sCl.catalogo[selecMostrar].getprecio()));
				lblexist.setText("Existencia: " + sCl.catalogo[selecMostrar].getexist());
				
				for(int i = 0; i < artisCarrito; i++){
					if(selecMostrar == carrito[i][0]){
						botonesAgregar(carrito[i][1]);
						break;
					} else{
						botonesQuitar();
					}
				}
	}
	
	public void generarVentana(){
		ventanaInicial();
		inicializar();
		if(carrito == null){
			carrito = new int[sCl.totalProductos][2];
		} else {
			 System.out.println(""+carrito[0][0]);
			actualizarCarro();
			pantallaInicial();		
		}
		
		
		//JFRAME
		this.setSize(700,700);
		this.setResizable(true);
		
		WindowListener exitListener = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirm = JOptionPane.showOptionDialog(
					 null, "\u00BFSeguro que desea salir de la aplicaci\u00F3n?", 
					 "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
					 JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (confirm == 0) {
					if(artisCarrito != 0)
						guardarCarrito();
					System.exit(0);
				} else {
				}
			}
		};
		
		this.addWindowListener(exitListener);

		this.add("North",p1);
		this.add("West",p2);
		this.add("Center",p3);
		this.setVisible(true);
	}
	
	
	
	public void ventanaInicial(){
		//PANEL 1 - Carrito
		btnCarrito =new JButton("Carrito");
		btnCarrito.setEnabled(true);
		btnCarrito.addActionListener(this);
		
		btnCarrito =new JButton("Carrito");
		btnCarrito.setEnabled(true);
		btnCarrito.addActionListener(this);
		
		p1 = new JPanel(new GridLayout(1, 1));
		p1.add(btnCarrito);
		
		//PANEL 2 - Catalogo
		lblcat = new JLabel("Cat\u00E1logo...             ");
		
		lista = new JList(sCl.Catalogo());
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setLayoutOrientation(JList.VERTICAL_WRAP);
		lista.setEnabled(true);
		lista.setSelectedIndex(0);
		lista.addListSelectionListener(listaListener);
		JScrollPane p2_1 = new JScrollPane(lista);
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.add(lblcat);
		p2.add(p2_1);
		
		//PANEL 3 - Articulo
		lblimagen = new JLabel();
		lblnombre = new JLabel();
		lbldesc = new JTextArea();
		lbldesc.setOpaque(false);
		lbldesc.setFont(lbldesc.getFont().deriveFont(Font.BOLD, lbldesc.getFont().getSize()));
		lbldesc.setLineWrap(true);
		lbldesc.setWrapStyleWord(true);
		lblclasif = new JLabel();
		lblprecio = new JLabel();
		lblexist = new JLabel();
		
		btnAgregar=new JButton();
		btnAgregar.setEnabled(true);
		btnAgregar.addActionListener(this);
		
		btnQuitar=new JButton("Remover del carrito");
		btnQuitar.setEnabled(true);
		btnQuitar.addActionListener(this);
		btnQuitar.setVisible(false);
		
		p3_2 = new JPanel(new GridLayout(1, 2));
		p3_2.add(btnQuitar);
		p3_2.add(btnAgregar);
		
		p3_1 = new JPanel(new GridLayout(6, 1));
		p3_1.add(lblnombre);
		p3_1.add(lbldesc);
		p3_1.add(lblclasif);
		p3_1.add(lblprecio);
		p3_1.add(lblexist);
		p3_1.add(p3_2);
		
		p3 = new JPanel(new GridLayout(2, 1));
		p3.add(lblimagen);
		p3.add(p3_1);
	}
	
	public void asignarImagen(int id){
		try{
			ByteArrayInputStream bis = new ByteArrayInputStream(sCl.catalogo[id].getimagen());
			BufferedImage bImage2 = ImageIO.read(bis);
			image = bImage2;
			imagenprod = new ImageIcon(image);
		}catch(Exception e){}
	}

	public void actionPerformed(ActionEvent e){
			if(e.getSource()==btnAgregar)
			{
				agregarAlCarro();
			}
			
			if(e.getSource()== btnCarrito)
			{
				mostrarCarro();
			}
			
			if(e.getSource()== btnQuitar)
			{
				quitarDelCarro();
			}
	}
	
	public void agregarAlCarro(){
		int cantidad = sCl.catalogo[selecMostrar].getexist()+1;
		
		while(cantidad > sCl.catalogo[selecMostrar].getexist()){
			cantidad = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la cantidad: "));
		}
		carrito[artisCarrito][0] = selecMostrar;
		carrito[artisCarrito][1] = cantidad;
		artisCarrito++;
		actualizarCarro();
		
		System.out.println("Se agreg\u00F3: "+cantidad+ " producto(s) con el c\u00F3digo "+selecMostrar+ " al carrito.");
		JOptionPane.showMessageDialog(null,"Has agregado " + cantidad + " "+comilla 
											+ sCl.catalogo[selecMostrar].getnombre()+ comilla + " a tu carrito exitosamente.");
		
		botonesAgregar(cantidad);
	}
	
	public void actualizarCarro(){
		for(int i = 0; i < artisCarrito; i++){
			totalAPagar += Double.valueOf(carrito[i][1]) * sCl.catalogo[carrito[i][0]].getprecio();
		
			carritoS[i][0] = sCl.catalogo[carrito[i][0]].getnombre();
			carritoS[i][1] = "" + carrito[i][1];
			carritoS[i][2] = "$ " + f.format(sCl.catalogo[carrito[i][0]].getprecio());
			carritoS[i][3] = "$ " + f.format(Double.valueOf(carrito[i][1]) * sCl.catalogo[carrito[i][0]].getprecio());
		}
		
	}
	
	public void botonesAgregar(int cantidad){
		btnQuitar.setVisible(true);
		btnAgregar.setEnabled(false);
		btnAgregar.setText("("+ cantidad +") Producto(s) ya en el carrito");
	}
	
	public void botonesQuitar(){
		btnQuitar.setVisible(false);
		btnAgregar.setEnabled(true);
		btnAgregar.setText("Agregar al carrito");
	}
	
	public void mostrarCarro(){
		if(artisCarrito > 0){
			String mensaje = "--------------------------------------------------------------------------------------------------------------------------\n";
			int contador = 0;
			while(contador<artisCarrito){
				mensaje+= "Art\u00CDculo "+ (contador+1) + ": " + carritoS[contador][0] + "\n"
							+ "Cantidad: " + carritoS[contador][1] + "\n"
							+ "Precio unitario: " + carritoS[contador][2] + "\n"
							+ "Total: " + carritoS[contador][3] + "\n"
							+ "--------------------------------------------------------------------------------------------------------------------------\n";
				contador++;
			}
			mensaje += "--------------------------------------------------------------------------------------------------------------------------\n"
						+ "Total a pagar: $ " + f.format(totalAPagar);
						
			int seleccion = JOptionPane.showOptionDialog(null, mensaje,
															"Selector de opciones",JOptionPane.YES_NO_CANCEL_OPTION,
															JOptionPane.QUESTION_MESSAGE,null,
															new Object[] { "Efecuar pedido", "Vaciar carrito", "Seguir comprando"},"Efecuar pedido");
						
			if (seleccion != -1){
			   System.out.println("seleccionada opcion: " + seleccion);
			}
			
			if(seleccion == 0){
				efectuarPedido();
			}else if(seleccion == 1){
				vaciarPedido();
			} else {
				
			}
		}else{
			JOptionPane.showMessageDialog(null,"Su carrito est\u00E1 vac\u00CDo :(.");
		}
	}
	
	public void efectuarPedido(){
		sCl = new ClienteO(1234,"127.0.0.1");
		sCl.EnviarPedido(carrito, totalAPagar, artisCarrito, true, user);
		sCl.CerrarConexion();
		
		sCl = new ClienteO(1234,"127.0.0.1");
		sCl.RecibeCatalogo();
		sCl.CerrarConexion();
		lista.setSelectedIndex(0);
		artisCarrito = 0;
		inicializar();
	}
	
	public void guardarCarrito(){
		sCl = new ClienteO(1234,"127.0.0.1");
		sCl.EnviarPedido(carrito, totalAPagar, artisCarrito, false, user);
		sCl.CerrarConexion();
	}
	
	public void quitarDelCarro(){
		int resp=JOptionPane.showConfirmDialog(null,"\u00BFSeguro que desea quitar este producto de su carrito?");
		if (JOptionPane.OK_OPTION == resp){
			int codigo = 0;
			for(int i = 0; i < artisCarrito; i++){
				if(selecMostrar == carrito[i][0]){
					codigo = i;
					break;
				}
			}
			carritoAux = carrito;
			for(int i = 0; i < artisCarrito; i++){
				if(i != codigo){
					carrito[i] = carritoAux[i];
				} else {
					i--;
					artisCarrito--;
				}
			}
			actualizarCarro();
			botonesQuitar();
			
			JOptionPane.showMessageDialog(null,"Se ha(n) removido " + carritoAux[codigo][1] + " "+comilla 
											+ sCl.catalogo[codigo].getnombre()+ comilla + " de tu carrito.");
		}
		else{
			System.out.println("No quitar.");
		}
	}
	
	public void inicializar(){
		selecMostrar = 0;
		asignarImagen(0);
		lblimagen.setIcon(imagenprod);
		lblnombre.setText("Nombre: " + sCl.catalogo[0].getnombre());
		lbldesc.setText("Descripci\u00F3n: " + sCl.catalogo[0].getdesc());
		lblclasif.setText("Clasificaci\u00F3n: " + sCl.catalogo[0].getclasif());
		lblprecio.setText("Precio: $" + f.format(sCl.catalogo[0].getprecio()));
		lblexist.setText("Existencia: " + sCl.catalogo[0].getexist());
		botonesQuitar();
	}
	
	public void vaciarPedido(){
		artisCarrito = 0;
		inicializar();
	}
	
}