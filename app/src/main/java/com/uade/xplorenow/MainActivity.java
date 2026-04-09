package com.uade.xplorenow;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.uade.xplorenow.data.local.SessionManager;
import com.uade.xplorenow.databinding.ActivityMainBinding;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    SessionManager sessionManager;

    private ActivityMainBinding binding;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        // Ocultar BottomNav en pantallas de auth
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment ||
                destination.getId() == R.id.registerFragment) {
                binding.bottomNavigation.setVisibility(android.view.View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(android.view.View.VISIBLE);
            }
        });

        // Auto-login: si hay token guardado, navegar directo a Home
        disposables.add(
            sessionManager.getToken()
                .take(1) // solo el primer valor
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    token -> {
                        if (!token.isEmpty()) {
                            navController.navigate(R.id.action_login_to_home);
                        }
                    },
                    error -> { /* sin sesión guardada, queda en Login */ }
                )
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
