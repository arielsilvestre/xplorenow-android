package com.uade.xplorenow.ui.reservations;

import dagger.hilt.android.AndroidEntryPoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;

import com.google.android.material.snackbar.Snackbar;
import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.databinding.FragmentReservationListBinding;

@AndroidEntryPoint
public class ReservationListFragment extends Fragment {

    private FragmentReservationListBinding binding;
    private ReservationViewModel viewModel;
    private ReservationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReservationListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        adapter = new ReservationAdapter();
        adapter.setCancelListener(this::confirmCancel);
        adapter.setVoucherListener(reservation -> {
            ReservationListFragmentDirections.ActionReservationListToVoucher action =
                    ReservationListFragmentDirections.actionReservationListToVoucher(reservation.getId());
            Navigation.findNavController(requireView()).navigate(action);
        });
        binding.rvReservations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReservations.setAdapter(adapter);

        loadActiveReservations();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) loadActiveReservations();
                else loadHistory();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadActiveReservations() {
        viewModel.getMyReservations().observe(getViewLifecycleOwner(), result -> showResult(result, "No tenés reservas activas"));
    }

    private void loadHistory() {
        viewModel.getReservationHistory().observe(getViewLifecycleOwner(), result -> showResult(result, "Sin historial de actividades"));
    }

    private void showResult(com.uade.xplorenow.util.Resource<java.util.List<Reservation>> result, String emptyText) {
        switch (result.getStatus()) {
            case LOADING:
                binding.progress.setVisibility(View.VISIBLE);
                binding.tvEmpty.setVisibility(View.GONE);
                binding.tvError.setVisibility(View.GONE);
                break;
            case SUCCESS:
                binding.progress.setVisibility(View.GONE);
                if (result.getData() == null || result.getData().isEmpty()) {
                    binding.tvEmpty.setText(emptyText);
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmpty.setVisibility(View.GONE);
                    adapter.setReservations(result.getData());
                }
                break;
            case ERROR:
                binding.progress.setVisibility(View.GONE);
                binding.tvError.setText(result.getMessage());
                binding.tvError.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void confirmCancel(Reservation reservation) {
        String name = reservation.getActivity() != null ? reservation.getActivity().getName() : "esta actividad";
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancelar reserva")
                .setMessage("¿Estás seguro de que querés cancelar la reserva de \"" + name + "\"?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> doCancel(reservation))
                .setNegativeButton("Volver", null)
                .show();
    }

    private void doCancel(Reservation reservation) {
        viewModel.cancelReservation(reservation.getId()).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    break;
                case SUCCESS:
                    Snackbar.make(binding.getRoot(), "Reserva cancelada", Snackbar.LENGTH_SHORT).show();
                    viewModel.getMyReservations().observe(getViewLifecycleOwner(), refreshed -> {
                        if (refreshed.getData() != null) adapter.setReservations(refreshed.getData());
                    });
                    break;
                case ERROR:
                    Snackbar.make(binding.getRoot(), result.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
