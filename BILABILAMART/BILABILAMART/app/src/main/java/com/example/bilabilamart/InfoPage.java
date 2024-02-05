package com.example.bilabilamart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bilabilamart.databinding.ActivityInfoPageBinding;
import com.example.bilabilamart.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class InfoPage extends AppCompatActivity {

    private EditText fullname;
    private EditText phonenumber;
    private EditText address;
    private Button checkout;
    private EditText type;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);

        fullname = findViewById(R.id.fullname);
        phonenumber = findViewById(R.id.phonenumber);
        address = findViewById(R.id.address);
        type = findViewById(R.id.type);
        checkout = findViewById(R.id.checkout);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(InfoPage.this,CheckOut.class));

                String getfullname = fullname.getText().toString();
                String getphonenumber = phonenumber.getText().toString();
                String getaddress = address.getText().toString();
                String gettype = type.getText().toString();

                HashMap <String,Object> hashMap = new HashMap<>();
                hashMap.put("full name", getfullname);
                hashMap.put("phonenumber", getphonenumber);
                hashMap.put("address", getaddress);
                hashMap.put("type", gettype);

                databaseReference.child("Profile")
                        .child(getfullname)
                        .setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
    }
}

