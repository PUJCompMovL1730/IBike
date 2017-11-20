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
    private double latitudDestino;
    private double longitudDestino;
    private double longitudOrigen;
    private double latitudOrigen;
    private String fecha;
    private double kilometros;
    private boolean realizado;
    private boolean programada;
    private boolean validaDominio;
    private List<String> usuariosRuta = new ArrayList<>();
    private String idReporte;
    private String nombreDestino;


    public Rutas(String idRuta, double latitudDestino, double longitudDestino, double longitudOrigen, double latitudOrigen, String fecha, double kilometros, boolean realizado, boolean validaDominio,String nombreDestino, boolean programada) {
        this.idRuta = idRuta;
        this.latitudDestino = latitudDestino;
        this.longitudDestino = longitudDestino;
        this.longitudOrigen = longitudOrigen;
        this.latitudOrigen = latitudOrigen;
        this.fecha = fecha;
        this.kilometros = kilometros;
        this.realizado = realizado;
        this.validaDominio = validaDominio;
        this.nombreDestino = nombreDestino;
        this.programada = programada;
    }

    public String getNombreDestino() {
        return nombreDestino;
    }

    public void setNombreDestino(String nombreDestino) {
        this.nombreDestino = nombreDestino;
    }

    public Rutas() {
    }

    public boolean isProgramada() {
        return programada;
    }

    public void setProgramada(boolean programada) {
        this.programada = programada;
    }

    public boolean isValidaDominio() {
        return validaDominio;
    }

    public void setValidaDominio(boolean validaDominio) {
        this.validaDominio = validaDominio;
    }

    public String getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(String idReporte) {
        this.idReporte = idReporte;
    }

    public double getLatitudDestino() {
        return latitudDestino;
    }

    public void setLatitudDestino(double latitudDestino) {
        this.latitudDestino = latitudDestino;
    }

    public double getLongitudDestino() {
        return longitudDestino;
    }

    public void setLongitudDestino(double longitudDestino) {
        this.longitudDestino = longitudDestino;
    }

    public double getLongitudOrigen() {
        return longitudOrigen;
    }

    public void setLongitudOrigen(double longitudOrigen) {
        this.longitudOrigen = longitudOrigen;
    }

    public double getLatitudOrigen() {
        return latitudOrigen;
    }

    public void setLatitudOrigen(double latitudOrigen) {
        this.latitudOrigen = latitudOrigen;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idCreador) {
        this.idRuta = idCreador;
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

}
