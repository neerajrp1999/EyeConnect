package com.neer.eyeconnect.ui.Found;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.neer.eyeconnect.ApiCallCallback;
import com.neer.eyeconnect.OnImageUploadCallback;
import com.neer.eyeconnect.R;
import com.neer.eyeconnect.Utility;
import com.neer.eyeconnect.ui.Lost.LostDetails;
import com.neer.eyeconnect.ui.Lost.ReportPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class FoundPersonFragment extends DialogFragment {
    Bitmap photo;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    ImageView itemImageView;
    TextView nameTextView;
    TextView description;
    TextView location;
    Button yes,no;
    TextView date;
    ImageButton deleteButton;
    private String apiURL= "https://neerajrp1999.pythonanywhere.com/request?id=";

    FirebaseVisionCloudDetectorOptions options =
            new FirebaseVisionCloudDetectorOptions.Builder()
                    .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                    .setMaxResults(15)
                    .build();
    FirebaseVisionFaceDetectorOptions options1;
    FirebaseVisionFaceDetector detector;
    public FoundPersonFragment(Bitmap photo){
        this.photo=photo;
    }

    LinearLayout linearlayout_;
    CardView resultCardView;
    ProgressBar progressBar;
    TextView showText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.found_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemImageView = view.findViewById(R.id.itemImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        description= view.findViewById(R.id.item_description);
        location = view.findViewById((R.id.location));
        date = view.findViewById(R.id.dateLost);
        deleteButton= view.findViewById(R.id.deleteButton);

        linearlayout_=view.findViewById(R.id.LinearLayout_);
        showText=view.findViewById(R.id.showText);
        progressBar=view.findViewById(R.id.progressBar);
        resultCardView=view.findViewById(R.id.resultCardView);

        yes=view.findViewById(R.id.yes);
        no=view.findViewById(R.id.no);

        showText.setText("Detecting face in image");
        progressBar.setVisibility(View.VISIBLE);

        options1 = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .build();
        detector = FirebaseVision.getInstance().getVisionFaceDetector(options1);

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                for (FirebaseVisionFace face : firebaseVisionFaces) {
                    showText.setText("face is detected...\nTry to recognize person...");
                    saveItemToFirebase(photo);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showError("face not detected",e.toString());
            }
        });

    }
    private String generateImageName() {
        return "image_" + Long.toString(System.nanoTime()) + ".jpg";
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        Log.d("ghhghghg","jhvhjnbnm");
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, System.nanoTime()+"Title", null);
        Log.d("ghhghghg","jhvhjnbnm");
        Log.d("ghhghghg",Uri.parse(path).toString());
        return Uri.parse(path);
    }
    private void saveImageToFirebaseStorage(Uri imageUri, OnImageUploadCallback callback) {

        String imageName = generateImageName();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("requestedImages/" + imageName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        callback.onSuccess(imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    callback.onFailure();
                });
    }


    private void saveItemToFirebase(Bitmap photo) {
        Uri imageUri=getImageUri(getContext(),photo);
        RequestSearch item=new RequestSearch();
        try {
            saveImageToFirebaseStorage(imageUri, new OnImageUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {

                    item.setImageURL(String.valueOf(imageUrl));
                    String id=Long.toString(System.nanoTime());
                    DocumentReference documentReference = Utility.getCollectionReferrenceForRealTimeRequest().document(id);
                    documentReference.set(item).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Utility.showToast(getContext(), "Data is uploaded successfully");
                            showText.setText("Data is uploaded successfully\nFinding person..."+id);

                            requestAPIcall(id, new ApiCallCallback() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    showResult(imageUrl);
                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                        } else {
                            Utility.showToast(getContext(), "Failed to uploaded data");
                            try {
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            showError("Failed to uploaded data","");
                            dismiss();
                        }
                    });
                }

                @Override
                public void onFailure() {
                    showError("An error occurred while uploading the image","");
                    Utility.showToast(getContext(), "An error occurred while uploading the image");
                }
            });
        } catch (Exception e) {
            showError("An error occurred while saving data",e.toString());
            Utility.showToast(getContext(),"An error occurred while saving data" );
        }
    }
    private void requestAPIcall(String id, ApiCallCallback apiCallCallback){
        String url = apiURL + id;
        mRequestQueue = Volley.newRequestQueue(getContext());
        showText.setText("Finding person...");
        // String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                apiCallCallback.onSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError("Error on finding person...",error.toString());
            }
        });

        mStringRequest.setRetryPolicy(
                new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 15000000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 15000000;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {
                        showError("Error on finding person...",error.toString());
                    }
                }
        );
        mRequestQueue.add(mStringRequest);
    }

    private void showResult(String string) {
        progressBar.setVisibility(View.GONE);
        linearlayout_.setVisibility(View.GONE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //   {"result": ["QTyVKQzrOIXEOkuZCFdW"]}
        JSONObject jsonObject;
        String resultString="";
        try{
            jsonObject = new JSONObject(string);

            // Extract the array from the JSON object
            JSONArray resultArray = jsonObject.getJSONArray("result");

            // Extract the string from the array
            resultString = resultArray.getString(0);

            Log.d("hfdshj","jkdsbkjvnksdd:"+resultString);
            //filter_string=jsonArray.getJSONArray(0).getString(0);
        }
        catch (Exception e){}


        Log.d("hfdshj","jkdsbkjvnksdd:"+resultString);
        //String filter_string=string;

        DocumentReference docRef = db.collection("reportPerson").document(resultString);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ReportPerson model = documentSnapshot.toObject(ReportPerson.class);
                Log.d("hfdshj","jkdsbkjvnksdd:"+model.getReportId());
                if(model.getReportId().isEmpty()){
                    showError("Face not found...","");
                    return;
                }
                /*
                Intent intent = new Intent(getContext().getApplicationContext(), LostDetails.class);
                Toast.makeText(getContext().getApplicationContext(), "Person Founded",Toast.LENGTH_SHORT).show();
                intent.putExtra("reportID", model.getUserId());
                dismiss();
                startActivity(intent);
                */
                if (model.getImageURI() != null && !model.getImageURI().isEmpty()) {
                    Glide.with(getContext().getApplicationContext())
                            .load(model.getImageURI())
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.baseline_image_search_24)
                            .into(itemImageView);
                }
                nameTextView.setText("Name:"+model.getName());
                description.setText("Description:"+model.getDescription());
                //location.setText(model.getLocation());
                date.setText(model.getDateLost());
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext().getApplicationContext(), LostDetails.class);
                        Toast.makeText(getContext().getApplicationContext(), "Person Founded",Toast.LENGTH_SHORT).show();
                        intent.putExtra("reportID", model.getReportId());

                        Log.d("hfdshj","jkdsbkjvnksdd:"+model.getReportId());


                        startActivity(intent);
                        dismiss();

                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resultCardView.setVisibility(View.GONE);
                        dismiss();
                    }
                });

            }
        });


        resultCardView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        /*
        Intent intent = new Intent(getContext().getApplicationContext(), LostDetails.class);
        Toast.makeText(getContext().getApplicationContext(), "Person Founded",Toast.LENGTH_SHORT).show();
        intent.putExtra("userID", resultString);
        dismiss();
        startActivity(intent);
        */

   }

    private void showError(String message,String errorMessage) {
        showText.setText(message);
        progressBar.setVisibility(View.GONE);
        Log.e("Error",errorMessage);
    }
}