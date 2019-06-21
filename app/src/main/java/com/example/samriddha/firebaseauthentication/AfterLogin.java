package com.example.samriddha.firebaseauthentication;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

public class AfterLogin extends AppCompatActivity {

    ImageView imageView;
    ProgressBar bar ;
    EditText editText ;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    StorageTask mUpoloadtask ;

    Uri mImageuri ;

    static final int MY_IMAGE = 1 ;
    String currentuserid ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);

        editText = (EditText)findViewById(R.id.pictitle);
        imageView = (ImageView) findViewById(R.id.cirimage);
        bar = (ProgressBar)findViewById(R.id.progressbarid);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentuserid = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(currentuserid);
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImage");

    }

    public void ChoosePic(View view) {

        OpenFileChooser();

    }

    private void OpenFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), MY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_IMAGE && resultCode == RESULT_OK && data != null
                && data.getData()!=null ) {

            mImageuri = data.getData();
            CropImage.activity(mImageuri).setAspectRatio(1, 1).start(AfterLogin.this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageuri = activityResult.getUri();
                Picasso.with(AfterLogin.this).load(mImageuri).into(imageView);

                /*currentuserid = firebaseUser.getUid();
                final StorageReference filepath = storageReference.child("ProfileImage").child(currentuserid + ".jpg");
                filepath.putFile(mImageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                            String url = task.getResult().getUploadSessionUri().toString();

                            databaseReference.child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bar.setProgress(0);
                                            }
                                        },3000);

                                        Toast.makeText(AfterLogin.this, "Upload Successfull", Toast.LENGTH_SHORT).show();
                                        Picasso.with(AfterLogin.this).load(uri).into(circularImageView);
                                    }

                                }
                            });
                        }else{
                            Toast.makeText(AfterLogin.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double myprogress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                                bar.setProgress((int)myprogress);
                            }
                        });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = activityResult.getError();

            */
            }
        }
    }

    private String getFileExtention(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public void UploadPic(View view) {

        if (mUpoloadtask != null && mUpoloadtask.isInProgress()){

            Toast.makeText(this, "Upload Is In Processes", Toast.LENGTH_SHORT).show();
        }else{

            methodUploadPic();

        }

    }

    private void methodUploadPic() {

        if(mImageuri != null){

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+ getFileExtention(mImageuri));

            mUpoloadtask = fileReference.putFile(mImageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    bar.setProgress(0);
                                }
                            },2000);

                            Toast.makeText(AfterLogin.this, "Upload Is Successfull", Toast.LENGTH_SHORT).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl(); // fuckin getDownloadUrl Method at last works...
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();

                            final String sdownload_url = String.valueOf(downloadUrl);

                            Upload upload = new Upload(editText.getText().toString().trim(),sdownload_url);
                            String uploadID = databaseReference.push().getKey();
                            databaseReference.child("images").child(uploadID).setValue(upload);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AfterLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                            bar.setProgress((int)progress);
                        }
                    });

        }else{

            Toast.makeText(this, "No File Is Selected", Toast.LENGTH_SHORT).show();

        }

    }

    public void MethodShowPic(View view) {

        startActivity(new Intent(AfterLogin.this,ShowImage.class));
    }
}