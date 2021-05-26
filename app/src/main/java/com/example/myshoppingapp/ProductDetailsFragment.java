package com.example.myshoppingapp;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.Properties;

public class ProductDetailsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView txtname,txtcate,txtscat,txtprice,txtdesc,txtavail,txtshare;
    private ImageView prod_image;
    private Spinner sp_size;
    private EditText txtqty,txtemail;
    private Button btnadd,btnsend;
    private String pid;
    private ProgressDialog pd;
    private DatabaseReference dbRef;
    private final String TAG = "ProductDetailsFragment";
    private ArrayList<String> list_size;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth auth;
    private String imageUri="";
    private LinearLayout emailLayout;

    public ProductDetailsFragment(String pid) {
        // Required empty public constructor
        this.pid = pid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_product_details, container, false);
        txtname = view.findViewById(R.id.prod_details_name);
        txtcate = view.findViewById(R.id.prod_details_cate);
        txtscat = view.findViewById(R.id.prod_details_scate);
        txtprice = view.findViewById(R.id.prod_details_price);
        txtdesc = view.findViewById(R.id.prod_details_desc);
        txtavail = view.findViewById(R.id.prod_details_aqty);//--
        prod_image = view.findViewById(R.id.prod_details_image);
        sp_size = view.findViewById(R.id.prod_details_sp_size);
        txtqty = view.findViewById(R.id.prod_details_qty); //--
        txtshare = view.findViewById(R.id.prod_details_share);
        txtemail = view.findViewById(R.id.prod_details_email);
        btnadd = view.findViewById(R.id.prod_details_btn_add);
        btnsend = view.findViewById(R.id.prod_details_btn_send);
        emailLayout = view.findViewById(R.id.prod_details_laytout_email);
        pd = new ProgressDialog(getContext());
        dbRef = FirebaseDatabase.getInstance().getReference();
        list_size = new ArrayList<String>();
        list_size.add(0,"--Select Size--");
        list_size.add(1,"Small");
        list_size.add(2,"Medium");
        list_size.add(3,"Large");
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,list_size);
        sp_size.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        sp_size.setOnItemSelectedListener(this);
        auth = FirebaseAuth.getInstance();


        fetchData();

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToCart();
            }
        });

        txtshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailLayout.getVisibility()==View.VISIBLE){
                    emailLayout.setVisibility(View.GONE);
                }
                else{
                    emailLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtemail.getText().toString();
                sendEmail(email);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        return view;
    }

    private void sendEmail(String email) {
        pd.setMessage("Sending...");
        pd.show();
        final String user = ""; //senders email address
        final String pass = ""; //senders email account password
        Properties prop = new Properties();
        prop.put("mail.smtp.auth","true");
        prop.put("mail.smtp.starttls.enable","true");
        prop.put("mail.smtp.host","smtp.gmail.com");
        prop.put("mail.smtp.port","587");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator(){
                    @Override
                   protected PasswordAuthentication getPasswordAuthentication(){
                       return new PasswordAuthentication(user,pass);
                   }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(user));
            msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
            msg.setSubject("Online Foods : "+txtname.getText().toString());
            Multipart multipart = new MimeMultipart("related");
            BodyPart bodyPart = new MimeBodyPart();
            String htmlText = "Hello,<br/>Following message has been shared by "+auth.getCurrentUser().getEmail();
            htmlText +="<p><b>Name: </b>"+txtname.getText().toString()+"</p>";
            htmlText +="<p><b>Price: Rs. </b>"+String.format("%.02f",Float.parseFloat(txtprice.getText().toString()))+"</p>";
            htmlText +="<img src='"+imageUri+"'/>";
            htmlText +="<p>Thank you,<br />Team - Online Foods</p>";
            bodyPart.setContent(htmlText,"text/html");
            multipart.addBodyPart(bodyPart);
            msg.setContent(multipart);
            Transport.send(msg);
            Toast.makeText(getContext(), "Sent", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
        catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }

    private void saveToCart() {
        if (auth.getCurrentUser()==null){
            Toast.makeText(getContext(), "Please login to your account to add products to the cart", Toast.LENGTH_SHORT).show();
            //Move to login screen
        }
        else if (sp_size.getSelectedItemPosition()==0){
            Toast.makeText(getContext(), "Please select the size", Toast.LENGTH_SHORT).show();
        }
        else if (txtqty.getText().toString().equals("")){
            Toast.makeText(getContext(), "Please enter the quantity", Toast.LENGTH_SHORT).show();
        }
        else if (Integer.parseInt(txtqty.getText().toString())<1){
            Toast.makeText(getContext(), "Please correct the quantity", Toast.LENGTH_SHORT).show();
        }
        else if(Integer.parseInt(txtqty.getText().toString())> Integer.parseInt(txtavail.getText().toString())){
            Toast.makeText(getContext(), "Available quantity is less than the requested. Available = "+txtavail.getText().toString(), Toast.LENGTH_SHORT).show();
        }
        else if(imageUri.equals("")){
            Toast.makeText(getContext(), "Image path is empty, try again...", Toast.LENGTH_SHORT).show();
        }
        else{
            pd.setMessage("Saving...");
            pd.show();
            String cid = auth.getCurrentUser().getUid();
            String cartid = dbRef.child("Cart").child(cid).child("Items").push().getKey();
            Cart cart = new Cart();
            cart.setProd_id(pid);
            cart.setCust_id(cid);
            cart.setProd_name(txtname.getText().toString().trim());
            cart.setProd_price(Float.parseFloat(txtprice.getText().toString().trim()));
            cart.setProd_qty(Integer.parseInt(txtqty.getText().toString().trim()));
            cart.setProd_size(sp_size.getSelectedItem().toString().toLowerCase());
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            cart.setDate_added(dateFormat.format(calendar.getTime()));
            cart.setTime_added(timeFormat.format(calendar.getTime()));
            cart.setProd_image(imageUri);
            cart.setCart_id(cartid);

    dbRef.child("Cart").child(cid).child("Items").child(cartid).setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){
                Toast.makeText(getContext(), "Successfully added", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(), "Try again...", Toast.LENGTH_SHORT).show();
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            Log.e(TAG,e.getMessage());
        }
    });
            pd.dismiss();
        }

    }
    private void fetchData() {
        pd.setMessage("Loading...");
        pd.show();
        dbRef.child("Products").child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot!=null){
                    Product prod = snapshot.getValue(Product.class);
                    txtname.setText(prod.getProd_name().trim());
                    Picasso.get().load(prod.getProd_image()).into(prod_image);
                    txtcate.setText(prod.getProd_cate());
                    txtscat.setText(prod.getProd_scat());
                    txtprice.setText(String.valueOf(prod.getProd_price()));
                    txtdesc.setText(prod.getProd_desc());
                    imageUri = prod.getProd_image();
                    pd.dismiss();
                }
                else{
                    Toast.makeText(getContext(), "Product details loading failed...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,error.getMessage());
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getSelectedItemPosition()!=0){
            String size = parent.getSelectedItem().toString();
            dbRef.child("Stock").child(pid).child(size.toLowerCase()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue()!=null){
                        txtavail.setText(snapshot.getValue().toString());
                    }
                    else{
                        txtavail.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}