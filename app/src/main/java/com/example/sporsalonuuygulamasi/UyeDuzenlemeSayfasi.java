package com.example.sporsalonuuygulamasi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UyeDuzenlemeSayfasi extends AppCompatActivity {
    EditText tcEditText, isimEditText, soyisimEditText, uyeKayitTarihiEditText, uyelikBitisTarihiEditText;
    Button kaydetButonu;
    private FirebaseFirestore db;
    private Map<String, Object> seciliKisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uye_duzenleme_sayfasi);

        // get ınstance metodu ile Firebase Firestore nesnesi oluşturdum fire base den gelen verileri işleyebilmek adına
        db = FirebaseFirestore.getInstance();

        // gerekli arayüz elemanlarımı tanımladım
        tcEditText = findViewById(R.id.tcEditText);
        isimEditText = findViewById(R.id.isimEditText);
        soyisimEditText = findViewById(R.id.soyisimEditText);
        uyeKayitTarihiEditText = findViewById(R.id.uyeKayitTarihiEditText);
        uyelikBitisTarihiEditText = findViewById(R.id.uyelikBitisTarihiEditText);
        kaydetButonu = findViewById(R.id.kaydetButonu);

        // Ana sayfadan gelen kullanıcı bilgilerinin aldım
        seciliKisi = (Map<String, Object>) getIntent().getSerializableExtra("seciliKisi");

        // Eğer bilgiler alındıysa, arayüz elemanlarına bu bilgilerin yerleştirdim
        if (seciliKisi != null) {
            tcEditText.setText((String) seciliKisi.get("TC: "));
            isimEditText.setText((String) seciliKisi.get("İsim"));
            soyisimEditText.setText((String) seciliKisi.get("Soyisim"));
            uyeKayitTarihiEditText.setText((String) seciliKisi.get("Üyelik Başlangıç Tarihi"));
            uyelikBitisTarihiEditText.setText((String) seciliKisi.get("Üyelik Bitis Tarihi"));
        }
        else {
            // Bilgiler alınamazsa kullanıcıyı bilgilendir ve sayfayı kapat
            Toast.makeText(this, "Kullanıcı bilgileri alınamadı", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Kaydet butonuna tıklanma durumunda olacak olaylar
        kaydetButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Yeni TC kimlik numarasının alınması durumda
                 String yeniTC = tcEditText.getText().toString();

                // Yenilenmiş kullanıcı bilgileri bir harita içine yerleştirilir
                Map<String, Object> yenilenmisKisi = new HashMap<>();
                yenilenmisKisi.put("TC: ", yeniTC);
                yenilenmisKisi.put("İsim", isimEditText.getText().toString());
                yenilenmisKisi.put("Soyisim", soyisimEditText.getText().toString());
                yenilenmisKisi.put("Üyelik Başlangıç Tarihi", uyeKayitTarihiEditText.getText().toString());
                yenilenmisKisi.put("Üyelik Bitis Tarihi", uyelikBitisTarihiEditText.getText().toString());

                // Eski TC kimlik numarasının alınması
                 String eskiTC = (String) seciliKisi.get("TC: ");

                // Eğer TC kimlik numarası değişmişse eski bilgileri siler ve yeni kayıt oluşturur
                if (!eskiTC.equals(yeniTC)) {
                    db.collection("Üyeler").document(eskiTC)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Eski bilgiler başarıyla silindiyse, yeni bilgilerin eklenmesi
                                    Toast.makeText(UyeDuzenlemeSayfasi.this, "Eski üye bilgileri silindi", Toast.LENGTH_SHORT).show();
                                    db.collection("Üyeler").document(yeniTC)
                                            .set(yenilenmisKisi)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Yeni bilgiler başarıyla eklendiyse, AnaSayfa'ya yönlendir ve bilgilendir
                                                    Intent anaSayfa = new Intent(UyeDuzenlemeSayfasi.this, AnaSayfa.class);
                                                    startActivity(anaSayfa);
                                                    Toast.makeText(UyeDuzenlemeSayfasi.this, "Üye bilgileri güncellendi", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Yeni bilgiler eklenirken hata olursa kullanıcıyı bilgilendir
                                                    Toast.makeText(UyeDuzenlemeSayfasi.this, "Üye bilgileri güncellenirken hata oluştu", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Eski bilgiler silinirken hata olursa kullanıcıyı bilgilendir
                                    Toast.makeText(UyeDuzenlemeSayfasi.this, "Eski üye bilgileri silinirken hata oluştu", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    // TC kimlik numarası değişmemişse sadece eski bilgileri günceller
                    db.collection("Üyeler").document(yeniTC)
                            .set(yenilenmisKisi)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Bilgiler başarıyla güncellendiyse, AnaSayfa'ya yönlendir ve bilgilendir
                                    Intent anaSayfa = new Intent(UyeDuzenlemeSayfasi.this, AnaSayfa.class);
                                    startActivity(anaSayfa);
                                    Toast.makeText(UyeDuzenlemeSayfasi.this, "Üye bilgileri güncellendi", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Bilgiler güncellenirken hata olursa kullanıcıyı bilgilendir
                                    Toast.makeText(UyeDuzenlemeSayfasi.this, "Üye bilgileri güncellenirken hata oluştu", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}
