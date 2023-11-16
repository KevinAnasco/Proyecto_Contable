import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.sql.Date;

public class GestionProductos extends JFrame{
    private JPanel PanelGestionProducto;
    private JTextField NombreProductoText;
    private JTextField CantidadProductoText;
    private JTextField PrecioCompraText;
    private JButton BotonRegistrarProducto;
    private JButton ELIMINARButton;
    private JButton MODIFICARButton;
    private JTextField PrecioVentaText;
    private JTextField CodProductoText;
    private JTextField FechaIngresoText;
    private JTextField FechaCaducidadText;
    private JTextField DescripcionText;
    private JTextField ProveedorProductoText;
    private JButton LISTADOButton;


    Connection conexion;
    PreparedStatement preparar;
    Statement traer;
    ResultSet Resultado;



    public GestionProductos() {

        BotonRegistrarProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    AgregarProductos();
                    JOptionPane.showMessageDialog(null,"Registro Exitoso");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"Registro Fallido"+ ex.getMessage());
                }
            }
        });
    }

    public Connection conectar(){
        try {
            conexion= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Proyecto_Contable","root","Kevin776anasco");

        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return conexion;
    }

    public void AgregarProductos() throws SQLException {
        conectar();
        preparar = conexion.prepareStatement("Insert into Inventario_Productos(Cod_producto,Nombre_producto,Cantidad,Precio_compra,Precio_Venta,Fecha_entrada,Fecha_caducidad,Desc_producto,Proveedor) values (?,?,?,?,?,?,?,?,?)");
        preparar.setInt(1,Integer.parseInt(CodProductoText.getText()));
        preparar.setString(2,NombreProductoText.getText());
        preparar.setInt(3,Integer.parseInt(CantidadProductoText.getText()));
        preparar.setDouble(4,Double.parseDouble(PrecioCompraText.getText()));
        preparar.setDouble(5,Double.parseDouble(PrecioVentaText.getText()));
        preparar.setString(6,FechaIngresoText.getText());
        preparar.setString(7,FechaCaducidadText.getText());
        preparar.setString(8,DescripcionText.getText());
        preparar.setString(9,ProveedorProductoText.getText());
        preparar.executeUpdate();

    }


    public static void main (String[]args){


        GestionProductos gestion1= new GestionProductos();
        gestion1.setContentPane(new GestionProductos().PanelGestionProducto);
        gestion1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gestion1.setVisible(true);
        gestion1.pack();

    }

   /* public void mostrarGestionP(){
        GestionProductos mostrar1 = new GestionProductos();
        mostrar1.setContentPane(new GestionProductos().PanelGestionProducto);
        mostrar1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mostrar1.setVisible(true);
        mostrar1.pack();

    }
*/

}



