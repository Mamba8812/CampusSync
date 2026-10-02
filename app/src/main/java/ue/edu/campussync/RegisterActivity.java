package ue.edu.campussync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    // 1. Declaramos las variables
    private EditText etRegisterEmail, etRegisterPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvIniciaSesion;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initObjects();

        // Si el usuario ya tiene cuenta, cerramos esta pantalla para que regrese al Login
        tvIniciaSesion.setOnClickListener(view -> finish());

        // Al presionar el botón, ejecutamos el método de registro
        btnRegister.setOnClickListener(view -> registrarUsuario());
    }

    // 2. Inicializamos los objetos
    private void initObjects() {
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvIniciaSesion = findViewById(R.id.tvIniciaSesion);

        mAuth = FirebaseAuth.getInstance();
    }

    // 3. metodo para crear la cuenta en Firebase
    private void registrarUsuario() {
        String correo = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validar que no haya campos vacíos
        if (correo.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que Firebase acepte la longitud de la contraseña (mínimo 6 caracteres)
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Comando de Firebase para CREAR el usuario
        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // ¡Registro exitoso!
                        Toast.makeText(RegisterActivity.this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();

                        // Opcional: Navegar directamente al MainActivity
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Cerramos la pantalla de registro
                    } else {
                        // Falló el registro (correo ya existe, formato inválido, etc.)
                        Toast.makeText(RegisterActivity.this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}