package co.edu.javeriana.proyectoibike;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Cristian on 29/10/2017.
 */

public class Rutas {


    private String idRuta;
    private LatLng destino;
    private String duracion;
    private String fecha;
    private double kilometros;
    private LatLng origen;
    private boolean realizado;
    private List<String> usuariosRuta = new ArrayList<>();
    private String clima;

    public Rutas() {
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idCreador) {
        this.idRuta = idCreador;
    }

    public LatLng getDestino() {
        return destino;
    }

    public void setDestino(LatLng destino) {
        this.destino = destino;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getKilometros() {
        return kilometros;
    }

    public void setKilometros(double kilometros) {
        this.kilometros = kilometros;
    }

    public LatLng getOrigen() {
        return origen;
    }

    public void setOrigen(LatLng origen) {
        this.origen = origen;
    }

    public boolean isRealizado() {
        return realizado;
    }

    public void setRealizado(boolean realizado) {
        this.realizado = realizado;
    }

    public List<String> getUsuariosRuta() {
        return usuariosRuta;
    }

    public void setUsuariosRuta(List<String> usuariosRuta) {
        this.usuariosRuta = usuariosRuta;
    }

    public String getClima() {
        return clima;
    }

    public void setClima(String clima) {
        this.clima = clima;
    }
}
