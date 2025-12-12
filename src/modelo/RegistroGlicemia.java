package modelo;

public class RegistroGlicemia {

    private String fechaHora;
    private int valor;
    private String registrado;

    public RegistroGlicemia(String fechaHora, int valor, String registrado) {
        this.setFechaHora(fechaHora);
        this.setValor(valor);
        this.setRegistrado(registrado);
    }

    public void setRegistrado(String registrado) {
        this.registrado = registrado;
    }
    
    

    public void setFechaHora(String fechaHora)
    {
        
        
        if (fechaHora != null && !fechaHora.trim().isEmpty()) {
            this.fechaHora = fechaHora;
        } else {
            this.fechaHora = "Sin fecha";
        }
    }

    public void setValor(int valor)
    {
        if (valor < 0) {
            this.valor = 0;
            return;
        }
        this.valor = valor;
    }

    public String getFechaHora(){
        return fechaHora;
    }

    public String getRegistrado() {
        return registrado;
    }
    

    public int getValor() {
        return valor;
    }
}
