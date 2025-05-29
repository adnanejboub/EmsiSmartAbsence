package com.example.emsismartpresence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private List<Document> documentList;

    public DocumentAdapter(List<Document> documentList) {
        this.documentList = documentList;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {
        Document document = documentList.get(position);
        holder.textViewTitre.setText(document.getTitre());
        holder.textViewDescription.setText(document.getDescription());
        holder.textViewDate.setText(document.getDate());
        holder.textViewType.setText(document.getType());
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitre, textViewDescription, textViewDate, textViewType;

        public DocumentViewHolder(View itemView) {
            super(itemView);
            textViewTitre = itemView.findViewById(R.id.text_view_titre);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewType = itemView.findViewById(R.id.text_view_type);
        }
    }
}