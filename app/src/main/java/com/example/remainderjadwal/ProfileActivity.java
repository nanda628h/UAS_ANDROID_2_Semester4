package com.example.remainderjadwal;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREF_NAME = "profile_pref";
    private static final String KEY_NAME = "nama";
    private static final String KEY_JURUSAN = "jurusan";
    private static final String KEY_PHOTO = "photo_path";

    private ImageView ivPhoto;
    private EditText etNama, etJurusan;
    private TextView tvTotalKuis, tvRataSkor;
    private SharedPreferences prefs;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        ivPhoto    = findViewById(R.id.ivPhoto);
        etNama     = findViewById(R.id.etNama);
        etJurusan  = findViewById(R.id.etJurusan);
        tvTotalKuis = findViewById(R.id.tvTotalKuis);
        tvRataSkor  = findViewById(R.id.tvRataSkor);
        Button btnSave      = findViewById(R.id.btnSave);
        Button btnPickPhoto = findViewById(R.id.btnPickPhoto);
        Button btnBack      = findViewById(R.id.btnBack);

        // Load data tersimpan
        etNama.setText(prefs.getString(KEY_NAME, ""));
        etJurusan.setText(prefs.getString(KEY_JURUSAN, ""));
        loadPhoto();
        loadStats();

        // Picker foto dari galeri
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        savePhotoToInternal(uri);
                        loadPhoto();
                    }
                }
        );

        btnPickPhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String jurusan = etJurusan.getText().toString().trim();
            prefs.edit()
                    .putString(KEY_NAME, nama)
                    .putString(KEY_JURUSAN, jurusan)
                    .apply();
            Toast.makeText(this, "✅ Profil berhasil disimpan!", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadPhoto() {
        String path = prefs.getString(KEY_PHOTO, null);
        if (path != null) {
            File f = new File(path);
            if (f.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(path);
                ivPhoto.setImageBitmap(bm);
                return;
            }
        }
        ivPhoto.setImageResource(android.R.drawable.ic_menu_myplaces);
    }

    private void savePhotoToInternal(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap bm = BitmapFactory.decodeStream(is);
            File file = new File(getFilesDir(), "profile_photo.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            prefs.edit().putString(KEY_PHOTO, file.getAbsolutePath()).apply();
        } catch (IOException e) {
            Toast.makeText(this, "Gagal simpan foto", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadStats() {
        // Hitung total kuis dari QuizStorage
        java.util.ArrayList<Quiz> quizzes = QuizStorage.getQuizzes(this);
        int totalKuis = quizzes != null ? quizzes.size() : 0;
        tvTotalKuis.setText("📚 Total Kuis: " + totalKuis);

        // Rata-rata skor dari SharedPreferences (disimpan tiap selesai kuis)
        int totalSkor = prefs.getInt("total_skor", 0);
        int jumlahMain = prefs.getInt("jumlah_main", 0);
        if (jumlahMain > 0) {
            int rata = totalSkor / jumlahMain;
            tvRataSkor.setText("⭐ Rata-rata Skor: " + rata + "%");
        } else {
            tvRataSkor.setText("⭐ Rata-rata Skor: Belum ada data");
        }
    }
}