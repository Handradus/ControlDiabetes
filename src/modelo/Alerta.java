package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/*
Alerta clínica generada automáticamente a partir de registros de glicemia fuera de los rangos normales. 
Quedan registradas para el seguimiento
 */

public class Alerta {

    private String tipo;
    private String mensaje;
    private String fechaHora;
    private boolean atendida;
    private Paciente paciente;

    public Alerta(String tipo, String mensaje, Paciente paciente) {
        this.setTipo(tipo);
        this.setMensaje(mensaje);
        this.setPaciente(paciente);
        this.atendida = false;

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

    public boolean isAtendida() {
        return atendida;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    @Override
    public String toString() {
        String nombre = paciente != null ? paciente.getNombre() : "Sin paciente";
        return "[" + fechaHora + "] (" + tipo + ") " + nombre + ": " + mensaje;
    }
}
