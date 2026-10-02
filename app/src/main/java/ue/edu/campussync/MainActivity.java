package ue.edu.campussync;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se eliminó EdgeToEdge.enable(this) y el ViewCompat para evitar el colapso visual
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Cargar el fragmento por defecto (Materias) al abrir la app
        if (savedInstanceState == null) {
            loadFragment(new MateriasFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_materias);
        }

        // Escuchar los clics en la barra inferior
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_tareas) {
                selectedFragment = new TareasFragment();
            } else if (id == R.id.nav_materias) {
                selectedFragment = new MateriasFragment();
            } else if (id == R.id.nav_apuntes) {
                selectedFragment = new ApuntesFragment();
            } else if (id == R.id.nav_perfil) {
                // Asegúrate de tener creado tu PerfilFragment
                selectedFragment = new PerfilFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}