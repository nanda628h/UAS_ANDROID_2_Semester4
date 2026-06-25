package com.example.remainderjadwal;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Badge> badges;

    public BadgeAdapter(Context context, ArrayList<Badge> badges) {
        this.context = context;
        this.badges = badges;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_badge, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Badge b = badges.get(position);

        holder.tvIcon.setText(b.getIcon());
        holder.tvName.setText(b.getName());
        holder.tvDesc.setText(b.getDescription());

        if (b.isUnlocked()) {
            holder.tvStatus.setText("✓ Terbuka");
            holder.tvStatus.setTextColor(Color.parseColor("#22C55E"));
            holder.card.setCardBackgroundColor(Color.parseColor("#0F2A1A"));
            holder.tvIcon.setAlpha(1f);
            holder.tvName.setTextColor(Color.parseColor("#F1F5F9"));
        } else {
            holder.tvStatus.setText("Terkunci");
            holder.tvStatus.setTextColor(Color.parseColor("#475569"));
            holder.card.setCardBackgroundColor(Color.parseColor("#1E293B"));
            holder.tvIcon.setAlpha(0.3f);
            holder.tvName.setTextColor(Color.parseColor("#475569"));
        }
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvDesc, tvStatus;
        CardView card;

        ViewHolder(View v) {
            super(v);
            tvIcon = v.findViewById(R.id.tvBadgeIcon);
            tvName = v.findViewById(R.id.tvBadgeName);
            tvDesc = v.findViewById(R.id.tvBadgeDesc);
            tvStatus = v.findViewById(R.id.tvBadgeStatus);
            card = (CardView) v; // item root adalah CardView
        }
    }
}