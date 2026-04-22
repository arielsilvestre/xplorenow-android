package com.uade.xplorenow.ui.reservations;

import android.view.LayoutInflater;
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

    private List<Reservation> reservations = new ArrayList<>();

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations != null ? reservations : new ArrayList<>();
        notifyDataSetChanged();
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
        holder.bind(reservations.get(position));
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

        void bind(Reservation reservation) {
            // Nombre de la actividad
            if (reservation.getActivity() != null) {
                binding.tvReservationActivity.setText(reservation.getActivity().getName());
            } else {
                binding.tvReservationActivity.setText("Actividad");
            }

            binding.tvReservationDate.setText(reservation.getDate());
            binding.tvReservationPeople.setText(reservation.getPeople() + " persona(s)");

            // Estado con color
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
