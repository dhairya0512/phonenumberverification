package dhairyapandya.com.otpverification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ManageOtp extends AppCompatActivity {
    EditText et1, et2, et3, et4, et5, et6;
    EditText t2;
    Button b2;
    String phonenumber;
    String otpid;
    FirebaseAuth mAuth;
    TextView sendotpagain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_otp);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFC107")));

        et1 = findViewById(R.id.inputotp1);
        et2 = findViewById(R.id.inputotp2);
        et3 = findViewById(R.id.inputotp3);
        et4 = findViewById(R.id.inputotp4);
        et5 = findViewById(R.id.inputotp5);
        et6 = findViewById(R.id.inputotp6);
        sendotpagain=findViewById(R.id.sendotp_again);
        phonenumber=getIntent().getStringExtra("mobile").toString();
//        t2=(EditText)findViewById(R.id.t2);
        String t2 =et1.getText().toString() +
                et2.getText().toString() +
                et3.getText().toString() +
                et4.getText().toString() +
                et5.getText().toString() +
                et6.getText().toString();
        b2=(Button)findViewById(R.id.btnsubmit);
        mAuth=FirebaseAuth.getInstance();
//        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

        initiateotp();
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(t2.isEmpty())
                    Toast.makeText(getApplicationContext(),"Blank Field can not be processed",Toast.LENGTH_LONG).show();
                else if(t2.length()!=6)
                    Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_LONG).show();
                else
                {
                    //OTP THIK HAI
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(otpid,t2);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });

        sendotpagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        ManageOtp.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {


                                Toast.makeText(ManageOtp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newbackendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {


                                otpid = newbackendotp;
                                Toast.makeText(ManageOtp.this, "OTP Send Sucessfuly", Toast.LENGTH_SHORT).show();


                            }
                        }

                );
            }
        });

    }
    private void initiateotp()
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                {
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
                    {
                        //When otp is not in the same device as sim is insertd
                        otpid=s;
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
                    {
                        //When otp is in the same device as sim is insertd

                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });        // OnVerificationStateChangedCallbacks

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            startActivity(new Intent(ManageOtp.this,dashboard.class));
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(),"Signin Code Error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}