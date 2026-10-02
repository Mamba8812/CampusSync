package ue.edu.campussync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder> {

    private List<Materia> listaMaterias;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MateriaAdapter(List<Materia> listaMaterias) {
        this.listaMaterias = listaMaterias;
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_materia, parent, false);
        return new MateriaViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        Materia materia = listaMaterias.get(position);
        holder.tvNombre.setText(materia.getNombreMateria());
        holder.tvProfesor.setText("Prof: " + materia.getNombreProfesor());
    }

    @Override
    public int getItemCount() {
        return listaMaterias.size();
    }

    public static class MateriaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvProfesor;

        public MateriaViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreMateria);
            tvProfesor = itemView.findViewById(R.id.tvDescripcionMateria);

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