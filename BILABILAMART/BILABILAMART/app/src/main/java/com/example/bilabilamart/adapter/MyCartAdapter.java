package com.example.bilabilamart.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bilabilamart.R;
import com.example.bilabilamart.eventbus.MyUpdateCartEvent;
import com.example.bilabilamart.model.CartModel;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder> {

    private Context context;
    private List<CartModel> cartModelList;

    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCartViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {
        Glide.with(context)
                .load(cartModelList.get(position).getImage())
                .into(holder.imageView);

        holder.txtPrice.setText(new StringBuilder("$").append(cartModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(cartModelList.get(position).getName()));
        holder.txtQuantity.setText(new StringBuilder().append(cartModelList.get(position).getQuantity()));

        //Event
        holder.btnMinus.setOnClickListener(view ->{
            minusCartItem(holder,cartModelList.get(position));
        });
        holder.btnPlus.setOnClickListener(view ->{
            plusCartItem(holder,cartModelList.get(position));
        });

        holder.btnDelete.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("DELETE ITEM")
                    .setMessage("DO YOU REALLY WANT TO DELETE ITEM")
                    .setNegativeButton("CANCEL", (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton("OK", (dialog12, which) -> {

                        //Temp Remove
                        notifyItemRemoved(position);

                        deleteFromFirebase (cartModelList.get(position));
                        dialog12.dismiss();

                    }).create();
            dialog.show();
        });


    }

    private void deleteFromFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE USER_ID")
                .child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(avoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    private void plusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        cartModel.setQuantity(cartModel.getQuantity()+1);
        cartModel.setTotalprice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

        holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
        updateFirebase(cartModel);
    }

    private void minusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        if (cartModel.getQuantity()>1)
        {
            cartModel.setQuantity(cartModel.getQuantity()-1);
            cartModel.setTotalprice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

            //Update Quantity
            holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
            updateFirebase(cartModel);
        }

    }

    private void updateFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE USER_ID")
                .child(cartModel.getKey())
                .setValue(cartModel)
                .addOnSuccessListener(avoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }



    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class MyCartViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btnMinus)
        ImageView btnMinus;
        @BindView(R.id.btnPlus)
        ImageView btnPlus;
        @BindView(R.id.btnDelete)
        ImageView btnDelete;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtPrice)
        TextView txtPrice;
        @BindView(R.id.txtQuantity)
        TextView txtQuantity;

        Unbinder unbinder;
        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}