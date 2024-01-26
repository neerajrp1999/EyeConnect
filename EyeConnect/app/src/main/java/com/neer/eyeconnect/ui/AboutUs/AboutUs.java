package com.neer.eyeconnect.ui.AboutUs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neer.eyeconnect.R;

public class AboutUs extends Fragment {
    TextView git, profileEmail, profilePhone, titleName;
    FirebaseFirestore database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_profile_about_us, container, false);
        git = root.findViewById(R.id.git);
        profileEmail = root.findViewById(R.id.profileEmail);
        profilePhone = root.findViewById(R.id.profilephone);
        titleName = root.findViewById(R.id.titlename);

        database = FirebaseFirestore.getInstance();

        fetchUserData();
        return root;
    }

    private void fetchUserData() {
       titleName.setText("Neeraj R Prajapati");
       git.setText("github.com/neerajrp1999");
       profileEmail.setText("neerajrp1999@gmail.com");
       profilePhone.setText("+91 8850482851");

    }
}