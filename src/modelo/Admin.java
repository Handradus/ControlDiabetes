package modelo;

public class Admin extends Usuario {

    /*
Usuario con rol administrador encargado de la gestión de cuentas de cuidadores.
Separa las tareas administrativas del registro clínico del sistema
 */
    
    public Admin(String nombreUsuario, String password) {
        super(nombreUsuario, password);
    }

    @Override
    public String getRol() {
        return "Administrador";
    }

   
}
