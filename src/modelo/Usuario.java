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
    
    
    //Constructor

    public Usuario(String nombreUsuario, String password) {
        this.setNombreUsuario (nombreUsuario);
        this.setPassword(password);
    }
    
    
    //Setter
    public void setNombreUsuario(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()){
            this.nombreUsuario = nombre;
        }
    }
    
     public void setPassword(String password) {
        if (password != null && !password.trim().isEmpty()){
            this.password = password;
        }
    }
    
    //Getter
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public String getPassword() {
        return password;
    }
    
    public abstract String getRol();

    @Override
    public String toString() {
        return "Usuario{" + "nombreUsuario=" + nombreUsuario + ", password=" + password + '}';
    }
    
    
    //poendiente manejo de contraseñas

}
