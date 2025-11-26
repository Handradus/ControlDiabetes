/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Diavuru
 */

public abstract class Usuario {
    private String nombreUsuario;
    private String password;
    
    
    // Constructor
    public Usuario(String nombreUsuario, String password) {
        this.setNombreUsuario(nombreUsuario);
        this.setPassword(password);
    }
    
    // Setters
    public void setNombreUsuario(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()){
            this.nombreUsuario = nombre;
        } else {
            System.err.println("Error: el nombre de usuario no puede estar vacío.");
        }
    }
    
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            System.err.println("Error: la contraseña no puede estar vacía.");
            return;
        }
        if (password.length() < 4) {
            System.err.println("Error: la contraseña debe tener al menos 4 caracteres.");
            return;
        }
        this.password = password;
    }
    
    // Getters
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public String getPassword() {
        return password;
    }
    
    // validador de contrasela
    public boolean passwordValida(String passwordIngresada) {
        return this.password != null && this.password.equals(passwordIngresada);
    }

    
    public abstract String getRol();

    @Override
    public String toString() {
        return "Usuario{" + 
                "nombreUsuario='" + nombreUsuario + '\'' + 
                ", password='********'" +  
                '}';
    }
    
}
