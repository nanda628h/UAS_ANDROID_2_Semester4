package com.example.remainderjadwal;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MuseumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);

        RecyclerView rv = findViewById(R.id.rvMuseum);
        TextView tvEmpty = findViewById(R.id.tvEmpty);

        ArrayList<WrongAnswer> list = MuseumStorage.getWrongAnswers(this);
        BadgeManager.checkMuseumWrongCount(this, list.size());

        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new MuseumAdapter(this, list));
        }
    }
}