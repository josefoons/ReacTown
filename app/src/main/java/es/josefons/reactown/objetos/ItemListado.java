package es.josefons.reactown.objetos;

public class ItemListado {

    private String id, name, autor, icon;

    public ItemListado() {}

    public ItemListado(String id, String icon, String name, String autor) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.autor = autor;
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

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
