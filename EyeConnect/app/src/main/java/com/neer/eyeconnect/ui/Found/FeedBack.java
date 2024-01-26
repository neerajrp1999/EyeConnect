package com.neer.eyeconnect.ui.Found;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neer.eyeconnect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FeedBack extends DialogFragment {
    String email;
    public FeedBack(String email){
        this.email=email;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_feedback_items, container, false);
        EditText e=v.findViewById(R.id.feed1);

        Button sumit=v.findViewById(R.id.submit_button);
        sumit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String,Object> m=new HashMap<>();
                m.put("feed",e.getText());
                List<Map<String,Object>> l=new ArrayList<>();
                l.add(m);
                Data1 n=new Data1(e.getText().toString().trim());
                db.collection("users").document(email).set(n).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        e.setText(null);
                                    }
                                }
                        )
                        .addOnFailureListener(e -> {
                            // Handle any errors here
                        });
                Toast.makeText(getActivity().getApplicationContext(),"Feed back Submited",Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}

