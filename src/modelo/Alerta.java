package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Alerta {

    private String tipo;
    private String mensaje;
    private String fechaHora;
    private Paciente paciente;
    private boolean atendida;

    public Alerta(String tipo, String mensaje, Paciente paciente) {
        this.setTipo(tipo);
        this.setMensaje(mensaje);
        this.setPaciente(paciente);
        this.setAtendida(false);

        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.fechaHora = ahora.format(f);
    }

    public void setTipo(String tipo) {
        if (tipo != null && !tipo.trim().isEmpty()) {
            this.tipo = tipo;
        } else {
            this.tipo = "GENERAL";
        }
    }

    public void setMensaje(String mensaje) {
        if (mensaje != null && !mensaje.trim().isEmpty()) {
            this.mensaje = mensaje;
        } else {
            this.mensaje = "Sin detalle";
        }
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setAtendida(boolean atendida) {
        this.atendida = atendida;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public boolean isAtendida() {
        return atendida;
    }

    @Override
    public String toString() {
        String nombre = "Sin paciente";
        if (paciente != null) {
            nombre = paciente.getNombre();
        }
        return "[" + fechaHora + "] (" + tipo + ") " + nombre + ": " + mensaje;
    }
}
