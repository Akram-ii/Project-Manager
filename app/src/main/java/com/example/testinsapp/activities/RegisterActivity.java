package com.example.testinsapp.activities;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.testinsapp.R;
import com.example.testinsapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {
TextInputEditText Email,Password,confirm,userName;
TextView login;
Button register;
FirebaseAuth auth=FirebaseAuth.getInstance();
FirebaseFirestore db=FirebaseFirestore.getInstance();
ProgressDialog p;
String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
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
        userName=findViewById(R.id.user);
        register=findViewById(R.id.registerBTN);
        Email=findViewById(R.id.email);
        Password=findViewById(R.id.password);
        confirm=findViewById(R.id.confirm);
        login=findViewById(R.id.loginTextView);
login.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
});
register.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String txtEmail = Email.getText().toString();
        String txtPassword = Password.getText().toString();
        String txtConfirm = confirm.getText().toString();
        String txtUserName = userName.getText().toString();
        if (txtUserName.length()<3 ) {
        userName.setError("Too short");
        } else if (txtUserName.length()>15 ) {
            userName.setError("Too long");
        }else if (txtEmail.isEmpty() ) {
            Email.setError("Enter your E-mail");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
            Email.setError("Not a valid E-mail");
        }else if(txtPassword.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
        } else if(txtPassword.length()<6){
            Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
        }else if (!txtPassword.equals(txtConfirm)){
            Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
        }else{
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
               if(task.isSuccessful()){
                   token=task.getResult();
                   Log.d("token user here ","   :::"+task.getResult());
                   registerUser(txtEmail,txtPassword,txtUserName, task.getResult());
               }
                }
            });
        }
    }
});
    }
    private void registerUser(String email,String password,String username,String token1) {
        p.setMessage("Please wait");
        p.show();
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", username);
                userInfo.put("id", Objects.requireNonNull(auth.getCurrentUser()).getUid());
                String currentDate = FirebaseUtil.timestampToStringNoDetail(Timestamp.now());
                userInfo.put("registerDate",currentDate);
                userInfo.put("token",token1);

                db.collection("Users").document(auth.getCurrentUser().getUid()).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        p.dismiss();
                        if (task.isSuccessful()) {
                            auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(RegisterActivity.this, "A confirmation e-mail has been sent to "+email, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                               Log.d(TAG,"onFailure:email not sent "+e.getMessage());
                                }
                            });
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();

                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                p.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}