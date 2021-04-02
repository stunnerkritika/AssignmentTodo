package com.tbcmad.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tbcmad.todoapp.users.users;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";

    private EditText name;
    private EditText password;
    private EditText email;
    private EditText phone;

    private Button button_register;
    ProgressBar progressBar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        button_register = findViewById(R.id.register_button);
        name = findViewById(R.id.name_input);
        password = findViewById(R.id.password_input);
        email = findViewById(R.id.email_input);
        phone = findViewById(R.id.phone_input);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name_string = name.getText().toString().trim();
                final String password_string = password.getText().toString().trim();
                final String email_string = email.getText().toString().trim();
                final String phone_string = phone.getText().toString().trim();

                if(TextUtils.isEmpty(name_string))
                {
                    name.setError("Enter your Full name");
                    return;
                }
                else if(TextUtils.isEmpty(password_string)||password_string.length() < 6)
                {
                    password.setError("Password not valid");
                    return;
                }
                else if(TextUtils.isEmpty(email_string))
                {
                    email.setError("Email not valid");
                    return;
                }
                else if(TextUtils.isEmpty(phone_string))
                {
                    phone.setError("Enter your contact number");
                    return;
                }
                else{
                    fAuth.createUserWithEmailAndPassword(email_string,password_string).addOnCompleteListener(new OnCompleteListener<AuthResult>(){

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                               users user = new users();
                                user.setPhone(phone_string);
                                user.setName(name_string);
                                user.setPassword(password_string);
                                user.setEmail(email_string);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>(){

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                                }
                                                else{
                                                    Toast.makeText(RegisterActivity.this, "Registered Unsucessfull !", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                                }
                                                progressBar.setVisibility(View.GONE);


                                            }
                                        });
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Creation of the user Unsucessfull !", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });


                }
            }
        });
    }
}