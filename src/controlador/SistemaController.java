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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;




public class SistemaController {
    private Paciente pacienteSeleccionado;
    private ArrayList<Paciente> pacientesEnTabla = new ArrayList<>();


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
            gestorPacientes.cargarTodo("pacientes.txt", "glicemias.txt", "alertas.txt");

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
            if (!panelLogin.validarCampos()) return;

            String nombreUsuario = panelLogin.getUsuarioLimpio();
            String pass = panelLogin.getPasswordLimpia();
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

    panelCuidador.getPctTabla()
        .getSelectionModel()
        .addListSelectionListener(e -> {

            if (e.getValueIsAdjusting()) return;

            int fila = panelCuidador.getPctTabla().getSelectedRow();

            if (fila == -1 || fila >= pacientesEnTabla.size()) {
                pacienteSeleccionado = null;
                panelCuidador.habilitarPanelPaciente(false);
                return;
            }

            pacienteSeleccionado = pacientesEnTabla.get(fila);

            panelCuidador.getActivoCheck()
                    .setSelected(pacienteSeleccionado.isActivo());

            panelCuidador.habilitarPanelPaciente(true);
            actualizarEstadoPaciente();
            cargarAlertasPaciente(pacienteSeleccionado);
        });

    panelCuidador.getActivoCheck().addActionListener(e -> {

        if (pacienteSeleccionado == null) return;

        boolean nuevoEstado = panelCuidador.getActivoCheck().isSelected();
        String accion = nuevoEstado ? "activar" : "desactivar";

        int resp = JOptionPane.showConfirmDialog(
            ventana,
            "¿Estás seguro que deseas " + accion + " al paciente?",
            "Confirmar acción",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (resp != JOptionPane.YES_OPTION) {
            panelCuidador.getActivoCheck()
                    .setSelected(pacienteSeleccionado.isActivo());
            return;
        }

        pacienteSeleccionado.setActivo(nuevoEstado);

        try {
            gestorPacientes.guardarTodo("pacientes.txt", "glicemias.txt", "alertas.txt");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                ventana,
                "Error al guardar el estado del paciente",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        listarPacientes();
        panelCuidador.habilitarPanelPaciente(false);
    });

    panelCuidador.getAddPctBtn().addActionListener(e -> mostrarAgregarPaciente());

    panelCuidador.getEditPctBtn().addActionListener(e -> {
        if (pacienteSeleccionado != null)
            mostrarEditarPaciente(pacienteSeleccionado);
    });

    panelCuidador.getRegistrarGlicemiaBtn()
            .addActionListener(e -> abrirRegistrarGlicemia());

    panelCuidador.getVerTratamientoBtn()
            .addActionListener(e -> abrirTratamientoPaciente());

    panelCuidador.getListarPctBtn()
            .addActionListener(e -> listarPacientes());

    panelCuidador.getLogoutBtn()
            .addActionListener(e -> cerrarSesion());

    panelCuidador.getBuscarRutTxt().getDocument()
        .addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarPacientesPorRut(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarPacientesPorRut(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarPacientesPorRut(); }
        });

    ventana.mostrarPanel(panelCuidador);
    listarPacientes();
    panelCuidador.habilitarPanelPaciente(false);
}
private void actualizarEstadoPaciente() {

    if (pacienteSeleccionado == null) {
        panelCuidador.getRegistrarGlicemiaBtn().setEnabled(false);
        panelCuidador.getEditPctBtn().setEnabled(false);
        panelCuidador.getVerTratamientoBtn().setEnabled(false);
        return;
    }

    boolean activo = pacienteSeleccionado.isActivo();

    panelCuidador.getRegistrarGlicemiaBtn().setEnabled(activo);
    panelCuidador.getEditPctBtn().setEnabled(activo);
    panelCuidador.getVerTratamientoBtn().setEnabled(activo);
}

private void filtrarPacientesPorRut() {

    String texto = panelCuidador.getBuscarRutTxt()
            .getText().trim();

    String filtro = Utilidades.limpiarRut(texto);

    pacientesEnTabla = new ArrayList<>();

    DefaultTableModel modelo = new DefaultTableModel(
        new String[]{"Nombre", "RUT", "Edad", "Habitación"}, 0
    );

    for (Paciente p : gestorPacientes.obtenerTodos()) {

        String rutPaciente = Utilidades.limpiarRut(p.getRut());

        if (filtro.isEmpty() || rutPaciente.startsWith(filtro)) {
            pacientesEnTabla.add(p);
            modelo.addRow(new Object[]{
                p.getNombre(),
                p.getRut(),
                p.getEdad(),
                p.getHabitacion()
            });
        }
    }

    panelCuidador.getPctTabla().setModel(modelo);
    panelCuidador.aplicarRenderPacientes(pacientesEnTabla);

    pacienteSeleccionado = null;
    panelCuidador.habilitarPanelPaciente(false);
}


 
    private void buscarPacientePorRut() {

        String rutBuscado = panelCuidador.getBuscarRutTxt().getText().trim();

        if (rutBuscado.isEmpty()) {
            listarPacientes();
            return;
        }

        Paciente p = gestorPacientes.buscarPorRut(rutBuscado);

        if (p == null) {
            JOptionPane.showMessageDialog(
                    ventana,
                    "No se encontró paciente con ese RUT.",
                    "Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Nombre");
        modelo.addColumn("RUT");
        modelo.addColumn("Edad");
        modelo.addColumn("Habitación");

        modelo.addRow(new Object[]{
                p.getNombre(),
                p.getRut(),
                p.getEdad(),
                p.getHabitacion()
        });

        panelCuidador.getPctTabla().setModel(modelo);
    }

    
    private void mostrarMenuAdmin() {
        panelAdmin = new PanelMenuAdmin();
       
        panelAdmin.getIngresarCuidadorBtn().addActionListener(e -> crearCuidador());
        panelAdmin.getListarCBtn().addActionListener(e -> listarCuidadores());
               
        panelAdmin.getLogoutBtn().addActionListener(e -> cerrarSesion());
        panelAdmin.getDelCuidadorBtn().addActionListener(e -> eliminarCuidadorSeleccionado());
        ventana.mostrarPanel(panelAdmin);
    }

    private void mostrarAgregarPaciente() {

        PanelAgregarPaciente panel = new PanelAgregarPaciente();

        panel.getAddPacienteBtn().addActionListener(e -> crearPaciente(panel));

        panel.getVolverBtn().addActionListener(e -> {
            ventana.mostrarPanel(panelCuidador);
            listarPacientes();
        });

        ventana.mostrarPanel(panel);
    }


private void mostrarEditarPaciente(Paciente paciente) {

    if (!paciente.isActivo()) {
        JOptionPane.showMessageDialog(
            ventana,
            "El paciente está desactivado.",
            "Acción no permitida",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    panelEdicionPCT = new PanelEditarPaciente();
    panelEdicionPCT.getNombrePacienteTxt().setText(paciente.getNombre());
    panelEdicionPCT.getRutPacienteTxt().setText(paciente.getRut());
    panelEdicionPCT.getRoomPctTxt().setText(paciente.getHabitacion());
    panelEdicionPCT.getEdadTxt().setText(String.valueOf(paciente.getEdad()));
    panelEdicionPCT.getVolverBtn().addActionListener(e -> {
            ventana.mostrarPanel(panelCuidador);
            listarPacientes();
        });

    panelEdicionPCT.getSaveEditPacienteBtn().addActionListener(e -> {

    String nombre = panelEdicionPCT.getNombrePacienteTxt().getText().trim();
    String habitacion = panelEdicionPCT.getRoomPctTxt().getText().trim();
    String edadTxt = panelEdicionPCT.getEdadTxt().getText().trim();

    if (Utilidades.esTextoVacio(nombre)
        || Utilidades.esTextoVacio(habitacion)
        || Utilidades.esTextoVacio(edadTxt)) {

        panelEdicionPCT.mostrarError("No pueden haber campos vacíos.");
        return;
    }

    if (!Utilidades.esSoloLetras(nombre)) {
        panelEdicionPCT.mostrarError("El nombre solo puede contener letras.");
        return;
    }

    int edad;
    try {
        edad = Integer.parseInt(edadTxt);
    } catch (NumberFormatException ex) {
        panelEdicionPCT.mostrarError("La edad debe ser numérica.");
        return;
    }

    if (!Utilidades.estaEnRango(edad, 0, 120)) {
        panelEdicionPCT.mostrarError("Edad fuera de rango.");
        return;
    }

    paciente.setNombre(Utilidades.normalizarNombre(nombre));
    paciente.setHabitacion(habitacion);
    paciente.setEdad(edad);

    try {
        gestorPacientes.guardarTodo("pacientes.txt", "glicemias.txt", "alertas.txt");
    } catch (IOException ex) {
        panelEdicionPCT.mostrarError("Error al guardar los cambios.");
        return;
    }

    panelEdicionPCT.mostrarInfo("Paciente actualizado correctamente.");
    ventana.mostrarPanel(panelCuidador);
    listarPacientes();
});

    ventana.mostrarPanel(panelEdicionPCT);
}


       
    private void crearCuidador() {
                
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
        gestorUsuarios.archivar("usuarios.txt");
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

    pacientesEnTabla = gestorPacientes.obtenerTodos();

    DefaultTableModel modelo = new DefaultTableModel(
        new String[]{"Nombre", "RUT", "Edad", "Habitación"}, 0
    );

    for (Paciente p : pacientesEnTabla) {
        modelo.addRow(new Object[]{
            p.getNombre(),
            p.getRut(),
            p.getEdad(),
            p.getHabitacion()
        });
    }

    panelCuidador.getPctTabla().setModel(modelo);
    panelCuidador.aplicarRenderPacientes(pacientesEnTabla);

    pacienteSeleccionado = null;
    panelCuidador.habilitarPanelPaciente(false);
}

    private void cerrarSesion() {
        mostrarLogin();
    }

    
private void crearPaciente(PanelAgregarPaciente panel) {

    String nombre = panel.getNombrePacienteTxt().getText().trim();
    String rut = panel.getRutPacienteTxt().getText().trim();
    String habitacion = panel.getRoomPctTxt().getText().trim();
    String edadTxt = panel.getEdadTxt().getText().trim();

    if (Utilidades.esTextoVacio(nombre)
        || Utilidades.esTextoVacio(rut)
        || Utilidades.esTextoVacio(habitacion)
        || Utilidades.esTextoVacio(edadTxt)) {

        panel.mostrarError("No pueden haber campos vacíos.");
        return;
    }

    if (!Utilidades.esSoloLetras(nombre)) {
        panel.mostrarError("El nombre solo puede contener letras.");
        return;
    }

    rut = Utilidades.limpiarRut(rut);
    if (!Utilidades.esRutValido(rut)) {
        panel.mostrarError("RUT inválido.");
        return;
    }

    int edad;
    try {
        edad = Integer.parseInt(edadTxt);
    } catch (NumberFormatException e) {
        panel.mostrarError("La edad debe ser numérica.");
        return;
    }

    if (!Utilidades.estaEnRango(edad, 0, 120)) {
        panel.mostrarError("Edad fuera de rango.");
        return;
    }

    if (habitacion.length() < 2) {
        panel.mostrarError("Habitación inválida.");
        return;
    }

    Paciente p = new Paciente(
        Utilidades.normalizarNombre(nombre),
        rut,
        edad,
        habitacion,
        null
    );

    if (!gestorPacientes.agregarPaciente(p)) {
        panel.mostrarError("Ya existe un paciente con ese RUT.");
        return;
    }

    try {
        gestorPacientes.guardarTodo("pacientes.txt", "glicemias.txt", "alertas.txt");
    } catch (IOException ex) {
        panel.mostrarError("Error al guardar paciente.");
        return;
    }

    panel.mostrarInfo("Paciente registrado correctamente.");
    abrirTratamientoParaEdicion(p);
}

    
private void abrirRegistrarGlicemia() {

    int fila = panelCuidador.getPctTabla().getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(ventana, "Seleccione un paciente primero.");
        return;
    }

    Paciente p = pacientesEnTabla.get(fila);

    if (!p.isActivo()) {
        JOptionPane.showMessageDialog(
            ventana,
            "El paciente está desactivado.",
            "Acción no permitida",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    panelRegistrarGlicemia = new PanelRegistrarGlicemia();
    panelRegistrarGlicemia.setNombrePaciente(p.getNombre());

    DefaultTableModel modelo = new DefaultTableModel(
        new String[]{"Fecha", "Valor", "Registrado por"}, 0
    );

    for (RegistroGlicemia r : p.getHistorialGlicemias()) {
        modelo.addRow(new Object[]{
            r.getFechaHora(),
            r.getValor(),
            r.getRegistrado()
        });
    }

    panelRegistrarGlicemia.getHgtTabla().setModel(modelo);

    panelRegistrarGlicemia.getRegistrarBtn().addActionListener(e -> {

        int valor;
        try {
            valor = Integer.parseInt(panelRegistrarGlicemia.getValorTxt().getText().trim());
        } catch (NumberFormatException ex) {
            panelRegistrarGlicemia.mostrarError("La glicemia debe ser un número.");
            return;
        }

        if (!Utilidades.esGlicemiaValida(valor)) {
            panelRegistrarGlicemia.mostrarError("Valor de glicemia fuera de rango clínico.");
            return;
        }

        String fecha = LocalDateTime.now().format(formatoHora);

        RegistroGlicemia reg = new RegistroGlicemia(
            fecha,
            valor,
            usuarioLogueado.getNombreUsuario()
        );

        p.agregarRegistroGlicemia(reg);

        mostrarMensajeClinico(valor, p.getTratamiento());

        try {
            gestorPacientes.guardarTodo("pacientes.txt", "glicemias.txt", "alertas.txt");
        } catch (IOException ex) {
            panelRegistrarGlicemia.mostrarError("Error al guardar.");
            return;
        }

        ventana.mostrarPanel(panelCuidador);
        listarPacientes();
        cargarAlertasPaciente(p);
    });

    panelRegistrarGlicemia.getVolverBtn()
        .addActionListener(e -> ventana.mostrarPanel(panelCuidador));

    ventana.mostrarPanel(panelRegistrarGlicemia);
}


private void mostrarMensajeClinico(int valor, Tratamiento t) {

    if (valor < 70) {
        JOptionPane.showMessageDialog(
            ventana,
            "⚠ Hipoglicemia (" + valor + ").\nAdministrar carbohidratos y avisar.",
            "Alerta clínica",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    if (valor > 300) {
        String msg = "🚨 GLICEMIA CRÍTICA (" + valor + ").\n";

        if (t != null && t.isUsaInsulinaCristalinaSOS()) {
            msg += "\nPAUTA INSULINA SOS:\n" + t.getPautaInsulinaSOS();
        }

        msg += "\nAvisar de inmediato / considerar SAMU.";

        JOptionPane.showMessageDialog(
            ventana,
            msg,
            "ALERTA CRÍTICA",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    if (valor > 180) {
        JOptionPane.showMessageDialog(
            ventana,
            "Glicemia elevada (" + valor + ").",
            "Advertencia",
            JOptionPane.INFORMATION_MESSAGE
        );
        return;
    }

    JOptionPane.showMessageDialog(
        ventana,
        "Glicemia registrada correctamente.",
        "Registro exitoso",
        JOptionPane.INFORMATION_MESSAGE
    );
}




private void abrirTratamientoPaciente() {

    if (pacienteSeleccionado == null) {
        JOptionPane.showMessageDialog(ventana, "Seleccione un paciente.");
        return;
    }

    Paciente p = pacienteSeleccionado;

    if (!p.isActivo()) {
        JOptionPane.showMessageDialog(
            ventana,
            "El paciente está desactivado.",
            "Acción no permitida",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    panelTratamiento = new PanelTratamiento();
    panelTratamiento.modoLectura();
    

    panelTratamiento.getNombreTxt().setText(p.getNombre());
    panelTratamiento.getRutTxt().setText(p.getRut());
    panelTratamiento.getNombreTxt().setEditable(false);
    panelTratamiento.getRutTxt().setEditable(false);

    if (p.getTratamiento() != null) {
        Tratamiento t = p.getTratamiento();
        
        

        panelTratamiento.getDietaTxt().setText(t.getDietaRecomendada());
        panelTratamiento.getMedsTxt().setText(t.getMedicamentosOrales());
        panelTratamiento.getSosCheck().setSelected(t.isUsaInsulinaCristalinaSOS());
        panelTratamiento.habilitarPautaSOS(t.isUsaInsulinaCristalinaSOS());
        panelTratamiento.getPautaSosTxt().setText(t.getPautaInsulinaSOS());
        panelTratamiento.getInsulinaCheck().setSelected(t.isUsaInsulinaLentaDiaria());
        panelTratamiento.getDosisTxt().setText(String.valueOf(t.getDosisInsulinaLentaDiaria()));
        panelTratamiento.getFreqTxt().setText(String.valueOf(t.getFrecuenciaHorasControles()));

        if (t.getHoraPrimerControl() != null) {
            LocalTime h = t.getHoraPrimerControl();
            panelTratamiento.getHoraCombo().setSelectedItem(String.format("%02d", h.getHour()));
            panelTratamiento.getMinCombo().setSelectedItem(String.format("%02d", h.getMinute()));
        }
    }

    panelTratamiento.getVolverBtn().addActionListener(e -> {
        ventana.mostrarPanel(panelCuidador);
        listarPacientes();
    });

    panelTratamiento.getEditarBtn().addActionListener(e -> {
        abrirTratamientoParaEdicion(p);
    });

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
    JCheckBox chkSos   = panelTratamiento.getSosCheck();

    panelTratamiento.getNombreTxt().setText(p.getNombre());
    panelTratamiento.getRutTxt().setText(p.getRut());
    panelTratamiento.getNombreTxt().setEditable(false);
    panelTratamiento.getRutTxt().setEditable(false);

    if (p.getTratamiento() != null) {
        Tratamiento t = p.getTratamiento();

        panelTratamiento.getDietaTxt().setText(t.getDietaRecomendada());
        panelTratamiento.getMedsTxt().setText(t.getMedicamentosOrales());

        chkSos.setSelected(t.isUsaInsulinaCristalinaSOS());
        panelTratamiento.habilitarPautaSOS(chkSos.isSelected());
        panelTratamiento.getPautaSosTxt().setText(
            t.getPautaInsulinaSOS() != null ? t.getPautaInsulinaSOS() : ""
        );

        chkLenta.setSelected(t.isUsaInsulinaLentaDiaria());
        panelTratamiento.getDosisTxt().setText(String.valueOf(t.getDosisInsulinaLentaDiaria()));
        panelTratamiento.getFreqTxt().setText(String.valueOf(t.getFrecuenciaHorasControles()));

        if (t.getHoraPrimerControl() != null) {
            LocalTime h = t.getHoraPrimerControl();
            panelTratamiento.getHoraCombo().setSelectedItem(String.format("%02d", h.getHour()));
            panelTratamiento.getMinCombo().setSelectedItem(String.format("%02d", h.getMinute()));
        }
    }

    panelTratamiento.habilitarDosisLenta(chkLenta.isSelected());

    chkLenta.addActionListener(e -> {
        boolean activo = chkLenta.isSelected();
        panelTratamiento.habilitarDosisLenta(activo);
        if (!activo) panelTratamiento.limpiarDosis();
    });

    chkSos.addActionListener(e -> {
        boolean activo = chkSos.isSelected();
        panelTratamiento.habilitarPautaSOS(activo);
        if (!activo) panelTratamiento.getPautaSosTxt().setText("");
    });

    panelTratamiento.getSaveTtoBtn().addActionListener(e -> {

        String dieta = panelTratamiento.getDietaTxt().getText().trim();
        String meds  = panelTratamiento.getMedsTxt().getText().trim();

        boolean usaSos   = chkSos.isSelected();
        boolean usaLenta = chkLenta.isSelected();

        String pautaSOS = usaSos
                ? panelTratamiento.getPautaSosTxt().getText().trim()
                : "";

        int dosisLenta = 0;
        if (usaLenta) {
            try {
                dosisLenta = Integer.parseInt(panelTratamiento.getDosisTxt().getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ventana, "Dosis inválida.");
                return;
            }

            if (!Utilidades.estaEnRango(dosisLenta, 1, 80)) {
                JOptionPane.showMessageDialog(ventana, "Dosis fuera de rango.");
                return;
            }
        }

        int frecuencia;
        try {
            frecuencia = Integer.parseInt(panelTratamiento.getFreqTxt().getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "Frecuencia inválida.");
            return;
        }

        if (!Utilidades.esFrecuenciaValida(frecuencia)) {
            JOptionPane.showMessageDialog(ventana, "Frecuencia fuera de rango.");
            return;
        }

        String hSel = (String) panelTratamiento.getHoraCombo().getSelectedItem();
        String mSel = (String) panelTratamiento.getMinCombo().getSelectedItem();
        LocalTime horaPrimerControl =
            LocalTime.of(Integer.parseInt(hSel), Integer.parseInt(mSel));

        String frecInsulina =
            (String) panelTratamiento.getComboFreqInsulinaLenta().getSelectedItem();

        Tratamiento nuevoT = new Tratamiento(
            dieta,
            meds,
            usaSos,
            usaLenta,
            dosisLenta,
            frecInsulina,
            frecuencia,
            horaPrimerControl,
            pautaSOS
        );

        try {
            p.setTratamiento(nuevoT);
            gestorPacientes.guardarTodo("pacientes.txt", "glicemias.txt", "alertas.txt");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ventana, "Error al guardar archivos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(ventana, "Tratamiento guardado correctamente.");
        ventana.mostrarPanel(panelCuidador);
        listarPacientes();
    });

    panelTratamiento.getVolverBtn()
        .addActionListener(e -> ventana.mostrarPanel(panelCuidador));

    ventana.mostrarPanel(panelTratamiento);
}




private void cargarAlertasPaciente(Paciente p) {

    DefaultTableModel modelo =
        (DefaultTableModel) panelCuidador.getAlertasTabla().getModel();

    modelo.setRowCount(0);

    if (p == null) {
        modelo.addRow(new Object[]{"-", "-", "Sin paciente seleccionado"});
        return;
    }

    for (Alerta a : p.getAlertas()) {
        String[] partes = a.getFechaHora().split(" ");
        String fecha = partes.length > 0 ? partes[0] : "-";
        String hora  = partes.length > 1 ? partes[1] : "-";

        modelo.addRow(new Object[]{
            fecha,
            hora,
            a.getTipo() + " - " + a.getMensaje()
        });
    }

    if (p.getProximoControl() != null) {

        DateTimeFormatter fFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter fHora  = DateTimeFormatter.ofPattern("HH:mm");

        modelo.addRow(new Object[]{
            p.getProximoControl().toLocalDate().format(fFecha),
            p.getProximoControl().toLocalTime().format(fHora),
            "PRÓXIMO CONTROL"
        });
    }
}




    /* private void cargarAlertasPaciente(Paciente p) {
        DefaultTableModel modelo =
            (DefaultTableModel) panelCuidador.getAlertasTabla().getModel();

        modelo.setRowCount(0);

        if (p.getAlertas() == null) return;

        for (Alerta a : p.getAlertas()) {
            modelo.addRow(new Object[]{
                a.getFechaHora(),
                a.getTipo(),
                a.getMensaje()
            });
        }*/

    private void eliminarCuidadorSeleccionado() {

    int fila = panelAdmin.getTabla().getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(ventana, "Seleccione un cuidador en la tabla.");
        return;
    }

    String nombreUsuario = (String) panelAdmin.getTabla().getValueAt(fila, 0);

    int resp = JOptionPane.showConfirmDialog(
        ventana,
        "¿Seguro que deseas eliminar al cuidador '" + nombreUsuario + "'?",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if (resp != JOptionPane.YES_OPTION) return;

    boolean eliminado = gestorUsuarios.eliminarCuidador(nombreUsuario);

    if (!eliminado) {
        JOptionPane.showMessageDialog(ventana, "No se pudo eliminar (¿no era cuidador?).");
        return;
    }

    try {
        gestorUsuarios.archivar("usuarios.txt");
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(
            ventana,
            "Se eliminó en memoria, pero falló al guardar en archivo.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    JOptionPane.showMessageDialog(ventana, "Cuidador eliminado.");
    listarCuidadores();
}

   
}

