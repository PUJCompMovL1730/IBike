package co.edu.javeriana.proyectoibike;

/**
 * Created by pike on 19/11/2017.
 */

public class Clima {

    public float dir_viento;
    public float vel_viento;
    public float humedad;
    public int temperatura;
    public String texto;


    public Clima() {
    }

    public float getDir_viento() {
        return dir_viento;
    }

    public void setDir_viento(float dir_viento) {
        this.dir_viento = dir_viento;
    }

    public float getVel_viento() {
        return vel_viento;
    }

    public void setVel_viento(float vel_viento) {
        this.vel_viento = vel_viento;
    }

    public float getHumedad() {
        return humedad;
    }

    public void setHumedad(float humedad) {
        this.humedad = humedad;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
