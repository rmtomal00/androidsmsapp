package com.photocleaner.smsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.photocleaner.smsapp.databinding.ActivityLoginBinding;
import com.photocleaner.smsapp.databinding.ActivityRegisterBinding;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Account creating");
        progressDialog.create();

        binding.fprgetpassword2.setOnClickListener(v->{
            startActivity(new Intent(this, ForgetPasswordActivity.class));
        });

        binding.signupRegister.setOnClickListener(v->{
            progressDialog.show();
            String username = binding.usernameRegister.getText().toString().trim();
            String email = binding.emailRegister.getText().toString().trim();
            String password = binding.passwordRegister.getText().toString().trim();

            if (TextUtils.isEmpty(username)){
                binding.usernameRegister.setError("Empty");
                return;
            }
            if (TextUtils.isEmpty(email)){
                binding.emailRegister.setError("Empty");
                return;
            }
            if (TextUtils.isEmpty(password)){
                binding.passwordRegister.setError("Empty");
            } else if (password.length() < 6) {
                binding.passwordRegister.setError("Minimum 6 characters");
                return;
            }
            signupAccount(username, email, password);

        });
    }

    private void signupAccount(String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null){
                                String phone = user.getPhoneNumber();
                                String uid = user.getUid();
                                String photo = String.valueOf(user.getPhotoUrl());


                                Map<String, Object> map1 = new HashMap<>();
                                map1.put("balance","0");
                                map1.put("username", username);
                                map1.put("email", email);
                                map1.put("photo", photo);
                                map1.put("phone", phone);
                                sendData(map1);
                            }
                        }
                    }
                });
    }

    private void sendData(Map<String, Object> userInfo) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(user.getUid()).updateChildren(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("done");
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                progressDialog.dismiss();
                finish();
            }
        });
    }


}