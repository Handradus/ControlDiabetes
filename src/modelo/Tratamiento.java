package modelo;

import java.time.LocalTime;

public class Tratamiento {

    private String dietaRecomendada;
    private String medicamentosOrales; 
    private boolean usaInsulinaCristalinaSOS;
    private boolean usaInsulinaLentaDiaria;
    private int dosisInsulinaLentaDiaria; 
    private String frecInsulina;
    private String pautaInsulinaSOS;

    private int frecuenciaHorasControles;
    private LocalTime horaPrimerControl;

    public Tratamiento(
            String dietaRecomendada,
            String medicamentosOrales,
            boolean usaInsulinaCristalinaSOS,
            boolean usaInsulinaLentaDiaria,
            int dosisInsulinaLentaDiaria,
            String frecInsulina,
            int frecuenciaHorasControles,
            LocalTime horaPrimerControl,
            String pautaInsulinaSOS
    ) {
        this.setDietaRecomendada(dietaRecomendada);
        this.setMedicamentosOrales(medicamentosOrales);
        this.setUsaInsulinaCristalinaSOS(usaInsulinaCristalinaSOS);
        this.setUsaInsulinaLentaDiaria(usaInsulinaLentaDiaria);
        this.setDosisInsulinaLentaDiaria(dosisInsulinaLentaDiaria);
        this.setFrecInsulina(frecInsulina);
        this.setFrecuenciaHorasControles(frecuenciaHorasControles);
        this.setHoraPrimerControl(horaPrimerControl);
        this.setPautaInsulinaSOS(pautaInsulinaSOS);
        validarCoherencia();
    }

    private void validarCoherencia() {

        if (!usaInsulinaLentaDiaria) {
            dosisInsulinaLentaDiaria = 0;
            frecInsulina = "";
        }

        if (!usaInsulinaCristalinaSOS) {
            pautaInsulinaSOS = "";
        }

        if (frecuenciaHorasControles > 0 && horaPrimerControl == null) {
            frecuenciaHorasControles = 0;
        }

        if (horaPrimerControl != null && frecuenciaHorasControles <= 0) {
            horaPrimerControl = null;
        }
    }

    public void setDietaRecomendada(String dietaRecomendada) {
        this.dietaRecomendada = Utilidades.esTextoVacio(dietaRecomendada)
                ? ""
                : dietaRecomendada.trim();
    }

    public void setMedicamentosOrales(String medicamentosOrales) {
        this.medicamentosOrales = Utilidades.esTextoVacio(medicamentosOrales)
                ? ""
                : medicamentosOrales.trim();
    }

    public void setUsaInsulinaCristalinaSOS(boolean usaInsulinaCristalinaSOS) {
        this.usaInsulinaCristalinaSOS = usaInsulinaCristalinaSOS;
    }

    public void setUsaInsulinaLentaDiaria(boolean usaInsulinaLentaDiaria) {
        this.usaInsulinaLentaDiaria = usaInsulinaLentaDiaria;
    }

    public void setDosisInsulinaLentaDiaria(int dosisInsulinaLentaDiaria) {
        if (!Utilidades.estaEnRango(dosisInsulinaLentaDiaria, 0, 80)) {
            this.dosisInsulinaLentaDiaria = 0;
            return;
        }
        this.dosisInsulinaLentaDiaria = dosisInsulinaLentaDiaria;
    }

    public void setFrecInsulina(String frecInsulina) {
        this.frecInsulina = Utilidades.esTextoVacio(frecInsulina)
                ? ""
                : frecInsulina.trim();
    }

    public void setFrecuenciaHorasControles(int frecuenciaHorasControles) {
        if (!Utilidades.esFrecuenciaValida(frecuenciaHorasControles)) {
            this.frecuenciaHorasControles = 0;
            return;
        }
        this.frecuenciaHorasControles = frecuenciaHorasControles;
    }

    public void setHoraPrimerControl(LocalTime horaPrimerControl) {
        this.horaPrimerControl = horaPrimerControl;
    }

    public void setPautaInsulinaSOS(String pautaInsulinaSOS) {
        this.pautaInsulinaSOS = Utilidades.esTextoVacio(pautaInsulinaSOS)
                ? ""
                : pautaInsulinaSOS.trim();
    }

    public String getDietaRecomendada() {
        return dietaRecomendada;
    }

    public String getMedicamentosOrales() {
        return medicamentosOrales;
    }

    public boolean isUsaInsulinaCristalinaSOS() {
        return usaInsulinaCristalinaSOS;
    }

    public boolean isUsaInsulinaLentaDiaria() {
        return usaInsulinaLentaDiaria;
    }

    public int getDosisInsulinaLentaDiaria() {
        return dosisInsulinaLentaDiaria;
    }

    public String getFrecInsulina() {
        return frecInsulina;
    }

    public String getPautaInsulinaSOS() {
        return pautaInsulinaSOS;
    }

    public int getFrecuenciaHorasControles() {
        return frecuenciaHorasControles;
    }

    public LocalTime getHoraPrimerControl() {
        return horaPrimerControl;
    }

    @Override
    public String toString() {
        return "Tratamiento{" +
                "dietaRecomendada=" + dietaRecomendada +
                ", medicamentosOrales=" + medicamentosOrales +
                ", usaInsulinaCristalinaSOS=" + usaInsulinaCristalinaSOS +
                ", usaInsulinaLentaDiaria=" + usaInsulinaLentaDiaria +
                ", dosisInsulinaLentaDiaria=" + dosisInsulinaLentaDiaria +
                ", frecInsulina=" + frecInsulina +
                ", pautaInsulinaSOS=" + pautaInsulinaSOS +
                ", frecuenciaHorasControles=" + frecuenciaHorasControles +
                ", horaPrimerControl=" + horaPrimerControl +
                '}';
    }
}
