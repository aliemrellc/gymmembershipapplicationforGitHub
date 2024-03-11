package com.example.sporsalonuuygulamasi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class KayitSayfasi extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText emailEditText, sifreEditText;
    Button kayitOlButonu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_sayfasi);

        // Firebase Authentication nesnesi oluşturulması
        mAuth =  FirebaseAuth.getInstance();

        // Arayüz elemanlarının tanımlanması
        emailEditText = findViewById(R.id.emailEditText);
        sifreEditText = findViewById(R.id.sifreEditText);
        kayitOlButonu = findViewById(R.id.kayitOlButonu);

        // Kayıt ol butonuna tıklanma durumu
        kayitOlButonu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Kullanıcının girdiği email ve şifre alınması
                String email = emailEditText.getText().toString();
                String password = sifreEditText.getText().toString();

                // Email veya şifre boşsa kullanıcıyı bilgilendir
                if (email.toString().isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Boşlukları doldurun", Toast.LENGTH_LONG).show();
                }
                else{
                    // Firebase Authentication ile kullanıcı kaydı yapar
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(KayitSayfasi.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // Kayıt işlemi tamamlandığında çalışacak metod
                                    if (task.isSuccessful()){
                                        // Başarılı kayıt durumunda kullanıcıyı bilgilendir, GirişSayfasi'na yönlendir
                                        Intent loginPage= new Intent(getApplicationContext(), GirisSayfasi.class);
                                        startActivity(loginPage);
                                        Toast.makeText(getApplicationContext(), "Kayıt başarılı", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                        // Kayıt işlemi başarısızsa kullanıcıyı bilgilendir ve hatayı göster
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}
