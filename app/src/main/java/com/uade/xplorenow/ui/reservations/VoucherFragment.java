package com.uade.xplorenow.ui.reservations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.uade.xplorenow.R;
import com.uade.xplorenow.databinding.FragmentVoucherBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VoucherFragment extends Fragment {

    private FragmentVoucherBinding binding;
    private ReservationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVoucherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        String reservationId = VoucherFragmentArgs.fromBundle(getArguments()).getReservationId();

        viewModel.getReservationById(reservationId).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.cardVoucher.setVisibility(View.GONE);
                    binding.tvError.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    binding.progress.setVisibility(View.GONE);
                    binding.tvError.setVisibility(View.GONE);
                    binding.cardVoucher.setVisibility(View.VISIBLE);

                    if (result.getData() != null) {
                        String actName = result.getData().getActivity() != null
                                ? result.getData().getActivity().getName()
                                : "Actividad";
                        binding.tvVoucherActivity.setText(actName);
                        binding.tvVoucherDate.setText(result.getData().getDate());
                        binding.tvVoucherPeople.setText(result.getData().getPeople() + " persona(s)");
                        binding.tvVoucherId.setText(result.getData().getId());

                        String status = result.getData().getStatus();
                        binding.chipVoucherStatus.setText(formatStatus(status));
                        int colorRes;
                        switch (status != null ? status : "") {
                            case "confirmed": colorRes = R.color.status_confirmed; break;
                            case "cancelled": colorRes = R.color.status_cancelled; break;
                            default:          colorRes = R.color.status_pending;   break;
                        }
                        binding.chipVoucherStatus.setChipBackgroundColorResource(colorRes);
                    }
                    break;

                case ERROR:
                    binding.progress.setVisibility(View.GONE);
                    binding.tvError.setText(result.getMessage());
                    binding.tvError.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private String formatStatus(String status) {
        if (status == null) return "Pendiente";
        switch (status) {
            case "confirmed": return "Confirmada";
            case "cancelled": return "Cancelada";
            default:          return "Pendiente";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
