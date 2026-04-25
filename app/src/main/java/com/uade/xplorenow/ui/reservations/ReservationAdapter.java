package com.uade.xplorenow.ui.reservations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.uade.xplorenow.R;
import com.uade.xplorenow.data.model.Reservation;
import com.uade.xplorenow.databinding.ItemReservationBinding;

import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    public interface OnCancelListener {
        void onCancel(Reservation reservation);
    }

    public interface OnVoucherListener {
        void onViewVoucher(Reservation reservation);
    }

    private List<Reservation> reservations = new ArrayList<>();
    private OnCancelListener cancelListener;
    private OnVoucherListener voucherListener;

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations != null ? reservations : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCancelListener(OnCancelListener listener) {
        this.cancelListener = listener;
    }

    public void setVoucherListener(OnVoucherListener listener) {
        this.voucherListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReservationBinding binding = ItemReservationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reservations.get(position), cancelListener, voucherListener);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReservationBinding binding;

        ViewHolder(ItemReservationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Reservation reservation, OnCancelListener cancelListener, OnVoucherListener voucherListener) {
            if (reservation.getActivity() != null) {
                binding.tvReservationActivity.setText(reservation.getActivity().getName());
            } else {
                binding.tvReservationActivity.setText("Actividad");
            }

            binding.tvReservationDate.setText(reservation.getDate());
            binding.tvReservationPeople.setText(reservation.getPeople() + " persona(s)");

            String status = reservation.getStatus();
            binding.chipStatus.setText(formatStatus(status));
            int colorRes;
            switch (status != null ? status : "") {
                case "confirmed": colorRes = R.color.status_confirmed; break;
                case "cancelled": colorRes = R.color.status_cancelled; break;
                default:          colorRes = R.color.status_pending;   break;
            }
            binding.chipStatus.setChipBackgroundColorResource(colorRes);
            binding.viewStatusBar.setBackgroundColor(
                    ContextCompat.getColor(binding.getRoot().getContext(), colorRes));

            // Botón cancelar: solo visible para pending y confirmed
            boolean canCancel = "pending".equals(status) || "confirmed".equals(status);
            binding.btnCancelReservation.setVisibility(canCancel ? View.VISIBLE : View.GONE);
            if (canCancel) {
                binding.btnCancelReservation.setOnClickListener(v -> {
                    if (cancelListener != null) cancelListener.onCancel(reservation);
                });
            }

            // Botón voucher: solo para confirmed
            boolean isConfirmed = "confirmed".equals(status);
            binding.btnViewVoucher.setVisibility(isConfirmed ? View.VISIBLE : View.GONE);
            if (isConfirmed) {
                binding.btnViewVoucher.setOnClickListener(v -> {
                    if (voucherListener != null) voucherListener.onViewVoucher(reservation);
                });
            }
        }

        private String formatStatus(String status) {
            if (status == null) return "Pendiente";
            switch (status) {
                case "confirmed": return "Confirmada";
                case "cancelled": return "Cancelada";
                default:          return "Pendiente";
            }
        }
    }
}
