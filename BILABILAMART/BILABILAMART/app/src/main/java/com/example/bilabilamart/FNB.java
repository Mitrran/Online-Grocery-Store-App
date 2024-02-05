package com.example.bilabilamart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.bilabilamart.adapter.MyDrinkAdapter;
import com.example.bilabilamart.eventbus.MyUpdateCartEvent;
import com.example.bilabilamart.listener.ICartLoadListener;
import com.example.bilabilamart.listener.IDrinkLoadListener;
import com.example.bilabilamart.model.CartModel;
import com.example.bilabilamart.model.DrinkModel;
import com.example.bilabilamart.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FNB extends AppCompatActivity implements IDrinkLoadListener, ICartLoadListener {

    @BindView(R.id.recycler_drink)
    RecyclerView recyclerDrink;
    @BindView(R.id.Layout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;
    @BindView(R.id.spinner)
    Spinner spinner;


    IDrinkLoadListener drinkLoadListener;
    ICartLoadListener cartLoadListener;


    @Override
    protected void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event)
    {
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnb);

        init();
        loadDrinkFromFirebase();
        countCartItem();

        ArrayList<String> categoryList = new ArrayList<>();
        //Add item to the list
        categoryList.add(0, "Choose Category");
        categoryList.add("Food & Beverages");
        categoryList.add("Stationary");
        categoryList.add("Toiletries");
        categoryList.add("Back To Home");

        //Create and set array adapter to spinner control
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryList);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    Toast.makeText(adapterView.getContext(), "Selected" + item, Toast.LENGTH_SHORT).show();

                    //open new activity upon selection from spinner

                    if (adapterView.getItemAtPosition(i).equals("Food & Beverages"))
                    {
                        Intent intent = new Intent(FNB.this, FNB.class);
                        startActivity(intent);
                    }

                    else if (adapterView.getItemAtPosition(i).equals("Stationary"))
                    {
                        Intent intent = new Intent(FNB.this, Stat.class);
                        startActivity(intent);
                    }

                    else if (adapterView.getItemAtPosition(i).equals("Toiletries"))
                    {
                        Intent intent = new Intent(FNB.this, Toil.class);
                        startActivity(intent);
                    }

                    else if (adapterView.getItemAtPosition(i).equals("Back To Home"))
                    {
                        Intent intent = new Intent(FNB.this, HomePage.class);
                        startActivity(intent);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void loadDrinkFromFirebase () {
        List<DrinkModel> drinkModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("0")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for(DataSnapshot drinkSnapshot:snapshot.getChildren())
                            {
                                DrinkModel drinkModel = drinkSnapshot.getValue(DrinkModel.class);
                                drinkModel.setKey(drinkSnapshot.getKey());
                                drinkModels.add(drinkModel);
                            }
                            drinkLoadListener.onDrinkLoadSuccess(drinkModels);
                        }
                        else
                            drinkLoadListener.onDrinkLoadFailed("cant find drink");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        drinkLoadListener.onDrinkLoadFailed(error.getMessage());

                    }
                });
    }

    private void init(){
        ButterKnife.bind(this);

        drinkLoadListener = this;
        cartLoadListener = this;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerDrink.setLayoutManager(gridLayoutManager);
        recyclerDrink.addItemDecoration(new SpaceItemDecoration());

        btnCart.setOnClickListener(v -> startActivity(new Intent(this,CartActivity.class)));


    }


    @Override
    public void onDrinkLoadSuccess(List<DrinkModel> drinkModelList) {
        MyDrinkAdapter adapter = new MyDrinkAdapter (this,drinkModelList,cartLoadListener);
        recyclerDrink.setAdapter(adapter);

    }

    @Override
    public void onDrinkLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {

        int cartSum=0;
        for (CartModel cartModel: cartModelList)
            cartSum += cartModel.getQuantity();
        badge.setNumber(cartSum);

    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase
                .getInstance().getReference("Cart")
                .child("UNIQUE USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartSnapshot:snapshot.getChildren())
                        {
                            CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());

                    }
                });
    }
}