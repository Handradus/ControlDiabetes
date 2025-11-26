/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.util.ArrayList;

public class GestorUsuarios {

    private ArrayList<Usuario> usuarios;

    public GestorUsuarios() {
        this.usuarios = new ArrayList<>();

        // Crear admin por defecto
        Admin admin = new Admin("admin", "1234");
        usuarios.add(admin);
    }

    public Usuario login(String nombreUsuario, String password) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)
                    && u.passwordValida(password)) {
                return u;
            }
        }
        return null;
    }

    public boolean existeUsuario(String nombreUsuario) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return true;
            }
        }
        return false;
    }

    public boolean agregarCuidador(String nombreUsuario, String password) {
        if (existeUsuario(nombreUsuario)) {
            System.err.println("Ya existe un usuario con ese nombre.");
            return false;
        }

        Cuidador nuevo = new Cuidador(nombreUsuario, password);
        usuarios.add(nuevo);
        return true;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }
}
