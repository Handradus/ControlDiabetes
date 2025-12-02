package controldiabetes;

import controlador.AuthService;
import controlador.ControladorLogin;
import controlador.SistemaController;
import modelo.GestorPacientes;
import modelo.GestorUsuarios;
import vista.ConsolaVista;
import vista.Login;

public class ControlDiabetes {

    public static void main(String[] args) {
        
        GestorPacientes gestorPacientes = new GestorPacientes();
        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        ConsolaVista vista = new ConsolaVista();
        Login loginVista = new Login();

        SistemaController controller = new SistemaController(gestorPacientes, gestorUsuarios, vista,loginVista);
        AuthService authService = new AuthService(gestorUsuarios);
        ControladorLogin loginController = new ControladorLogin(loginVista, authService, controller);
        
        loginController.iniciarLogin();
        
    }
}
