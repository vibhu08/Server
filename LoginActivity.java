package com.example.hp.server;

        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
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

        import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText et1,et2;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1= (EditText) findViewById(R.id.editText);
        et2= (EditText) findViewById(R.id.editText2);
    }

    public void mLogin(View v)
    {
        uid=et1.getText().toString().trim();
        String p=et2.getText().toString().trim();

        HashMap<String,String> hm=new HashMap<>();
        hm.put("email",uid);
        hm.put("pass",p);
        StringBuffer sb=RegisterActivity.getEncodedParams(hm);
        String url="http://192.168.43.96/empPhp/loginGET.php"+"?"+sb;
        if (isOnLine())
        {
            MyServerTask task=new MyServerTask();
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

    class MyServerTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params) {
            String s=RegisterActivity.mSendData(params[0]);
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            // Toast.makeText(LoginActivity.this, ""+s, Toast.LENGTH_SHORT).show();
            String r=null;
            try {
                JSONArray aa=new JSONArray(s);
                JSONObject obj=aa.getJSONObject(0);
                r=obj.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (r.trim().equals("success"))
            {
                Intent i=new Intent(LoginActivity.this,ProfileActivity.class);
                i.putExtra("k",uid);
                LoginActivity.this.startActivity(i);
                finish();
            }
            else {

                Toast.makeText(LoginActivity.this, "Invalid UserId or Password", Toast.LENGTH_SHORT).show();
            }
            Log.d("SERVER",s);
        }
    }


    public void mNewUser(View v)
    {
        Intent i=new Intent(this,RegisterActivity.class);
        startActivity(i);
    }
}
