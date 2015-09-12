package com.smartlockinc.smartlocks;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.picasso.Picasso;

import android.content.BroadcastReceiver;
import android.util.Log;
import static com.smartlockinc.smartlocks.CommonUtilities.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    TextView gcmmessgae;
    AsyncTask<Void,Void,Void>mRegisterTask;
    AlertDialogueManager alert= new AlertDialogueManager();
    ConnectionDetector cd;
    SessionManager session;
    Gcmsessionmanager gcmsessionmanager;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    GoogleCloudMessaging gcm;
    photourl uri;
    TextView usrname;
    TextView name;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        img = (ImageView)findViewById(R.id.imgAvatar);
        usrname  = (TextView)findViewById(R.id.txtUserEmail);
        name  = (TextView)findViewById(R.id.txtUsername);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        gcm = GoogleCloudMessaging.getInstance(this);

        cd = new ConnectionDetector(getApplicationContext());
        if (!cd.isConnectingtoInternet()) {
            alert.ShowALert(MainActivity.this, "No Internet Conection", "Connect and login again", false);

        }
        else {
            session = new SessionManager(MainActivity.this);
            if (session.checklogin() == false) {
                 Intent intent = new Intent(MainActivity.this, Signupstartup.class);
                startActivity(intent);
                finish();
                new RegisterBackground().execute();


            } else {
                Toast.makeText(MainActivity.this, session.Email(), Toast.LENGTH_LONG).show();
                gcmsessionmanager = new Gcmsessionmanager(MainActivity.this);
                Toast.makeText(MainActivity.this, gcmsessionmanager.id() , Toast.LENGTH_LONG).show();

            }

        }
        session = new SessionManager(MainActivity.this);
        uri = new photourl(MainActivity.this);
        Picasso.with(this)
                .load(uri.geturi())
                .into(img);
        name.setText(session.Email());
        usrname.setText(session.Password());
            }
    class RegisterBackground extends AsyncTask<String,String,String>

    {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                }

                String regid = gcm.register(SENDER_ID);
                msg = regid;
                Log.d("111", msg);


            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
            gcmsessionmanager = new Gcmsessionmanager(MainActivity.this);
            gcmsessionmanager.regidregis(msg);

        }
    }





    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override

        public void onNavigationDrawerItemSelected(int position) {
            // update the main content by replacing fragments
            switch (position) {
                case 0:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                    break;

                case 1:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new LockLogsFragment()).commit();
                    break;
                case 2:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new SharedKeyFragment()).commit();
                    break;
                case 3:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new Signout()).commit();
                    break;
                /*case 4:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new Register()).commit();
                    break;*/

            }

        }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    public void unlockbuttonOnClick(View v1) {
        session = new SessionManager(MainActivity.this);
        if (session.checklogin() == true) {
            String keyword = "unlock";
            postmethod(this, keyword);
        }
        else
        {
            alert.ShowALert(MainActivity.this,"Can not unlock","Login with a valid account",false);
        }

    }

    public void lockbuttonOnClick(View v1) {
        session = new SessionManager(MainActivity.this);
        if (session.checklogin() == true) {
            String keyword = "Lock";
            postmethod(this, keyword);
        }
        else
        {
            alert.ShowALert(MainActivity.this,"Can not lock","Login with a valid account",false);
        }

    }

    public  void postmethod(final Context context, final String keyword) {

            RequestQueue rq = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("keyword",keyword);
            }catch(JSONException e)
            {
                e.printStackTrace();
            }

            JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, "http://120.56.230.123:9000/smartlock/action", jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {
                        String data = response.getString("success");
                        Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error [" + error + "]");

                }
            }); /*{
                @Override

                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("keyword",keyword);
                    return params;
                }


            };*/

            rq.add(postReq);



    }



}

