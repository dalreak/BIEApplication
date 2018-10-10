package com.projectbie.toojs.bieapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class LoginActivity extends AppCompatActivity {

    private LoginThread loginThread;
    private TextView loginTv;
    private TextView passwordTv;
    private Button btn_login;
    private Button btn_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        CertManager cm = new CertManager();
        try{
            cm.InitCert();
        }
        catch (CertificateException|IOException|KeyStoreException|NoSuchAlgorithmException|KeyManagementException e){
            Log.e("BIE", "Exception occurred.");
        }

        loginThread = new LoginThread(this);

        loginTv = findViewById(R.id.text_name);
        passwordTv = findViewById(R.id.text_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        btn_login.setOnClickListener(loginOnClickListener);

        btn_register.setOnClickListener(registerOnClickListener);

    }

    private void ShowAlert(String title, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        // Alert dialog button
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Alert dialog action goes here
                        // onClick button code here
                        dialog.dismiss();// use dismiss to cancel alert dialog
                    }
                });
        alertDialog.show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null) return;
        if (activity.getCurrentFocus() == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        activity.getCurrentFocus().clearFocus();
    }

    Button.OnClickListener loginOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String name = loginTv.getText().toString();
            String password = passwordTv.getText().toString();
            if(name.length() == 0)
                ShowAlert(getString(R.string.login_error_title), getString(R.string.login_error_idblank));
            else if(password.length() == 0)
                ShowAlert(getString(R.string.login_error_title), getString(R.string.login_error_pwblank));
            else{
                int result = 0;
                try{
                    hideSoftKeyboard(LoginActivity.this);
                    result = loginThread.RequestLogin(loginTv.getText().toString(), passwordTv.getText().toString());
                }
                catch(Exception e){

                }
                finally {
                    if(result == 0){

                    }
                    else if(result == 1){
                        Toast.makeText(LoginActivity.this, R.string.login_error_failedtoconnect, Toast.LENGTH_SHORT).show();
                    }
                    else if(result == 2){
                        Toast.makeText(LoginActivity.this, R.string.login_error_noid, Toast.LENGTH_SHORT).show();
                    }
                    else if(result == 3){
                        Toast.makeText(LoginActivity.this, R.string.login_error_idpwmismatch, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    Button.OnClickListener registerOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ShowAlert("Action not implemented", "Still working on it");
        }
    };
}
