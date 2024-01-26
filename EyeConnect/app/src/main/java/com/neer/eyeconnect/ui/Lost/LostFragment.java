package com.neer.eyeconnect.ui.Lost;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.neer.eyeconnect.R;
import com.neer.eyeconnect.Utility;
import com.neer.eyeconnect.databinding.FragmentLostBinding;
import com.google.firebase.firestore.Query;


public class LostFragment extends Fragment {

    private FragmentLostBinding binding;
    FloatingActionButton addBtn;
    RecyclerView recView;
    LostPersonAdapter adapter;
    String selectedCategory = "";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recView = root.findViewById(R.id.lostRecyclerView);
        setupRecyclerView();

        addBtn = root.findViewById(R.id.add_lost);

        addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReportLostFragment dialogFragment = new ReportLostFragment();
                    dialogFragment.show(getParentFragmentManager(), "form_dialog");
                }
        });
        setupRecyclerView();
        return root;
    }

    void setupRecyclerView(){
        Query query;
        if (selectedCategory.isEmpty()) {
            query = Utility.getCollectionReferrenceForItems2().orderBy("dateLost", Query.Direction.DESCENDING);
        } else {
            query = Utility.getCollectionReferrenceForItems2().whereEqualTo("category", selectedCategory)
                    .orderBy("dateLost", Query.Direction.DESCENDING);
        }
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Log.d("LostFragment", "Document ID: " + documentSnapshot.getId());
                // Log other fields to ensure that the data is retrieved correctly
            }
        }).addOnFailureListener(e -> {
            Log.e("LostFragment", "Error fetching data: " + e.getMessage());
        });

        FirestoreRecyclerOptions<ReportPerson> options = new FirestoreRecyclerOptions.Builder<ReportPerson>()
                .setQuery(query, ReportPerson.class).build();

        if (adapter == null || !adapter.getCategory().equals(selectedCategory)) {
            if (adapter != null) {
                adapter.stopListening();
            }
            adapter = new LostPersonAdapter(options, requireContext(), selectedCategory,false);
            adapter.setCategory(selectedCategory);

            recView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recView.setAdapter(adapter);
            adapter.startListening();
        } else {
            adapter.updateOptions(options);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}