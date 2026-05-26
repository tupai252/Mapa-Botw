package model;

public class Marcador {
    private int idMarcador;
    private String nombre;
    private String categoria;
    private float coordX;
    private float coordY;
    private boolean tachado;

    // Constructor vacío
    public Marcador() {}

    // Constructor completo
    public Marcador(int idMarcador, String nombre, String categoria, float coordX, float coordY, boolean tachado) {
        this.idMarcador = idMarcador;
        this.nombre = nombre;
        this.categoria = categoria;
        this.coordX = coordX;
        this.coordY = coordY;
        this.tachado = tachado;
    }

    // Getters y Setters
    public int getIdMarcador() { return idMarcador; }
    public void setIdMarcador(int idMarcador) { this.idMarcador = idMarcador; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public float getCoordX() { return coordX; }
    public void setCoordX(float coordX) { this.coordX = coordX; }
    
    public float getCoordY() { return coordY; }
    public void setCoordY(float coordY) { this.coordY = coordY; }
    
    public boolean isTachado() { return tachado; }
    public void setTachado(boolean tachado) { this.tachado = tachado; }
}