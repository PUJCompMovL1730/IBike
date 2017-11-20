package co.edu.javeriana.proyectoibike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luisd on 19/11/2017.
 */

public class Evento {

    private String idEmpresario;
    private boolean estado;
    private String fecha;
    private String descripcion;
    private List<String> idUsuarios = new ArrayList<>();
    private double latitudOrigen;
    private double latitudDestino;
    private double longitudOrigen;
    private double longitudDestino;
    private String idEvento;

    public Evento() {
    }

    public double getLongitudOrigen() {
        return longitudOrigen;
    }

    public void setLongitudOrigen(double longitudOrigen) {
        this.longitudOrigen = longitudOrigen;
    }

    public double getLongitudDestino() {
        return longitudDestino;
    }

    public void setLongitudDestino(double longitudDestino) {
        this.longitudDestino = longitudDestino;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getIdEmpresario() {
        return idEmpresario;
    }

    public void setIdEmpresario(String idEmpresario) {
        this.idEmpresario = idEmpresario;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getIdUsuarios() {
        return idUsuarios;
    }

    public void setIdUsuarios(List<String> idUsuarios) {
        this.idUsuarios = idUsuarios;
    }

    public double getLatitudOrigen() {
        return latitudOrigen;
    }

    public void setLatitudOrigen(double latitudOrigen) {
        this.latitudOrigen = latitudOrigen;
    }

    public double getLatitudDestino() {
        return latitudDestino;
    }

    public void setLatitudDestino(double latitudDestino) {
        this.latitudDestino = latitudDestino;
    }

    @Override
    public String toString() {
        String info;
        return info = "Evento: " + descripcion + "\n" +
                        "Fecha: " + fecha + "\n" +
                "Participantes: " + idUsuarios.size();
    }
}
