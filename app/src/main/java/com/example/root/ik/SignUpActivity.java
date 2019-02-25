package com.example.root.ik;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.EditText;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    android.widget.EditText username;
    android.widget.EditText email;
    android.widget.EditText phone;
    android.widget.EditText pasword;
    Button register_btn;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pasword = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        register_btn = findViewById(R.id.register_btn);

        auth = FirebaseAuth.getInstance();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtUsername = username.getText().toString();
                String txtemail = email.getText().toString();
                String txtPhone = phone.getText().toString();
                String txtpassword = pasword.getText().toString();

                if (TextUtils.isEmpty(txtUsername) ||TextUtils.isEmpty(txtemail) ||
                        TextUtils.isEmpty(txtpassword)){
                    Toast.makeText(SignUpActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                }else if (txtpassword.length() < 6){
                    Toast.makeText(SignUpActivity.this, "password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                }else {
                    register(txtUsername,txtemail,txtPhone,txtpassword);
                }
            }
        });
    }

    private void register(final String username, String email, final String phone, final String pasword){

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating user");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email,pasword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            databaseReference.keepSynced(true);

                            HashMap<String ,String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("name",username);
                            hashMap.put("phone",phone);
                            hashMap.put("imageUrl",null);
                            hashMap.put("status","offline");
                            hashMap.put("password",pasword);



                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    }else {
                                        Toast.makeText(SignUpActivity.this, "You can't register with this email", Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();
                                    }
                                }
                            });

                        }
                    }
                });
    }
}
