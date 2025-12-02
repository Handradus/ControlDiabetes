/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Usuario;
import modelo.Admin;
import modelo.Cuidador;
import vista.Login;
import controlador.SistemaController;
import controlador.AuthService;
import javax.swing.JOptionPane;



/**
 *
 * @author Diavuru
 */
public class ControladorLogin {

    private Login vista;
    private AuthService authService;
    private SistemaController sistemaController;

    public ControladorLogin(Login vista, AuthService authService, SistemaController sistemaController) {
        this.vista = vista;
        this.authService = authService;
        this.sistemaController = sistemaController;
        
    }

    public void iniciarLogin() {
        this.vista.setVisible(true);
        vista.getIngresarBtn().addActionListener(e -> intentarLogin());
    }

    public void intentarLogin() {
        String user = vista.getUserTxt().getText();
        String pass = new String(vista.getPassTxt().getPassword());

        try {
            Usuario u = (Usuario) authService.autenticar(user, pass);
            this.vista.mostrarInfo("Logueo Exitoso");
            vista.dispose();
            vista.getUserTxt().setText("");
            vista.getPassTxt().setText("");
            
            sistemaController.mostrarInterfazSegunRol(u);
            
        } catch (Exception ex) {
            vista.mostrarError("Error en login :"+ex);
            vista.getPassTxt().setText("");
        }
    }
    
    
     
    
}

   




