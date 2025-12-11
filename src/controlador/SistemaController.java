package controlador;

import java.io.IOException;
import modelo.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import vista.LoginPanel;
import vista.PanelAgregarPaciente;
import vista.PanelMenuAdmin;
import vista.PanelMenuCuidador;
import vista.VentanaPrincipal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;
import vista.PanelEditarPaciente;
import vista.PanelRegistrarGlicemia;
import vista.PanelTratamiento;

public class SistemaController {

   

    private final VentanaPrincipal ventana;
    private final GestorPacientes gestorPacientes;
    private final GestorUsuarios gestorUsuarios;
    
    private PanelMenuAdmin panelAdmin;
    private PanelMenuCuidador panelCuidador;
    private PanelEditarPaciente panelEdicionPCT;
    
    private PanelRegistrarGlicemia panelRegistrarGlicemia;
    private PanelTratamiento panelTratamiento;

    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    

    public SistemaController(VentanaPrincipal ventana,
                             GestorPacientes gestorPacientes,
                             GestorUsuarios gestorUsuarios) {
        this.ventana = ventana;
        this.gestorPacientes = gestorPacientes;
        this.gestorUsuarios = gestorUsuarios;
        
        try {
            gestorUsuarios.cargarUsuarios("usuarios.txt");
            gestorPacientes.cargarPacientes("pacientes.txt");
        } catch (IOException e) {
        
            System.out.println("No se pudieron cargar datos: " + e.getMessage());
        }
        iniciarReloj();
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
        panelCuidador.getEditPctBtn().addActionListener(e -> {
            int fila = panelCuidador.getPctTabla().getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(ventana, "Seleccione un paciente primero.");
                return;
            }

    // Supongamos que en la columna 1 está el RUT
            String rutSeleccionado = (String) panelCuidador.getPctTabla()
            .getValueAt(fila, 1); // cambia el índice si tu tabla es distinta

            Paciente paciente = gestorPacientes.buscarPorRut(rutSeleccionado);
            if (paciente == null) {
                JOptionPane.showMessageDialog(ventana, "No se encontró el paciente en memoria.");
                return;
            }

            mostrarEditarPaciente(paciente);
        });
        
        
        panelCuidador.getListarPctBtn().addActionListener(e -> listarPacientes());
        panelCuidador.getLogoutBtn().addActionListener(e -> cerrarSesion());
        panelCuidador.getRegistrarGlicemiaBtn().addActionListener(e -> abrirRegistrarGlicemia());
        panelCuidador.getVerTratamientoBtn().addActionListener(e -> abrirTratamientoPaciente());

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

    private void mostrarEditarPaciente(Paciente paciente) {
        panelEdicionPCT = new PanelEditarPaciente();
        panelEdicionPCT.getNombrePacienteTxt().setText(paciente.getNombre());
        panelEdicionPCT.getRutPacienteTxt().setText(paciente.getRut());
        panelEdicionPCT.getRoomPctTxt().setText(paciente.getHabitacion());
        panelEdicionPCT.getEdadTxt().setText(String.valueOf(paciente.getEdad()));

        //nuevos datos
        
        panelEdicionPCT.getSaveEditPacienteBtn().addActionListener(e -> {
            String nuevoNombre = panelEdicionPCT.getNombrePacienteTxt().getText();
            String nuevoRut    = panelEdicionPCT.getRutPacienteTxt().getText();
            String nuevaHab    = panelEdicionPCT.getRoomPctTxt().getText();
            String nuevaEdadStr = panelEdicionPCT.getEdadTxt().getText();

            if (nuevoNombre.isBlank() || nuevoRut.isBlank() || nuevaHab.isBlank()) {
                JOptionPane.showMessageDialog(ventana, "Complete todos los campos.");
                return;
            }

            paciente.setNombre(nuevoNombre);
            paciente.setRut(nuevoRut);
            paciente.setHabitacion(nuevaHab);

            try {
                int nuevaEdad = Integer.parseInt(nuevaEdadStr);
                paciente.setEdad(nuevaEdad);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ventana, "Edad inválida");
                return;
            }
            try {
            gestorPacientes.guardarPacientes("pacientes.txt");  // usa el nombre real que estés usando
            } catch (IOException ex1) {
                JOptionPane.showMessageDialog(ventana, "Error al guardar pacientes: " + ex1.getMessage());
            
            }

            JOptionPane.showMessageDialog(ventana, "Paciente actualizado correctamente.");

        // 2.3) Volver al menú/cuidador y refrescar la tabla
            ventana.mostrarPanel(panelCuidador);
        listarPacientes();  // método tuyo para volver a cargar los datos en la JTable
        });

    // 3) Mostrar el panel de edición
        ventana.mostrarPanel(panelEdicionPCT);
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
            
            try {
        gestorUsuarios.archivar("usuarios.txt");  // 👈 AHORA sí guardas usuarios
            } catch (IOException ex) {
                panelAdmin.mostrarError("Cuidador creado en memoria, pero falló al escribir archivo de usuarios.");
            }
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
            try {
                gestorPacientes.guardarPacientes("pacientes.txt");
            } catch (IOException ex) {
                panel.mostrarError("Paciente guardado en memoria, pero falló al escribir archivo.");
            }
        mostrarMenuCuidador(); // volver al menú
        } else {
            panel.mostrarError("Error al registrar paciente.");
        }
    }

    private void abrirRegistrarGlicemia() {
        int fila = panelCuidador.getPctTabla().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(ventana, "Seleccione un paciente primero.");
            return;
        }

        String rut = (String) panelCuidador.getPctTabla().getValueAt(fila, 1);
        Paciente p = gestorPacientes.buscarPorRut(rut);

        if (p == null) {
            JOptionPane.showMessageDialog(ventana, "No se encontró el paciente.");
            return;
        }

        panelRegistrarGlicemia = new PanelRegistrarGlicemia();

        panelRegistrarGlicemia.getRegistrarBtn().addActionListener(e -> {
            String valorStr = panelRegistrarGlicemia.getValorTxt().getText();
            int valor;

            try {
                valor = Integer.parseInt(valorStr);
            } catch (NumberFormatException ex) {
                panelRegistrarGlicemia.mostrarError("La glicemia debe ser un número.");
                return;
            }

            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String fecha = LocalDateTime.now().format(f);

            RegistroGlicemia reg = new RegistroGlicemia(fecha, valor);

            p.agregarRegistroGlicemia(reg);

            panelRegistrarGlicemia.mostrarInfo("Glicemia registrada.");

            try {
                gestorPacientes.guardarPacientes("pacientes.txt");
            } catch (IOException ex2) {
                panelRegistrarGlicemia.mostrarError("Guardado en memoria, pero error al escribir archivo.");
            }

            ventana.mostrarPanel(panelCuidador);
            listarPacientes();
        });

        panelRegistrarGlicemia.getVolverBtn().addActionListener(e -> ventana.mostrarPanel(panelCuidador));

        ventana.mostrarPanel(panelRegistrarGlicemia);
    }


    private void abrirTratamientoPaciente() {
        int fila = panelCuidador.getPctTabla().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(ventana, "Seleccione un paciente.");
            return;
        }

        String rut = (String) panelCuidador.getPctTabla().getValueAt(fila, 1);
        Paciente p = gestorPacientes.buscarPorRut(rut);

        if (p == null) {
            JOptionPane.showMessageDialog(ventana, "Paciente no encontrado.");
            return;
        }

        panelTratamiento = new PanelTratamiento();

        panelTratamiento.getNombreTxt().setText(p.getNombre());
        panelTratamiento.getRutTxt().setText(p.getRut());
        panelTratamiento.getDietaTxt().setText(p.getTratamiento().getDietaRecomendada());
        panelTratamiento.getMedsTxt().setText(p.getTratamiento().getMedicamentosOrales());
        panelTratamiento.getSosTxt().setText(String.valueOf(p.getTratamiento().isUsaInsulinaCristalinaSOS()));
        panelTratamiento.getLentaTxt().setText(String.valueOf(p.getTratamiento().isUsaInsulinaLentaDiaria()));
        panelTratamiento.getDosisTxt().setText(String.valueOf(p.getTratamiento().getDosisInsulinaLentaDiaria()));
        panelTratamiento.getFreqTxt().setText(String.valueOf(p.getTratamiento().getFrecuenciaHorasControles()));

        if (p.getTratamiento().getHoraPrimerControl() != null) {
            panelTratamiento.getHoraTxt().setText(p.getTratamiento().getHoraPrimerControl().toString());
        } else {
            panelTratamiento.getHoraTxt().setText("No definido");
        }

        panelTratamiento.getVolverBtn().addActionListener(e -> ventana.mostrarPanel(panelCuidador));

        ventana.mostrarPanel(panelTratamiento);
    }

    private void iniciarReloj() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime ahora = LocalDateTime.now();
            String textoHora = ahora.format(formatoHora);
            ventana.getHoraLabel().setText(textoHora);
        });
        timer.start();
    }
}
