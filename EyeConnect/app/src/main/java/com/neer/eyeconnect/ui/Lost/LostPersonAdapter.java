package com.neer.eyeconnect.ui.Lost;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.neer.eyeconnect.R;
import com.neer.eyeconnect.Utility;

public class LostPersonAdapter extends FirestoreRecyclerAdapter<ReportPerson, LostPersonAdapter.ItemViewHolder> {

    Context context;
    private String category;
    private boolean showDeleteButton;

    public LostPersonAdapter(@NonNull FirestoreRecyclerOptions<ReportPerson> options, Context context, String category, boolean showDeleteButton) {
        super(options);
        this.context = context;
        this.category = category;
        this.showDeleteButton=showDeleteButton;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_item_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReportPerson model) {
            if (model.getImageURI() != null && !model.getImageURI().isEmpty()) {
                Glide.with(context)
                        .load(model.getImageURI())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.baseline_image_search_24)
                        .into(holder.itemImageView);
            }
            holder.nameTextView.setText("Name:"+model.getName());
            holder.description.setText("Description:"+model.getDescription());
            holder.location.setText(model.getLocation());
            holder.date.setText(model.getDateLost());
            if(this.showDeleteButton){
                holder.deleteButton.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, LostDetails.class);
                Toast.makeText(context.getApplicationContext(), model.getReportId(),Toast.LENGTH_SHORT).show();
                intent.putExtra("reportID", model.getReportId());
                context.startActivity(intent);
            });

            holder.deleteButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                DialogInterface.OnClickListener dialogClickListenerPositive = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("hcgchhghg","yes");
                                //String documentId = getSnapshots().getSnapshot(position).getId();
                                Utility.getCollectionReferrenceForItems2().whereEqualTo("reportId",model.getReportId())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                        Toast.makeText(context.getApplicationContext(), document.getId(),Toast.LENGTH_SHORT).show();

                                                        Utility.getCollectionReferrenceForItems2().document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(v.getContext(), "report delete!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(v.getContext(), "Couldn't delete!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    Toast.makeText(v.getContext(), "Couldn't delete!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            // An error occurred, handle the error appropriately
                                            Toast.makeText(v.getContext(), "Couldn't delete!", Toast.LENGTH_SHORT).show();
                                        });
                                dialog.dismiss();
                    }
                };


                DialogInterface.OnClickListener dialogClickListenerNegative = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };


                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListenerPositive)
                        .setNegativeButton("No", dialogClickListenerNegative).show();
            });


    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView nameTextView;
        TextView description;
        TextView location;
        TextView date;
        ImageButton deleteButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            description= itemView.findViewById(R.id.item_description);
            location = itemView.findViewById((R.id.location));
            date = itemView.findViewById(R.id.dateLost);
            deleteButton= itemView.findViewById(R.id.deleteButton);
        }
    }
}

