package es.josefons.reactown.objetos;

public class Usuario {
    String id, name, correo, img;
    int permiso;

    public Usuario() {
    }

    public Usuario(String id, String name, String correo, int permiso, String img) {
        this.id = id;
        this.name = name;
        this.correo = correo;
        this.permiso = permiso;
        this.img = img;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
