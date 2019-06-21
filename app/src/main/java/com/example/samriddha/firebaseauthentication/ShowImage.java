package com.example.samriddha.firebaseauthentication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ShowImage extends AppCompatActivity implements OnItemClickListner{

    private RecyclerView recyclerView ;
    private RecyclerViewAdaptar mAdaptar ;
    private DatabaseReference databaseRef ;
    private FirebaseStorage mStorage;
    private List<Upload> mUploads ;
    private FirebaseUser firebaseUser ;
    private String getCurrentUser ;
    private ProgressBar progressBar ;
    private ValueEventListener valueEventListener ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        progressBar = (ProgressBar)findViewById(R.id.loadingimage_id);
        recyclerView = findViewById(R.id.recyclerviewid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mAdaptar = new RecyclerViewAdaptar(ShowImage.this , mUploads);
        recyclerView.setAdapter(mAdaptar);
        mAdaptar.setOnItemClickListner(ShowImage.this);

        mStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getCurrentUser = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference("user").child(getCurrentUser).child("images");

        valueEventListener = databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();

                for ( DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Upload uploadClas = snapshot.getValue(Upload.class);
                    uploadClas.setmKey(snapshot.getKey());
                    mUploads.add(uploadClas);
                }

                mAdaptar.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ShowImage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });



    }

    @Override
    public void OnSimpleClick(int position) {

        Toast.makeText(this, "Simple Click at Position"+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnDoNothingClick(int position) {

        Toast.makeText(this, "Do Nothing at Position"+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnDeleteImageClick(int position) {

        Upload currentUp = mUploads.get(position);
        final String currentKey = currentUp.getmKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(currentUp.getImageUrl());

        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                databaseRef.child(currentKey).removeValue();
                Toast.makeText(ShowImage.this, "Image Deleted", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseRef.removeEventListener(valueEventListener);
    }
}
