package com.example.remainderjadwal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Schedule> schedules;

    public ScheduleAdapter(Context context, ArrayList<Schedule> schedules) {
        this.context = context;
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_schedule_weather, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule s = schedules.get(position);

        holder.tvDosen.setText(s.getDosen());
        holder.tvJam.setText(s.getJam());
        holder.tvRuangan.setText("📍 " + s.getRuangan());

        // Hitung weather berdasarkan jam
        WeatherInfo weather = getWeather(s.getJam());
        holder.tvWeatherIcon.setText(weather.icon);
        holder.tvWeatherStatus.setText(weather.status);
        holder.layoutWeather.setBackgroundColor(Color.parseColor(weather.bgColor));
        holder.cardWeather.setCardBackgroundColor(Color.parseColor(weather.bgColor));

        // Klik item → Detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ScheduleDetailActivity.class);
            intent.putExtra("schedule", s);
            context.startActivity(intent);
        });

        // Tombol Hapus
        holder.btnDelete.setOnClickListener(v -> {
            ScheduleStorage.deleteSchedule(context, s.getId());
            schedules.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, schedules.size());
            Toast.makeText(context, "Jadwal dihapus", Toast.LENGTH_SHORT).show();
        });
    }

    private WeatherInfo getWeather(String jam) {
        try {
            String[] parts = jam.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar now = Calendar.getInstance();
            int nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
            int scheduleMinutes = hour * 60 + minute;
            int diff = scheduleMinutes - nowMinutes;

            if (diff < 0) {
                // Sudah lewat
                return new WeatherInfo("🌙", "Sudah selesai", "#1E293B");
            } else if (diff <= 30) {
                // Kurang dari 30 menit = BADAI
                return new WeatherInfo("⛈️", "Segera dimulai! " + diff + " menit lagi", "#7F1D1D");
            } else if (diff <= 120) {
                // 30 menit - 2 jam = BERAWAN
                return new WeatherInfo("⛅", "Dalam " + (diff / 60 > 0 ? diff / 60 + " jam " : "") + (diff % 60) + " menit lagi", "#1E3A5F");
            } else {
                // Lebih dari 2 jam = CERAH
                return new WeatherInfo("🌤️", "Masih " + (diff / 60) + " jam lagi", "#14532D");
            }
        } catch (Exception e) {
            return new WeatherInfo("📚", "Jadwal", "#1E293B");
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    static class WeatherInfo {
        String icon, status, bgColor;
        WeatherInfo(String icon, String status, String bgColor) {
            this.icon = icon;
            this.status = status;
            this.bgColor = bgColor;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDosen, tvJam, tvRuangan, tvWeatherIcon, tvWeatherStatus;
        ImageButton btnDelete;
        CardView cardWeather;
        View layoutWeather;

        ViewHolder(View itemView) {
            super(itemView);
            tvDosen = itemView.findViewById(R.id.tvDosen);
            tvJam = itemView.findViewById(R.id.tvJam);
            tvRuangan = itemView.findViewById(R.id.tvRuangan);
            tvWeatherIcon = itemView.findViewById(R.id.tvWeatherIcon);
            tvWeatherStatus = itemView.findViewById(R.id.tvWeatherStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardWeather = itemView.findViewById(R.id.cardWeather);
            layoutWeather = itemView.findViewById(R.id.layoutWeather);
        }
    }
}