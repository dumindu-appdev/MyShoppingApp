package com.example.myshoppingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    ArrayList<Product> pList;
    Context context;

    public ProductAdapter(ArrayList<Product> pList, Context context) {
        this.pList = pList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_items,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product prod = pList.get(position);
        holder.name.setText(prod.getProd_name());
        holder.cate.setText(prod.getProd_cate());
        holder.scate.setText(prod.getProd_scat());
        holder.price.setText(String.valueOf(prod.getProd_price()));
        Picasso.get().load(prod.getProd_image()).into(holder.imgprod);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pid = prod.getProd_id();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().add(R.id.frag_container,new ProductDetailsFragment(pid)).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView name,cate,scate,price;
        ImageView imgprod;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.prod_view_name);
            cate = itemView.findViewById(R.id.prod_view_cate);
            scate = itemView.findViewById(R.id.prod_view_scate);
            price = itemView.findViewById(R.id.prod_view_price);
            imgprod = itemView.findViewById(R.id.prod_view_image);
        }
    }
}
