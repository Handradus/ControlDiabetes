package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;



/*
Inicializa el gestor de usuarios del sistema. Se crea un usuario administrador por defecto
*/
public class GestorUsuarios {

    private ArrayList<Usuario> usuarios;

    public GestorUsuarios() {
        this.usuarios = new ArrayList<>();
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

    //Verifica si ya existe un usuario con el nombre que estya indicado, así se evita la duplicación de cuentas en el sistema
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
    

    public boolean eliminarCuidador(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) return false;

        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);

            // Solo cuidadores se pueden eliminar
            if (u instanceof Cuidador && u.getNombreUsuario().equals(nombreUsuario)) {
                usuarios.remove(i);
                return true;
            }
        }
        return false;
    }


    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    
    //Guarda los usuarios del sistema en un archivo de texto, incluyendo credenciales y rol asignado, luego se cargan los usuarios
    // desde un archivo de texto que reconstruye las cuetnas según su rol
    
    
    
    public void archivar(String nombreArchivo) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))){
            for(Usuario u : usuarios){
                String rol = u.getRol();
                if (u instanceof Admin) {
                rol = "ADMIN";
            } else {
                rol = "CUIDADOR";
            }

                bw.write(u.getNombreUsuario() + ";" + u.getPassword() + ";" + rol);
                bw.newLine();
            }
        }
    }
    
    public void cargarUsuarios(String nombreArchivo) throws FileNotFoundException, IOException{
        java.io.File f = new java.io.File(nombreArchivo);
        
        if (!f.exists()) {
        System.out.println("Archivo de usuarios no existe, uso solo admin por defecto.");
        return;
    }
        this.usuarios.clear();        
        try(BufferedReader bf = new BufferedReader(new FileReader(nombreArchivo))){
            
            String linea;
            while((linea = bf.readLine()) != null){
                String[] partes = linea.split(";");
                String nombre = partes[0];
                String pass   = partes[1];
                String rol    = partes[2];
                Usuario u;
            if ("ADMIN".equalsIgnoreCase(rol)) {
                u = new Admin(nombre, pass);
            } else {
                u = new Cuidador(nombre, pass);
            }
                usuarios.add(u);
            }
        }
        
         if (usuarios.isEmpty()) {
        usuarios.add(new Admin("admin", "1234"));
    }
    }
}
