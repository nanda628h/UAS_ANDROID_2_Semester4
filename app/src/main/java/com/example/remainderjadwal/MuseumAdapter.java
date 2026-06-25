package com.example.remainderjadwal;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MuseumAdapter extends RecyclerView.Adapter<MuseumAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<WrongAnswer> list;

    public MuseumAdapter(Context context, ArrayList<WrongAnswer> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_wrong_answer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WrongAnswer wa = list.get(position);

        holder.tvQuizTitle.setText("📚 " + wa.getQuizTitle());
        holder.tvPertanyaan.setText(wa.getPertanyaan());
        holder.tvJawabanUser.setText("❌ " + wa.getJawabanUser());
        holder.tvJawabanBenar.setText("✅ " + wa.getJawabanBenar());
        holder.etAlasan.setText(wa.getAlasan());

        // Simpan alasan saat user mengetik
        holder.etAlasan.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                wa.setAlasan(s.toString());
                ArrayList<WrongAnswer> all = MuseumStorage.getWrongAnswers(context);
                for (WrongAnswer item : all) {
                    if (item.getPertanyaan().equals(wa.getPertanyaan()) &&
                            item.getTimestamp() == wa.getTimestamp()) {
                        item.setAlasan(s.toString());
                        break;
                    }
                }
                MuseumStorage.saveWrongAnswers(context, all);
                BadgeManager.checkMuseumReasonFilled(context);
            }
        });

        // Tombol hapus
        holder.btnHapus.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;

            // Hapus dari list lokal
            WrongAnswer toDelete = list.get(pos);
            list.remove(pos);
            notifyItemRemoved(pos);

            // Hapus dari storage
            ArrayList<WrongAnswer> all = MuseumStorage.getWrongAnswers(context);
            all.removeIf(item ->
                    item.getPertanyaan().equals(toDelete.getPertanyaan()) &&
                            item.getTimestamp() == toDelete.getTimestamp()
            );
            MuseumStorage.saveWrongAnswers(context, all);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizTitle, tvPertanyaan, tvJawabanUser, tvJawabanBenar, btnHapus;
        EditText etAlasan;

        ViewHolder(View v) {
            super(v);
            tvQuizTitle = v.findViewById(R.id.tvQuizTitle);
            tvPertanyaan = v.findViewById(R.id.tvPertanyaan);
            tvJawabanUser = v.findViewById(R.id.tvJawabanUser);
            tvJawabanBenar = v.findViewById(R.id.tvJawabanBenar);
            etAlasan = v.findViewById(R.id.etAlasan);
            btnHapus = v.findViewById(R.id.btnHapus);
        }
    }
}