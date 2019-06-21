package com.example.samriddha.firebaseauthentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {


    EditText logemail , logpassword ;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        logemail = (EditText)findViewById(R.id.logmailid);
        logpassword = (EditText)findViewById(R.id.logpassid);
        firebaseAuth=FirebaseAuth.getInstance();

    }

    public void LogIn(View view) {

            String email2 = logemail.getText().toString();
            String password2 = logpassword.getText().toString();
            if(email2.isEmpty()){

                logemail.setError("Please Enter Name");
            }else if(password2.isEmpty()){

                logpassword.setError("Please Enter Email");
            }else {

                loginMethod(email2,password2);
            }


    }

    private void loginMethod(final String email2,final String password2) {

        firebaseAuth.signInWithEmailAndPassword(email2,password2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LogIn.this, "Log In Successfull", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LogIn.this,AfterLogin.class));
                    finish();
                }
                else{
                    Toast.makeText(LogIn.this, "Worng Email Or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
