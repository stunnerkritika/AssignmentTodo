package com.tbcmad.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    Button btnRegister;
    private EditText email, password;
    FirebaseAuth firebase_Auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);

        firebase_Auth=FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.login_activity_btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email_string = email.getText().toString().trim();
                final String  password_string = password.getText().toString().trim();
                if(TextUtils.isEmpty(email_string))
                {
                    email.setError("Please Enter Email");
                    return;
                }
                else if(TextUtils.isEmpty(password_string))
                {
                    password.setError("Please Enter Password");
                    return;
                }
                else{
                    firebase_Auth.signInWithEmailAndPassword(email_string,password_string).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user.isEmailVerified())
                                {
                                    SharedPreferences preference = getApplicationContext().getSharedPreferences("todo_pref",  0);
                                    SharedPreferences.Editor editor = preference.edit();
                                    editor.putBoolean("authentication",true);
                                    editor.commit();
                                    Intent intent= new Intent(LoginActivity.this,Profile.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    user.sendEmailVerification();
                                    Toast.makeText(LoginActivity.this, "Email has been sent to you verify it to continue ! ", Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Toast.makeText(LoginActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
        btnRegister= findViewById(R.id.register_btn);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preference = getApplicationContext().getSharedPreferences("todo_pref",  0);
                SharedPreferences.Editor editor = preference.edit();
                editor.putBoolean("authentication",true);
                editor.commit();
                Intent intent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}