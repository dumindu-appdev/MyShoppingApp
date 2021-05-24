package com.example.myshoppingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    ArrayList<Cart> cList;
    Context context;
    float gtotal;
    DatabaseReference dbRef;

    public CartAdapter(ArrayList<Cart> cList, Context context) {
        this.cList = cList;
        this.context = context;
        gtotal = 0;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_items,parent,false);
        return new CartViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cList.get(position);
        holder.pname.setText(cart.getProd_name());
        if (!cart.getProd_image().equals("")) {
            Picasso.get().load(cart.getProd_image()).into(holder.imgprod);
        }
        holder.pprice.setText("Price: Rs. "+String.valueOf(cart.getProd_price()));
        holder.pqty.setText("Qty: "+String.valueOf(cart.getProd_qty()));
        float total = cart.getProd_price() * cart.getProd_qty();
        holder.ptotal.setText("Total: Rs. "+String.valueOf(total));

        dbRef.child("Stock").child(cart.getProd_id()).child(cart.getProd_size()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int stock = Integer.parseInt(snapshot.getValue().toString()); //existing stock
                if(stock<cart.getProd_qty()) {
                    holder.stock.setText("Out of Stock");
                    holder.checkBox.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,1);
                dbRef.child("Cart").child(cart.getCust_id()).child("Items").child(cart.getCart_id()).removeValue();

                if(holder.checkBox.isChecked()){
                    gtotal -=total;
                    CartFragment.updateGrandTotal("Total: Rs. "+String.valueOf(gtotal));
                    CartFragment.selectedItems.remove(cart);
                }
                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
            }
        });


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    gtotal +=total;
                    CartFragment.selectedItems.add(cart);
                }
                else{
                    gtotal -=total;
                    CartFragment.selectedItems.remove(cart);
                }
                CartFragment.updateGrandTotal("Total: Rs. "+String.valueOf(gtotal));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView imgprod;
        TextView pname,pprice,pqty,ptotal,remove,stock;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cart_item_check);
            imgprod = itemView.findViewById(R.id.cart_item_image);
            pname = itemView.findViewById(R.id.cart_item_name);
            pprice = itemView.findViewById(R.id.cart_item_price);
            pqty = itemView.findViewById(R.id.cart_item_qty);
            ptotal= itemView.findViewById(R.id.cart_item_total);
            remove = itemView.findViewById(R.id.cart_item_remove);
            stock = itemView.findViewById(R.id.cart_item_stock);
        }
    }
}
