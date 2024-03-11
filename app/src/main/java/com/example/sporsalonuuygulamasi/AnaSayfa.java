package com.example.sporsalonuuygulamasi;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnaSayfa extends AppCompatActivity {
    Button yenilemeButonu, uyeEklemeButonu;
    private ListView uyeListesi;
    private FirebaseFirestore db;
    private List<Map<String, Object>> uyeBilgisiHaritasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);

        // get ınstance metodunu kullanarak Firebasei veritabanı bağlantısını kullandım
        db = FirebaseFirestore.getInstance();

        // Arayüz öğelerinin tanımlanımladım
        uyeListesi = findViewById(R.id.uyeListesi);
        yenilemeButonu = findViewById(R.id.yenilemeButonu);
        uyeEklemeButonu = findViewById(R.id.uyeEklemeButonu);

        // Yenileme butonu tıklanma durumunu burda gösterdim
        yenilemeButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshUserList();
            }
        });

        // Üye ekleme butonu tıklanma durumunu burda gösterdim
        uyeEklemeButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uyeKayit = new Intent(AnaSayfa.this, UyeKayitSayfasi.class);
                startActivity(uyeKayit);
            }
        });

        // Firestore'dan üyelerin çekilmesi ve liste oluşturulması
        db.collection("Üyeler")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Veri listesi ve haritasının oluşturulması
                        uyeBilgisiHaritasi = new ArrayList<>();

                        // Liste görünümü için veri listesi
                        List<String> uyeBilgileriListesi = new ArrayList<>();

                        // Firestore'dan gelen her bir belgeyi döngüye al
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Belgedeki verileri al
                            Map<String, Object> uyeBilgileri = documentSnapshot.getData();

                            // Veriyi haritaya ekle
                            uyeBilgisiHaritasi.add(uyeBilgileri);

                            // Görünümde gösterilecek bilgileri oluştur
                            String uyeBilgisi = "TC: " + uyeBilgileri.get("TC: ") +
                                    "\nİsim: " + uyeBilgileri.get("İsim") +
                                    "\nSoyisim: " + uyeBilgileri.get("Soyisim") +
                                    "\nÜyelik Başlangıç Tarihi: " + uyeBilgileri.get("Üyelik Başlangıç Tarihi") +
                                    "\nÜyelik Bitis Tarihi: " + uyeBilgileri.get("Üyelik Bitis Tarihi");

                            // Liste görünümü için veriyi ekledim
                            uyeBilgileriListesi.add(uyeBilgisi);
                        }

                        // Liste görünümü için adaptör oluşturdum
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AnaSayfa.this, android.R.layout.simple_list_item_1, uyeBilgileriListesi);

                        // Liste görünümüne adaptörü bağladım
                        uyeListesi.setAdapter(adapter);

                        // Liste elemanlarına uzun tıklama menüsü bağladım
                        registerForContextMenu(uyeListesi);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hata durumunda kullanıcıyı bilgilendir
                        Toast.makeText(AnaSayfa.this, "Veri alınırken hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Uzun tıklama menüsünü oluşturdum
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // contex Menüyü oluşturdum
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    // Uzun tıklama menüsünden seçim yapıldığında çalışan metod üstüne basılı tutulunca yani
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Tıklama bilgilerini al
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        // Seçili kişinin bilgilerini al
        Map<String, Object> seciliKisi = uyeBilgisiHaritasi.get(position);

        // Menü öğesinin ID'sini al
        int itemId = item.getItemId();

        // İlgili menü öğesine göre işlemleri yap
        if (itemId == R.id.uye_duzenle) {
            // Üye düzenleme sayfasına git
            Intent intent = new Intent(AnaSayfa.this, UyeDuzenlemeSayfasi.class);
            intent.putExtra("seciliKisi", (Serializable) seciliKisi);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.uye_sil) {
            // Üyeyi sil
            String tc = (String) seciliKisi.get("TC: ");
            db.collection("Üyeler").document(tc)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Silme başarılı olursa kullanıcıyı bilgilendir ve liste güncelle
                            Toast.makeText(AnaSayfa.this, "Üye başarıyla silindi", Toast.LENGTH_SHORT).show();
                            refreshUserList();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Silme başarısız olursa kullanıcıyı bilgilendir
                            Toast.makeText(AnaSayfa.this, "Üye silinirken hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        } else {
            // Diğer durumlarda varsayılan işlemi yap
            return super.onContextItemSelected(item);
        }
    }

    // Kullanıcı listesini yenileme metod
    private void refreshUserList() {
        // Firestore'dan tekrar üyelerin çekilmesi ve liste oluşturulması
        db.collection("Üyeler")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Veri listesini temizle
                        uyeBilgisiHaritasi.clear();

                        // Liste görünümü için veri listesi
                        List<String> uyeBilgileriListesi = new ArrayList<>();

                        // Firestore'dan gelen her bir belgeyi döngüye al
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Belgedeki verileri al
                            Map<String, Object> uyeBilgileri = documentSnapshot.getData();

                            // Veriyi haritaya ekle
                            uyeBilgisiHaritasi.add(uyeBilgileri);

                            // Görünümde gösterilecek bilgileri oluştur
                            String uyeBilgisi = "TC: " + uyeBilgileri.get("TC: ") +
                                    "\nİsim: " + uyeBilgileri.get("İsim") +
                                    "\nSoyisim: " + uyeBilgileri.get("Soyisim") +
                                    "\nÜyelik Başlangıç Tarihi: " + uyeBilgileri.get("Üyelik Başlangıç Tarihi") +
                                    "\nÜyelik Bitis Tarihi: " + uyeBilgileri.get("Üyelik Bitis Tarihi");

                            // Liste görünümü için veriyi ekle
                            uyeBilgileriListesi.add(uyeBilgisi);
                        }

                        // Liste görünümü için adaptör oluştur
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AnaSayfa.this, android.R.layout.simple_list_item_1, uyeBilgileriListesi);

                        // Liste görünümüne adaptörü bağla
                        uyeListesi.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hata durumunda kullanıcıyı bilgilendir
                        Toast.makeText(AnaSayfa.this, "Veri alınırken hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
