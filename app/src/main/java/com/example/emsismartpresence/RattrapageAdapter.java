package com.example.emsismartpresence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RattrapageAdapter extends RecyclerView.Adapter<RattrapageAdapter.ViewHolder> {
    private List<RattrapageItem> rattrapageList;

    public RattrapageAdapter(List<RattrapageItem> rattrapageList) {
        this.rattrapageList = rattrapageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rattrapage_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RattrapageItem item = rattrapageList.get(position);
        holder.courseName.setText(item.getCourseName());
        holder.groupId.setText("Groupe: " + item.getGroupId());
        holder.date.setText("Date: " + item.getDate());
        holder.time.setText("Horaire: " + item.getStartTime() + " - " + item.getEndTime());
        holder.room.setText("Salle: " + item.getRoom());
        holder.siteId.setText("Site: " + item.getSiteId());
        holder.level.setText("Niveau: " + item.getLevel());
    }

    @Override
    public int getItemCount() {
        return rattrapageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, groupId, date, time, room, siteId, level;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            groupId = itemView.findViewById(R.id.group_id);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            room = itemView.findViewById(R.id.room);
            siteId = itemView.findViewById(R.id.site_id);
            level = itemView.findViewById(R.id.level);
        }
    }
}