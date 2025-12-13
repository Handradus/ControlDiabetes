package modelo;

import java.time.LocalTime;

public class Tratamiento {

    
    private String dietaRecomendada;
    private String medicamentosOrales; 
    private boolean usaInsulinaCristalinaSOS;
    private boolean usaInsulinaLentaDiaria;
    private int dosisInsulinaLentaDiaria; 
    private String pautaInsulinaSOS;

    private int frecuenciaHorasControles;     // 8, 12, 24
    private LocalTime horaPrimerControl;      // hora del primer control del día

    // Constructor
    public Tratamiento(String dietaRecomendada,
                   String medicamentosOrales,
                   boolean usaInsulinaCristalinaSOS,
                   boolean usaInsulinaLentaDiaria,
                   int dosisInsulinaLentaDiaria,
                   int frecuenciaHorasControles,
                   LocalTime horaPrimerControl,
                   String pautaInsulinaSOS) {

    this.setDietaRecomendada(dietaRecomendada);
    this.setMedicamentosOrales(medicamentosOrales);
    this.setUsaInsulinaCristalinaSOS(usaInsulinaCristalinaSOS);
    this.setUsaInsulinaLentaDiaria(usaInsulinaLentaDiaria);
    this.setDosisInsulinaLentaDiaria(dosisInsulinaLentaDiaria);
    this.setFrecuenciaHorasControles(frecuenciaHorasControles);
    this.setHoraPrimerControl(horaPrimerControl);
    this.setPautaInsulinaSOS(pautaInsulinaSOS);
}


    // setters
        
      
    public void setMedicamentosOrales(String medicamentosOrales) {
        if (medicamentosOrales != null && !medicamentosOrales.trim().isEmpty()){
            this.medicamentosOrales = medicamentosOrales;
        }
    }
    
    public void setPautaInsulinaSOS(String pautaInsulinaSOS) {
    this.pautaInsulinaSOS = (pautaInsulinaSOS == null) ? "" : pautaInsulinaSOS.trim();
}

    public void setUsaInsulinaCristalinaSOS(boolean usaInsulinaCristalinaSOS) {
        this.usaInsulinaCristalinaSOS = usaInsulinaCristalinaSOS;
    }
    

    public boolean isUsaInsulinaLentaDiaria() {
        return usaInsulinaLentaDiaria;
    }

    public void setUsaInsulinaLentaDiaria(boolean usaInsulinaLentaDiaria) {
        this.usaInsulinaLentaDiaria = usaInsulinaLentaDiaria;
    }
   
    
    public void setDosisInsulinaLentaDiaria(int dosisInsulinaLentaDiaria) {
    
        if (dosisInsulinaLentaDiaria < 0) {
            System.err.println("Error: La dosis de insulina no puede ser un número negativo.");
            return;
        }
    
        if (dosisInsulinaLentaDiaria > 80) {
             System.err.println("Error: La dosis de insulina (" + dosisInsulinaLentaDiaria + ") es demasiado alta.");
             return;
        }
   
    this.dosisInsulinaLentaDiaria = dosisInsulinaLentaDiaria;
}
   
    public void setFrecuenciaHorasControles(int frecuenciaHorasControles) {
    
        if (frecuenciaHorasControles < 0) {
            System.err.println("Error: La dosis de insulina no puede ser un número negativo.");
            return;
        }
    //editar segun el controller
        if (frecuenciaHorasControles > 24) {
             System.err.println("Error: La cantidad de controles (" + frecuenciaHorasControles + ") es demasiado alta.");
             return;
        }
   
    this.frecuenciaHorasControles = frecuenciaHorasControles;
}
   
    public void setHoraPrimerControl(LocalTime horaPrimerControl) {
        this.horaPrimerControl = horaPrimerControl;
    }
    
    //Getters
    public String getMedicamentosOrales() {
        return medicamentosOrales;
    }
    
    public String getDietaRecomendada() {
        return dietaRecomendada;
    }
     public int getDosisInsulinaLentaDiaria() {
        return dosisInsulinaLentaDiaria;
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
    public void setDietaRecomendada(String dietaRecomendada) {
        this.dietaRecomendada = dietaRecomendada;
    }
    public boolean isUsaInsulinaCristalinaSOS() {
        return usaInsulinaCristalinaSOS;
    }

    @Override
    public String toString() {
        return "Tratamiento{" +
                "dieta='" + dietaRecomendada + '\'' +
                ", medsOrales='" + medicamentosOrales + '\'' +
                ", insulinaSOS=" + usaInsulinaCristalinaSOS +
                ", insulinaLenta=" + usaInsulinaLentaDiaria +
                ", dosisLenta=" + dosisInsulinaLentaDiaria +
                ", freqControles=" + frecuenciaHorasControles +
                ", horaPrimerControl=" + horaPrimerControl +
                '}';
    }
}
