/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalTime;

/**
 *
 * @author Diavuru
 */
public class HorarioDosis {
    private LocalTime hora;          // 08:00, 14:00, 20:00
    private boolean administrada;    // ¿ya se puso la dosis de hoy?
    private boolean alertaActiva;    // para saber si ya se disparó una alerta persistente

    // constructor

    public HorarioDosis(LocalTime hora, boolean administrada, boolean alertaActiva) {
        this.setHora (hora);
        this.setAdministrada (administrada);
        this.setAlertaActiva (alertaActiva);
    }
    
    //SEtters

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setAdministrada(boolean administrada) {
        this.administrada = administrada;
    }

    public void setAlertaActiva(boolean alertaActiva) {
        this.alertaActiva = alertaActiva;
    }
    
    
    //Getters

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
