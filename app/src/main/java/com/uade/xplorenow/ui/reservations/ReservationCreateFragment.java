package com.uade.xplorenow.ui.reservations;

import dagger.hilt.android.AndroidEntryPoint;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.uade.xplorenow.databinding.FragmentReservationCreateBinding;
import com.uade.xplorenow.ui.activities.ActivityViewModel;

import java.util.Calendar;
import java.util.Locale;

@AndroidEntryPoint
public class ReservationCreateFragment extends Fragment {

    private FragmentReservationCreateBinding binding;
    private ReservationViewModel viewModel;
    private int peopleCount = 1;
    private String selectedDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReservationCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        // Recibir activityId via SafeArgs
        String activityId = ReservationCreateFragmentArgs.fromBundle(getArguments()).getActivityId();

        // Cargar nombre de la actividad para mostrarlo readonly
        ActivityViewModel activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        activityViewModel.getActivityById(activityId).observe(getViewLifecycleOwner(), result -> {
            if (result.getData() != null) {
                binding.etActivity.setText(result.getData().getName());
            }
        });

        // DatePicker
        binding.tilDate.setEndIconOnClickListener(v -> showDatePicker());
        binding.etDate.setOnClickListener(v -> showDatePicker());

        // Contador de personas
        updatePeopleCount();
        binding.btnDecrease.setOnClickListener(v -> {
            if (peopleCount > 1) {
                peopleCount--;
                updatePeopleCount();
            }
        });
        binding.btnIncrease.setOnClickListener(v -> {
            if (peopleCount < 20) {
                peopleCount++;
                updatePeopleCount();
            }
        });

        // Confirmar reserva
        binding.btnConfirm.setOnClickListener(v -> {
            if (selectedDate.isEmpty()) {
                binding.tvError.setText("Seleccioná una fecha");
                binding.tvError.setVisibility(View.VISIBLE);
                return;
            }
            binding.tvError.setVisibility(View.GONE);
            createReservation(activityId);
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                requireContext(),
                (datePicker, year, month, day) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
                    binding.etDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updatePeopleCount() {
        binding.tvPeopleCount.setText(String.valueOf(peopleCount));
        binding.btnDecrease.setEnabled(peopleCount > 1);
    }

    private void createReservation(String activityId) {
        viewModel.createReservation(activityId, selectedDate, peopleCount)
                .observe(getViewLifecycleOwner(), result -> {
                    switch (result.getStatus()) {
                        case LOADING:
                            binding.progress.setVisibility(View.VISIBLE);
                            binding.btnConfirm.setEnabled(false);
                            break;
                        case SUCCESS:
                            binding.progress.setVisibility(View.GONE);
                            // Volver a la lista de reservas
                            Navigation.findNavController(requireView()).navigateUp();
                            break;
                        case ERROR:
                            binding.progress.setVisibility(View.GONE);
                            binding.btnConfirm.setEnabled(true);
                            binding.tvError.setText(result.getMessage());
                            binding.tvError.setVisibility(View.VISIBLE);
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
