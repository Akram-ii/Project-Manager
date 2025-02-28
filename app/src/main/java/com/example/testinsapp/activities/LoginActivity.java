package com.example.testinsapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.testinsapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

TextInputEditText password,email;
TextInputLayout i;
Button login;
TextView forgot,register;
FirebaseAuth auth= FirebaseAuth.getInstance();
ProgressDialog p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        p=new ProgressDialog(this);
        i=findViewById(R.id.ptexti);
        login=findViewById(R.id.loginBTN);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        forgot=findViewById(R.id.forgotPassword);
        register=findViewById(R.id.registerTextView);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        EditText resetMail=new EditText(v.getContext());

                AlertDialog.Builder passwordResetDialog=new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset password?");
                passwordResetDialog.setMessage("Enter your email to receive a reset link");
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setPositiveButton("send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail=resetMail.getText().toString();
                        if(mail.isEmpty()){
                            Toast.makeText(LoginActivity.this,"You have to enter your email",Toast.LENGTH_SHORT).show();
                        }else {
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this, "Reset link sent to "+mail, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "link not sent because "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });}
                    }
                });
                passwordResetDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = passwordResetDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail=email.getText().toString();
                String txtPassword=password.getText().toString();
                if (txtEmail.isEmpty() || txtPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
                    email.setError("Not a valid E-mail");
                } else if(txtPassword.length()<6){
                    Toast.makeText(LoginActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                }else {
                    loginUser(txtEmail,txtPassword);
                }
            }
        });
    }
    protected void onStart() {
        super.onStart();
        FirebaseUser e= FirebaseAuth.getInstance().getCurrentUser();
        if(e!=null && e.isEmailVerified()){

            Toast.makeText(LoginActivity.this,"Welcome back",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
    private void loginUser(String email, String password) {
        p.setMessage("Please wait");
        p.show();
    auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
            p.dismiss();
            FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
            if(user.isEmailVerified()){
            Toast.makeText(LoginActivity.this,"Welcome back!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();}else{
                showAlertDialog();
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            p.dismiss();
            Toast.makeText(LoginActivity.this,"Error "+e.getMessage(),Toast.LENGTH_SHORT).show();

        }
    });
}

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified yet");
        builder.setMessage("Please verify your email.you can not login without email verification ");

        builder.setPositiveButton("Verify now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           Intent intent=new Intent(Intent.ACTION_MAIN);
           intent.addCategory(Intent.CATEGORY_APP_EMAIL);
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(intent);
            }
        });
        builder.setNegativeButton("Resend email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
FirebaseUser u=FirebaseAuth.getInstance().getCurrentUser();
dialog.dismiss();
u.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
    @Override
    public void onSuccess(Void unused) {
        Toast.makeText(LoginActivity.this,"Verification Email sent to "+email.getText().toString(),Toast.LENGTH_SHORT).show();
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
   Toast.makeText(LoginActivity.this,"error "+e.getMessage(),Toast.LENGTH_SHORT).show();
    }
});
            }
        });
        builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}