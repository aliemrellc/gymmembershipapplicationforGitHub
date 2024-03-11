package com.example.sporsalonuuygulamasi;

// gerekli kütüphanelerimi import ettim son ikisi firebase ile ilgili olmak üzere
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class GirisSayfasi extends AppCompatActivity {
    EditText emailEditText, sifreEditText;
    Button girisButonu, kayitOlButonu;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_sayfasi);

        // Get ınstance metodu kullanarak Firebase autentıc nesnesini oluşturdum
        mAuth = FirebaseAuth.getInstance();

        // Arayüz elemanlarının tanımlanması
        emailEditText = findViewById(R.id.emailEditText);
        sifreEditText = findViewById(R.id.sifreEditText);
        girisButonu = findViewById(R.id.girisButonu);
        kayitOlButonu = findViewById(R.id.kayitOlButonu);

        // Giriş butonuna tıklanma durumu
        girisButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Giriş için kullanıcıdan  email ve şifre alınması
                String email = emailEditText.getText().toString();
                String sifre = sifreEditText.getText().toString();

                // Email ve şifre kontrolü
                if (!email.isEmpty() && !sifre.isEmpty()) {

                    mAuth.signInWithEmailAndPassword(email, sifre)
                            .addOnCompleteListener(GirisSayfasi.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        // Eğer giriş başarılı ise kullanıcıyı AnaSayfaya yönlendir
                                        Toast.makeText(GirisSayfasi.this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(GirisSayfasi.this, AnaSayfa.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Başarısız giriş durumunda giriş başarısız diye toast string mesajı bastırıyo
                                        Toast.makeText(GirisSayfasi.this, "Giriş başarısız Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Email veya şifre alanları boşsa kullanıcıyı bilgilendir
                    Toast.makeText(GirisSayfasi.this, "Email ve şifre alanları boş bırakılamaz", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Kayıt ol butonuna tıklanma durumu
        kayitOlButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kayıt sayfasına yönlendirme
                Intent kayitSayfasi = new Intent(GirisSayfasi.this, KayitSayfasi.class);
                startActivity(kayitSayfasi);
            }
        });
    }
}
