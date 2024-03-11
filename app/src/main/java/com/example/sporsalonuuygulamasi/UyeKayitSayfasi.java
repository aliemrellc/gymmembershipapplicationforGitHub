package com.example.sporsalonuuygulamasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UyeKayitSayfasi extends AppCompatActivity {
    EditText isimEditText, soyisimEditText, uyeKayitTarihiEditText, uyelikBitisTarihiEditText, tcEditText;
    Button kaydetButonu;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uye_kayit_sayfasi);

        // Firebase Firestore nesnesi oluşturulması
        db = FirebaseFirestore.getInstance();

        // Arayüz elemanlarının tanımlanması
        isimEditText = findViewById(R.id.isimEditText);
        soyisimEditText = findViewById(R.id.soyisimEditText);
        uyeKayitTarihiEditText = findViewById(R.id.uyeKayitTarihiEditText);
        uyelikBitisTarihiEditText = findViewById(R.id.uyelikBitisTarihiEditText);
        tcEditText = findViewById(R.id.tcEditText);
        kaydetButonu = findViewById(R.id.kaydetButonu);

        // Bugünün tarihini alarak üyelik başlangıç tarihine set etme
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String bugununTarihi = dateFormat.format(Calendar.getInstance().getTime());
        uyeKayitTarihiEditText.setText(bugununTarihi);

        // Kaydet butonuna tıklanma durumu
        kaydetButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gerekli bilgilerin alınması
                String isim = isimEditText.getText().toString();
                String soyisim = soyisimEditText.getText().toString();
                String uyeKayitTarihi = uyeKayitTarihiEditText.getText().toString();
                String uyelikBitisTarihi = uyelikBitisTarihiEditText.getText().toString();
                String tc = tcEditText.getText().toString();

                // Gerekli bilgilerin boş olup olmadığının kontrolü
                if (!isim.isEmpty() && !soyisim.isEmpty() && !uyelikBitisTarihi.isEmpty() && !tc.isEmpty()) {
                    // Üye bilgilerinin bir harita içine yerleştirilmesi
                    Map<String, Object> uyeBilgileri = new HashMap<>();
                    uyeBilgileri.put("İsim", isim);
                    uyeBilgileri.put("Soyisim", soyisim);
                    uyeBilgileri.put("Üyelik Başlangıç Tarihi", uyeKayitTarihi);
                    uyeBilgileri.put("Üyelik Bitis Tarihi", uyelikBitisTarihi);
                    uyeBilgileri.put("TC: ", tc);

                    // Firestore'a üye bilgilerinin eklenmesi
                    db.collection("Üyeler").document(tc)
                            .set(uyeBilgileri)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Başarıyla kaydedilirse AnaSayfa'ya yönlendir ve bilgilendir
                                    Intent anaSayfa = new Intent(UyeKayitSayfasi.this, AnaSayfa.class);
                                    startActivity(anaSayfa);
                                    Toast.makeText(UyeKayitSayfasi.this, "Üye bilgileri Firestore'a kaydedildi", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    // Kaydetme sırasında hata oluşursa kullanıcıyı bilgilendir
                                    Toast.makeText(UyeKayitSayfasi.this, "Üye bilgileri kaydedilirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    // Gerekli bilgiler eksikse kullanıcıyı bilgilendir
                    Toast.makeText(UyeKayitSayfasi.this, "Tüm alanları doldurunuz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
