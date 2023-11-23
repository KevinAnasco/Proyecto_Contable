import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistroEmpleados  extends JFrame{
    private JPanel panelRegistroE;
    private JTextField IdEmpleText;
    private JTextField NombreEmpreadoText;
    private JTextField DocEmpleadoText;
    private JTextField RolEmpleadoText;
    private JTextField TelEmpleadoText;
    private JTextField UsuarioEmpleadoText;
    private JTextField ContraEmpleadoText;
    private JButton BotRegistarEmple;
    private JButton BotLoginVolver;
    private JTextField EmailEmpleado;
    private JTextField DireccionEmpleado;
    private JTextField SalarioEmpleado;

    Connection conexion;
    PreparedStatement preparar;
    Statement traer;
    ResultSet Resultado;

    public RegistroEmpleados() {

        BotRegistarEmple.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    agregarEmpleado();
                    JOptionPane.showMessageDialog(null,"Registro Exitoso");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"Registro Fallido"+ ex.getMessage());
                }
            }
        });

        BotLoginVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginEmpleados enlazar =  new LoginEmpleados();
                enlazar.mostrarLogin();
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

    public void agregarEmpleado() throws SQLException {
        conectar();
        preparar = conexion.prepareStatement("Insert into Registrar_Empleado(Id_Emple,Nombre,Documento,Rol,Telefono,Email,Direccion,Salario,Usuario,Contraseña) values (?,?,?,?,?,?,?,?,?,?)");
        preparar.setInt(1,Integer.parseInt(IdEmpleText.getText()));
        preparar.setString(2,NombreEmpreadoText.getText());
        preparar.setInt(3,Integer.parseInt(DocEmpleadoText.getText()));
        preparar.setString(4,RolEmpleadoText.getText());
        preparar.setString(5,TelEmpleadoText.getText());
        preparar.setString(6,EmailEmpleado.getText());
        preparar.setString(7,DireccionEmpleado.getText());
        preparar.setDouble(8,Double.parseDouble(SalarioEmpleado.getText()));
        preparar.setString(9,UsuarioEmpleadoText.getText());
        preparar.setString(10,ContraEmpleadoText.getText());
        preparar.executeUpdate();
    }
   public void mostrarRegistroEmpleados(){
        RegistroEmpleados registroE1 = new RegistroEmpleados();
        registroE1.setContentPane(new RegistroEmpleados().panelRegistroE);
        registroE1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       registroE1.pack();
       registroE1.setLocationRelativeTo(null);
        registroE1.setVisible(true);
    }
}
