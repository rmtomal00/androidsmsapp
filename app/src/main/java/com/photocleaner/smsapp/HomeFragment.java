package com.photocleaner.smsapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Debug;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private EditText body, number;
    private Button sendButton;

    Adapter customAdapter;

    private NestedScrollView nestedScrollView;
    Gson gson = new Gson();
    private ArrayList list1 = new ArrayList<>();
    ArrayList list2 = new ArrayList<>();
    ArrayList list;
    RecyclerView recyclerView;
    Context context;
    //
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]

    private FirebaseAuth mAuth;

    String apiUrl = BuildConfig.apiUrl;


    ProgressDialog progressDialog;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    // [END declare_auth]



    private String messageBody, sendNumber,sendStatus, time, balance;
    private String email, username, photo;
    double sms;

    String id = "" ;
    FirebaseUser currentUser;
    private LocationManager locationManager;
    private LocationListener locationListener;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Data is Loading...");
        progressDialog.show();



        mAuth = FirebaseAuth.getInstance();

        body = view.findViewById(R.id.sendMessage);
        number = view.findViewById(R.id.sendNumber);
        sendButton = view.findViewById(R.id.sendMessageButton);
        nestedScrollView = view.findViewById(R.id.nest);
        locationMathod();
        /*if (!Settings.System.canWrite(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + context.getApplicationInfo().packageName));
            startActivity(intent);
        } else {
            int developerOptions = Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

            if (developerOptions == 1) {
                // Developer options are enabled
                //Toast.makeText(context, "Developer options are enabled", Toast.LENGTH_LONG).show();
                dialogShow("Developer options are enabled");
            }

        }*/


        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            readUserData(currentUser.getUid());
        }

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        list = new ArrayList<>();

        list1 = list;
        if (!(list2 == null)){
            list2 = list;
            list2.clear();
        }
        customAdapter = new Adapter(context, list);
        recyclerView.setAdapter(customAdapter);
        nestedScrollView.setNestedScrollingEnabled(false);
        Collections.reverse(list);
        list1 = list;

        Collections.reverse(list);
        customAdapter.notifyDataSetChanged();



        body.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println(charSequence);
                String msg = charSequence.toString();
                char[] charsArray = msg.toCharArray();
                for (char c: charsArray) {

                    CharacterCheck check = new CharacterCheck();
                    if (!check.isAscii(c)){
                        int size = msg.length();
                        if (size > 70){
                            body.setError("Massage more than 70 Characters");
                            break;
                        }
                    }else {
                        int size = msg.length();
                        if (size > 150){
                            body.setError("Massage more than 150 Characters");
                            break;
                        }
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendButton.setOnClickListener(v->{


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String pattern  = "dd-MM-yyyy hh:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
                time = simpleDateFormat.format(new Date());
                System.out.println(time);
            }
            if (number.length() < 11 | number.length() > 11){
                number.setError("Invalid Phone Number");
                return;
            }
            messageBody = body.getText().toString().trim();
            char[] charsArray = messageBody.toCharArray();
            if (TextUtils.isEmpty(messageBody)){
                body.setError("Write a Message");
                return;
            }
            for (char c: charsArray) {

                CharacterCheck check = new CharacterCheck();
                if (!check.isAscii(c)){
                    int size = messageBody.length();
                    if (size > 70){
                        body.setError("Massage more than 70 Characters");
                        return;
                    }
                }

                System.out.println("WORK");

            }
            if (messageBody.length() > 150){
                body.setError("Massage more than 150 Characters");
                return;
            }
            sendNumber = number.getText().toString().trim();
            if (TextUtils.isEmpty(sendNumber)){
                number.setError("Write a Phone Number");
                return;
            }
            if (sms < 1){
                Toast.makeText(context, "Not enough balance", Toast.LENGTH_SHORT).show();
            } else {
                if (messageBody.length() > 150){
                    body.setError("Massage more than 150 Characters");
                    return;
                }else {
                    volley(sendNumber, messageBody, currentUser.getUid());
                    progressDialog.show();
                }
            }

            if (!TextUtils.isEmpty(messageBody)){
                body.setText("");
            }



        });

        return view;
    }

    @Keep
    private void locationMathod() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String liveLocation = "lati: " + latitude + " long: " + longitude;
                new DataSend().userLocation(liveLocation);
            }
        };
    }

    private void dialogShow(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finishAffinity();
            }
        });
        builder.create().show();

    }

    private void readUserData(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> map = new HashMap<>();
                for (DataSnapshot data : snapshot.getChildren()){
                    String key = data.getKey();
                    Object value = data.getValue();
                    map.put(key, value);
                }
                String balance = String.valueOf(map.get("balance"));
                 sms = Double.parseDouble(balance);
                System.out.println("sms balance(current user): "+sms);
                Toast.makeText(context, "sms balance(current user): "+sms, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getMessage());
            }
        });
    }

    public void volley(String number, String message, String uid){
        if (message.length() > 150){
            dialogShow("Message length up to 150 character");
        }else {

            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("uid", uid);
                jsonBody.put("number", number);
                jsonBody.put("text", message);

                String url = apiUrl+"/smsapi/send_sms";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Handle the response here
                                SmsReportData reportData = gson.fromJson(response.toString(), SmsReportData.class);
                                if (reportData.isError()){
                                    sendStatus = "failed";
                                }else {
                                    sendStatus = "success";
                                }
                                SmsData data = new SmsData(sendNumber, messageBody, time, sendStatus);
                                Collections.reverse(list);
                                list.add(data);
                                list1 = list;

                                Collections.reverse(list);
                                customAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println(error);
                                // Handle errors here
                                System.out.println("Error: " + error.getMessage());
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "fail : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                // Add the request to the RequestQueue.
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(jsonObjectRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        //
        String returnJson = sharedPreferences.getString("myArrayListKey", null);
        if (!(returnJson == null)){
            try {
                JSONArray jsonArray = new JSONArray(returnJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    SmsData data = gson.fromJson(jsonObject.toString(), SmsData.class);

                    list2.add(data);

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Gson json = new Gson();
        System.out.println(list1);
        String data = json.toJson(list1);
        System.out.println(data);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("myArrayListKey", data);
        editor.apply();
        list1.clear();
    }

}