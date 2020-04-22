package es.josefons.reactown;

public class ItemListado {

    private int icon;
    private String id, name, autor;

    public ItemListado(String id, int icon, String name, String autor) {
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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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
}
