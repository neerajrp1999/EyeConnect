package com.neer.eyeconnect.ui.Lost;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.*;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.neer.eyeconnect.R;


public class LostDetails extends AppCompatActivity {

    private ImageView img;
    private TextView address, description, ownerName, dateLost, timeLost;
    private Button call, sms;
    String phoneNum;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_details);

        db = FirebaseFirestore.getInstance();

        img = findViewById(R.id.img);
        address = findViewById(R.id.address);
        description = findViewById(R.id.description);
        ownerName = findViewById(R.id.ownerName);
        dateLost = findViewById(R.id.dateLost);
        call = findViewById(R.id.call);
        sms = findViewById(R.id.sms);
        timeLost = findViewById(R.id.timeLost);

        String userID = getIntent().getStringExtra("reportID");

        Log.d("hfdshj","jkdsbkjvnksdd: userID :"+userID);

        Query query = db.collection("reportPerson").whereEqualTo("reportId", userID);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentReference itemRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                    itemRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                String imageUrl = documentSnapshot.getString("imageURI");
                                String itemAddress = documentSnapshot.getString("location");
                                String itemDescription = documentSnapshot.getString("description");
                                String name = documentSnapshot.getString("name");
                                String itemDateLost = documentSnapshot.getString("dateLost");
                                String phone = String.valueOf(documentSnapshot.getLong("phnum")) ;
                                String itemtimeLost = documentSnapshot.getString("timeLost");
                                Log.d("hfdshj","jkdsbkjvnksdd:"+itemDescription);
                                // Load the image using Glide and adjust the ImageView size
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(LostDetails.this)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.placeholder_image)
                                            .error(R.drawable.baseline_image_search_24)
                                            .into(img);
                                }


                                // Set data to TextViews
                                address.setText(itemAddress);
                                description.setText(itemDescription);
                                ownerName.setText(name);
                                dateLost.setText(itemDateLost);
                                timeLost.setText(itemtimeLost);
                                phoneNum=phone;
                            } else {
                                Toast.makeText(LostDetails.this, "Data not found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(LostDetails.this, "Document not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNum));
                startActivity(intent);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:" + phoneNum));
                intent.putExtra("sms_body", "Hello, I want to inquire about your lost item.");
                startActivity(intent);
            }
        });

    }
}