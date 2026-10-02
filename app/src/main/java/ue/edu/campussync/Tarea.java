package ue.edu.campussync;

public class Tarea {
    private Long idTarea;
    private Long idMateria;
    private String titulo;
    private String descripcion;
    private String fechaVencimiento;
    private String estado;

    public Tarea(Long idTarea, Long idMateria, String titulo, String descripcion, String fechaVencimiento, String estado) {
        this.idTarea = idTarea;
        this.idMateria = idMateria;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    // Getters
    public Long getIdTarea() { return idTarea; }
    public Long getIdMateria() { return idMateria; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getFechaVencimiento() { return fechaVencimiento; }
    public String getEstado() { return estado; }

    // Setters (¡Este era el que faltaba y causaba el error en rojo!)
    public void setIdMateria(Long idMateria) { this.idMateria = idMateria; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public void setEstado(String estado) { this.estado = estado; }
}