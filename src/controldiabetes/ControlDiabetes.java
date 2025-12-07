package controldiabetes;

import controlador.AuthService;
import controlador.SistemaController;
import modelo.GestorPacientes;
import modelo.GestorUsuarios;
import vista.VentanaPrincipal;

public class ControlDiabetes {

    public static void main(String[] args) {
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            GestorPacientes gestorPacientes = new GestorPacientes();
            GestorUsuarios gestorUsuarios = new GestorUsuarios();
            VentanaPrincipal ventana = new VentanaPrincipal();

            SistemaController controller =
                    new SistemaController(ventana, gestorPacientes, gestorUsuarios);

            controller.iniciar();
        
    });
}
}
