package co.edu.javeriana.proyectoibike;

/**
 * Created by luisd on 19/11/2017.
 */

public class Marcador {

    private String idMarcador;
    private String descripcion;
    private String idEmpresario;
    private double latitudMarcador;
    private double longitudMarcador;
    private int multiplicador;
    private int visitas;

    public Marcador() {
    }

    public int getVisitas() {
        return visitas;
    }

    public void setVisitas(int visitas) {
        this.visitas = visitas;
    }

    public String getIdMarcador() {
        return idMarcador;
    }

    public void setIdMarcador(String idMarcador) {
        this.idMarcador = idMarcador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdEmpresario() {
        return idEmpresario;
    }

    public void setIdEmpresario(String idEmpresario) {
        this.idEmpresario = idEmpresario;
    }

    public double getLatitudMarcador() {
        return latitudMarcador;
    }

    public void setLatitudMarcador(double latitudMarcador) {
        this.latitudMarcador = latitudMarcador;
    }

    public double getLongitudMarcador() {
        return longitudMarcador;
    }

    public void setLongitudMarcador(double longitudMarcador) {
        this.longitudMarcador = longitudMarcador;
    }

    public int getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(int multiplicador) {
        this.multiplicador = multiplicador;
    }
}
