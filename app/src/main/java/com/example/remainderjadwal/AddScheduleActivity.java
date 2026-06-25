package com.example.remainderjadwal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {

    private EditText etDosen, etRuangan, etJam, etCatatan;
    private Spinner spReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        etDosen   = findViewById(R.id.etDosen);
        etRuangan = findViewById(R.id.etRuangan);
        etJam     = findViewById(R.id.etJam);
        etCatatan = findViewById(R.id.etCatatan);
        spReminder = findViewById(R.id.spReminder);
        Button btnSave   = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Setup Spinner
        String[] reminderOptions = {"Tanpa Pengingat", "10 menit sebelum", "5 menit sebelum"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, reminderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReminder.setAdapter(adapter);

        // Jam pakai TimePicker biar lebih rapi
        etJam.setFocusable(false);
        etJam.setOnClickListener(v -> {
            TimePickerDialog tpd = new TimePickerDialog(this, (view, hour, minute) -> {
                String jam = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                etJam.setText(jam);
            }, 8, 0, true);
            tpd.show();
        });

        // Tombol Batal
        btnCancel.setOnClickListener(v -> finish());

        // Tombol Simpan
        btnSave.setOnClickListener(v -> {
            String dosen   = etDosen.getText().toString().trim();
            String ruangan = etRuangan.getText().toString().trim();
            String jam     = etJam.getText().toString().trim();
            String catatan = etCatatan.getText().toString().trim();

            if (dosen.isEmpty() || ruangan.isEmpty() || jam.isEmpty()) {
                Toast.makeText(this, "Isi Dosen, Ruangan, dan Jam terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            int reminderMinutes = 0;
            int selectedPos = spReminder.getSelectedItemPosition();
            if (selectedPos == 1) reminderMinutes = 10;
            else if (selectedPos == 2) reminderMinutes = 5;

            Schedule s = new Schedule(dosen, ruangan, jam, catatan, reminderMinutes);

            ArrayList<Schedule> list = ScheduleStorage.getSchedules(this);
            list.add(s);
            ScheduleStorage.saveSchedules(this, list);
            BadgeManager.checkScheduleCount(this, list.size());

            if (reminderMinutes > 0) {
                AlarmUtil.scheduleReminder(this, s);
            }

            hideKeyboard();

            if (!isFinishing()) finish();
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}