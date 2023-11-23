import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class clientes extends JFrame{
    private JPanel panelclientes;
    private JTextField Texttelcliente;
    private JTextField Textnomcliente;
    private JTextField Textemailcliente;
    private JTextField Textdoccliente;
    private JTextField Textdirecliente;
    private JButton BotonregistrarCli;
    private JButton Botonvolvermenu;
    Connection conexion;

    public clientes() {
        BotonregistrarCli.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    registrarCliente();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        Botonvolvermenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SeleccionDeventana enlazar = new SeleccionDeventana();
                enlazar.mostrarSeleccionVentana();
            }
        });
    }
    public void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Proyecto_Contable", "root", "Kevin776anasco");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    void registrarCliente() throws SQLException {
        conectar();
        PreparedStatement preparar = conexion.prepareStatement("insert into clientes (Nombre_Cliente, Documento, Telefono, Email, Direccion) values (?,?,?,?,?)");
        preparar.setString(1, Textnomcliente.getText());
        preparar.setInt(2, Integer.parseInt(Textdoccliente.getText()));
        preparar.setString(3,Texttelcliente.getText());
        preparar.setString(4, Textemailcliente.getText());
        preparar.setString(5, Textdirecliente.getText());

        if (preparar.executeUpdate() > 0) {
            JOptionPane.showMessageDialog(null, "Cliente registrado con éxito");
        }
    }

    public void mostrarregistrocliente(){
        clientes mostrarClientes = new clientes();
        mostrarClientes.setContentPane(new clientes().panelclientes);
        mostrarClientes.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mostrarClientes.pack();
        mostrarClientes.setLocationRelativeTo(null);
        mostrarClientes.setVisible(true);
    }
}
