import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Usuarios extends JFrame {
    private JPanel Panel;
    private JTextField idText;
    private JTextField NombreText;
    private JTextField RolText;
    private JButton ingresarBoton;
    private JButton consultarBoton;
    private JList Lista;
    Connection conexion;
    PreparedStatement preparar;
    DefaultListModel modelo = new DefaultListModel();
    Statement traer;
    ResultSet Resultado;

    public Usuarios() {
        consultarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    consultar();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ingresarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    ingresar();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void conectar(){
        try {
conexion= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/usuario327","root","Kevin776anasco");

        } catch (SQLException e) {
          throw new RuntimeException();
        }
    }

    void consultar() throws SQLException {
conectar();
Lista.setModel(modelo);
traer = conexion.createStatement();
Resultado = traer.executeQuery("Select id, nombre,rol from usuario");
modelo.removeAllElements();
while (Resultado.next()){
    modelo.addElement(Resultado.getString(1)+"||"+Resultado.getString(2)+"||"+ Resultado.getString(3));
}
    }
    void ingresar() throws SQLException {
        conectar();
preparar = conexion.prepareStatement("Insert into usuario(id,nombre,rol) values (?,?,?)");
preparar.setInt(1,Integer.parseInt(idText.getText()));
preparar.setString(2,NombreText.getText());
preparar.setString(3,RolText.getText());
if (preparar.executeUpdate()>0){
Lista.setModel(modelo);
modelo.removeAllElements();
modelo.addElement("El usuario ha sido ingresado xd");

idText.setText("");
NombreText.setText("");
RolText.setText("");
}
    }
public void mostrarventanaUsuario(){
Usuarios usuario1 = new Usuarios();
usuario1.setContentPane(new Usuarios().Panel);
usuario1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
usuario1.setVisible(true);
usuario1.pack();

    }
}


