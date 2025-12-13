package modelo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

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
        this.setNombre(nombre);
        this.setRut(rut);
        this.setEdad(edad);
        this.setHabitacion(habitacion);
        this.setTratamiento(tratamiento);

        this.activo = true;
        this.historialGlicemias = new ArrayList<>();
        this.horariosDosis = new ArrayList<>();
        this.alertas = new ArrayList<>();
    }

    public void setNombre(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()){
            this.nombre = nombre;
        }
    }
    
    

    public void setRut(String rut) {
        if (rut != null && !rut.trim().isEmpty()){
            this.rut = rut;
        }
    }

    public void setHabitacion(String habitacion) {
        if (habitacion != null && !habitacion.trim().isEmpty()){
            this.habitacion = habitacion;
        }
    }

   public void setTratamiento(Tratamiento tratamiento) {
    this.tratamiento = tratamiento;
    if (tratamiento != null) {
        recalcularProximoControl();
    } else {
        proximoControl = null;
    }
}



    public void setEdad(int edad) {

        if (edad < 0) {
            System.err.println("Error: La edad no puede ser un número negativo.");
            return;
        }

        if (edad < 18) {
            System.err.println("Advertencia: El usuario es menor de edad.");
        }

        if (edad > 120) {
            System.err.println("Error: La edad ingresada (" + edad + ") es irrealmente alta.");
            return;
        }

        this.edad = edad;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // ===== Getters =====

    public String getNombre() {
        return nombre;
    }

    public String getRut() {
        return rut;
    }

    public LocalDateTime getProximoControl() {
        return proximoControl;
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

    public ArrayList<RegistroGlicemia> getHistorialGlicemias() {
        return historialGlicemias;
    }

    public ArrayList<HorarioDosis> getHorariosDosis() {
        return horariosDosis;
    }

    public ArrayList<Alerta> getAlertas() {
        return alertas;
    }

    // ===== Métodos =====

    public void agregarRegistroGlicemia(RegistroGlicemia registro) {
        if (registro != null) {
            this.historialGlicemias.add(registro);
        }
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

    
    while (!candidato.isAfter(ahora)) {
        candidato = candidato.plusHours(freq);
    }

    proximoControl = candidato;
    }
    
    
    
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

    // por si estaba atrasado (ej: el sistema estuvo cerrado), empújalo al futuro
    LocalDateTime ahora = LocalDateTime.now();
    while (!proximoControl.isAfter(ahora)) {
        proximoControl = proximoControl.plusHours(freq);
    }
}
    
    
    


}
