/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.util.ArrayList;

/**
 *
 * @author Diavuru
 */
public class Paciente {
    private String nombre;
    private String rut;
    private int edad;
    private String habitacion;

    private Tratamiento tratamiento;  
    private ArrayList<RegistroGlicemia> historialGlicemias;
     private ArrayList<HorarioDosis> horariosDosis;

    public Paciente(String nombre, String rut, int edad, String habitacion, Tratamiento tratamiento) {
        this.setNombre(nombre);
        this.setRut(rut);
        this.setEdad(edad);
        this.setHabitacion(habitacion);
        this.setTratamiento(tratamiento);
        this.historialGlicemias = new ArrayList<>();
        this.horariosDosis = new ArrayList<>();

        
    }
    
    //setter
    
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
    
        if (tratamiento != null) {
            this.tratamiento = tratamiento;
        } else {

            System.err.println("Error: No se puede asignar un tratamiento nulo al paciente.");
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
    //Getters

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

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public ArrayList<RegistroGlicemia> getHistorialGlicemias() {
    return historialGlicemias;
    }
    public ArrayList<HorarioDosis> getHorariosDosis() {
        return horariosDosis;
    }
    //Metodos

    @Override
    public String toString() {
        return "Paciente{" + "nombre=" + nombre + ", rut=" + rut + ", edad=" + edad + ", habitacion=" + habitacion + ", tratamiento=" + tratamiento + ", historialGlicemias=" + historialGlicemias + '}';
    }
    
    public void agregarRegistroGlicemia(RegistroGlicemia registro) {
    this.historialGlicemias.add(registro);
    }
    
    public void agregarHorarioDosis(HorarioDosis horario) {
        this.horariosDosis.add(horario);
    }
}
