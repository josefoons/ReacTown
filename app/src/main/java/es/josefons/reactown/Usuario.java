package es.josefons.reactown;

public class Usuario {
    String id, name, correo;
    int permiso;

    public Usuario() {
    }

    public Usuario(String id, String name, String correo, int permiso) {
        this.id = id;
        this.name = name;
        this.correo = correo;
        this.permiso = permiso;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getPermiso() {
        return permiso;
    }

    public void setPermiso(int permiso) {
        this.permiso = permiso;
    }
}
