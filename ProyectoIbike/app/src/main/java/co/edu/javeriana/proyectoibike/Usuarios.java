package co.edu.javeriana.proyectoibike;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristian on 27/10/2017.
 */

public class Usuarios {

    private String Nombre;
    private String Apellido;
    private String id;
    private String correo;
    private String photoPath;
    private List<String> listaAmigos = new ArrayList<>();
    private String Equipo;
    private List<String> rutas = new ArrayList<>();
    private LatLng posActual;

    public List<String> getListaAmigos() {
        return listaAmigos;
    }

    public void setListaAmigos(List<String> listaAmigos) {
        this.listaAmigos = listaAmigos;
    }

    public String getEquipo() {
        return Equipo;
    }

    public void setEquipo(String equipo) {
        Equipo = equipo;
    }

    public List<String> getRutas() {
        return rutas;
    }

    public void setRutas(List<String> rutas) {
        this.rutas = rutas;
    }

    public LatLng getPosActual() {
        return posActual;
    }

    public void setPosActual(LatLng posActual) {
        this.posActual = posActual;
    }



    public Usuarios(String nombre, String apellido, String id, String correo, List<String> listaAmigos, String equipo, List<String> rutas) {
        Nombre = nombre;
        Apellido = apellido;
        this.id = id;
        this.correo = correo;
        this.listaAmigos = listaAmigos;
        Equipo = equipo;
        this.rutas = rutas;
    }

    public Usuarios() {
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return
               Nombre + " "+ Apellido ;
    }
}
