package ue.edu.campussync;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // ==== MATERIAS ====
    @GET("api/materias")
    Call<List<Materia>> getMaterias();

    @POST("api/materias")
    Call<Materia> crearMateria(@Body Materia materia);

    @PUT("api/materias/{id}")
    Call<Materia> actualizarMateria(@Path("id") Long id, @Body Materia materia);

    @DELETE("api/materias/{id}")
    Call<Void> eliminarMateria(@Path("id") Long id);


    // ==== TAREAS ====
    @GET("api/tareas")
    Call<List<Tarea>> getTodasLasTareas();

    // Este endpoint es clave para el botón "Ver Tareas de la Materia" que conectaremos después
    @GET("api/tareas/materia/{idMateria}")
    Call<List<Tarea>> getTareasPorMateria(@Path("idMateria") Long idMateria);

    @POST("api/tareas")
    Call<Tarea> crearTarea(@Body Tarea tarea);

    @PUT("api/tareas/{id}")
    Call<Tarea> actualizarTarea(@Path("id") Long id, @Body Tarea tarea);

    @DELETE("api/tareas/{id}")
    Call<Void> eliminarTarea(@Path("id") Long id);
}