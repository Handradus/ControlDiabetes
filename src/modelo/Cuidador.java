/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Diavuru
 */

public class Cuidador extends Usuario {

   

    public Cuidador(String nombreUsuario, String password) {
        super(nombreUsuario, password);
    }

    @Override
    public String getRol() {
        return "Cuidador";
    }

    
}

