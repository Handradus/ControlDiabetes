package modelo;

public class RegistroGlicemia {

    private String fechaHora;
    private int valor;

    public RegistroGlicemia(String fechaHora, int valor) {
        this.setFechaHora(fechaHora);
        this.setValor(valor);
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

    public int getValor() {
        return valor;
    }
}
