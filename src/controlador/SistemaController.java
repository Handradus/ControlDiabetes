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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JCheckBox;
import javax.swing.Timer;
import vista.PanelEditarPaciente;
import vista.PanelRegistrarGlicemia;
import vista.PanelTratamiento;

public class SistemaController {

   

    private final VentanaPrincipal ventana;
    private final GestorPacientes gestorPacientes;
    private final GestorUsuarios gestorUsuarios;
    private Usuario usuarioLogueado;

    
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
            gestorPacientes.cargarTodo("pacientes.txt", "glicemias.txt");

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
            
            this.usuarioLogueado = u;
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
            gestorPacientes.guardarTodo("pacientes.txt", "glicemias.txt");

            
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

        Paciente p = new Paciente(nombre, rut, edad, habitacion, null);

        boolean agregado = gestorPacientes.agregarPaciente(p);

        if (agregado) {
            panel.mostrarInfo("Paciente registrado correctamente.");
            try {
                gestorPacientes.guardarTodo("pacientes.txt", "glicemias.txt");

            } catch (IOException ex) {
                panel.mostrarError("Paciente guardado en memoria, pero falló al escribir archivo.");
            }
        abrirTratamientoParaEdicion(p);
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
        
        //cambia lbl a nombre de paciente usando el metodo en la vista de glicemia
        panelRegistrarGlicemia.setNombrePaciente(p.getNombre());
        
        //Tabla para listar glicemia
        
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Fecha");
        modelo.addColumn("Valor");
        modelo.addColumn("Registrado por");

        for (RegistroGlicemia r : p.getHistorialGlicemias()) {
            modelo.addRow(new Object[]{
                    r.getFechaHora(),
                    r.getValor(),
                    r.getRegistrado()
            });
        }

        panelRegistrarGlicemia.getHgtTabla().setModel(modelo);


        panelRegistrarGlicemia.getRegistrarBtn().addActionListener(e -> {
            String valorStr = panelRegistrarGlicemia.getValorTxt().getText();
            String registrado = usuarioLogueado.getNombreUsuario();

            int valor;

            try {
                valor = Integer.parseInt(valorStr);
            } catch (NumberFormatException ex) {
                panelRegistrarGlicemia.mostrarError("La glicemia debe ser un número.");
                return;
            }

            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String fecha = LocalDateTime.now().format(f);

            RegistroGlicemia reg = new RegistroGlicemia(fecha, valor,registrado);

            p.agregarRegistroGlicemia(reg);

            panelRegistrarGlicemia.mostrarInfo("Glicemia registrada.");

            try {
                gestorPacientes.guardarTodo("pacientes.txt", "glicemias.txt");
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
    JCheckBox chkLenta = panelTratamiento.getInsulinaCheck();

  
    chkLenta.addActionListener(e -> {
        // Si está en modo lectura, no hacer nada
        if (!chkLenta.isEnabled()) return;

        boolean activo = chkLenta.isSelected();
        panelTratamiento.habilitarDosisLenta(activo);

        if (!activo) {
            panelTratamiento.limpiarDosis();
        }
    });

    panelTratamiento.modoLectura();

    // Datos del paciente
    panelTratamiento.getNombreTxt().setText(p.getNombre());
    panelTratamiento.getRutTxt().setText(p.getRut());

    //Carga tto
    Tratamiento t = p.getTratamiento();
    if (t != null) {
        panelTratamiento.getDietaTxt().setText(t.getDietaRecomendada());
        panelTratamiento.getMedsTxt().setText(t.getMedicamentosOrales());
        panelTratamiento.getSosCheck().setSelected(t.isUsaInsulinaCristalinaSOS());

        chkLenta.setSelected(t.isUsaInsulinaLentaDiaria());

        panelTratamiento.getDosisTxt()
                .setText(String.valueOf(t.getDosisInsulinaLentaDiaria()));
        panelTratamiento.getFreqTxt()
                .setText(String.valueOf(t.getFrecuenciaHorasControles()));

        if (t.getHoraPrimerControl() != null) {
            LocalTime h = t.getHoraPrimerControl();
            panelTratamiento.getHoraCombo()
                    .setSelectedItem(String.format("%02d", h.getHour()));
            panelTratamiento.getMinCombo()
                    .setSelectedItem(String.format("%02d", h.getMinute()));
        }
    }

    
    panelTratamiento.habilitarDosisLenta(chkLenta.isSelected());

   
    panelTratamiento.getEditarBtn().addActionListener(e -> {
        panelTratamiento.modoEdicion(); // habilita campos y checkbox
        panelTratamiento.habilitarDosisLenta(chkLenta.isSelected());
    });

   
    panelTratamiento.getVolverBtn()
            .addActionListener(e -> ventana.mostrarPanel(panelCuidador));

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
    
   private void abrirTratamientoParaEdicion(Paciente p) {

    panelTratamiento = new PanelTratamiento();
    JCheckBox chkLenta = panelTratamiento.getInsulinaCheck();

    // Datos del paciente (solo lectura)
    panelTratamiento.getNombreTxt().setText(p.getNombre());
    panelTratamiento.getRutTxt().setText(p.getRut());
    panelTratamiento.getNombreTxt().setEditable(false);
    panelTratamiento.getRutTxt().setEditable(false);

    // ===== Precarga tratamiento si existe =====
    if (p.getTratamiento() != null) {
        Tratamiento t = p.getTratamiento();

        panelTratamiento.getDietaTxt().setText(t.getDietaRecomendada());
        panelTratamiento.getMedsTxt().setText(t.getMedicamentosOrales());
        panelTratamiento.getSosCheck().setSelected(t.isUsaInsulinaCristalinaSOS());

        chkLenta.setSelected(t.isUsaInsulinaLentaDiaria());
        panelTratamiento.getDosisTxt()
                .setText(String.valueOf(t.getDosisInsulinaLentaDiaria()));
        panelTratamiento.getFreqTxt()
                .setText(String.valueOf(t.getFrecuenciaHorasControles()));

        if (t.getHoraPrimerControl() != null) {
            LocalTime h = t.getHoraPrimerControl();
            panelTratamiento.getHoraCombo()
                    .setSelectedItem(String.format("%02d", h.getHour()));
            panelTratamiento.getMinCombo()
                    .setSelectedItem(String.format("%02d", h.getMinute()));
        }
    }

    // ===== Estado inicial del campo dosis =====
    panelTratamiento.habilitarDosisLenta(chkLenta.isSelected());

    // ===== Listener del checkbox (SIEMPRE) =====
    chkLenta.addActionListener(e -> {
        boolean activo = chkLenta.isSelected();
        panelTratamiento.habilitarDosisLenta(activo);
        if (!activo) {
            panelTratamiento.limpiarDosis();
        }
    });

    // ===== Guardar tratamiento =====
    panelTratamiento.getSaveTtoBtn().addActionListener(e -> {
        try {
            String dieta = panelTratamiento.getDietaTxt().getText().trim();
            String meds  = panelTratamiento.getMedsTxt().getText().trim();

            boolean usaSos   = panelTratamiento.getSosCheck().isSelected();
            boolean usaLenta = chkLenta.isSelected();

            int dosisLenta = 0;
            if (usaLenta) {
                dosisLenta = Integer.parseInt(
                        panelTratamiento.getDosisTxt().getText().trim()
                );
            }

            int frecuencia = Integer.parseInt(
                    panelTratamiento.getFreqTxt().getText().trim()
            );

            String hSel = (String) panelTratamiento.getHoraCombo().getSelectedItem();
            String mSel = (String) panelTratamiento.getMinCombo().getSelectedItem();
            LocalTime horaPrimerControl =
                    LocalTime.of(Integer.parseInt(hSel), Integer.parseInt(mSel));

            Tratamiento nuevoT = new Tratamiento(
                    dieta,
                    meds,
                    usaSos,
                    usaLenta,
                    dosisLenta,
                    frecuencia,
                    horaPrimerControl
            );

            p.setTratamiento(nuevoT);
            gestorPacientes.guardarTodo("pacientes.txt", "glicemias.txt");

            JOptionPane.showMessageDialog(ventana, "Tratamiento guardado correctamente.");
            ventana.mostrarPanel(panelCuidador);
            listarPacientes();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    ventana,
                    "Revisa dosis y frecuencia.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    ventana,
                    "Error al guardar archivos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    });

    panelTratamiento.getVolverBtn()
            .addActionListener(e -> ventana.mostrarPanel(panelCuidador));

    ventana.mostrarPanel(panelTratamiento);
}      
           

}
