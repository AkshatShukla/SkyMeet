package com.rcoem.project.skymeet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText emailField;
    private Button resetBtn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        emailField = (EditText) findViewById(R.id.emailField);
        resetBtn = (Button) findViewById(R.id.resetBtn);

        mAuth = FirebaseAuth.getInstance();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = emailField.getText().toString();

                if (!TextUtils.isEmpty(emailAddress) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                    mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(PasswordResetActivity.this, "A password reset mail has been sent to your email", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PasswordResetActivity.this, MainActivity.class));
                        }
                    });
                }
                else {
                    Toast.makeText(PasswordResetActivity.this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
