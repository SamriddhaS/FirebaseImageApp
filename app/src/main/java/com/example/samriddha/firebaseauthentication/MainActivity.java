package com.example.samriddha.firebaseauthentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText name,email , password ;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name= (EditText)findViewById(R.id.name);
        email= (EditText)findViewById(R.id.email);
        password= (EditText)findViewById(R.id.password);

        firebaseAuth = FirebaseAuth.getInstance();


    }

    public void SignUpMethod(View view) {

        String name1 = name.getText().toString();
        String email1 = email.getText().toString();
        String password1 = password.getText().toString();
        if(name1.isEmpty()){

            name.setError("Please Enter Name");
        }else if(email1.isEmpty()){

            email.setError("Please Enter Email");
        }else if(password1.isEmpty()){

            password.setError("Please Enter Password");
        }else {

            sendData(name1,email1,password1);
        }
    }

    private void sendData(final String name1,final String email1,final String password1) {

        firebaseAuth.createUserWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String currentuser = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(currentuser);

                    HashMap<String,String> hashMap = new HashMap<>();

                    hashMap.put("user",name1);
                    hashMap.put("email",email1);
                    hashMap.put("password",password1);

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                name.setText("");
                                email.setText("");
                                password.setText("");

                                Toast.makeText(MainActivity.this, "Successfully Registeed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




                }else {
                    Toast.makeText(MainActivity.this, "Can't Complete Task", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    public void GoLogIn(View view) {

        startActivity(new Intent(MainActivity.this,LogIn.class));
        finish();
    }
}


