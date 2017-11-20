package co.edu.javeriana.proyectoibike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luisd on 19/11/2017.
 */

public class Empresario {

    private String nombre;
    private String apellido;
    private boolean empresario;
    private String id;
    private List<String> idEventos = new ArrayList<>();
    private List<String> idMarcadores = new ArrayList<>();
    private String correo;

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

    public boolean isEmpresario() {
        return empresario;
    }

    public void setEmpresario(boolean empresario) {
        this.empresario = empresario;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
