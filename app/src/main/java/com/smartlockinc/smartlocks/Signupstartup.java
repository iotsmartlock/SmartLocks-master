package com.smartlockinc.smartlocks;


    import android.app.Activity;
    import android.content.Context;
    import android.content.ContextWrapper;
    import android.content.DialogInterface;

    import com.rey.material.widget.*;

    import android.content.Intent;
    import android.content.IntentSender;
    import android.content.pm.PackageInfo;
    import android.content.pm.PackageManager;
    import android.content.pm.Signature;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.drawable.Drawable;
    import android.os.AsyncTask;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Environment;
    import android.support.design.widget.Snackbar;
    import android.support.design.widget.TextInputLayout;
    import android.support.v4.app.Fragment;

    import android.text.Editable;
    import android.text.TextWatcher;
    import android.util.Base64;
    import android.util.Log;
    import com.rey.material.widget.*;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.View.OnClickListener;
    import android.view.ViewGroup;
    import android.view.Window;
    import android.view.WindowManager;
    import android.view.inputmethod.InputMethodManager;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.RelativeLayout;
    import android.widget.Toast;

    import com.facebook.GraphRequest;
    import com.facebook.GraphResponse;
    import com.facebook.internal.CallbackManagerImpl;
    import com.facebook.login.widget.LoginButton;
    import com.facebook.CallbackManager;
    import com.facebook.FacebookCallback;
    import com.facebook.FacebookException;
    import com.facebook.FacebookSdk;
    import com.facebook.login.LoginResult;
    import com.facebook.login.widget.LoginButton;
    import com.google.android.gms.common.SignInButton;
    import com.google.android.gms.common.ConnectionResult;
    import com.google.android.gms.common.GooglePlayServicesUtil;
    import com.google.android.gms.common.api.GoogleApiClient;
    import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
    import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
    import com.google.android.gms.gcm.GoogleCloudMessaging;
    import com.google.android.gms.plus.Plus;
    import com.google.android.gms.plus.model.people.Person;
    import com.facebook.login.LoginManager;
    import com.google.android.gcm.GCMRegistrar;

    import com.squareup.picasso.Picasso;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.IOException;
    import java.util.Arrays;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.InputStream;
    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    import com.squareup.picasso.Target;

    import static com.smartlockinc.smartlocks.CommonUtilities.*;


    /**
     * Created by SunnySingh on 7/5/2015.
     */
    public class Signupstartup extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        AlertDialogueManager alert = new AlertDialogueManager();
        private RelativeLayout mroot;
        private GoogleApiClient mGoogleApiClient;
        private CallbackManager callbackmanager;
        private LoginButton fbbutton;
        private static final int RC_SIGN_IN = 0;
        private boolean mIntentInProgress;
        private ConnectionResult mConnectionResult;
        private SignInButton signinButton;
        private boolean isValidEmail(String emailInput) {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(emailInput);
            return matcher.matches();
        }

        ConnectionDetector cd;
        SessionManager session;
        EditText password;
        photourl uri;
        EditText email;
        String ValidEmail;
        static String Emailid;
        static String Password;
        Context mcontext;
        com.rey.material.widget.Button register;
        private static int RC_FB_SIGN_IN;




        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(Build.VERSION.SDK_INT>=21)
            {
                Window window =getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
            }
            FacebookSdk.sdkInitialize(getApplicationContext());
            setContentView(R.layout.signup);

            mroot=(RelativeLayout) findViewById(R.id.signup);
            final TextInputLayout et1= (TextInputLayout) findViewById(R.id.et1);
            final TextInputLayout et2 = (TextInputLayout) findViewById(R.id.et2);

            et1.setHint("Username");

            et2.setHint("Password");

            mcontext = getApplicationContext();

            fbbutton = (LoginButton) findViewById(R.id.fbbutton);
            fbbutton.setOnClickListener(this);
            RC_FB_SIGN_IN = fbbutton.getRequestCode();
            signinButton = (SignInButton) findViewById(R.id.btn_sign_in);
            signinButton.setOnClickListener(this);

            cd = new ConnectionDetector(getApplicationContext());

            if (!cd.isConnectingtoInternet()) {
                alert.ShowALert(getApplicationContext(), "Internet Connection error", "Connect to a network", false);


            }


            if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                    || SENDER_ID.length() == 0) {
                // GCM sernder id / server url is missing
                alert.ShowALert(getApplicationContext(), "Configuration Error!",
                        "Please set your Server URL and GCM Sender ID", false);
                // stop executing code by return

            }

            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();


            email = (EditText)findViewById(R.id.Email);
            ValidEmail=email.getText().toString().trim();
            password =(EditText) findViewById(R.id.Password);
            register = (com.rey.material.widget.Button) findViewById(R.id.btnRegister);

            register.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Password = password.getText().toString();
                    Emailid = email.getText().toString();
                    ValidEmail=email.getText().toString().trim();
                    InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

                    if (Password.equals("") && Emailid.equals("")) {
                        final Snackbar snackbar = Snackbar.make(mroot, "One or More Fields are Empty", Snackbar.LENGTH_SHORT);
                        snackbar.setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }
                    else if (Password.equals("") && !Emailid.equals(""))
                    {
                        et2.setError("Password Cannot be Empty");
                        et1.setError(null);

                    }

                    else if (!Password.equals("") && Emailid.equals(""))
                    {
                        et1.setError("Email Cannot be Empty");
                        et2.setError(null);

                    }
                    else {
                        if(!isValidEmail(ValidEmail))
                        {
                            final Snackbar msnackbar = Snackbar.make(mroot, "Invalid Email Address", Snackbar.LENGTH_SHORT);
                            msnackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    msnackbar.dismiss();
                                }
                            });
                            msnackbar.show();
                        }
                        else {

                            session = new SessionManager(mcontext);
                            session.atlogin(Emailid, Password);
                            Intent i = new Intent(mcontext, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }


            });

        }



        @Override
        protected void onActivityResult(int reqCode, int resCode, Intent i) {

                   if(reqCode==RC_SIGN_IN) {
                       if (resCode == Activity.RESULT_OK) {


                       }
                       mIntentInProgress = false;
                       if (!mGoogleApiClient.isConnecting()) {
                           mGoogleApiClient.connect();
                       }
                   }

                   if(reqCode== RC_FB_SIGN_IN) {
                       callbackmanager.onActivityResult(reqCode, resCode, i);
                   }


            }





        protected void onStart() {
            super.onStart();
            mGoogleApiClient.connect();
        }

        protected void onStop() {
            super.onStop();
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
        private void resolveSignInError() {
            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult(this,RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            if (!result.hasResolution()) {
                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
                return;
            }

            if (!mIntentInProgress) {
                // store mConnectionResult
                mConnectionResult = result;


            }
        }


        @Override
        public void onConnected(Bundle arg0) {
            session=new SessionManager(mcontext);
            if(session.checklogin()==false) {
                Toast.makeText(mcontext, "Connected", Toast.LENGTH_LONG).show();
                getProfileInformation();
            }
        }

        private void getProfileInformation() {
            try {
                if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                    Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    Password = currentPerson.getDisplayName();
                    String personPhotoUrl = currentPerson.getImage().getUrl();
                    uri = new photourl(this);
                    session = new SessionManager(mcontext);
                    Emailid = currentPerson.getUrl();
                    session.atlogin(Emailid, Password);
                    uri.photourl(personPhotoUrl);
                    Intent i = new Intent(mcontext, MainActivity.class);
                    startActivity(i);
                    finish();



                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onConnectionSuspended(int cause) {
            mGoogleApiClient.connect();

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_sign_in:
                    googlePlusLogin();
                    break;
                case R.id.fbbutton:
                    onfblogin();
                    break;

            }

        }


        private void googlePlusLogin() {
            if (!mGoogleApiClient.isConnecting()) {
                resolveSignInError();
            }
        }

        private void googlePlusLogout() {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
                session.atlogout();

            }

        }
        private void onfblogin()
        {
            callbackmanager = CallbackManager.Factory.create();

            LoginManager.getInstance().registerCallback(callbackmanager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {

                            System.out.println("Success");

                            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                                    try {
                                        //String email = graphResponse.getJSONObject().getString("email");
                                        JSONObject data = graphResponse.getJSONObject();
                                        String name = graphResponse.getJSONObject().getString("first_name");
                                        String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                        uri = new photourl(mcontext);
                                        session = new SessionManager(mcontext);
                                        session.atlogin(name,name);
                                        uri.photourl(profilePicUrl);
                                        Intent fbLogged = new Intent();
                                        fbLogged.setClass(mcontext, MainActivity.class);
                                        startActivity(fbLogged);
                                        finish();

                                    } catch (org.json.JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,first_name,picture.type(large)");
                            request.setParameters(parameters);
                            request.executeAsync();

                        }

                        @Override
                        public void onCancel() {
                            Log.d(TAG,"On cancel");
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d(TAG,error.toString());

                        }
                    });
        }

    }

