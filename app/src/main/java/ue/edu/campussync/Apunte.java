package ue.edu.campussync;

import android.graphics.Bitmap;

public class Apunte {
    private Long idApunte;
    private String titulo;
    private String contenido;
    private String fechaCreacion; // Usamos String temporalmente para el formato de fecha (AAAA-MM-DD)
    private Long idMateria; // Para identificar la materia asociada fácilmente
    private Bitmap fotoBitmap; // Para la foto opcional tomada con la cámara

    // Constructor completo
    public Apunte(Long idApunte, String titulo, String contenido, String fechaCreacion, Long idMateria, Bitmap fotoBitmap) {
        this.idApunte = idApunte;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
        this.idMateria = idMateria;
        this.fotoBitmap = fotoBitmap;
    }

    // Getters
    public Long getIdApunte() { return idApunte; }
    public String getTitulo() { return titulo; }
    public String getContenido() { return contenido; }
    public String getFechaCreacion() { return fechaCreacion; }
    public Long getIdMateria() { return idMateria; }
    public Bitmap getFotoBitmap() { return fotoBitmap; }

    // Setters
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setIdMateria(Long idMateria) { this.idMateria = idMateria; }
    public void setFotoBitmap(Bitmap fotoBitmap) { this.fotoBitmap = fotoBitmap; }
}