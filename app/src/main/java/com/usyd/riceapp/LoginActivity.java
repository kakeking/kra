package com.usyd.riceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "LoginActivity";

    private LinearLayout login_block, verify_block;
    private EditText verifyCode, userPhone;
    private TextView termsLink;
    private Button submitBtn, verifyBtn, resendBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private MaterialCheckBox checkbox;
    private CustomDialog mDialog;
    private CountryCodePicker ccp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private String mVerificationId, phoneNumber;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog mProgressDialog;

    public static final Pattern PHONE_PATTERN = Pattern.compile("[\\s\\./0-9]*$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.activity_login);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null){
            //user is signed in log user out
//            signOut();
        }
        userPhone = findViewById(R.id.login_phone);
        ccp = findViewById(R.id.login_ccp);
        ccp.registerCarrierNumberEditText(userPhone);
        login_block = findViewById(R.id.login_block);
        verify_block = findViewById(R.id.verify_block);
        termsLink = findViewById(R.id.terms_cons);
        checkbox = findViewById(R.id.check_box);
        verifyCode = findViewById(R.id.verify_code);
        submitBtn = findViewById(R.id.btn_login);
        verifyBtn = findViewById(R.id.verify_btn);
        resendBtn = findViewById(R.id.resend_btn);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    submitBtn.setEnabled(true);
                }else{
                    submitBtn.setEnabled(false);
                }
            }
        });
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phoneNumber, mResendToken);
                startTime();
            }
        });
        termsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new CustomDialog(LoginActivity.this);
                mDialog.setTitle(getResources().getString(R.string.terms_title));
                mDialog.setMessage(getResources().getString(R.string.terms_and_conditions));
                mDialog.setYesOnclickListener("Accept", new CustomDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        checkbox.setChecked(true);
                        mDialog.dismiss();
                    }
                });
                mDialog.setNoOnclickListener("Cancel", new CustomDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        mDialog.dismiss();
                    }
                });
                mDialog.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = ccp.getFullNumberWithPlus();

                if (!validateForm()) {
                    return;
                }
                startPhoneNumberVerification(phoneNumber);
            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = verifyCode.getText().toString();
                if(code.equals("")){
                    verifyCode.setError("please enter verificaton code");
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                showProgressDialog(getResources().getString(R.string.loading_message));
                signInWithPhoneAuthCredential(credential);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Invalid Phone number",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    resendBtn.setActivated(false);
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                hideProgressDialog();
                login_block.setVisibility(View.GONE);
                verify_block.setVisibility(View.VISIBLE);
            }

        };

    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    private boolean validateForm() {
        boolean valid = true;
        String phone = userPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            userPhone.setError("phone number is required");
            valid = false;
        } else {
            if (!isValidPhone(phone)) {
                valid = false;
            } else {
                userPhone.setError(null);
            }
        }
        return valid;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
        mVerificationInProgress = true;
    }
    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        User user = documentSnapshot.toObject(User.class);
                                        ((UserClient)(getApplicationContext())).setUser(user);
                                        SendUserToMainActivity();
                                    }else{
                                        final User newUser = new User(phoneNumber, "");
                                        docRef.set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    ((UserClient)(getApplicationContext())).setUser(newUser);
                                                    SendUserToMainActivity();
                                                }else{
                                                    Log.e(TAG, "create document failed");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                            // [START_EXCLUDE]

                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                Snackbar.make(findViewById(android.R.id.content), "Invalid verification code",
                                        Snackbar.LENGTH_SHORT).show();
                                // [END_EXCLUDE]
                            }
                        }
                    }
                });
    }

    public void SendUserToMainActivity(){
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        //can be deleted depend on main page
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    public void hideTitleBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    private void startTime() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText(getResources().getString(R.string.login_resend)+" ("+ millisUntilFinished / 1000 + ")");
                resendBtn.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resendBtn.setEnabled(true);
                resendBtn.setText(getResources().getString(R.string.login_resend));
            }
        }.start();
    }
}
