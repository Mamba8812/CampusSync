package ue.edu.campussync;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //1.Declaramos variables

    private TextView tvTitleLogin;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvNoCuenta;
    private TextView tvRegistrarse;
    private TextView tvOlvidoPassword;

    //clase oficial que proporciona Google en su libreria para manejar todo lo relacionado con cuentas, control de sesiones, correos y contraseñas.
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        initObjects();
        btnLogin.setOnClickListener(view -> iniciarSesion());

        // Navegar a la pantalla de Registro
        tvRegistrarse.setOnClickListener(view -> {
            android.content.Intent intent = new android.content.Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvOlvidoPassword.setOnClickListener(view -> recuperarPassword());
    }

    //5. creamos metodo OnStart para hacer verificaciones rápidas justo en el momento en que el usuario vuelve a ver la aplicación
    @Override
    protected void onStart() {
        super.onStart();
        // Verificamos si ya hay un usuario logueado en el celular
        if (mAuth.getCurrentUser() != null) {
            // Si ya existe, lo mandamos directo al MainActivity
            android.content.Intent intent = new android.content.Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Cerramos el Login para que no pueda retroceder
        }
    }
    //2. inicializamos los objetos/atributos declarados
    public void initObjects(){
        this.tvTitleLogin = findViewById(R.id.tvTitleLogin);
        this.etEmail = findViewById(R.id.etEmail);
        this.etPassword = findViewById(R.id.etPassword);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.tvNoCuenta = findViewById(R.id.tvNoCuenta);
        this.tvRegistrarse = findViewById(R.id.tvRegistrarse);
        this.tvOlvidoPassword = findViewById(R.id.tvOlvidoPassword);
        mAuth = FirebaseAuth.getInstance();
    }

    //3.Creamos metodo para iniciar sesion
    private void iniciarSesion() {
        // campturamos lo que el usuario escribió [trim]función de Java que recorta y elimina cualquier espacio en blanco que haya quedado al principio o al final de un texto.
        String correo = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar que no dejen campos vacíos
        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        //Enviar las credenciales a Firebase para validarlas. aca usamos el objeto (mAuth) instanciado de la calse FirebaseAuth
        //para llamar al metodo ignInWithEmailAndPassword, el cual valida correo y contraseña del usuario (este proceso es asincrono
        //por esto mismo usamos la funcion addOnCompleteListener)
        mAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // ¡Login exitoso!
                        Toast.makeText(LoginActivity.this, "¡Bienvenido a CampusSync!", Toast.LENGTH_SHORT).show();

                        // Crear el "puente" hacia la pantalla principal
                        android.content.Intent intent = new android.content.Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        // Cerrar la pantalla de login para que al presionar el botón "Atrás" del celular no se devuelva aquí
                        finish();
                    } else {
                        // Falló el login (contraseña incorrecta, usuario no existe, etc.)
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //4.creamos metodo para recuperar contraseña como mensaje emergente
    private void recuperarPassword() {

        // Construimos la estructura de la ventana emergente
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Contraseña");
        builder.setMessage("Ingresa tu correo institucional para enviarte el enlace de recuperación:");

        // Creamos un campo de texto por código para meterlo en la ventana
        final EditText input = new EditText(this);
        input.setHint("estudiante@ue.edu.co");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Programamos el botón "Enviar" de la ventana
        builder.setPositiveButton("Enviar enlace", (dialog, which) -> {
            String correo = input.getText().toString().trim();

            if (correo.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Debes ingresar un correo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Enviamos la petición a Firebase
            mAuth.sendPasswordResetEmail(correo)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "¡Enlace enviado! Revisa tu correo.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: Verifica que el correo esté registrado.", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Programamos el botón "Cancelar" para simplemente cerrar la ventana
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        // Mostramos la ventana emergente en pantalla
        builder.show();
    }


}