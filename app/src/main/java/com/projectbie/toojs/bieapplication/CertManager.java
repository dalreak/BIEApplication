package com.projectbie.toojs.bieapplication;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class CertManager {

    private String certString = "-----BEGIN CERTIFICATE-----\n" +
            "MIICWzCCAcSgAwIBAgIJAJAQbHAUyRvqMA0GCSqGSIb3DQEBCwUAMEUxCzAJBgNV\n" +
            "BAYTAktSMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBX\n" +
            "aWRnaXRzIFB0eSBMdGQwHhcNMTgxMDA4MDk0MzM5WhcNMTgxMTA3MDk0MzM5WjBF\n" +
            "MQswCQYDVQQGEwJLUjETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50\n" +
            "ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKB\n" +
            "gQC9uBKqulEcAfqzE3AOmjmTsrfiZglmgJZLM731StXGOfe4Isq7f0AOtnGTFu5S\n" +
            "6HRBsu29lET9eeAkCy1xa/CqOqftMvorL1N7ywSbtbZcLaoTmGt6OKrHOopEdUv/\n" +
            "4ItGEPXEXVuhOUiRq7K7PEDH230XJRP43/GxZbeRLVQlDQIDAQABo1MwUTAdBgNV\n" +
            "HQ4EFgQUSf6wSIMO3Lv/WN6UC2BtL4by8RcwHwYDVR0jBBgwFoAUSf6wSIMO3Lv/\n" +
            "WN6UC2BtL4by8RcwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOBgQCk\n" +
            "eyqVLuhnkpLEqu4ryUmUefTYxSTanABncQfo4U85d61aT9hE8tmVlDEpvT5YttAt\n" +
            "VVz4/cypwbBXPt7IM+UWBQP+SRJ1oWWIzAxkJR8TNbpMSmvzg/XeyKwgnMewL00E\n" +
            "M4ER6EdT+VCVeOg+yD+wyPuY8KyzEGMYFKObfS3UdA==\n" +
            "-----END CERTIFICATE-----\n";

    private static SSLContext context;

    public void InitCert() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // Load CAs from an InputStream
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = new ByteArrayInputStream(certString.getBytes());
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
    }

    public static SSLContext getSSLContext(){
        if(context != null)
            return context;
        return null;
    }
}