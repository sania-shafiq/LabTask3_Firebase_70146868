package com.uol.lostfound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** RecyclerView adapter (Lab 7) with item clicks (Lab Task 2). */
public class LostItemAdapter extends RecyclerView.Adapter<LostItemAdapter.ViewHolder> {

    /** Item-click interface from Lab Task 2. */
    public interface OnItemClickListener {
        void onItemClick(LostItem item, int position);
    }

    private final Context context;
    private final List<LostItem> items;
    private OnItemClickListener listener;

    public LostItemAdapter(Context context, List<LostItem> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // ---- the three overridden methods ----

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_lost, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LostItem item = items.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.tvLocation.setText(item.getLocation());
        holder.tvContact.setText(item.getContact());

        // Type pill
        boolean isLost = "Lost".equalsIgnoreCase(item.getType());
        holder.tvType.setText(isLost ? "LOST" : "FOUND");
        holder.tvType.setBackgroundResource(isLost ? R.drawable.bg_type_lost : R.drawable.bg_type_found);
        holder.tvType.setTextColor(ContextCompat.getColor(context,
                isLost ? R.color.lost_red : R.color.found_green));

        // Status + Returned look (grey card)
        boolean returned = "Returned".equalsIgnoreCase(item.getStatus());
        if (returned) {
            holder.tvStatus.setText("\u2713 Returned");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.found_green));
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_returned));
            holder.card.setCardElevation(0f);
            holder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.text_returned));
        } else {
            holder.tvStatus.setText("\u25CF Open");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.open_orange));
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.card.setCardElevation(4f * context.getResources().getDisplayMetrics().density);
            holder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.text_dark));
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(items.get(pos), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ---- static inner ViewHolder ----

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvItemName, tvType, tvLocation, tvContact, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardRoot);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvType = itemView.findViewById(R.id.tvType);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
