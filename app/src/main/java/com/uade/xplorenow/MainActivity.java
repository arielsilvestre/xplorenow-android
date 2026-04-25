package com.uade.xplorenow;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.uade.xplorenow.data.local.TokenManager;
import com.uade.xplorenow.databinding.ActivityMainBinding;
import com.uade.xplorenow.util.AuthEventBus;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    TokenManager tokenManager;

    @Inject
    AuthEventBus authEventBus;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment ||
                    destination.getId() == R.id.registerFragment) {
                binding.bottomNavigation.setVisibility(android.view.View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(android.view.View.VISIBLE);
            }
        });

        // Auto-login sincrónico
        if (tokenManager.hasToken()) {
            navController.navigate(R.id.action_login_to_home);
        }

        // Sesión expirada (401 desde el interceptor)
        authEventBus.getSessionExpired().observe(this, expired -> {
            if (Boolean.TRUE.equals(expired)) {
                authEventBus.reset();
                navController.navigate(R.id.loginFragment, null,
                        new NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build());
            }
        });

        setupOfflineBanner();
    }

    private void setupOfflineBanner() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                runOnUiThread(() -> binding.tvOfflineBanner.setVisibility(View.GONE));
            }

            @Override
            public void onLost(@NonNull Network network) {
                runOnUiThread(() -> binding.tvOfflineBanner.setVisibility(View.VISIBLE));
            }
        };
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        cm.registerNetworkCallback(request, callback);

        // Estado inicial
        Network activeNetwork = cm.getActiveNetwork();
        boolean connected = activeNetwork != null &&
                cm.getNetworkCapabilities(activeNetwork) != null &&
                cm.getNetworkCapabilities(activeNetwork)
                        .hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        binding.tvOfflineBanner.setVisibility(connected ? View.GONE : View.VISIBLE);
    }
}
