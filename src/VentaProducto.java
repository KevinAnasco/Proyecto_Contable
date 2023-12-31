import java.io.FileOutputStream;
import java.sql.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class VentaProducto extends JFrame {
    private JTextField Nombre_Empleado;
    private JTextField Nombre_Producto;
    private JTextField CantidadProducto;
    private JTextField PrecioProducto;
    private JTextField PrecioTotal;
    private JTextField NombreCliente;
    private JTextField BilleteIngresado;
    private JButton BotonAgregarP;
    private JButton BotonEliminarP;
    private JList ListaComprarV;
    private JButton BotonVender;
    private JList ListaProductosV;
    private JButton Botondeconsultar;
    private JPanel PanelVenta;
    private JButton BotonMenu;

    DefaultListModel modelo = new DefaultListModel();
    DefaultListModel modelo2 = new DefaultListModel();

    Connection conexion;
    PreparedStatement preparar;
    Statement traer;
    ResultSet Resultado;

    public VentaProducto() {
        BotonEliminarP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int selectedIndex = ListaComprarV.getSelectedIndex();
                if (selectedIndex != -1) {
                    String productoInfo = (String) modelo2.getElementAt(selectedIndex);
                    String[] partes = productoInfo.split("\\|");
                    String nombreProducto = partes[0].trim();
                    int cantidadProducto = Integer.parseInt(partes[1].trim().split(":")[1].trim());

                    modelo2.removeElementAt(selectedIndex);
                    devolverCantidadEnBD(nombreProducto, cantidadProducto);
                    calcularPrecioTotal();
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un producto para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        Botondeconsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    consultarproductosL();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        BotonAgregarP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ListaComprarV.setModel(modelo2);

                String nombreProducto = Nombre_Producto.getText();
                String cantidadProducto = CantidadProducto.getText();
                String precioProducto = PrecioProducto.getText();

                if (verificarProducto(nombreProducto, cantidadProducto, precioProducto)) {
                    String infoProducto = nombreProducto + " | Cantidad: " + cantidadProducto + " | Precio: " + precioProducto;

                    modelo2.addElement(infoProducto);
                    Nombre_Producto.setText("");
                    CantidadProducto.setText("");
                    PrecioProducto.setText("");

                    restarCantidadEnBD(nombreProducto, Integer.parseInt(cantidadProducto));
                    calcularPrecioTotal();
                } else {
                    JOptionPane.showMessageDialog(null, "No hay suficientes productos en stock.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        BotonMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SeleccionDeventana enlazar = new SeleccionDeventana();
                enlazar.mostrarSeleccionVentana();
            }
        });
        BotonVender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double precioTotal = Double.parseDouble(PrecioTotal.getText());
                double billeteIngresado = Double.parseDouble(BilleteIngresado.getText());

                if (precioTotal > billeteIngresado) {
                    JOptionPane.showMessageDialog(null, "No es posible hacer esta venta. El monto ingresado es insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    double cambio = billeteIngresado - precioTotal;
                    try {
                        String nombreCliente = NombreCliente.getText();
                        if (clienteRegistrado(nombreCliente)) {
                            sumarPuntoCliente(nombreCliente);
                        }
                        registrarVenta();
                        generarFacturaPDF();
                        modelo2.clear();
                        ListaComprarV.setModel(modelo2);

                        PrecioTotal.setText("");
                        Nombre_Empleado.setText("");
                        NombreCliente.setText("");
                        BilleteIngresado.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al realizar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private boolean verificarProducto(String nombreProducto, String cantidadProducto, String precioProducto) {
        try {
            String consulta = "SELECT Cantidad FROM Inventario_Productos WHERE Nombre_producto = ? AND Precio_Venta = ?";
            PreparedStatement verificarStmt = conexion.prepareStatement(consulta);
            verificarStmt.setString(1, nombreProducto);
            verificarStmt.setDouble(2, Double.parseDouble(precioProducto));
            ResultSet resultado = verificarStmt.executeQuery();

            if (resultado.next()) {
                int cantidadEnStock = resultado.getInt("Cantidad");
                int cantidadRequerida = Integer.parseInt(cantidadProducto);

                return cantidadEnStock >= cantidadRequerida;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void restarCantidadEnBD(String nombreProducto, int cantidad) {
        try {
            String updateQuery = "UPDATE Inventario_Productos SET Cantidad = Cantidad - ? WHERE Nombre_producto = ?";
            PreparedStatement updateStmt = conexion.prepareStatement(updateQuery);
            updateStmt.setInt(1, cantidad);
            updateStmt.setString(2, nombreProducto);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void calcularPrecioTotal() {
        double precioTotal = 0.0;

        for (int i = 0; i < modelo2.getSize(); i++) {
            String productoInfo = (String) modelo2.getElementAt(i);
            String[] partes = productoInfo.split("\\|");

            int cantidad = Integer.parseInt(partes[1].trim().split(":")[1].trim());
            double precioUnitario = Double.parseDouble(partes[2].trim().split(":")[1].trim());

            precioTotal += cantidad * precioUnitario;
        }
        PrecioTotal.setText(String.valueOf(precioTotal));
    }
    private void devolverCantidadEnBD(String nombreProducto, int cantidad) {
        try {
            String updateQuery = "UPDATE Inventario_Productos SET Cantidad = Cantidad + ? WHERE Nombre_producto = ?";
            PreparedStatement updateStmt = conexion.prepareStatement(updateQuery);
            updateStmt.setInt(1, cantidad);
            updateStmt.setString(2, nombreProducto);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
    public void consultarproductosL() throws SQLException {
        conectar();
        ListaProductosV.setModel(modelo);
        traer = conexion.createStatement();
        Resultado = traer.executeQuery("Select * from Inventario_Productos");
        modelo.removeAllElements();
        while (Resultado.next()) {
            modelo.addElement(Resultado.getString(1) + " | " + Resultado.getString(2) + " | Cantidad " + Resultado.getString(3)
                    + " | " + " | Precio " + Resultado.getString(5) + " | F.V " + Resultado.getString(7));
        }
    }
    private void generarFacturaPDF() {
        String nombreEmpleado = Nombre_Empleado.getText();
        String nombreCliente = NombreCliente.getText();
        double precioTotal = Double.parseDouble(PrecioTotal.getText());
        double billeteIngresado = Double.parseDouble(BilleteIngresado.getText());
        double cambio = billeteIngresado - precioTotal;
        Document document = new Document();
        try {
            String pdfFilePath = "D:\\Factura/factura.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date fechaActual = new Date();
            document.add(new Paragraph("Fecha: " + dateFormat.format(fechaActual)));

            document.add(new Paragraph("FACTURA"));
            document.add(new Paragraph("-----------------------------"));
            document.add(new Paragraph("Empleado: " + nombreEmpleado));
            document.add(new Paragraph("Cliente: " + nombreCliente));
            document.add(new Paragraph("Productos Comprados:"));

            for (int i = 0; i < modelo2.getSize(); i++) {
                String productoInfo = (String) modelo2.getElementAt(i);
                document.add(new Paragraph(productoInfo));
            }
            document.add(new Paragraph("Precio Total: " + precioTotal));
            document.add(new Paragraph("Billete Ingresado: " + billeteIngresado));
            document.add(new Paragraph("Cambio: " + cambio));
            document.close();

            JOptionPane.showMessageDialog(null, "Factura generada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar la factura.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void registrarVenta() {
        try {
            conectar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaActual = new Date();
            String fechaVenta = dateFormat.format(fechaActual);

            String insertQuery = "INSERT INTO Ventas (Cod_Producto, Cantidad, Precio_Total, Fecha_Venta) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conexion.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < modelo2.getSize(); i++) {
                String productoInfo = (String) modelo2.getElementAt(i);
                String[] partes = productoInfo.split("\\|");

                String nombreProducto = partes[0].trim();
                int cantidad = Integer.parseInt(partes[1].trim().split(":")[1].trim());
                double precioUnitario = Double.parseDouble(partes[2].trim().split(":")[1].trim());

                int codProducto = obtenerCodigoProducto(nombreProducto);

                insertStmt.setInt(1, codProducto);
                insertStmt.setInt(2, cantidad);
                insertStmt.setDouble(3, precioUnitario * cantidad);
                insertStmt.setString(4, fechaVenta);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();

            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            while (generatedKeys.next()) {
                int idVenta = generatedKeys.getInt(1);
                System.out.println("Venta registrada con éxito. Id_Venta generado: " + idVenta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private int obtenerCodigoProducto(String nombreProducto) {
        try {
            String selectQuery = "SELECT Cod_producto FROM Inventario_Productos WHERE Nombre_producto = ?";
            PreparedStatement selectStmt = conexion.prepareStatement(selectQuery);
            selectStmt.setString(1, nombreProducto);
            ResultSet resultado = selectStmt.executeQuery();

            if (resultado.next()) {
                return resultado.getInt("Cod_producto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    private boolean clienteRegistrado(String nombreCliente) {
        try {
            String consulta = "SELECT COUNT(*) AS cantidad FROM Clientes WHERE Nombre_Cliente = ?";
            PreparedStatement verificarStmt = conexion.prepareStatement(consulta);
            verificarStmt.setString(1, nombreCliente);
            ResultSet resultado = verificarStmt.executeQuery();

            if (resultado.next()) {
                int cantidad = resultado.getInt("cantidad");
                return cantidad > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al verificar si el cliente está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    private int obtenerPuntosCliente(String nombreCliente) {
        try {
            String selectQuery = "SELECT Puntos FROM Clientes WHERE Nombre_Cliente = ?";
            PreparedStatement selectStmt = conexion.prepareStatement(selectQuery);
            selectStmt.setString(1, nombreCliente);
            ResultSet resultado = selectStmt.executeQuery();

            if (resultado.next()) {
                return resultado.getInt("Puntos");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    private void sumarPuntoCliente(String nombreCliente) {
        try {
            conectar();

            int puntosActuales = obtenerPuntosCliente(nombreCliente);

            if (puntosActuales >= 100) {
                double precioTotalActual = Double.parseDouble(PrecioTotal.getText());
                double descuento = precioTotalActual * 0.20;
                double nuevoPrecioTotal = precioTotalActual - descuento;

                PrecioTotal.setText(String.valueOf(nuevoPrecioTotal));

                String resetPuntosQuery = "UPDATE Clientes SET Puntos = '1' WHERE Nombre_Cliente = ?";
                PreparedStatement resetPuntosStmt = conexion.prepareStatement(resetPuntosQuery);
                resetPuntosStmt.setString(1, nombreCliente);
                resetPuntosStmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Descuento del 20% aplicado debido a 100 puntos acumulados.", "Descuento Aplicado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String updateQuery = "UPDATE Clientes SET Puntos = CAST(Puntos AS SIGNED) + 1 WHERE Nombre_Cliente = ?";
                PreparedStatement updateStmt = conexion.prepareStatement(updateQuery);
                updateStmt.setString(1, nombreCliente);
                updateStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        VentaProducto mostrarVentap1 = new VentaProducto();
        mostrarVentap1.setContentPane(new VentaProducto().PanelVenta);
        mostrarVentap1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mostrarVentap1.pack();
        mostrarVentap1.setLocationRelativeTo(null);
        mostrarVentap1.setVisible(true);
    }
    public void mostrarVentaproducto() {
        VentaProducto mostrarVentap = new VentaProducto();
        mostrarVentap.setContentPane(new VentaProducto().PanelVenta);
        mostrarVentap.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mostrarVentap.pack();
        mostrarVentap.setLocationRelativeTo(null);
        mostrarVentap.setVisible(true);
    }
}
//Prueba repositorio