package controlador;

import modelo.*;
import vista.ConsolaVista;
import java.util.ArrayList;
import vista.Login;
import vista.MenuAdmin;

public class SistemaController {

   

    private GestorPacientes gestorPacientes;
    private GestorUsuarios gestorUsuarios;
    private ConsolaVista vista;
    private Login loginVista;
    

    public SistemaController(GestorPacientes gp, GestorUsuarios gu, ConsolaVista vista,Login loginVista) {
        this.gestorPacientes = gp;
        this.gestorUsuarios = gu;
        this.vista = vista;
        this.loginVista = loginVista;
       
    }
    
     public void mostrarLogin() {
         loginVista.setVisible(true);
     }

    
    //interfaz por consola antigua
    /*public void iniciar() {
        Usuario usuarioLogeado = null;

        while (usuarioLogeado == null) {
            String usuario = vista.leerTexto("Usuario: ");
            String pass = vista.leerTexto("Contraseña: ");
            usuarioLogeado = gestorUsuarios.login(usuario, pass);

            if (usuarioLogeado == null) {
                vista.mostrarMensaje("Credenciales incorrectas.");
            }
        }

        if (usuarioLogeado instanceof Admin) {
            menuAdmin();
        } else {
            menuCuidador();
        }
    }*/

   

    private void menuCuidador() {
        boolean continuar = true;

        while (continuar) {
            vista.mostrarMensaje(
                "\n1. Registrar glicemia" +
                "\n2. Listar pacientes" +
                "\n3. Cerrar sesión"
            );
            int opcion = vista.leerEntero("Opción: ");

            if (opcion == 1) {
                registrarGlicemia();
            } else if (opcion == 2) {
                listarPacientes();
            } else if (opcion == 3) {
                continuar = false;
            }
        }
    }

    private void registrarPaciente() {
        String nombre = vista.leerTexto("Nombre: ");
        String rut = vista.leerTexto("Rut: ");
        int edad = vista.leerEntero("Edad: ");
        String habitacion = vista.leerTexto("Habitación: ");

        Tratamiento t = new Tratamiento(
                "Sin dieta",
                "Ninguno",
                false,
                false,
                0,
                0,
                null
        );

        Paciente p = new Paciente(nombre, rut, edad, habitacion, t);

        boolean agregado = gestorPacientes.agregarPaciente(p);

        if (agregado) {
            vista.mostrarMensaje("Paciente registrado.");
        } else {
            vista.mostrarMensaje("No se pudo registrar.");
        }
    }

    private void listarPacientes() {
        ArrayList<Paciente> lista = gestorPacientes.obtenerTodos();
        vista.mostrarPacientes(lista);
    }

    private void registrarGlicemia() {
        String rut = vista.leerTexto("Rut del paciente: ");
        Paciente p = gestorPacientes.buscarPorRut(rut);

        if (p == null) {
            vista.mostrarMensaje("Paciente no encontrado.");
            return;
        }

        int valor = vista.leerGlicemia();
        RegistroGlicemia r = new RegistroGlicemia("", valor);

        p.agregarRegistroGlicemia(r);
        vista.mostrarMensaje("Glicemia registrada.");
    }

    private void crearCuidador() {
        String usuario = vista.leerTexto("Nuevo nombre de usuario: ");
        String pass = vista.leerTexto("Contraseña: ");

        boolean creado = gestorUsuarios.agregarCuidador(usuario, pass);

        if (creado) {
            vista.mostrarMensaje("Cuidador creado.");
        } else {
            vista.mostrarMensaje("No se pudo crear.");
        }
    }

    
    public void mostrarInterfazSegunRol(Usuario u) {
        if (u instanceof Admin) {
            MenuAdmin v = new MenuAdmin();
            new ControladorAdmin(v, gestorPacientes, gestorUsuarios, u,this);
            v.setLocationRelativeTo(null);
            v.setVisible(true);      
        } else {
            menuCuidador();
        }
    }
}
