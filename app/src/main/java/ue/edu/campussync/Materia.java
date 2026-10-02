package ue.edu.campussync;

public class Materia {
    private Long idMateria;
    private String nombreMateria;
    private String nombreProfesor;
    private String fechaInicio; // Manejado como String (AAAA-MM-DD) en Android
    private String fechaFinal;  // Manejado como String (AAAA-MM-DD) en Android

    public Materia(Long idMateria, String nombreMateria, String nombreProfesor, String fechaInicio, String fechaFinal) {
        this.idMateria = idMateria;
        this.nombreMateria = nombreMateria;
        this.nombreProfesor = nombreProfesor;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
    }

    // Getters
    public Long getIdMateria() { return idMateria; }
    public String getNombreMateria() { return nombreMateria; }
    public String getNombreProfesor() { return nombreProfesor; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFinal() { return fechaFinal; }

    // Setters
    public void setIdMateria(Long idMateria) { this.idMateria = idMateria; }
    public void setNombreMateria(String nombreMateria) { this.nombreMateria = nombreMateria; }
    public void setNombreProfesor(String nombreProfesor) { this.nombreProfesor = nombreProfesor; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFinal(String fechaFinal) { this.fechaFinal = fechaFinal; }
}