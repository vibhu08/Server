package com.example.hp.server;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView tv1,tv2;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tv1= (TextView) findViewById(R.id.textView5);
        tv2= (TextView) findViewById(R.id.textView3);
        Intent i=getIntent();
        user=i.getStringExtra("k");
        tv1.setText("Welcome :"+user);
    }

    public void mShowInfo(View v)
    {
        HashMap<String,String> hm=new HashMap<>();
        hm.put("email",user);
        StringBuffer sb=RegisterActivity.getEncodedParams(hm);
        String url="http://192.168.43.96/empPhp/readGET.php"+"?"+sb;
        if (isOnLine())
        {
            MyReadTask task=new MyReadTask();
            task.execute(url);
            Toast.makeText(this, "Task has been started", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    class MyReadTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params) {
            String data=mReadData(params[0]);
            Log.d("SERVER",data);
            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONArray aa=new JSONArray(s);
                JSONObject obj=aa.getJSONObject(0);
                String n=obj.getString("name");
                String e=obj.getString("email");
                String p=obj.getString("pass");
                String c=obj.getString("phone");
                String d=obj.getString("dob");

                tv2.setText("Student name-"+n+"\n"
                        +"Student Email-"+e+"\n"
                        +"Student password-"+p+"\n"
                        +"Student Phone-"+c+"\n"
                        +"Student DOB-"+d);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    String mReadData(String u)
    {

        HttpURLConnection con=null;
        InputStream is=null;
        StringBuffer b=new StringBuffer();
        try {
            URL url=new URL(u);
            Log.d("SERVER","REad done-1");
            con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(2000);
            int resCode=con.getResponseCode();
            if (resCode==HttpURLConnection.HTTP_OK)
            {
                Log.d("SERVER","Read done-2");
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
    public void mLogout(View v)
    {
        Intent i=new Intent(this,LoginActivity.class);
        startActivity(i);
        finish();
    }
    public void mDeleteAccount(View v)
    {
    }
}
