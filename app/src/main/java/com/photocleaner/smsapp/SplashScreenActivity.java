package com.photocleaner.smsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.progressbar);
        LoadTime();

    }



    private void LoadTime() {
        TimerTask timerTask1 = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (internetConnectionCheck() & !isDeviceRooted()){
                            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                            finish();
                        }else {
                            Toast.makeText(SplashScreenActivity.this,"Please Check Internet Connection.", Toast.LENGTH_LONG).show();
                            internetConnectionLost();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask1,3000);
    }

    private boolean internetConnectionCheck() {
        try {
            String cmd = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(cmd).waitFor() == 0);

        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public boolean isDeviceRooted() {
        // Check for the existence of known root binaries
        String[] knownRootBinaries = new String[]{"su", "busybox"};
        for (String binary : knownRootBinaries) {
            if (new File("/system/bin/" + binary).exists()) {
                return true;
            }
        }

        // Check for the existence of root-specific directories
        String[] knownRootDirectories = new String[]{"/system/xbin", "/system/app/Superuser.apk"};
        for (String directory : knownRootDirectories) {
            if (new File(directory).exists()) {
                return true;
            }
        }

        // Compare the system files on the device to the original system files
        // This can be done by comparing the MD5 checksums of the files
        // or by using a tool such as diff

        return false;
    }



    private void internetConnectionLost() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        TextView text = (TextView) dialog.findViewById(R.id.textInternet);
        Button dialogButton = (Button) dialog.findViewById(R.id.exit);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        Button retry = dialog.findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadTime();
                progressBar.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}