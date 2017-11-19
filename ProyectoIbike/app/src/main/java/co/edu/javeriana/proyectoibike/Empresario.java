package co.edu.javeriana.proyectoibike;

import java.util.List;

/**
 * Created by luisd on 19/11/2017.
 */

public class Empresario {

    private String nombre;
    private String apellido;
    private boolean estado;
    private String id;
    private List<String> idEventos;
    private List<String> idMarcadores;

    public Empresario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public List<String> getIdEventos() {
        return idEventos;
    }

    public void setIdEventos(List<String> idEventos) {
        this.idEventos = idEventos;
    }

    public List<String> getIdMarcadores() {
        return idMarcadores;
    }

    public void setIdMarcadores(List<String> idMarcadores) {
        this.idMarcadores = idMarcadores;
    }


}
