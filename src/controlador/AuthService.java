package controlador;

import modelo.Usuario;
import modelo.Admin;
import modelo.Cuidador;
import modelo.GestorUsuarios;


public class AuthService {

    private GestorUsuarios gestorUsuarios;

    public AuthService(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;
    }

    public Usuario autenticar(String nombreUsuario, String password) throws Exception {
        Usuario u = gestorUsuarios.login(nombreUsuario, password);
        if (u == null) {
            throw new Exception("Usuario o contraseña incorrectos");
        }
        return u;
    }
}