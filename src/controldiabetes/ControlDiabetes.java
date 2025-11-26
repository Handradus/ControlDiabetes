package controldiabetes;

import controlador.SistemaController;
import modelo.GestorPacientes;
import modelo.GestorUsuarios;
import vista.ConsolaVista;

public class ControlDiabetes {

    public static void main(String[] args) {

        GestorPacientes gestorPacientes = new GestorPacientes();
        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        ConsolaVista vista = new ConsolaVista();

        SistemaController controller = new SistemaController(gestorPacientes, gestorUsuarios, vista);
        controller.iniciar();
        
    }
}
