package com.projectbie.toojs.bieapplication;

import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class NullHostNameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        Log.i("BIE", "Approving certificate for " + hostname);
        return true;
    }

}