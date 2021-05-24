package com.example.myshoppingapp;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PaymentFragment extends Fragment {
    private TableLayout table;
    private TextView address;
    private RadioButton optcod,optcard;
    private Button btnconfirm;
    private EditText txtcno,txtcvc,txtexp;
    private LinearLayout layout_card;
    private DatabaseReference dbRef;
    private FirebaseAuth auth;
    private ArrayList<Cart> selectedItems;
    private ProgressDialog pd;
    private final String TAG = "PaymentFragment";
    private float gtotal;
    private int item_count;

    public PaymentFragment(ArrayList<Cart> selectedItems) {
        // Required empty public constructor
        this.selectedItems = selectedItems;
        this.gtotal = 0;
        this.item_count = 0;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_payment, container, false);
        table = view.findViewById(R.id.payment_table_details);
        address = view.findViewById(R.id.payment_address);
        optcod = view.findViewById(R.id.payment_opt_cod);
        optcard = view.findViewById(R.id.payment_opt_card);
        layout_card = view.findViewById(R.id.payment_layout_card);
        btnconfirm =  view.findViewById(R.id.payment_btn_confirm);
        txtcno = view.findViewById(R.id.payment_card_no);
        txtcvc = view.findViewById(R.id.payment_card_cvc);
        txtexp = view.findViewById(R.id.payment_card_exp);
        dbRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(getContext());
        String uid = auth.getUid();

        pd.setMessage("Loading...");
        pd.show();
        fetchCartData();
        getShippingAddress(uid);

        optcod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_card.setVisibility(View.GONE);
            }
        });

        optcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_card.setVisibility(View.VISIBLE);
            }
        });

        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(optcod.isChecked()==false && optcard.isChecked()==false){
                    Toast.makeText(getContext(), "Please select the payment method", Toast.LENGTH_SHORT).show();
                }
                else if(optcard.isChecked()==true){
                    if(txtcno.getText().toString().equals("") || txtcvc.getText().toString().equals("") || txtexp.getText().toString().equals("")){
                        Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    }
                    else if(txtcno.getText().length()!=16){
                        Toast.makeText(getContext(), "Invalid Card no", Toast.LENGTH_SHORT).show();
                    }
                    else if (txtcvc.getText().length()!=3){
                        Toast.makeText(getContext(), "Invalid CVC", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String exp = txtexp.getText().toString(); //    02/22
                        int month = Integer.parseInt(exp.substring(0,2));   // 2
                        int year = Integer.parseInt(exp.substring(3));   // 22
                        int cmonth = Calendar.getInstance().get(Calendar.MONTH)+1;  // 5
                        int cyear = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2));  //21

                        if((month>12) || (month<1) || (year<cyear) || (year==cyear && month<cmonth) || (year>cyear+5)){
                            Toast.makeText(getContext(), "Invalid expiery", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            placeOrder(uid,"card");
                        }
                    }
                }
                else if(optcod.isChecked()==true){
                    placeOrder(uid,"cod");
                }
            }
        });
        return view;
    }

    private void placeOrder(String custid,String mode) {
        //Order management
        String order_id = dbRef.child("Orders").child(custid).push().getKey();
        Order order = new Order();
        order.setOrder_id(order_id);
        order.setCust_id(custid);
        order.setOrder_amount(this.gtotal);
        order.setOrder_qty(item_count);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        order.setOrder_date(dateFormat.format(calendar.getTime()).toString());
        order.setOrder_time(timeFormat.format(calendar.getTime()).toString());
        order.setOrder_status("processing");
        for (Cart cart : selectedItems){
            String details_id = dbRef.child("Orders").child(custid).child(order_id).child("Details").push().getKey();
            OrderDetails details = new OrderDetails();
            details.setDetails_id(details_id);
            details.setProd_id(cart.getProd_id());
            details.setProd_size(cart.getProd_size());
            details.setProd_price(cart.getProd_price());
            details.setProd_qty(cart.getProd_qty());
            order.order_details.add(details);
        }
        dbRef.child("Orders").child(custid).child(order_id).setValue(order); //saving the order

        //Payment management
        Payment payment = new Payment();
        String pay_id = dbRef.child("Payments").child(custid).push().getKey();
        payment.setPay_id(pay_id);
        payment.setOrder_id(order_id);
        payment.setPay_amount(gtotal);
        payment.setPay_mode(mode);
        if (mode.equals("card")){
            payment.setPay_date(dateFormat.format(calendar.getTime()).toString());
            payment.setPay_time(timeFormat.format(calendar.getTime()).toString());
            payment.setPay_status("completed");
        }
        else if(mode.equals("cod")){
            payment.setPay_date("");
            payment.setPay_time("");
            payment.setPay_status("pending");
        }
        dbRef.child("Payments").child(custid).child(pay_id).setValue(payment); //saving the payment

        //Update Cart
        for (Cart cart : selectedItems){
            String cart_id = cart.getCart_id();
            String prod_id = cart.getProd_id();
            String size = cart.getProd_size();
            int qty = cart.getProd_qty();

            dbRef.child("Cart").child(custid).child("Items").child(cart_id).removeValue(); //remove cart entry

            //update stock
            dbRef.child("Stock").child(prod_id).child(size).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int stock = Integer.parseInt(snapshot.getValue().toString());
                    int new_stock = stock-qty;
                    dbRef.child("Stock").child(prod_id).child(size).setValue(new_stock);
                    selectedItems.remove(cart);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        CTRL_MGT();
        Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
        getFragmentManager().beginTransaction().replace(R.id.frag_container,new HomeFragment()).commit();
    }

    private void CTRL_MGT() {
        optcard.setChecked(false);
        optcod.setChecked(false);
        txtcno.setText("");
        txtcvc.setText("");
        txtexp.setText("");
    }

    private void getShippingAddress(String uid) {
        dbRef.child("Customer").child(uid).child("cust_address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                address.setText(snapshot.getValue().toString());
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,error.getMessage());
            }
        });
    }

    private void fetchCartData() {
        for(Cart cart : this.selectedItems){
            TableRow row = new TableRow(getContext());
            row.setPadding(5,5,5,5);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
            TextView tv_name = new TextView(getContext());
            TextView tv_qty = new TextView(getContext());
            TextView tv_price = new TextView(getContext());
            TextView tv_total = new TextView(getContext());
            tv_qty.setGravity(Gravity.RIGHT);
            tv_price.setGravity(Gravity.RIGHT);
            tv_total.setGravity(Gravity.RIGHT);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,1);
            tv_name.setLayoutParams(params);
            tv_qty.setLayoutParams(params);
            tv_price.setLayoutParams(params);
            tv_total.setLayoutParams(params);
            int qty;
            float price,total;
            tv_name.setText(cart.getProd_name());
            qty = cart.getProd_qty();
            price = cart.getProd_price();
            total = qty*price;
            this.gtotal += total;
            tv_qty.setText(String.valueOf(qty));
            tv_price.setText(String.format("%.02f",price));
            tv_total.setText(String.format("%.02f",total));
            row.addView(tv_name);
            row.addView(tv_qty);
            row.addView(tv_price);
            row.addView(tv_total);
            table.addView(row);
            this.item_count +=1;
        }
        TableRow lastRow = new TableRow(getContext());
        lastRow.setPadding(5,5,5,5);
        lastRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1));
        TextView tv_gtot_lbl = new TextView(getContext());
        TextView tv_gtot = new TextView(getContext());
        lastRow.addView(tv_gtot_lbl);
        lastRow.addView(tv_gtot);

        tv_gtot_lbl.setTypeface(null, Typeface.BOLD);
        tv_gtot.setTypeface(null, Typeface.BOLD);
        tv_gtot_lbl.setText("Grand Total: ");
        tv_gtot.setText(String.format("%.02f",gtotal));

        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,1);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,1);
        params1.span = 3;
        params1.gravity = Gravity.RIGHT;
        tv_gtot_lbl.setLayoutParams(params1);

        params2.gravity = Gravity.RIGHT;
        tv_gtot.setLayoutParams(params2);

        table.addView(lastRow);

    }
}