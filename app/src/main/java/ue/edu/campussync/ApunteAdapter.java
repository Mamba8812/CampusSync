package ue.edu.campussync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ApunteAdapter extends RecyclerView.Adapter<ApunteAdapter.ApunteViewHolder> {

    private List<Apunte> listaApuntes;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ApunteAdapter(List<Apunte> listaApuntes) {
        this.listaApuntes = listaApuntes;
    }

    @NonNull
    @Override
    public ApunteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apunte, parent, false);
        return new ApunteViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ApunteViewHolder holder, int position) {
        Apunte apunte = listaApuntes.get(position);
        holder.tvTitulo.setText(apunte.getTitulo());
        holder.tvContenido.setText(apunte.getContenido());

        if (apunte.getFotoBitmap() != null) {
            holder.ivMiniatura.setImageBitmap(apunte.getFotoBitmap());
            holder.ivMiniatura.setVisibility(View.VISIBLE);
        } else {
            holder.ivMiniatura.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaApuntes.size();
    }

    public static class ApunteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvContenido;
        ImageView ivMiniatura;

        public ApunteViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloApunte);
            tvContenido = itemView.findViewById(R.id.tvContenidoApunte);
            ivMiniatura = itemView.findViewById(R.id.ivFotoMiniatura);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}