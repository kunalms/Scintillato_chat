package com.scintillato.scintillatochat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

public class VerifyOTP extends AppCompatActivity implements VerificationListener {

    Verification mVerification;
    String mobileno,country_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_otp);

        Bundle b=getIntent().getExtras();
        mobileno=b.getString("number");
        //country_code=b.getString("countrycode");
        country_code="91";
        mVerification = SendOtpVerification.createSmsVerification(this, mobileno, this, country_code);
        mVerification.initiate();
    }

    @Override
    public void onInitiated(String response) {
        Toast.makeText(getApplicationContext(),"Sending otp to"+mobileno,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitiationFailed(Exception paramException) {
        Toast.makeText(getApplicationContext(),"Sending otp to"+mobileno+"failed",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onVerified(String response) {
            Toast.makeText(getApplicationContext(),"Verified Successful"+mobileno,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onVerificationFailed(Exception paramException) {
        Toast.makeText(getApplicationContext(),"Verified Failed!"+mobileno,Toast.LENGTH_LONG).show();
    }
}
