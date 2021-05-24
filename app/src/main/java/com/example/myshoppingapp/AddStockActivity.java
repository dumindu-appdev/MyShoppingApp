package com.example.myshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddStockActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner sp_cate,sp_prod,sp_size;
    private EditText txtavail,txtqty;
    private Button btnadd,btncancel;
    private ArrayList<String> list_cate,list_prod,list_size,list_prod_id;
    private ArrayAdapter<String> adp_cate,adp_prod,adp_size;
    private DatabaseReference dbRef;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);
        sp_cate = findViewById(R.id.stock_category);
        sp_prod = findViewById(R.id.stock_product);
        sp_size = findViewById(R.id.stock_size);
        txtavail = findViewById(R.id.stock_avail);
        txtqty = findViewById(R.id.stock_qty);
        btnadd = findViewById(R.id.stock_add);
        btncancel = findViewById(R.id.stock_cancel);

        dbRef = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);

        list_cate = new ArrayList<String>();
        adp_cate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,list_cate);
        sp_cate.setAdapter(adp_cate);
        sp_cate.setOnItemSelectedListener(this);

        list_prod  =new ArrayList<String>();
        list_prod_id = new ArrayList<String>();
        list_prod.add(0,"--Select Product--");
        adp_prod = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,list_prod);
        sp_prod.setAdapter(adp_prod);
        sp_prod.setOnItemSelectedListener(this);

        list_size = new ArrayList<String>();
        list_size.add(0,"--Select Size--");
        list_size.add(1,"Small");
        list_size.add(2,"Medium");
        list_size.add(3,"Large");
        adp_size = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,list_size);
        sp_size.setAdapter(adp_size);
        sp_size.setOnItemSelectedListener(this);

        populateCategory();

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStock();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addStock() {
        if(sp_cate.getSelectedItemPosition()==0 || sp_prod.getSelectedItemPosition()==0 || sp_size.getSelectedItemPosition()==0 || txtqty.getText().toString().equals("")){
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        pd.setMessage("Saving...");
        pd.show();
        String pid = list_prod_id.get(sp_prod.getSelectedItemPosition()-1);
        String size = sp_size.getSelectedItem().toString();
        int avail = Integer.parseInt(txtavail.getText().toString());
        int qty = Integer.parseInt(txtqty.getText().toString());
        int newqty = avail+qty;
        dbRef.child("Stock").child(pid).child(size.toLowerCase()).setValue(newqty);
        Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show();
        CTRL_MGT();
        pd.dismiss();
    }

    private void CTRL_MGT() {
        sp_size.setSelection(0);
        txtavail.setText("");
        txtqty.setText("");
    }

    private void populateCategory() {
        pd.setMessage("Loading...");
        pd.show();
        list_cate.clear();
        dbRef.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data:snapshot.getChildren()){
                    list_cate.add(data.getValue().toString());
                }
                list_cate.add(0,"--Select Category--");
                adp_cate.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int item = parent.getId();
        if (item==R.id.stock_category){
            if (sp_cate.getSelectedItemPosition()!=0){
                pd.setMessage("Loading products...");
                pd.show();
                list_prod.clear();
                list_prod_id.clear();
                String cate = sp_cate.getSelectedItem().toString();
                dbRef.child("Products").orderByChild("prod_cate").equalTo(cate).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()){
                            Product product = data.getValue(Product.class);
                            list_prod.add(product.getProd_name());
                            list_prod_id.add(product.getProd_id());
                        }
                        list_prod.add(0,"--Select Product--");
                        adp_prod.notifyDataSetChanged();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else{
                list_prod.clear();
                list_prod_id.clear();
                list_prod.add(0,"--Select Product--");
                adp_prod.notifyDataSetChanged();
            }
        }
        else if(item==R.id.stock_size){
            if (sp_size.getSelectedItemPosition()!=0){
                String size = sp_size.getSelectedItem().toString();
                int pos = sp_prod.getSelectedItemPosition();
                if (pos==0){
                    Toast.makeText(this, "Please select the product", Toast.LENGTH_SHORT).show();
                    sp_size.setSelection(0);
                    return;
                }
                pd.setMessage("Loading...");
                pd.show();
                String pid = list_prod_id.get(pos-1);
                dbRef.child("Stock").child(pid).child(size.toLowerCase()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue()==null){
                            txtavail.setText("0");
                        }
                        else{
                            txtavail.setText(snapshot.getValue().toString());
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else{
                txtavail.setText("");
                txtqty.setText("");
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}