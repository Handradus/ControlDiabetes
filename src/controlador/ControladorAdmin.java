/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Admin;
import modelo.Cuidador;
import modelo.GestorPacientes;
import modelo.GestorUsuarios;
import modelo.Paciente;
import modelo.Usuario;
import vista.MenuAdmin;

/**
 *
 * @author Diavuru
 */

public class ControladorAdmin {

    private MenuAdmin vista;
    private GestorPacientes gestorPacientes;
    private GestorUsuarios gestorUsuarios;
    private Admin adminLogueado; //???
    private SistemaController sistemaController;

    public ControladorAdmin(MenuAdmin vista,
                            GestorPacientes gestorPacientes,
                            GestorUsuarios gestorUsuarios,
                            Usuario usuarioLogueado, SistemaController sistema) {
        this.vista = vista;
        this.gestorPacientes = gestorPacientes;
        this.gestorUsuarios = gestorUsuarios;
        this.adminLogueado = (Admin) usuarioLogueado;
        this.sistemaController = sistema;
        iniciar();
    }

    private void iniciar() {
        vista.getIngresarCuidadorBtn().addActionListener(e -> crearCuidador());
        vista.getListarCBtn().addActionListener(e -> listarCuidadores());
        vista.getListarPBtn().addActionListener(e -> listarPacientes());
        vista.getLogoutBtn().addActionListener(e -> cerrarSesion());
    }

    private void crearCuidador() {
        String nombre = vista.getNombreCTxt().getText().trim();
        String pass = new String(vista.getPassTxt().getPassword()).trim();

        if (nombre.isEmpty() || pass.isEmpty()) {
            vista.mostrarInfo("No pueden haber campos vacios");
            return;
        }

        boolean creado = gestorUsuarios.agregarCuidador(nombre, pass);

        if (creado) {
            vista.mostrarInfo("Cuidador creado Correctamente");
            vista.getNombreCTxt().setText("");
            vista.getPassTxt().setText("");
            listarCuidadores();
        } else {
            vista.mostrarInfo("Error al crear al nuevo cuidador");//cambair a ctach? para mostrar el error
        }
    }

    private void listarCuidadores() {
        ArrayList<Usuario> usuarios = gestorUsuarios.getUsuarios();

        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Usuario");
        modelo.addColumn("Rol");

        for (Usuario u : usuarios) {
            if (u instanceof Cuidador) {
                modelo.addRow(new Object[]{u.getNombreUsuario(), u.getRol()});
            }
        }

        vista.getTabla().setModel(modelo);
    }

    private void listarPacientes() {
        ArrayList<Paciente> pacientes = gestorPacientes.obtenerTodos();

        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Nombre");
        modelo.addColumn("RUT");
        modelo.addColumn("Edad");
        modelo.addColumn("Habitación");

        for (Paciente p : pacientes) {
            modelo.addRow(new Object[]{
                    p.getNombre(),
                    p.getRut(),
                    p.getEdad(),
                    p.getHabitacion()
            });
        }

        vista.getTabla().setModel(modelo);
    }

    private void cerrarSesion() {
        vista.dispose();
        //Vuelve al login
        sistemaController.mostrarLogin();
    }
}
