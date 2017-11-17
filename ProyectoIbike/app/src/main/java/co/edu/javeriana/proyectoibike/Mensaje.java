package co.edu.javeriana.proyectoibike;

import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by Felipe on 03/09/2017.
 */

public class Mensaje {

    public String remitente;
    public String destino;
    public long fecha;
    public String texto;

    public Mensaje(String remitente, String texto, String destino) {

        this.remitente = remitente;
        this.destino= destino;
        this.texto = texto;
        this.fecha= new Date().getTime();
    }

    public Mensaje() {
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }




    @Override
    public String toString() {
        return               remitente + "\n" +
                (DateFormat.format("dd-MM-yyyy (HH:mm)",
                 fecha) ) +"\n"+
                texto  ;
    }
}
