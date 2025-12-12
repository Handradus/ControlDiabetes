/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
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

    
    public void guardarPacientes(String nombreArchivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Paciente p : listaPacientes) {
                Tratamiento t = p.getTratamiento();

                String dieta = (t != null && t.getDietaRecomendada() != null) ? t.getDietaRecomendada() : "";
                String meds = (t != null && t.getMedicamentosOrales() != null) ? t.getMedicamentosOrales() : "";
                boolean sos = (t != null && t.isUsaInsulinaCristalinaSOS());
                boolean lenta = (t != null && t.isUsaInsulinaLentaDiaria());
                int dosisLenta = (t != null && t.isUsaInsulinaLentaDiaria())
                                                                            ? t.getDosisInsulinaLentaDiaria()
                                                                            : 0;

                int freq = (t != null) ? t.getFrecuenciaHorasControles() : 0;

                String horaPrimera = "";
                if (t != null && t.getHoraPrimerControl() != null) {
                    horaPrimera = t.getHoraPrimerControl().toString();
                }

                bw.write(
                    p.getNombre() + ";" +
                    p.getRut() + ";" +
                    p.getEdad() + ";" +
                    p.getHabitacion() + ";" +
                    dieta + ";" +
                    meds + ";" +
                    sos + ";" +
                    lenta + ";" +
                    dosisLenta + ";" +
                    freq + ";" +
                    horaPrimera
                );
                bw.newLine();
            }
        }
    }


    public void cargarPacientes(String nombreArchivo) throws IOException {
        listaPacientes.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length < 4) continue;

                String nombre = partes[0];
                String rut = partes[1];
                int edad = Integer.parseInt(partes[2]);
                String habitacion = partes[3];

                String dieta = partes.length > 4 ? partes[4] : "";
                String medicamento = partes.length > 5 ? partes[5] : "";
                boolean sos = partes.length > 6 && Boolean.parseBoolean(partes[6]);
                boolean lenta = partes.length > 7 && Boolean.parseBoolean(partes[7]);
                int dosisLenta = partes.length > 8 ? Integer.parseInt(partes[8]) : 0;
                if (!lenta) {
                        dosisLenta = 0;
                    }
                int freq = partes.length > 9 ? Integer.parseInt(partes[9]) : 0;

                LocalTime horaPrimera = null;
                if (partes.length > 10 && !partes[10].isBlank()) {
                    horaPrimera = LocalTime.parse(partes[10]);
                }

                Tratamiento t = new Tratamiento(
                        dieta,
                        medicamento,
                        sos,
                        lenta,
                        dosisLenta,
                        freq,
                        horaPrimera
                );

                Paciente p = new Paciente(nombre, rut, edad, habitacion, t);

                p.getHorariosDosis().clear();

                if (freq > 0 && horaPrimera != null) {
                    LocalTime h = horaPrimera;

                    for (int i = 0; i < 24; i += freq) {
                        p.agregarHorarioDosis(new HorarioDosis(h, false, false));
                        h = h.plusHours(freq);
                        if (h.equals(horaPrimera)) break;
                    }
                }

                listaPacientes.add(p);
            }
        }
    }
    
    public void guardarGlicemias(String archivoGlicemias) throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoGlicemias))) {
        for (Paciente p : listaPacientes) {
            for (RegistroGlicemia g : p.getHistorialGlicemias()) {
                bw.write(
                    p.getRut() + ";" +
                    g.getFechaHora() + ";" +
                    g.getValor() + ";" +
                    g.getRegistrado()
                );
                bw.newLine();
            }
        }
    }
}
    
    public void cargarGlicemias(String archivoGlicemias) throws IOException {

    // Limpia para no duplicar si recargas
    for (Paciente p : listaPacientes) {
        p.getHistorialGlicemias().clear();
    }

    try (BufferedReader br = new BufferedReader(new FileReader(archivoGlicemias))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] partes = linea.split(";", -1);
            if (partes.length < 4) continue;

            String rut = partes[0];
            String fechaHora = partes[1];
            int valor = Integer.parseInt(partes[2]);
            String registrado = partes[3];

            Paciente p = buscarPorRut(rut);
            if (p != null) {
                p.agregarRegistroGlicemia(new RegistroGlicemia(fechaHora, valor, registrado));
            }
        }
    }
}

 public void guardarTodo(String archivoPacientes, String archivoGlicemias) throws IOException {
    guardarPacientes(archivoPacientes);
    guardarGlicemias(archivoGlicemias);
}
 
 public void cargarTodo(String archivoPacientes, String archivoGlicemias) throws IOException {
    cargarPacientes(archivoPacientes);
    cargarGlicemias(archivoGlicemias);
}

}
