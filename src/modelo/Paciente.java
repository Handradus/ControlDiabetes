package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

// Este es el paciente del sistema que tiene la información personal y el tratamiento que tienen asignados, sumando igual el
// historial de glicemias y las alertas que tiene asociadas
public class Paciente {

    private String nombre;
    private String rut;
    private int edad;
    private String habitacion;
    private LocalDateTime proximoControl;

    private boolean activo;
    private Tratamiento tratamiento;

    private ArrayList<RegistroGlicemia> historialGlicemias;
    private ArrayList<HorarioDosis> horariosDosis;
    private ArrayList<Alerta> alertas;

    public Paciente(String nombre, String rut, int edad, String habitacion, Tratamiento tratamiento) {
        this.historialGlicemias = new ArrayList<>();
        this.horariosDosis = new ArrayList<>();
        this.alertas = new ArrayList<>();

        this.activo = true;

        this.setNombre(nombre);
        this.setRut(rut);
        this.setEdad(edad);
        this.setHabitacion(habitacion);
        this.setTratamiento(tratamiento);
    }

    public void setNombre(String nombre) {
        if (!Utilidades.esTextoVacio(nombre) && Utilidades.esSoloLetras(nombre)) {
            this.nombre = Utilidades.normalizarNombre(nombre);
        }
    }

    public void setRut(String rut) {
        if (!Utilidades.esTextoVacio(rut) && Utilidades.esRutValido(rut)) {
            this.rut = Utilidades.formatearRut(rut);
        }
    }

    public void setHabitacion(String habitacion) {
        if (!Utilidades.esTextoVacio(habitacion)) {
            this.habitacion = habitacion.trim().toUpperCase();
        }
    }

    public void setEdad(int edad) {
        if (Utilidades.estaEnRango(edad, 0, 120)) {
            this.edad = edad;
        }
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
        if (tratamiento != null) {
            recalcularProximoControl();
        } else {
            proximoControl = null;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getRut() {
        return rut;
    }

    public int getEdad() {
        return edad;
    }

    public String getHabitacion() {
        return habitacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public LocalDateTime getProximoControl() {
        return proximoControl;
    }

    public ArrayList<RegistroGlicemia> getHistorialGlicemias() {
        return historialGlicemias;
    }

    public ArrayList<HorarioDosis> getHorariosDosis() {
        return horariosDosis;
    }

    public ArrayList<Alerta> getAlertas() {
        return alertas;
    }

    public void agregarRegistroGlicemia(RegistroGlicemia registro) {
        if (registro == null) return;

        this.historialGlicemias.add(registro);
        generarAlertaPorGlicemia(registro);
        avanzarProximoControlTrasRegistro();
    }

    public void agregarHorarioDosis(HorarioDosis horario) {
        if (horario != null) {
            this.horariosDosis.add(horario);
        }
    }

    public void agregarAlerta(Alerta alerta) {
        if (alerta != null) {
            this.alertas.add(alerta);
        }
    }
    
    //calculo de criticidad por glicemias

    public void generarAlertaPorGlicemia(RegistroGlicemia r) {

        if (r == null) return;

        int valor = r.getValor();
        Alerta alerta = null;

        if (valor < 70) {
            alerta = new Alerta(
                "HIPOGLICEMIA",
                "Glicemia baja (" + valor + ")",
                this
            );
        } else if (valor > 300) {
            alerta = new Alerta(
                "CRITICA",
                "Glicemia crítica (" + valor + ")",
                this
            );
        } else if (valor > 180) {
            alerta = new Alerta(
                "HIPERGLICEMIA",
                "Glicemia elevada (" + valor + ")",
                this
            );
        }

        if (alerta != null) {
            this.alertas.add(alerta);
        }
    }

    //Caluclo inicial de control de glicemia
    public void recalcularProximoControl() {

        if (tratamiento == null) {
            proximoControl = null;
            return;
        }

        int freq = tratamiento.getFrecuenciaHorasControles();
        LocalTime primer = tratamiento.getHoraPrimerControl();

        if (freq <= 0 || primer == null) {
            proximoControl = null;
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();

        LocalDateTime candidato = LocalDateTime.of(hoy, primer);

        while (candidato.isBefore(ahora)) {
            candidato = candidato.plusHours(freq);
        }

        proximoControl = candidato;
    }
    
    //funcion para calculo de siguiente control al ingresar un control de glicemia

    public void avanzarProximoControlTrasRegistro() {

        if (tratamiento == null) return;

        int freq = tratamiento.getFrecuenciaHorasControles();
        LocalTime primer = tratamiento.getHoraPrimerControl();

        if (freq <= 0 || primer == null) return;

        if (proximoControl == null) {
            recalcularProximoControl();
            return;
        }

        proximoControl = proximoControl.plusHours(freq);

        LocalDateTime ahora = LocalDateTime.now();
        while (!proximoControl.isAfter(ahora)) {

            /*asegura que el próximo control siempre quede programado en una fecha futura */
            proximoControl = proximoControl.plusHours(freq);
        }
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "nombre=" + nombre +
                ", rut=" + rut +
                ", edad=" + edad +
                ", habitacion=" + habitacion +
                ", activo=" + activo +
                ", tratamiento=" + tratamiento +
                ", historialGlicemias=" + historialGlicemias +
                ", alertas=" + alertas +
                '}';
    }
}
