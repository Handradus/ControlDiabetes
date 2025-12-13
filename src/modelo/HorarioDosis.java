package modelo;

import java.time.LocalTime;

public class HorarioDosis {
    private LocalTime hora;
    private boolean administrada;
    private boolean alertaActiva;

    public HorarioDosis(LocalTime hora, boolean administrada, boolean alertaActiva) {
        this.setHora (hora);
        this.setAdministrada (administrada);
        this.setAlertaActiva (alertaActiva);
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setAdministrada(boolean administrada) {
        this.administrada = administrada;
    }

    public void setAlertaActiva(boolean alertaActiva) {
        this.alertaActiva = alertaActiva;
    }

    public LocalTime getHora() {
        return hora;
    }

    public boolean isAdministrada() {
        return administrada;
    }

    public boolean isAlertaActiva() {
        return alertaActiva;
    }
    
    
}
