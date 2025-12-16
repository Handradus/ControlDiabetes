package modelo;

public class Utilidades {

    
/*
Clase utilitaria que centraliza las validaciones y la normalización de datos utilizados en el sistema.
*/
 
    
//Si texto esta nulo o vacio    
    public static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    //Si es un texto con solo letras y ciertos espacios
    public static boolean esSoloLetras(String texto) {
        return texto != null && texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }

    //Aqui se deja el nombre con la primera letra de cada palabra en mayúscula
    public static String normalizarNombre(String nombre) {
        if (nombre == null) return "";
        nombre = nombre.trim().toLowerCase();
        String[] partes = nombre.split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String p : partes) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)))
              .append(p.substring(1))
              .append(" ");
        }
        return sb.toString().trim();
    }

    public static boolean esNumeroEntero(String texto) {
        try {
            Integer.parseInt(texto);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esNumeroPositivo(String texto) {
        try {
            return Integer.parseInt(texto) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean estaEnRango(int valor, int min, int max) {
        return valor >= min && valor <= max;
    }

    public static boolean esGlicemiaValida(int valor) {
        return valor >= 20 && valor <= 600;
    }

    //En este metodo se eliminan los puntos y los guiones de un RUT
    public static String limpiarRut(String rut) {
        if (rut == null) return "";
        return rut.replace(".", "").replace("-", "").toUpperCase();
    }

   //Si es valido el RUT chileno y se agregan los puntos y guión respectivo 
    public static boolean esRutValido(String rut) {
        rut = limpiarRut(rut);
        if (rut.length() < 8) return false;

        try {
            String cuerpo = rut.substring(0, rut.length() - 1);
            char dv = rut.charAt(rut.length() - 1);

            int suma = 0;
            int multiplo = 2;

            for (int i = cuerpo.length() - 1; i >= 0; i--) {
                suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplo;
                multiplo = multiplo == 7 ? 2 : multiplo + 1;
            }

            int resto = 11 - (suma % 11);
            char dvEsperado;

            if (resto == 11) dvEsperado = '0';
            else if (resto == 10) dvEsperado = 'K';
            else dvEsperado = Character.forDigit(resto, 10);

            return dv == dvEsperado;

        } catch (Exception e) {
            return false;
        }
    }

    public static String formatearRut(String rut) {
        rut = limpiarRut(rut);
        if (rut.length() < 2) return rut;

        String cuerpo = rut.substring(0, rut.length() - 1);
        char dv = rut.charAt(rut.length() - 1);

        StringBuilder sb = new StringBuilder(cuerpo);
        int i = sb.length() - 3;

        while (i > 0) {
            sb.insert(i, ".");
            i -= 3;
        }

        return sb + "-" + dv;
    }

    public static boolean esHoraValida(int hora, int minuto) {
        return hora >= 0 && hora <= 23 && minuto >= 0 && minuto <= 59;
    }

    //Verifica la frecuencia de horas y si esta en un rango permitido
    public static boolean esFrecuenciaValida(int horas) {
        return horas > 0 && horas <= 24;
    }
}
