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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PerfilFragment extends Fragment {

    private ImageView ivFotoPerfil;
    private TextView tvNombreUsuario;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public PerfilFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        CardView cvFotoPerfil = view.findViewById(R.id.cvFotoPerfil);
        LinearLayout llEditarNombre = view.findViewById(R.id.llEditarNombre);
        Switch switchNotificaciones = view.findViewById(R.id.switchNotificaciones);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        // 1. Configurar Notificaciones (Simulación)
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Notificaciones activadas", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Notificaciones silenciadas", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Configurar edición de nombre
        llEditarNombre.setOnClickListener(v -> mostrarDialogoEditarNombre());

        // 3. Configurar foto de perfil
        cvFotoPerfil.setOnClickListener(v -> mostrarOpcionesFoto());

        // Lanzador de cámara
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                        ivFotoPerfil.setPadding(0,0,0,0);
                        ivFotoPerfil.setImageBitmap(photo);
                    }
                }
        );

        // Lanzador de galería
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        ivFotoPerfil.setPadding(0,0,0,0);
                        ivFotoPerfil.setImageURI(uri);
                    }
                }
        );

        // Lanzador de permisos de cámara
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirCamara();
                    } else {
                        Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 4. Configurar botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void mostrarDialogoEditarNombre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Editar Nombre");

        final EditText input = new EditText(getContext());
        input.setText(tvNombreUsuario.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = input.getText().toString().trim();
            if (!nuevoNombre.isEmpty()) {
                tvNombreUsuario.setText(nuevoNombre);
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarOpcionesFoto() {
        CharSequence[] opciones = {"Tomar Foto", "Elegir de la Galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Actualizar Foto de Perfil");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    abrirCamara();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            } else if (which == 1) {
                galleryLauncher.launch("image/*");
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(takePictureIntent);
    }
}