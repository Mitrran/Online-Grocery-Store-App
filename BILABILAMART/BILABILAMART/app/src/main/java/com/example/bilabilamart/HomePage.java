package com.example.bilabilamart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;

public class HomePage extends AppCompatActivity {

    private ImageButton foodButton;
    private ImageButton stationeryButton;
    private ImageButton toiletriesButton;
    Spinner spinner5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        spinner5 = findViewById(R.id.spinner5);

        ArrayList<String> categoryList = new ArrayList<>();
        //Add item to the list
        categoryList.add(0, "Choose Category");
        categoryList.add("Food & Beverages");
        categoryList.add("Stationary");
        categoryList.add("Toiletries");
        categoryList.add("Logout");

        //Create and set array adapter to spinner control
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryList);
        spinner5.setAdapter(adapter);

        spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (adapterView.getItemAtPosition(i).equals("Choose Category"))
                {
                    //do nothing
                }
                else
                {
                    //on selecting a spinner item
                    String item = adapterView.getItemAtPosition(i).toString();

                    //show selected spinner item
                    Toast.makeText(adapterView.getContext(), "Selected " + item, Toast.LENGTH_SHORT).show();

                    //open new activity upon selection from spinner

                    if (adapterView.getItemAtPosition(i).equals("Food & Beverages"))
                    {
                        Intent intent = new Intent(HomePage.this, FNB.class);
                        startActivity(intent);
                    }

                    else if (adapterView.getItemAtPosition(i).equals("Stationary"))
                    {
                        Intent intent = new Intent(HomePage.this, Stat.class);
                        startActivity(intent);
                    }

                    else if (adapterView.getItemAtPosition(i).equals("Toiletries"))
                    {
                        Intent intent = new Intent(HomePage.this, Toil.class);
                        startActivity(intent);
                    }

                    else if (adapterView.getItemAtPosition(i).equals("Logout"))
                    {
                        Intent intent = new Intent(HomePage.this, LoginActivity.class);
                        startActivity(intent);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        foodButton = (ImageButton) findViewById(R.id.foodButton);
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity2();
            }
        });

        stationeryButton= (ImageButton) findViewById(R.id.stationeryButton);
        stationeryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {openMainActivity3();
            }
        });

        toiletriesButton = (ImageButton) findViewById(R.id.toiletriesButton);
        toiletriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity4();
            }
        });

    }

    public void openMainActivity2(){
        Intent intent = new Intent(this, FNB.class);
        startActivity(intent);
    }

    public void openMainActivity3(){
        Intent intent = new Intent(this, Stat.class);
        startActivity(intent);
    }

    public void openMainActivity4(){
        Intent intent = new Intent(this, Toil.class);
        startActivity(intent);
    }
}