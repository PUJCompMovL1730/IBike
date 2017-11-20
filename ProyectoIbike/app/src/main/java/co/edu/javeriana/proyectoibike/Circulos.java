package co.edu.javeriana.proyectoibike;

/**
 * Created by Cristian on 19/11/2017.
 */

public class Circulos {

    private String idequipo;
    private int puntos_amarillo;
    private int puntos_azul;
    private int puntos_rojo;
    int radioactual;
    int radiomaximo;
    int radiominimo;

    public Circulos() {
    }

    public String getIdequipo() {
        return idequipo;
    }

    public void setIdequipo(String idequipo) {
        this.idequipo = idequipo;
    }

    public int getPuntos_amarillo() {
        return puntos_amarillo;
    }

    public void setPuntos_amarillo(int puntos_amarillo) {
        this.puntos_amarillo = puntos_amarillo;
    }

    public int getPuntos_azul() {
        return puntos_azul;
    }

    public void setPuntos_azul(int puntos_azul) {
        this.puntos_azul = puntos_azul;
    }

    public int getPuntos_rojo() {
        return puntos_rojo;
    }

    public void setPuntos_rojo(int puntos_rojo) {
        this.puntos_rojo = puntos_rojo;
    }

    public int getRadioactual() {
        return radioactual;
    }

    public void setRadioactual(int radioactual) {
        this.radioactual = radioactual;
    }

    public int getRadiomaximo() {
        return radiomaximo;
    }

    public void setRadiomaximo(int radiomaximo) {
        this.radiomaximo = radiomaximo;
    }

    public int getRadiominimo() {
        return radiominimo;
    }

    public void setRadiominimo(int radiominimo) {
        this.radiominimo = radiominimo;
    }


}
