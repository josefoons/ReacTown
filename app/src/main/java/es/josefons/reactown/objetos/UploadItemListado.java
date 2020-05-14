package es.josefons.reactown.objetos;

public class UploadItemListado {
    private String propuestaNombre;
    private String propuestaUsuario;
    private String propuestaDescripcion;
    private String propuestaImagen;

    public UploadItemListado() {
    }

    public UploadItemListado(String propuestaNombre, String propuestaUsuario, String propuestaDescripcion, String propuestaImagen) {
        this.propuestaNombre = propuestaNombre;
        this.propuestaUsuario = propuestaUsuario;
        this.propuestaDescripcion = propuestaDescripcion;
        this.propuestaImagen = propuestaImagen;
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
}

