package com.neer.eyeconnect.ui.MyItems;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.neer.eyeconnect.R;
import com.neer.eyeconnect.Utility;
import com.neer.eyeconnect.databinding.FragmentLostBinding;
import com.neer.eyeconnect.ui.Lost.LostPersonAdapter;
import com.neer.eyeconnect.ui.Lost.ReportPerson;

public class MyItems extends Fragment {

    private FragmentLostBinding binding;
    private LostPersonAdapter lostAdapter;
    private String userId;

    private RecyclerView lostRecyclerView;
    private FloatingActionButton add;
    TextView filterButton ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        lostRecyclerView = root.findViewById(R.id.lostRecyclerView);
        add = root.findViewById(R.id.add_lost);
        filterButton = root.findViewById(R.id.filterButton);

        setupRecyclerView(true);

        return root;
    }

    void setupRecyclerView(boolean showDeleteButton) {
        add.setVisibility(View.GONE);

        Query lostQuery = Utility.getCollectionReferrenceForItems2().whereEqualTo("userId", userId);
        FirestoreRecyclerOptions<ReportPerson> lostOptions = new FirestoreRecyclerOptions.Builder<ReportPerson>()
                .setQuery(lostQuery, ReportPerson.class).build();

        lostRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        lostAdapter = new LostPersonAdapter(lostOptions, requireContext(), "", showDeleteButton);
        lostRecyclerView.setAdapter(lostAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (lostAdapter != null) {
            lostAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lostAdapter != null) {
            lostAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lostAdapter != null) {
            lostAdapter.notifyDataSetChanged();
        }
    }
}
