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


public class GestorPacientes {

    private ArrayList<Paciente> listaPacientes;

    public GestorPacientes() {
        this.listaPacientes = new ArrayList<>();
    }

    public boolean agregarPaciente(Paciente p) {
        if (p == null) return false;

        // evitar duplicados por RUT
        if (buscarPorRut(p.getRut()) != null) {
            System.err.println("Ya existe un paciente con ese RUT.");
            return false;
        }

        listaPacientes.add(p);
        return true;
    }

    public Paciente buscarPorRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) return null;

        for (Paciente p : listaPacientes) {
            if (p.getRut().equalsIgnoreCase(rut)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Paciente> obtenerTodos() {
        return listaPacientes;
    }

    public boolean eliminarPaciente(String rut) {
        Paciente p = buscarPorRut(rut);
        if (p != null) {
            listaPacientes.remove(p);
            return true;
        }
        return false;
    }
}
