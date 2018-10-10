package com.projectbie.toojs.bieapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class LoginThread {

    private Context context;

    public LoginThread(Context c){
        context = c;
    }

    public int RequestLogin(String name, String password){
        int result;
        try{
            result = new LoginAsyncTask(context).execute(name, password).get();
        }
        catch(ExecutionException|InterruptedException e){
            return 1;
        }
        return result;
    }
}

/*
    반환코드
    0 : 성공
    1 : 서버 연결 실패
    2 : 아이디가 존재하지 않음
    3 : 아이디/비번 불일치
 */
class LoginAsyncTask extends AsyncTask<String, Integer, Integer> {

    private String loginURL = "https://binglebingle.tk/?c=requestLogin";
    private WeakReference<ViewGroup> vg;
    private WeakReference<View> modalView;
    private WeakReference<TextView> modalTextView;
    private WeakReference<Context> context;

    public LoginAsyncTask(Context c){
        context = new WeakReference<>(c);
        vg = new WeakReference<>((ViewGroup)((Activity)c).findViewById(R.id.login_layoutroot).getParent());
        modalView = new WeakReference<>(((Activity) c).getLayoutInflater().inflate(R.layout.modal_progress, vg.get(), false));
        vg.get().addView(modalView.get());
    }

    @Override
    protected void onPreExecute() {
        modalTextView = new WeakReference<>((TextView)modalView.get().findViewById(R.id.textview_modal));
        modalTextView.get().setText(R.string.login_loggingin);
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        HttpsURLConnection connection = null;
        int resCode = 1;
        try{
            URL url = new URL(loginURL);
            connection = (HttpsURLConnection)url.openConnection();
            connection.setSSLSocketFactory(CertManager.getSSLContext().getSocketFactory());
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setRequestProperty("Content-Type","application/json");
            String body = "{\"name\": \"" + strings[0] + "\",\"password\":\"" + strings[1] + "\"}";
            byte[] outputInBytes = body.getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write(outputInBytes);
            os.close();
            connection.connect();

            String response;
            InputStream is = null;
            ByteArrayOutputStream baos = null;
            int responseCode = connection.getResponseCode();

            if(responseCode == HttpsURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();

                response = new String(byteData);

                JSONObject responseJSON = new JSONObject(response);
                boolean success = (Boolean) responseJSON.get("success");
                resCode = (Integer) responseJSON.get("res");
            }
        }
        catch(IOException|JSONException e){
            Log.d("BIE", e.toString());
            return 1;
        }
        finally{
            if(connection != null)
                connection.disconnect();
            ((Activity)context.get()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    vg.get().removeView(modalView.get());
                }
            });
        }
        return resCode;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }
}
