package vista;

import java.util.Scanner;
import modelo.Paciente;
import modelo.Usuario;
import java.util.ArrayList;

public class ConsolaVista {

    private Scanner scanner;

    public ConsolaVista() {
        scanner = new Scanner(System.in);
    }

    
    
    
    public String leerTexto(String mensaje)
    {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    public int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (!scanner.hasNextInt())
        {
            System.out.print("Ingrese un número válido: ");
            scanner.next();
        }
        
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
        
    }

    public String leerRutPaciente()
    {
        System.out.print("Ingrese RUT del paciente: ");
        return scanner.nextLine();
    }    

    public int leerGlicemia() {
        System.out.print("Valor de glicemia: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Ingrese un número válido: ");
            scanner.next();
        }
        
        
        int valor = scanner.nextInt();
        scanner.nextLine();
        
        return valor;
    }

    
    
    
    public void mostrarMensaje(String msg) {
        System.out.println(msg);
    }
    
    public void mostrarPacientes(ArrayList<Paciente> pacientes) {
        System.out.println("\nLista de pacientes:");
        
        for (int i = 0; i < pacientes.size(); i++)
        {
            Paciente p = pacientes.get(i);
            
            System.out.println(p.getNombre() + " - " + p.getRut());
        }
    }

    public void mostrarUsuarios(ArrayList<Usuario> usuarios) {
        System.out.println("\nLista de usuarios:");
        
        for (int i = 0; i < usuarios.size(); i++)
        {
            Usuario u = usuarios.get(i);
            
            System.out.println(u.getNombreUsuario() + " - " + u.getRol());
        }
    }
}
