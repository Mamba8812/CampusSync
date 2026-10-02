package ue.edu.campussync;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ApuntesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ApunteAdapter adapter;
    private List<Apunte> listaApuntes;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private ImageView ivPreviewDialogTemporal;
    private Bitmap fotoCapturadaTemporal = null;

    public ApuntesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apuntes, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewApuntes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FloatingActionButton fabAgregar = view.findViewById(R.id.fabAgregarApunte);

        listaApuntes = new ArrayList<>();
        listaApuntes.add(new Apunte(1L, "Introducción a Arquitectura", "Vimos los patrones MVC y capas principales.", "2026-06-05", 1L, null));

        adapter = new ApunteAdapter(listaApuntes);
        recyclerView.setAdapter(adapter);

        // Lanzador de cámara
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        fotoCapturadaTemporal = (Bitmap) extras.get("data");
                        if (ivPreviewDialogTemporal != null && fotoCapturadaTemporal != null) {
                            ivPreviewDialogTemporal.setImageBitmap(fotoCapturadaTemporal);
                            ivPreviewDialogTemporal.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        // Lanzador de permisos
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirCamaraSistema();
                    } else {
                        Toast.makeText(getContext(), "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 1. Clic en botón flotante: CREAR apunte
        fabAgregar.setOnClickListener(v -> mostrarDialogoFormularioApunte(null, -1));

        // 2. Clic en una tarjeta de la lista: EDITAR apunte
        adapter.setOnItemClickListener(position -> {
            Apunte apunteSeleccionado = listaApuntes.get(position);
            mostrarDialogoFormularioApunte(apunteSeleccionado, position);
        });

        return view;
    }

    private void mostrarDialogoFormularioApunte(Apunte apunteExistente, int position) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_apunte, null);

        Spinner spinnerMaterias = dialogView.findViewById(R.id.spinnerMateriasApunte);
        EditText etTitulo = dialogView.findViewById(R.id.etTituloApunte);
        EditText etContenido = dialogView.findViewById(R.id.etContenidoApunte);
        Button btnTomarFoto = dialogView.findViewById(R.id.btnTomarFotoDialog);
        ivPreviewDialogTemporal = dialogView.findViewById(R.id.ivPreviewDialog);

        // Configurar Spinner de materias
        String[] nombresMaterias = {"Ingeniería de Software", "Álgebra Lineal", "Ética Profesional"};
        Long[] idsMaterias = {1L, 2L, 3L};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, nombresMaterias);
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerMaterias.setAdapter(spinnerAdapter);

        boolean esEdicion = (apunteExistente != null);

        if (esEdicion) {
            etTitulo.setText(apunteExistente.getTitulo());
            etContenido.setText(apunteExistente.getContenido());
            fotoCapturadaTemporal = apunteExistente.getFotoBitmap();

            if (fotoCapturadaTemporal != null) {
                ivPreviewDialogTemporal.setImageBitmap(fotoCapturadaTemporal);
                ivPreviewDialogTemporal.setVisibility(View.VISIBLE);
            }

            // Posicionar el Spinner en la materia correspondiente
            for (int i = 0; i < idsMaterias.length; i++) {
                if (idsMaterias[i].equals(apunteExistente.getIdMateria())) {
                    spinnerMaterias.setSelection(i);
                    break;
                }
            }
        } else {
            fotoCapturadaTemporal = null;
        }

        btnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                abrirCamaraSistema();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        new AlertDialog.Builder(getContext())
                .setTitle(esEdicion ? "Editar Apunte" : "Nuevo Apunte")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String contenido = etContenido.getText().toString().trim();
                    int selectedIndex = spinnerMaterias.getSelectedItemPosition();
                    Long idMateriaSeleccionada = idsMaterias[selectedIndex];

                    if (!titulo.isEmpty() || !contenido.isEmpty()) {
                        if (esEdicion) {
                            // Actualizar el apunte existente
                            apunteExistente.setTitulo(titulo);
                            apunteExistente.setContenido(contenido);
                            apunteExistente.setIdMateria(idMateriaSeleccionada);
                            apunteExistente.setFotoBitmap(fotoCapturadaTemporal);
                            adapter.notifyItemChanged(position);
                        } else {
                            // Crear un apunte nuevo
                            Long nuevoId = (long) (listaApuntes.size() + 1);
                            String fechaHoy = "2026-06-05";
                            Apunte nuevoApunte = new Apunte(nuevoId, titulo, contenido, fechaHoy, idMateriaSeleccionada, fotoCapturadaTemporal);
                            listaApuntes.add(nuevoApunte);
                            adapter.notifyItemInserted(listaApuntes.size() - 1);
                            recyclerView.scrollToPosition(listaApuntes.size() - 1);
                        }
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> ivPreviewDialogTemporal = null)
                .show();
    }

    private void abrirCamaraSistema() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(takePictureIntent);
    }
}