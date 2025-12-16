package modelo;

public class RegistroGlicemia {
    
    
    /* Aqui se representa el registro de glicemia individual con el almacenaje del valor medido, así como la fecha en la que
    se registro y que cuidador realizó la medición*/
    private String fechaHora;
    private int valor;
    private String registrado;

    public RegistroGlicemia(String fechaHora, int valor, String registrado) {
        this.setFechaHora(fechaHora);
        this.setValor(valor);
        this.setRegistrado(registrado);
    }

    public void setRegistrado(String registrado) {
        if (!Utilidades.esTextoVacio(registrado)) {
            this.registrado = Utilidades.normalizarNombre(registrado);
        } else {
            this.registrado = "Desconocido";
        }
    }

    

    public void setFechaHora(String fechaHora) {
        if (!Utilidades.esTextoVacio(fechaHora)) {
            this.fechaHora = fechaHora;
        } else {
            this.fechaHora = "Sin fecha";
        }
    }


    public void setValor(int valor) {
        if (!Utilidades.esGlicemiaValida(valor)) {
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
