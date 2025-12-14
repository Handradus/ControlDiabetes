package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class GestorPacientes {

    private ArrayList<Paciente> listaPacientes;

    public GestorPacientes() {
        this.listaPacientes = new ArrayList<>();
    }

    public boolean agregarPaciente(Paciente p) {
        if (p == null) return false;
        if (buscarPorRut(p.getRut()) != null) return false;
        listaPacientes.add(p);
        return true;
    }

    public Paciente buscarPorRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) return null;
        for (Paciente p : listaPacientes) {
            if (p.getRut().equalsIgnoreCase(rut)) return p;
        }
        return null;
    }

    public ArrayList<Paciente> obtenerTodos() {
        return listaPacientes;
    }

    public void guardarPacientes(String archivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            for (Paciente p : listaPacientes) {
                Tratamiento t = p.getTratamiento();

                String dieta = (t != null) ? t.getDietaRecomendada() : "";
                String meds  = (t != null) ? t.getMedicamentosOrales() : "";
                boolean sos  = (t != null && t.isUsaInsulinaCristalinaSOS());
                boolean lenta = (t != null && t.isUsaInsulinaLentaDiaria());
                int dosis = (lenta && t != null) ? t.getDosisInsulinaLentaDiaria() : 0;
                int freq = (t != null) ? t.getFrecuenciaHorasControles() : 0;
                String frecIns = (t != null) ? t.getFrecInsulina() : "";
                String hora = (t != null && t.getHoraPrimerControl() != null)
                        ? t.getHoraPrimerControl().toString()
                        : "";
                String pauta = (t != null && t.getPautaInsulinaSOS() != null)
                        ? t.getPautaInsulinaSOS().replace(";", ",")
                        : "";

                bw.write(
                    p.getNombre() + ";" +
                    p.getRut() + ";" +
                    p.getEdad() + ";" +
                    p.getHabitacion() + ";" +
                    dieta + ";" +
                    meds + ";" +
                    sos + ";" +
                    lenta + ";" +
                    dosis + ";" +
                    freq + ";" +
                    frecIns + ";" +
                    hora + ";" +
                    pauta
                );
                bw.newLine();
            }
        }
    }

    public void cargarPacientes(String archivo) throws IOException {
        listaPacientes.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length < 4) continue;

                String nombre = p[0];
                String rut = p[1];
                int edad = Integer.parseInt(p[2]);
                String habitacion = p[3];

                String dieta = p.length > 4 ? p[4] : "";
                String meds  = p.length > 5 ? p[5] : "";
                boolean sos  = p.length > 6 && Boolean.parseBoolean(p[6]);
                boolean lenta = p.length > 7 && Boolean.parseBoolean(p[7]);
                int dosis = p.length > 8 ? Integer.parseInt(p[8]) : 0;
                int freq = p.length > 9 ? Integer.parseInt(p[9]) : 0;
                String frecIns = p.length > 10 ? p[10] : "";
                LocalTime hora = (p.length > 11 && !p[11].isBlank())
                        ? LocalTime.parse(p[11])
                        : null;
                String pauta = p.length > 12 ? p[12] : "";

                Tratamiento t = new Tratamiento(
                        dieta, meds, sos, lenta, dosis,
                        frecIns, freq, hora, pauta
                );

                Paciente pac = new Paciente(nombre, rut, edad, habitacion, t);
                pac.recalcularProximoControl();
                listaPacientes.add(pac);
            }
        }
    }

    public void guardarGlicemias(String archivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
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

    public void cargarGlicemias(String archivo) throws IOException {
        for (Paciente p : listaPacientes) {
            p.getHistorialGlicemias().clear();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length < 4) continue;

                Paciente pac = buscarPorRut(p[0]);
                if (pac != null) {
                    pac.agregarRegistroGlicemia(
                        new RegistroGlicemia(p[1], Integer.parseInt(p[2]), p[3])
                    );
                }
            }
        }
    }

    public void guardarAlertas(String archivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            for (Paciente p : listaPacientes) {
                for (Alerta a : p.getAlertas()) {
                    bw.write(
                        p.getRut() + ";" +
                        a.getFechaHora() + ";" +
                        a.getTipo() + ";" +
                        a.getMensaje() + ";" +
                        a.isAtendida()
                    );
                    bw.newLine();
                }
            }
        }
    }

    public void cargarAlertas(String archivo) throws IOException {
        for (Paciente p : listaPacientes) {
            p.getAlertas().clear();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";", 5);
                if (p.length < 5) continue;

                Paciente pac = buscarPorRut(p[0]);
                if (pac != null) {
                    Alerta a = new Alerta(p[2], p[3], pac);
                    a.setAtendida(Boolean.parseBoolean(p[4]));
                    pac.agregarAlerta(a);
                }
            }
        }
    }

    public void guardarTodo(String pac, String glic, String alert) throws IOException {
        guardarPacientes(pac);
        guardarGlicemias(glic);
        guardarAlertas(alert);
    }

    public void cargarTodo(String pac, String glic, String alert) throws IOException {
        cargarPacientes(pac);
        cargarGlicemias(glic);
        cargarAlertas(alert);
    }
}
