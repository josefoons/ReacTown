package es.josefons.reactown.objetos;

import java.util.ArrayList;
import java.util.Map;

public class UploadItemListado {
    private String propuestaNombre;
    private String propuestaUsuario;
    private String propuestaDescripcion;
    private String propuestaImagen;
    private Map<String, String> propuestaVotos;

    public UploadItemListado() {
    }

    public UploadItemListado(String propuestaNombre, String propuestaUsuario, String propuestaDescripcion, String propuestaImagen) {
        this.propuestaNombre = propuestaNombre;
        this.propuestaUsuario = propuestaUsuario;
        this.propuestaDescripcion = propuestaDescripcion;
        this.propuestaImagen = propuestaImagen;
    }

    public UploadItemListado(String propuestaNombre, String propuestaUsuario, String propuestaDescripcion, String propuestaImagen, Map<String, String> propuestaVotos) {
        this.propuestaNombre = propuestaNombre;
        this.propuestaUsuario = propuestaUsuario;
        this.propuestaDescripcion = propuestaDescripcion;
        this.propuestaImagen = propuestaImagen;
        this.propuestaVotos = propuestaVotos;
    }

    public String getPropuestaNombre() {
        return propuestaNombre;
    }

    public void setPropuestaNombre(String propuestaNombre) {
        this.propuestaNombre = propuestaNombre;
    }

    public String getPropuestaUsuario() {
        return propuestaUsuario;
    }

    public void setPropuestaUsuario(String propuestaUsuario) {
        this.propuestaUsuario = propuestaUsuario;
    }

    public String getPropuestaDescripcion() {
        return propuestaDescripcion;
    }

    public void setPropuestaDescripcion(String propuestaDescripcion) {
        this.propuestaDescripcion = propuestaDescripcion;
    }

    public String getPropuestaImagen() {
        return propuestaImagen;
    }

    public void setPropuestaImagen(String propuestaImagen) {
        this.propuestaImagen = propuestaImagen;
    }

    public Map<String, String> getPropuestaVotos() {
        return propuestaVotos;
    }

    public void setPropuestaVotos(Map<String, String> propuestaVotos) {
        this.propuestaVotos = propuestaVotos;
    }
}


