import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class SeleccionDeventana extends JFrame{
    private JPanel PanelEleccion;
    private JButton BotLlevarRegProduc;
    private JButton BotLlevarVentas;
    private JButton BotLlevarRegClientes;
    private JButton BotSalirAPP;


    public SeleccionDeventana() {
        BotLlevarRegProduc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
               GestionProductos enlazarRegPro = new GestionProductos();
                enlazarRegPro.GestioMostrarse();
            }
        });
        BotLlevarVentas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VentaProducto enlazarVenPro = new VentaProducto();
                enlazarVenPro.mostrarVentaproducto();
            }
        });
        BotLlevarRegClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientes enlazar = new clientes();
                enlazar.mostrarregistrocliente();
            }
        });
    }

    public void mostrarSeleccionVentana(){
        SeleccionDeventana mostrarVSeleccion = new SeleccionDeventana();
        mostrarVSeleccion.setContentPane(new SeleccionDeventana().PanelEleccion);
        mostrarVSeleccion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mostrarVSeleccion.setVisible(true);
        mostrarVSeleccion.pack();

    }
}
