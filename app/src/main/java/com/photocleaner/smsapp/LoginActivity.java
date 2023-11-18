package com.photocleaner.smsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.photocleaner.smsapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        mAuth = FirebaseAuth.getInstance();
        context = this;

        binding.forgetPassLogin.setOnClickListener( view->{
            startActivity(new Intent(this, ForgetPasswordActivity.class));
        });

        //permission
        checkPermission();

        //login
        binding.loginButton.setOnClickListener(view->{
            String email = binding.emailLogin.getText().toString().trim();
            String password = binding.passwordLogin.getText().toString().trim();
            if (TextUtils.isEmpty(email)){
            binding.emailLogin.setError("Empty");
            return;
            }
            if (TextUtils.isEmpty(password)){
                binding.passwordLogin.setError("Empty");
            }else if (password.length() < 6){
                binding.emailLogin.setError("Minimum need 6 character");
                return;
            }
            loginWithPassword(email, password);
        });

        //create account
        binding.registerLogin.setOnClickListener( view->{
            startActivity(new Intent(this, RegisterActivity.class));
        });

    }

    private void loginWithPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            updateUi();
                        }else {
                            Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUi() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void checkPermission(){

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("permission check");
        } else {
            requestPermissions(new String[]{
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.WRITE_SETTINGS,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },100);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            updateUi();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "granted", Toast.LENGTH_SHORT).show();
            }  else {
                dialogShow("Permission is not granted");
            }
        }
    }

    private void dialogShow(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        builder.create().show();
    }


}