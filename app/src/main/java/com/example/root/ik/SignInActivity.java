package com.example.root.ik;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.ik.common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {

    TextInputEditText email,password;
    FButton signInBtn;
    CheckBox chkRemember;
    TextView txtForgotPassword, signUp;

    //firebase
    FirebaseDatabase database;
    DatabaseReference table_user;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //init Firebase
        database = FirebaseDatabase.getInstance();



//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance();


        email = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);

        signInBtn = findViewById(R.id.btn_signIn_activity);

        chkRemember = findViewById(R.id.ckbRememberMe);

        signUp = findViewById(R.id.signUpAct);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        //init paper
        Paper.init(this);



        table_user = database.getReference("User");
        table_user.keepSynced(true);


        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (auth.getCurrentUser() == null || !auth.getCurrentUser().isEmailVerified()){

                }else {

                }
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.isConnectedToInternet(getBaseContext())){

                    // TODO: save user and password

                    final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();
                    if (chkRemember.isChecked()){
                        Paper.book().write(Common.USER_KEY, email.getText().toString());
                        Paper.book().write(Common.PASSWORD_KEY,password.getText().toString());
                    }

                    String txtemail = email.getText().toString();
                    String txtpassword = password.getText().toString();

                    if (TextUtils.isEmpty(txtemail) ||
                            TextUtils.isEmpty(txtpassword)){

                        Toast.makeText(SignInActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();

                    }else {
                        auth.signInWithEmailAndPassword(txtemail,txtpassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            progressDialog.dismiss();
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Toast.makeText(SignInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();

                                        }
                                    }
                                });
                    }

//                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                            // To check if user does not exist in db
//                            if (dataSnapshot.child(email.getText().toString()).exists()) {
//
//
//                                // Get user info
//
//                                progressDialog.dismiss();
//                                User user = dataSnapshot.child(email.getText().toString()).getValue(User.class);
//                                user.setEmail(email.getText().toString()); // set phone
//                                if (user.getPassword().equals(password.getText().toString())) {
//                                    {
//                                        Toast.makeText(SignInActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
//                                        Intent homeIntent = new Intent(SignInActivity.this, HomeActivity.class);
//                                        Common.current_user = user;
//                                        startActivity(homeIntent);
//                                        finish();
//
//                                        table_user.removeEventListener(this);
//                                    }
//
//
//
//                                } else {
//                                    Toast.makeText(SignInActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
//
//                                }
//                            }else {
//                                progressDialog.dismiss();
//                                Toast.makeText(SignInActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
                }
                else {
                    Toast.makeText(SignInActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }


}
