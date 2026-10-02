package ue.edu.campussync;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MateriasFragment extends Fragment {

    private RecyclerView recyclerView;
    private MateriaAdapter adapter;
    private List<Materia> listaMaterias;
    private ApiService apiService;

    public MateriasFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materias, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMaterias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FloatingActionButton fabAgregar = view.findViewById(R.id.fabAgregarMateria);

        listaMaterias = new ArrayList<>();
        adapter = new MateriaAdapter(listaMaterias);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        cargarMateriasDesdeServidor();

        fabAgregar.setOnClickListener(v -> mostrarDialogoFormularioMateria(null, -1));

        adapter.setOnItemClickListener(position -> {
            Materia materiaSeleccionada = listaMaterias.get(position);
            mostrarOpcionesMateria(materiaSeleccionada, position);
        });

        return view;
    }

    private void cargarMateriasDesdeServidor() {
        apiService.getMaterias().enqueue(new Callback<List<Materia>>() {
            @Override
            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMaterias.clear();
                    listaMaterias.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar materias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Materia>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", "Fallo: ", t);
            }
        });
    }

    private void mostrarOpcionesMateria(Materia materia, int position) {
        CharSequence[] opciones = {"Ver Tareas de la Materia", "Ver Apuntes de la Materia", "Editar Materia", "Eliminar Materia"};

        new AlertDialog.Builder(getContext())
                .setTitle(materia.getNombreMateria())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        // === EL PUENTE: ENVIAR EL ID DE LA MATERIA A LAS TAREAS ===

                        // 1. Creamos una "mochila" (Bundle) para enviar datos entre pantallas
                        Bundle bundle = new Bundle();
                        bundle.putLong("id_materia", materia.getIdMateria()); // Metemos el ID de la materia seleccionada

                        // 2. Preparamos la pantalla de destino (TareasFragment)
                        TareasFragment tareasFragment = new TareasFragment();
                        tareasFragment.setArguments(bundle); // Le colgamos la mochila con el ID

                        // 3. Hacemos el cambio de pantalla manualmente para no perder los datos
                        // Nota: Asumo que el contenedor en tu MainActivity se llama "fragment_container"
                        // (o "frame_layout"). Si se llama distinto, cámbialo aquí.
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, tareasFragment)
                                .addToBackStack(null) // Esto permite que el botón "Atrás" del celular regrese a Materias
                                .commit();

                    } else if (which == 1) {
                        // Aquí conectaremos los apuntes más adelante
                    } else if (which == 2) {
                        mostrarDialogoFormularioMateria(materia, position);
                    } else if (which == 3) {
                        confirmarEliminacionMateria(materia, position);
                    }
                })
                .show();
    }

    private void mostrarDialogoFormularioMateria(Materia materiaExistente, int position) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_materia, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombreMateria);
        EditText etProfesor = dialogView.findViewById(R.id.etNombreProfesor);
        EditText etFechaInicio = dialogView.findViewById(R.id.etFechaInicio);
        EditText etFechaFinal = dialogView.findViewById(R.id.etFechaFinal);

        boolean esEdicion = (materiaExistente != null);
        if (esEdicion) {
            etNombre.setText(materiaExistente.getNombreMateria());
            etProfesor.setText(materiaExistente.getNombreProfesor());
            etFechaInicio.setText(materiaExistente.getFechaInicio());
            etFechaFinal.setText(materiaExistente.getFechaFinal());
        }

        new AlertDialog.Builder(getContext())
                .setTitle(esEdicion ? "Editar Materia" : "Nueva Materia")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String profesor = etProfesor.getText().toString().trim();
                    String fInicio = etFechaInicio.getText().toString().trim();
                    String fFinal = etFechaFinal.getText().toString().trim();

                    if (!nombre.isEmpty()) {
                        if (esEdicion) {
                            materiaExistente.setNombreMateria(nombre);
                            materiaExistente.setNombreProfesor(profesor);
                            materiaExistente.setFechaInicio(fInicio);
                            materiaExistente.setFechaFinal(fFinal);
                            actualizarMateriaEnServidor(materiaExistente, position);
                        } else {
                            Materia nuevaMateria = new Materia(null, nombre, profesor, fInicio, fFinal);
                            guardarMateriaEnServidor(nuevaMateria);
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarMateriaEnServidor(Materia nuevaMateria) {
        apiService.crearMateria(nuevaMateria).enqueue(new Callback<Materia>() {
            @Override
            public void onResponse(Call<Materia> call, Response<Materia> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMaterias.add(response.body());
                    adapter.notifyItemInserted(listaMaterias.size() - 1);
                    recyclerView.scrollToPosition(listaMaterias.size() - 1);
                    Toast.makeText(getContext(), "Materia guardada en BD", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Materia> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // === NUEVOS MÉTODOS DE ACTUALIZAR Y ELIMINAR ===

    private void actualizarMateriaEnServidor(Materia materia, int position) {
        // NOTA: Si en tu modelo Materia.java el getter se llama getId(), cámbialo aquí.
        apiService.actualizarMateria(materia.getIdMateria(), materia).enqueue(new Callback<Materia>() {
            @Override
            public void onResponse(Call<Materia> call, Response<Materia> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMaterias.set(position, response.body());
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), "Materia actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Materia> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarEliminacionMateria(Materia materia, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Materia")
                .setMessage("¿Estás seguro de que deseas eliminar '" + materia.getNombreMateria() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarMateriaEnServidor(materia, position))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarMateriaEnServidor(Materia materia, int position) {
        apiService.eliminarMateria(materia.getIdMateria()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listaMaterias.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Materia eliminada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}