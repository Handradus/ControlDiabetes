package controlador;

import modelo.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import vista.EditarPaciente;
import vista.LoginPanel;
import vista.PanelAgregarPaciente;
import vista.PanelMenuAdmin;
import vista.PanelMenuCuidador;
import vista.VentanaPrincipal;

public class SistemaController {

   

   private final VentanaPrincipal ventana;
    private final GestorPacientes gestorPacientes;
    private final GestorUsuarios gestorUsuarios;
    
    private PanelMenuAdmin panelAdmin;
    private PanelMenuCuidador panelCuidador;
    
    

    public SistemaController(VentanaPrincipal ventana,
                             GestorPacientes gestorPacientes,
                             GestorUsuarios gestorUsuarios) {
        this.ventana = ventana;
        this.gestorPacientes = gestorPacientes;
        this.gestorUsuarios = gestorUsuarios;
    }

    
     public void iniciar() {
        ventana.setVisible(true);
        mostrarLogin();
    }

    private void mostrarLogin() {
        LoginPanel panelLogin = new LoginPanel();

        panelLogin.getIngresarBtn().addActionListener(e -> {
            String nombreUsuario = panelLogin.getUserTxt().getText();
            String pass = new String(panelLogin.getPassTxt().getPassword());

            // Acá haces la validación con tu modelo / AuthService
            // Ejemplo muy genérico:
             Usuario u = gestorUsuarios.login(nombreUsuario, pass);

            if (u != null) {
            if (u instanceof Admin) {
                mostrarMenuAdmin();
            } else if (u instanceof Cuidador) {
                mostrarMenuCuidador();
            } else {
                javax.swing.JOptionPane.showMessageDialog(
                        ventana,
                        "Rol no reconocido para este usuario.",
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(
                    ventana,
                    "Usuario o contraseña incorrectos",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    });

    ventana.mostrarPanel(panelLogin);
}

    private void mostrarMenuCuidador() {
        panelCuidador = new PanelMenuCuidador();

        panelCuidador.getAddPctBtn().addActionListener(e -> mostrarAgregarPaciente());
        panelCuidador.getEditPctBtn().addActionListener(e -> mostrarEditarPaciente());
        panelCuidador.getListarPctBtn().addActionListener(e -> listarPacientes());
        panelCuidador.getLogoutBtn().addActionListener(e -> cerrarSesion());

        ventana.mostrarPanel(panelCuidador);
    }

    private void mostrarMenuAdmin() {
        panelAdmin = new PanelMenuAdmin();
       
        panelAdmin.getIngresarCuidadorBtn().addActionListener(e -> crearCuidador());
        panelAdmin.getListarCBtn().addActionListener(e -> listarCuidadores());
        
        panelAdmin.getLogoutBtn().addActionListener(e -> cerrarSesion());
        ventana.mostrarPanel(panelAdmin);
    }

    private void mostrarAgregarPaciente() {
        PanelAgregarPaciente panel = new PanelAgregarPaciente();
        panel.getAddPacienteBtn().addActionListener(e -> crearPaciente(panel));

        ventana.mostrarPanel(panel);
              
    }

    private void mostrarEditarPaciente() {
        EditarPaciente panelCuidador = new EditarPaciente();
        // lógica similar
        ventana.mostrarPanel(panelCuidador);
    }

       
      private void crearCuidador() {
                
          
        // Usamos el panel de admin actual
        String nombre = panelAdmin.getNombreCTxt().getText().trim();
        String pass = new String(panelAdmin.getPassTxt().getPassword()).trim();

        if (nombre.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(
                    ventana,
                    "No pueden haber campos vacíos",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean creado = gestorUsuarios.agregarCuidador(nombre, pass);

        if (creado) {
            JOptionPane.showMessageDialog(
                    ventana,
                    "Cuidador creado correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            panelAdmin.getNombreCTxt().setText("");
            panelAdmin.getPassTxt().setText("");
            listarCuidadores();
        } else {
            JOptionPane.showMessageDialog(
                    ventana,
                    "Error al crear al nuevo cuidador",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
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

        panelAdmin.getTabla().setModel(modelo);
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

        panelCuidador.getPctTabla().setModel(modelo);
    }

    private void cerrarSesion() {
        // No se cierra la ventana principal, solo volvemos al login
        mostrarLogin();
    }

    private void crearPaciente(PanelAgregarPaciente panel) {
        String nombre = panel.getNombrePacienteTxt().getText().trim();
        String rut = panel.getRutPacienteTxt().getText().trim();
        String habitacion = panel.getRoomPctTxt().getText().trim();

        int edad;
    try {
        edad = Integer.parseInt(panel.getEdadTxt().getText().trim());
    } catch (NumberFormatException e) {
        panel.mostrarError("La edad debe ser un número.");
        return;
    }

    if (nombre.isEmpty() || rut.isEmpty() || habitacion.isEmpty()) {
        panel.mostrarError("No pueden haber campos vacíos." );
        return;
    }

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
        panel.mostrarInfo("Paciente registrado correctamente.");
        mostrarMenuCuidador(); // volver al menú
    } else {
        panel.mostrarError("Error al registrar paciente.");
    }
    }
}