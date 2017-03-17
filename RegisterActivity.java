package com.example.hp.server;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity {

    EditText et1,et2,et3,et4,et5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et1= (EditText) findViewById(R.id.editText3);
        et2= (EditText) findViewById(R.id.editText4);
        et3= (EditText) findViewById(R.id.editText5);
        et4= (EditText) findViewById(R.id.editText6);
        et5= (EditText) findViewById(R.id.editText7);
    }

    public void mRegister(View v)
    {
        String n=et1.getText().toString().trim();
        String e=et2.getText().toString().trim();
        String p=et3.getText().toString().trim();
        String c=et4.getText().toString().trim();
        String d=et5.getText().toString().trim();
        HashMap<String,String> hm=new HashMap<>();
        hm.put("name",n);
        hm.put("email",e);
        hm.put("pass",p);
        hm.put("phone",c);
        hm.put("dob",d);
        StringBuffer sb=getEncodedParams(hm);
        Log.d("SERVER",sb.toString());
        String url="http://192.168.43.96/empPhp/insertGET-db.php"+"?"+sb;

        if (isOnLine()) {
            Mytask task = new Mytask();
            task.execute(url);
            Toast.makeText(this, "Task has been started", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    boolean isOnLine(){
        ConnectivityManager cm= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=cm.getActiveNetworkInfo();
        if (info!=null)
        {
            if (info.isConnectedOrConnecting())
                return  true;
            else
                return false;
        }
        else
        {
            return false;
        }
    }

    class Mytask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params) {
            String d=mSendData(params[0]);
            return d;
        }

        @Override
        protected void onPostExecute(String s) {

            String r=null;
            try {
                JSONArray arr=new JSONArray(s);
                r=arr.getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (r.trim().equals("successfull"))
            {
                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
                Toast.makeText(RegisterActivity.this, "Registration successful, Now You can Login", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(RegisterActivity.this, "Something Went Wrong Please try again", Toast.LENGTH_SHORT).show();
            }

        }
    }


    static String mSendData(String u)
    {
        HttpURLConnection con=null;
        InputStream is=null;
        StringBuffer b=new StringBuffer();
        try {
            URL url=new URL(u);
            Log.d("SERVER","done-1");
            con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(2000);
            int resCode=con.getResponseCode();
            if (resCode==HttpURLConnection.HTTP_OK)
            {
                Log.d("SERVER","done-2");
                is=con.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader reader=new BufferedReader(isr);
                String line=null;
                while ((line=reader.readLine())!=null)
                {
                    b.append(line);
                }
                Log.d("SERVER",b.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SERVER","Exception-"+e.toString());
        }
        return b.toString();
    }

    static StringBuffer getEncodedParams(HashMap<String,String> hm)
    {
        StringBuffer sb=new StringBuffer();
        Set<String> keys=hm.keySet();
        String value=null;
        for (String k:keys)
        {
            String v=hm.get(k);
            try {
                value=URLEncoder.encode(v,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (sb.length()>0)
            {
                sb.append("&");
            }
            sb.append(k+"="+value);
        }
        return sb;
    }
}
