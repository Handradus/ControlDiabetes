package modelo;

public class Cuidador extends Usuario {

   

    public Cuidador(String nombreUsuario, String password) {
        super(nombreUsuario, password);
    }

    @Override
    public String getRol() {
        return "Cuidador";
    }

    
}

