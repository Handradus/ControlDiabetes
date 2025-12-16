package modelo;

public class Cuidador extends Usuario {

   
/*
Usuario con rol cuidador encargado de la gestión diaria de los pacientes.
Permite registrar glicemias, administrar tratamientos y revisar alertas clínicas.
*/

    public Cuidador(String nombreUsuario, String password) {
        super(nombreUsuario, password);
    }

    @Override
    public String getRol() {
        return "Cuidador";
    }

    
}

