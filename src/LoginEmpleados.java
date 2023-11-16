import com.mysql.cj.log.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginEmpleados  extends JFrame{
    private JPanel panelLogin;
    private JTextField usuarioText;
    private JTextField contraText;
    private JButton RegistrarBoton;
    private JButton IngresarBoton;
    private RegistroEmpleados registroempleados;

    Connection conexion;
    PreparedStatement preparar;
    Statement traer;
    ResultSet Resultado;



    public LoginEmpleados() {

        IngresarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ValidadAdmini();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

      //Este boton no se como solucionarlo
        RegistrarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                RegistroEmpleados enlazar = new RegistroEmpleados();
                enlazar.mostrarRegistroEmpleados();
            }
        });



    }

    // hasta aqui

    public Connection conectar(){
        try {
            conexion= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Proyecto_Contable","root","Kevin776anasco");

        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return conexion;
    }

        public void ValidadAdmini() throws SQLException{
        conectar();
        int validacion =0;
        String usuario=usuarioText.getText();
        String pass= String.valueOf(contraText.getText());

        try{
            traer = conexion.createStatement();
            Resultado = traer.executeQuery("Select Usuario,Contraseña from Registrar_Empleado where Usuario ='"+usuario+"'and Contraseña ='"+pass+"'");
            if (Resultado.next()){
                validacion = 1;
                if(validacion==1){
                    Usuarios enlazar = new Usuarios();
                    enlazar.mostrarventanaUsuario();
                }
            }else {
                JOptionPane.showMessageDialog(null,"Error de acceso, Usuario no autorizado");

            }
        }catch (SQLException e){

            JOptionPane.showMessageDialog(null,"Error"+e.getMessage());
        }

        }

   public static void main (String[]args){


           LoginEmpleados Login1 = new LoginEmpleados();
           Login1.setContentPane(new LoginEmpleados().panelLogin);
           Login1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           Login1.setVisible(true);
           Login1.pack();

    }

   public void mostrarLogin(){
       LoginEmpleados mostrar = new LoginEmpleados();
        mostrar.setContentPane(new LoginEmpleados().panelLogin);
       mostrar.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mostrar.setVisible(true);
        mostrar.pack();

    }


}
