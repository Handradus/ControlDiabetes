package modelo;

public class Admin extends Usuario {

    public Admin(String nombreUsuario, String password) {
        super(nombreUsuario, password);
    }

    @Override
    public String getRol() {
        return "Administrador";
    }

   
}