import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.*;

public class GestionProductos extends JFrame {
    private JPanel PanelGestionProducto;
    private JTextField NombreProductoText;
    private JTextField CantidadProductoText;
    private JTextField PrecioCompraText;
    private JButton BotonRegistrarProducto;
    private JButton BotonEliminarDatos;
    private JButton BotonModificar;
    private JTextField PrecioVentaText;
    private JTextField CodProductoText;
    private JTextField FechaIngresoText;
    private JTextField FechaCaducidadText;
    private JTextField DescripcionText;
    private JTextField ProveedorProductoText;
    private JButton BotonListadoProductos;
    private JList ListaProductosC;
    private JButton BotonMenuu;

    DefaultListModel modelo = new DefaultListModel();
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
                    JOptionPane.showMessageDialog(null, "Registro Exitoso");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Registro Fallido" + ex.getMessage());
                }
            }
        });

        BotonListadoProductos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    consultarproductos();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        BotonEliminarDatos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                EliminarProductos();

            }
        });
        ListaProductosC.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                    try {
                        ModificarProducto();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error al modificar producto: " + ex.getMessage());
                    }
                }
            }
        });

        BotonModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ModificoProducto();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al modificar producto: " + ex.getMessage());
                }
            }
        });

        BotonMenuu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SeleccionDeventana enlazar = new SeleccionDeventana();
                enlazar.mostrarSeleccionVentana();
            }
        });
    }
    public void ModificarProducto() throws SQLException {
        int filaSeleccionada = ListaProductosC.getSelectedIndex();
        if (filaSeleccionada != -1) {
            conectar();

            String codigoProductoStr = ListaProductosC.getSelectedValue().toString().split("\\|")[0].trim();

            int codigoProducto = Integer.parseInt(codigoProductoStr);

            String sql = "SELECT * FROM Inventario_Productos WHERE Cod_producto=?";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoProducto);
                ResultSet resultado = preparedStatement.executeQuery();

                if (resultado.next()) {
                    CodProductoText.setText(Integer.toString(resultado.getInt("Cod_producto")));
                    NombreProductoText.setText(resultado.getString("Nombre_producto"));
                    CantidadProductoText.setText(Integer.toString(resultado.getInt("Cantidad")));
                    PrecioCompraText.setText(Double.toString(resultado.getDouble("Precio_compra")));
                    PrecioVentaText.setText(Double.toString(resultado.getDouble("Precio_Venta")));
                    FechaIngresoText.setText(resultado.getString("Fecha_entrada"));
                    FechaCaducidadText.setText(resultado.getString("Fecha_caducidad"));
                    DescripcionText.setText(resultado.getString("Desc_producto"));
                    ProveedorProductoText.setText(resultado.getString("Proveedor"));

                    JOptionPane.showMessageDialog(null, "Producto seleccionado para modificación.");
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró el producto.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un producto para modificar.");
        }
    }

    public Connection conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Proyecto_Contable", "root", "Kevin776anasco");

        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return conexion;
    }

    public void EliminarProductos() {
        try {
            int filaSeleccionada = ListaProductosC.getSelectedIndex();
            if (filaSeleccionada != -1) {
                conectar();
                String codigoProductoStr = ListaProductosC.getSelectedValue().toString().split("\\|")[0].trim();

                int codigoProducto = Integer.parseInt(codigoProductoStr);

                String sql = "DELETE FROM Inventario_Productos WHERE Cod_producto=?";
                try (PreparedStatement preparedStatement = conexion.prepareStatement(sql)) {
                    preparedStatement.setInt(1, codigoProducto);
                    int resultado = preparedStatement.executeUpdate();

                    if (resultado > 0) {
                        JOptionPane.showMessageDialog(null, "Producto eliminado");
                        modelo.removeElementAt(filaSeleccionada);
                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo eliminar el producto");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecciona un producto para eliminar.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
////////

    public void ModificoProducto() throws SQLException {
        int filaSeleccionada = ListaProductosC.getSelectedIndex();
        if (filaSeleccionada != -1) {
            conectar();

            String codigoProductoStr = ListaProductosC.getSelectedValue().toString().split("\\|")[0].trim();
            int codigoProducto = Integer.parseInt(codigoProductoStr);

            String sql = "SELECT * FROM Inventario_Productos WHERE Cod_producto=?";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                preparedStatement.setInt(1, codigoProducto);
                ResultSet resultado = preparedStatement.executeQuery();

                if (resultado.next()) {
                    String nuevoNombre = NombreProductoText.getText();
                    int nuevaCantidad = Integer.parseInt(CantidadProductoText.getText());
                    double nuevoPrecioCompra = Double.parseDouble(PrecioCompraText.getText());
                    double nuevoPrecioVenta = Double.parseDouble(PrecioVentaText.getText());
                    String nuevaFechaIngreso = FechaIngresoText.getText();
                    String nuevaFechaCaducidad = FechaCaducidadText.getText();
                    String nuevaDescripcion = DescripcionText.getText();
                    String nuevoProveedor = ProveedorProductoText.getText();

                    //// Actualizar los valores
                    resultado.updateInt("Cod_producto", Integer.parseInt(CodProductoText.getText()));
                    resultado.updateString("Nombre_producto", nuevoNombre);
                    resultado.updateInt("Cantidad", nuevaCantidad);
                    resultado.updateDouble("Precio_compra", nuevoPrecioCompra);
                    resultado.updateDouble("Precio_Venta", nuevoPrecioVenta);
                    resultado.updateString("Fecha_entrada", nuevaFechaIngreso);
                    resultado.updateString("Fecha_caducidad", nuevaFechaCaducidad);
                    resultado.updateString("Desc_producto", nuevaDescripcion);
                    resultado.updateString("Proveedor", nuevoProveedor);

                    //// Actualizar en la base de datos
                    resultado.updateRow();

                    JOptionPane.showMessageDialog(null, "Producto modificado exitosamente.");

                    //// Actualizar la lista de productos después de la modificación
                    consultarproductos();
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró el producto.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al ejecutar la consulta: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un producto para modificar.");
        }
    }


    ////////
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



    void consultarproductos() throws SQLException {
        conectar();
        ListaProductosC.setModel(modelo);
        traer = conexion.createStatement();
        Resultado = traer.executeQuery("Select * from Inventario_Productos");
        modelo.removeAllElements();
        while (Resultado.next()){
            modelo.addElement(Resultado.getString(1)+" | "+Resultado.getString(2)+" | "+ Resultado.getString(3)
                    +" | "+ Resultado.getString(4)+" | "+ Resultado.getString(5)+" | "+ Resultado.getString(6)+" | "+ Resultado.getString(7)
                    +" | "+ Resultado.getString(8)+" | "+ Resultado.getString(9));
        }
    }

    public void GestioMostrarse() {
        GestionProductos Gestion1 = new GestionProductos();
        Gestion1.setContentPane(new GestionProductos().PanelGestionProducto);
        Gestion1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Gestion1.setVisible(true);
        Gestion1.pack();
    }

}


