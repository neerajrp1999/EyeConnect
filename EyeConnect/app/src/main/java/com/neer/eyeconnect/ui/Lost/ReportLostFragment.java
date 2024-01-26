package com.neer.eyeconnect.ui.Lost;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.neer.eyeconnect.OnImageUploadCallback;
import com.neer.eyeconnect.R;
import com.neer.eyeconnect.Utility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReportLostFragment extends DialogFragment {
    private ImageButton datePickerButton;

    private ImageButton timePickerButton;
    private TextView dateEdit,timeEdit;
    FirebaseVisionFaceDetectorOptions options1;
    FirebaseVisionFaceDetector detector;

    LinearLayout ll;
    ProgressBar p;
    Bitmap photo;
    Button upload;
    Uri imageUri;
    EditText description, location ;
    final int REQ_CODE=1000;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String date = null;
    String time = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lost_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ll=view.findViewById(R.id.ll);
        p=view.findViewById(R.id.progressBar);
        description = view.findViewById(R.id.description);
        datePickerButton = view.findViewById(R.id.datePickerButton);
        timePickerButton= view.findViewById(R.id.timePickerButton);
        datePickerButton.setOnClickListener(v -> showDatePicker());
        timePickerButton.setOnClickListener(v -> showTimePicker());
        dateEdit= view.findViewById(R.id.selectedDateEditText);
        timeEdit= view.findViewById(R.id.selectedTimeEditText);
        location= view.findViewById(R.id.location);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        upload = view.findViewById(R.id.uploadImageButton);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,REQ_CODE);
            }
        });

        Button submitButton = view.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> {
            ll.setVisibility(View.GONE);
            p.setVisibility(View.VISIBLE);

            EditText person_name_edittext =  view.findViewById(R.id.person_name_edittext);
            String personName =  person_name_edittext.getText().toString();

            if (personName.isEmpty()) {
                ll.setVisibility(View.VISIBLE);
                p.setVisibility(View.GONE);
                Utility.showToast(getContext(), "Name cannot be empty");
                return;
            }

            if (date == null) {
                ll.setVisibility(View.VISIBLE);
                p.setVisibility(View.GONE);
                showDatePicker();
                return;
            }

            if (time == null) {
                ll.setVisibility(View.VISIBLE);
                p.setVisibility(View.GONE);
                showTimePicker();
                return;
            }

            String loc = location.getText().toString();
            if (loc.isEmpty()) {
                ll.setVisibility(View.VISIBLE);
                p.setVisibility(View.GONE);
                Utility.showToast(getContext(), "Please provide location");
                return;
            }

            String desc = description.getText().toString();
            if (desc.isEmpty()) {
                ll.setVisibility(View.VISIBLE);
                p.setVisibility(View.GONE);
                Utility.showToast(getContext(), "Please add description");
                return;
            }

            if (imageUri == null) {
                Utility.showToast(getContext(), "Please add image of missing person");
                return;
            }

            try {
                photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
                        Rect bounds = face.getBoundingBox();

                        ReportPerson reportPerson = new ReportPerson();
                        reportPerson.setName(personName);
                        reportPerson.setDateLost(date);
                        reportPerson.setTimeLost(time);

                        reportPerson.setLocation(location.getText().toString());
                        reportPerson.setDescription(description.getText().toString());

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        if (currentUser != null) {
                            String userEmail = currentUser.getEmail();
                            String userID = currentUser.getUid();

                            db.collection("users")
                                    .whereEqualTo("email", userEmail)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String userPhone = document.getString("phone");
                                                reportPerson.setPhnum(Long.valueOf(userPhone));
                                                reportPerson.setEmail(userEmail);
                                                reportPerson.setUserId(userID);
                                                reportPerson.setReportId(Long.toString(System.nanoTime())+userID);
                                                saveItemToFirebase(reportPerson);
                                            }
                                        } else {
                                            ll.setVisibility(View.VISIBLE);
                                            p.setVisibility(View.GONE);
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    });

                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utility.showToast(getContext(), "Face not detected");
                    ll.setVisibility(View.VISIBLE);
                    p.setVisibility(View.GONE);
                    dismiss();
                }
            });

        });

    }
    private String generateImageName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "image_" + timeStamp + ".jpg";
    }
    private void saveImageToFirebaseStorage(Uri imageUri, OnImageUploadCallback callback) {

        String imageName = generateImageName();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + imageName);

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


    private void saveItemToFirebase(ReportPerson item) {
        try {
            saveImageToFirebaseStorage(imageUri, new OnImageUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    item.setImageURI(imageUrl);
                    DocumentReference documentReference = Utility.getCollectionReferrenceForItems2().document();
                    documentReference.set(item).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Utility.showToast(getContext(), "Item added successfully");
                            ll.setVisibility(View.VISIBLE);
                            p.setVisibility(View.GONE);
                            dismiss();
                        } else {
                            ll.setVisibility(View.VISIBLE);
                            p.setVisibility(View.GONE);
                            Utility.showToast(getContext(), "Failed to add item");
                            dismiss();
                        }
                    });
                }

                @Override
                public void onFailure() {
                    ll.setVisibility(View.VISIBLE);
                    p.setVisibility(View.GONE);
                    Utility.showToast(getContext(), "An error occurred while uploading the image");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ll.setVisibility(View.VISIBLE);
            p.setVisibility(View.GONE);
            Utility.showToast(getContext(), "An error occurred while saving data");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQ_CODE){
                // for gallery
                imageUri = data.getData();
                upload.setText("Image added");

            }
        }
    }


    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDateButton();
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        updateDateButton();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    mHour = hourOfDay;
                    mMinute = minute;
                    updateTimeButton();
                }, mHour, mMinute, false);
        timePickerDialog.show();
        updateTimeButton();
    }

    private String updateDateButton() {
        String date = mDay + "/" + (mMonth + 1) + "/" + mYear;
        dateEdit.setText(date);
        this.date = date;
        return date;
    }

    private String updateTimeButton() {
        String AM_PM;
        if (mHour < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        int hour = mHour % 12;
        if (hour == 0) {
            hour = 12;
        }
        String time = hour + ":" + mMinute + " " + AM_PM;
      timeEdit.setText(time);
        this.time=time;
        return time;
    }

}
