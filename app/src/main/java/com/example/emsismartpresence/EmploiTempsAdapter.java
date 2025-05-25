package com.example.emsismartpresence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EmploiTempsAdapter extends RecyclerView.Adapter<EmploiTempsAdapter.EmploiTempsViewHolder> {
    private List<EmploiTempsItem> emploiTempsList;

    public EmploiTempsAdapter(List<EmploiTempsItem> emploiTempsList) {
        this.emploiTempsList = emploiTempsList;
    }

    @NonNull
    @Override
    public EmploiTempsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.emploi_temps_item, parent, false);
        return new EmploiTempsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmploiTempsViewHolder holder, int position) {
        EmploiTempsItem item = emploiTempsList.get(position);
        holder.dayText.setText(item.getDay());
        holder.timeText.setText(item.getStartTime() + " - " + item.getEndTime());
        holder.courseText.setText(item.getCourseName());
        holder.roomText.setText("Salle: " + item.getRoom());
        holder.groupText.setText("Groupe: " + item.getGroupId());
        holder.siteText.setText("Site: " + item.getSiteId());
        holder.levelText.setText("Niveau: " + item.getLevel());
    }

    @Override
    public int getItemCount() {
        return emploiTempsList.size();
    }

    static class EmploiTempsViewHolder extends RecyclerView.ViewHolder {
        TextView dayText, timeText, courseText, roomText, groupText, siteText, levelText;

        EmploiTempsViewHolder(View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.emploi_temps_day);
            timeText = itemView.findViewById(R.id.emploi_temps_time);
            courseText = itemView.findViewById(R.id.emploi_temps_course);
            roomText = itemView.findViewById(R.id.emploi_temps_room);
            groupText = itemView.findViewById(R.id.emploi_temps_group);
            siteText = itemView.findViewById(R.id.emploi_temps_site);
            levelText = itemView.findViewById(R.id.emploi_temps_level);
        }
    }
}