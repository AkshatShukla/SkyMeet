package com.rcoem.project.skymeet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SignUp extends AppCompatActivity implements View.OnClickListener {


    private EditText mName;
    private EditText emailtext;
    private EditText passwordtext;
    private EditText repasswordtext;
    private Button signUpBtn;
    private RelativeLayout li;
    private RadioGroup designation;

    public String departmentSelected;
    public String email;
    public String password;
    public String repassword;

    private ProgressDialog mProgress;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    //defining FireBase Auth object
    public FirebaseAuth firebaseAuth;

    //defining FireBase real-time database object
    public DatabaseReference mDatabase1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpBtn = (Button) findViewById(R.id.signupBtn);

        mProgress  = new ProgressDialog(this);

        signUpBtn.setOnClickListener(this);

    }


    //After clicking Sign Up button, following method is executed
    @Override
    public void onClick(final View view) {

        boolean signUpOk = false;
        boolean nameAllGood = false;
        boolean emailAllGood = false;
        boolean passwordAllGood = false;


        mName = (EditText) findViewById(R.id.editName);
        emailtext = (EditText) findViewById(R.id.sign_up_email_text);
        passwordtext = (EditText) findViewById(R.id.sign_up_password_text);
        repasswordtext = (EditText) findViewById(R.id.sign_up_repassword_text);
        li = (RelativeLayout) findViewById(R.id.sign_up_relative);

        //designation = (RadioGroup) findViewById(R.id.desg_rdio_btn);

        final String Name = mName.getText().toString().trim();

        //Validate for empty Name field
        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(SignUp.this, "Please Enter Name", Toast.LENGTH_LONG).show();
        } else {
            nameAllGood = true;
        }


        //Validate Email
        email = emailtext.getText().toString().trim();
        if (!isValidEmail(email)) {
            emailtext.setError("Enter a valid Email Address");
            Toast.makeText(this, R.string.incorrect_email, Toast.LENGTH_LONG).show();
        } else {
            emailAllGood = true;
        }

        //Validate Password
        password = passwordtext.getText().toString();
        repassword = repasswordtext.getText().toString();
        if (password.length() < 8) {
            passwordtext.setError("Must be atleast 8 characters long");
            Toast.makeText(this, "Must be atleast 8 characters long", Toast.LENGTH_LONG).show();
        } else if (password.length() >= 8) {
            if (!password.equals(repassword)) {
                passwordtext.setError("Passwords do not match.");
                Toast.makeText(this, "Password do not match", Toast.LENGTH_LONG).show();
            } else {
                passwordAllGood = true;
            }
        }


        //Complete SignUpAction
        if (nameAllGood && emailAllGood && passwordAllGood) {
            signUpOk = true;
            mProgress.setMessage("Signing you up...");
            mProgress.setIndeterminate(true);
            mProgress.setCancelable(false);
            mProgress.show();
        }


        if (signUpOk) {

            //Initializing FireBase Auth object
            firebaseAuth = FirebaseAuth.getInstance();


            //creating a new user in FireBase Auth Database
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //checking if success
                            if (task.isSuccessful()) {
                                //display some message here
                            } else {
                                //display some message here

                                Toast.makeText(SignUp.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            /*  This code is executed after 4000ms or 4 seconds because first the user is created with
                a randomly generated firebase unique UID in the Authentication Database and ONLY THEN that UID is taken to create a
                child "Users" in the Real-time Database from the Authentication Database. If this delay is not given, it will
                result in a NULL pointer exception as the user will not be allotted a UID in sufficient time and by then the Firebase
                will search for an empty "Users" child in Real-time database thus resulting in app crash with Null Pointer Exception.
             */

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {

                        calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);

                        month = calendar.get(Calendar.MONTH) + 1;   //Month in Calendar API start with 0.
                        day = calendar.get(Calendar.DAY_OF_MONTH);
                        final String currentDate = day + "/" + month + "/" + year;

                        //Create a new node named "Users" in your real-time database.
                        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");

                        final String Name = mName.getText().toString().trim();      //Set an attribute child in it as name (as user entered during sign up
                        final String user_id = firebaseAuth.getCurrentUser().getUid();  //Set parent as UID

                        final String emailid = emailtext.getText().toString().trim();
                        final String pass = passwordtext.getText().toString().trim();

                        //add name attribute and set its value as Name entered by user under "Users"
                        mDatabase1.child(user_id).child("name").setValue(Name);

                        //add emailid attribute and set its value as E-Mail entered by user under "Users"
                        mDatabase1.child(user_id).child("email").setValue(emailid);

                        //pushing default image for user
                        mDatabase1.child(user_id).child("images").setValue("https://firebasestorage.googleapis.com/v0/b/skymeet-28d12.appspot.com/o/cloud.png?alt=media&token=691ba99d-25b5-476c-8b87-c746e6092967");

                        //Creating a new child password under the parent "Second Empty Parent"
                        mDatabase1.child(user_id).child("time").setValue(currentDate);

                        mProgress.dismiss();
                        Intent intent = new Intent(SignUp.this,MainActivity.class);
                        startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(SignUp.this, "", Toast.LENGTH_LONG).show();
                    }
                }
            }, 500);   //0.5 seconds delay


        }
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
