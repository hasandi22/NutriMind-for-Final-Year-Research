package com.example.nutrimind;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    // UI Components
    EditText edUsername, edPassword;
    Button btnLogin;
    TextView tViewSignUp;
    ImageView imgFacebook, imgGoogle, imgInstagram;

    // Firebase Authentication
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI
        edUsername = findViewById(R.id.editTextLoginUserName);
        edPassword = findViewById(R.id.editTextLoginPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        tViewSignUp = findViewById(R.id.textViewSignUp);

        imgFacebook = findViewById(R.id.imgFacebook);
        imgGoogle = findViewById(R.id.imgGoogle);
        imgInstagram = findViewById(R.id.imgInstagram);

        // Facebook
        imgFacebook.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/"))));

        // Google
        imgGoogle.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://accounts.google.com/"))));

        // Instagram
        imgInstagram.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/"))));

        // Login Button
        btnLogin.setOnClickListener(v -> {

            String email = edUsername.getText().toString().trim();
            String password = edPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(Login.this,
                        "Please enter Email and Password",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            // Firebase Login
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(Login.this,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Login.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                        } else {

//                            Toast.makeText(Login.this,
//                                    "Login Failed\n" +
//                                            task.getException().getMessage(),
//                                    Toast.LENGTH_LONG).show();
                            Toast.makeText(
                                    Login.this,
                                    task.getException() != null
                                            ? task.getException().getMessage()
                                            : "Login Failed",
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    });

        });

        // Sign Up
        tViewSignUp.setOnClickListener(v ->
                startActivity(new Intent(Login.this, Signup.class)));

    }
}