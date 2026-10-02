package ue.edu.campussync;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TareasFragment extends Fragment {

    // === DECLARACIÓN DE VARIABLES PRINCIPALES ===
    private RecyclerView recyclerView;
    private TareaAdapter adapter;
    private List<Tarea> listaTareas;
    private ApiService apiService;
    private Long idMateriaFiltro = -1L;

    public TareasFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tareas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTareas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FloatingActionButton fabAgregar = view.findViewById(R.id.fabAgregarTarea);

        listaTareas = new ArrayList<>();
        adapter = new TareaAdapter(listaTareas);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        if (getArguments() != null) {
            idMateriaFiltro = getArguments().getLong("id_materia", -1L);
        }

        cargarTareasDesdeServidor();

        fabAgregar.setOnClickListener(v -> mostrarDialogoFormularioTarea(null, -1));

        adapter.setOnItemClickListener(position -> {
            Tarea tareaSeleccionada = listaTareas.get(position);
            mostrarOpcionesTarea(tareaSeleccionada, position);
        });

        return view;
    }

    private void cargarTareasDesdeServidor() {
        Call<List<Tarea>> call;
        if (idMateriaFiltro != -1L) {
            call = apiService.getTareasPorMateria(idMateriaFiltro);
        } else {
            call = apiService.getTodasLasTareas();
        }

        call.enqueue(new Callback<List<Tarea>>() {
            @Override
            public void onResponse(Call<List<Tarea>> call, Response<List<Tarea>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTareas.clear();
                    listaTareas.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar tareas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tarea>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarOpcionesTarea(Tarea tarea, int position) {
        CharSequence[] opciones = {"Editar Tarea", "Eliminar Tarea"};
        new AlertDialog.Builder(getContext())
                .setTitle(tarea.getTitulo())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoFormularioTarea(tarea, position);
                    } else if (which == 1) {
                        confirmarEliminacionTarea(tarea, position);
                    }
                })
                .show();
    }

    // === FORMULARIO CON MEJORAS (SPINNERS Y CALENDARIO) ===
    private void mostrarDialogoFormularioTarea(Tarea tareaExistente, int position) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_tarea, null);

        EditText etTitulo = dialogView.findViewById(R.id.etTituloTarea);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionTarea);
        EditText etFecha = dialogView.findViewById(R.id.etFechaVencimiento);

        // Enlazamos los nuevos Spinners
        Spinner spMateria = dialogView.findViewById(R.id.spMateriaTarea);
        Spinner spEstado = dialogView.findViewById(R.id.spEstadoTarea);

        boolean esEdicion = (tareaExistente != null);

        // === MEJORA 1: CALENDARIO PARA LA FECHA ===
        etFecha.setOnClickListener(v -> {
            // Obtenemos la fecha actual del sistema para que el calendario abra en hoy
            Calendar calendario = Calendar.getInstance();
            int anio = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            // Creamos y mostramos el DatePickerDialog
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        // Formateamos la fecha para que siempre tenga 2 dígitos (ej. 2026-05-09)
                        String fechaSeleccionada = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                        etFecha.setText(fechaSeleccionada);
                    }, anio, mes, dia);
            datePicker.show();
        });

        // === MEJORA 2: CONFIGURAR EL SPINNER DE ESTADOS ===
        String[] opcionesEstado = {"Pendiente", "En progreso", "Completada"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, opcionesEstado);
        spEstado.setAdapter(adapterEstado);

        // === MEJORA 3: CARGAR LAS MATERIAS EN EL SPINNER ===
        // Necesitamos guardar las materias reales (con su ID) y crear una lista solo de nombres para mostrar
        List<Materia> materiasDisponibles = new ArrayList<>();
        List<String> nombresMaterias = new ArrayList<>();
        ArrayAdapter<String> adapterMateria = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombresMaterias);
        spMateria.setAdapter(adapterMateria);

        // Hacemos una petición GET a las materias para llenar el menú desplegable
        apiService.getMaterias().enqueue(new Callback<List<Materia>>() {
            @Override
            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    materiasDisponibles.addAll(response.body());
                    for (int i = 0; i < materiasDisponibles.size(); i++) {
                        Materia m = materiasDisponibles.get(i);
                        nombresMaterias.add(m.getNombreMateria()); // Agregamos el nombre al adaptador

                        // Si estamos editando y esta es la materia actual de la tarea, la seleccionamos
                        if (esEdicion && m.getIdMateria().equals(tareaExistente.getIdMateria())) {
                            spMateria.setSelection(i);
                        }
                        // Si no estamos editando, pero venimos desde el filtro de una materia, la preseleccionamos
                        else if (!esEdicion && idMateriaFiltro != -1L && m.getIdMateria().equals(idMateriaFiltro)) {
                            spMateria.setSelection(i);
                        }
                    }
                    adapterMateria.notifyDataSetChanged(); // Avisamos que hay datos nuevos
                }
            }
            @Override
            public void onFailure(Call<List<Materia>> call, Throwable t) {
                Toast.makeText(getContext(), "No se pudieron cargar las materias", Toast.LENGTH_SHORT).show();
            }
        });

        // === LLENAR DATOS SI ESTAMOS EDITANDO ===
        if (esEdicion) {
            etTitulo.setText(tareaExistente.getTitulo());
            etDescripcion.setText(tareaExistente.getDescripcion());
            etFecha.setText(tareaExistente.getFechaVencimiento());

            // Ubicamos el estado correcto en el Spinner ("Pendiente" = 0, "En progreso" = 1, etc.)
            for (int i = 0; i < opcionesEstado.length; i++) {
                if (opcionesEstado[i].equalsIgnoreCase(tareaExistente.getEstado())) {
                    spEstado.setSelection(i);
                    break;
                }
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle(esEdicion ? "Editar Tarea" : "Nueva Tarea")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    String fecha = etFecha.getText().toString().trim();

                    // Extraemos el estado seleccionado del Spinner
                    String estado = spEstado.getSelectedItem().toString();

                    // Extraemos el ID de la materia seleccionada
                    int posMateriaSeleccionada = spMateria.getSelectedItemPosition();
                    Long idMateriaParaGuardar = -1L;
                    if (posMateriaSeleccionada >= 0 && !materiasDisponibles.isEmpty()) {
                        idMateriaParaGuardar = materiasDisponibles.get(posMateriaSeleccionada).getIdMateria();
                    }

                    if (!titulo.isEmpty() && idMateriaParaGuardar != -1L) {
                        if (esEdicion) {
                            tareaExistente.setTitulo(titulo);
                            tareaExistente.setDescripcion(descripcion);
                            tareaExistente.setFechaVencimiento(fecha);
                            tareaExistente.setEstado(estado);
                            tareaExistente.setIdMateria(idMateriaParaGuardar); // Actualizamos también la materia
                            actualizarTareaEnServidor(tareaExistente, position);
                        } else {
                            Tarea nuevaTarea = new Tarea(null, idMateriaParaGuardar, titulo, descripcion, fecha, estado);
                            guardarTareaEnServidor(nuevaTarea);
                        }
                    } else {
                        Toast.makeText(getContext(), "El título y la materia son obligatorios", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarTareaEnServidor(Tarea nuevaTarea) {
        apiService.crearTarea(nuevaTarea).enqueue(new Callback<Tarea>() {
            @Override
            public void onResponse(Call<Tarea> call, Response<Tarea> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTareas.add(response.body());
                    adapter.notifyItemInserted(listaTareas.size() - 1);
                    Toast.makeText(getContext(), "Tarea guardada", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Tarea> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarTareaEnServidor(Tarea tarea, int position) {
        apiService.actualizarTarea(tarea.getIdTarea(), tarea).enqueue(new Callback<Tarea>() {
            @Override
            public void onResponse(Call<Tarea> call, Response<Tarea> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTareas.set(position, response.body());
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), "Tarea actualizada", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Tarea> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarEliminacionTarea(Tarea tarea, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Tarea")
                .setMessage("¿Estás seguro de que deseas eliminar '" + tarea.getTitulo() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarTareaEnServidor(tarea, position))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarTareaEnServidor(Tarea tarea, int position) {
        apiService.eliminarTarea(tarea.getIdTarea()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listaTareas.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Tarea eliminada", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}