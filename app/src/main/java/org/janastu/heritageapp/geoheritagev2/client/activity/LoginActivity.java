package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.janastu.heritageapp.geoheritagev2.client.LoginResponse;

import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.RestServerComunication;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageAppDTO;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageCategory;
import org.janastu.heritageapp.geoheritagev2.client.pojo.HeritageRegionNameDTO;
import org.janastu.heritageapp.geoheritagev2.client.pojo.Token;
import org.janastu.heritageapp.geoheritagev2.client.pojo.User;
import org.janastu.heritageapp.geoheritagev2.client.pojo.UserDetails;
import org.janastu.heritageapp.geoheritagev2.client.services.TokenProvider;
import org.janastu.heritageapp.geoheritagev2.client.services.TokenValidator;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    String LOGGER ="LoginActivity" ;
    EditText usernameTxtView, passwordTxtView;
    String username, password;
    TextView error;
    Button loginButton;

    Button registerButton;
    //User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        setContentView(R.layout.activity_design_login_screen);

        usernameTxtView = (EditText) findViewById(R.id.userName);
        passwordTxtView = (EditText) findViewById(R.id.password);



        loginButton = (Button) findViewById(R.id.btnlogin);
        registerButton = (Button) findViewById(R.id.btn_Register);
     //   error = (TextView) findViewById(R.id.error);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);


        SharedPreferences settings = getSharedPreferences(MaterialMainActivity.PREFS_NAME, 0);

        String userName = settings.getString(MaterialMainActivity.PREFS_USERNAME , "");
        String password = settings.getString(MaterialMainActivity.PREFS_PASSWORD , "");
        String currentToken = settings.getString(MaterialMainActivity.PREFS_ACCESS_TOKEN , "");

        Log.d(TAG, "SharedPreferences userName"+userName);
        Log.d(TAG, "SharedPreferences password"+password);
        Log.d(TAG, "SharedPreferences currentToken"+currentToken);

        if(userName != null || password != null) {
            usernameTxtView.setText(userName);
            passwordTxtView.setText(password);
        }

        if(currentToken != null && TokenValidator.checkAuthenticationStatus(currentToken))
        {
            ///valid token

            Intent i = new Intent(getApplicationContext(), MaterialMainActivity.class);
            startActivity(i);

            //send username
            //password
            startActivity(i);
        }
        else
        {
            //pls login
        }
        //get the token ;

        //generate the token;

        /*
        Save the token in shared preferences

        In case the token is found check if the username is matching -
        if username is found check if the token has not expired ;
        */

        TokenProvider t = new TokenProvider();
        UserDetails userDetails = new UserDetails(userName,password );
        Token generateToken = t.createToken(userDetails);

        Log.d(TAG, "generated token"+generateToken.getToken());
        Log.d(TAG, "stored token"+currentToken);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
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
    public void onClick(View v) {
        Log.d(TAG, "btn clicked");
        switch (v.getId()) {
            case R.id.btnlogin:
                username = usernameTxtView.getText().toString();
                password = passwordTxtView.getText().toString();
                Log.d(TAG, "logging with "+"username"+username + "password"+password);
                //Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                new LoginTask().execute(username, password);

                //i.putExtra("userName",username.getText().toString());
                //startActivity(i);
                //finish();
                break;

            case R.id.btn_Register:
                Log.d(TAG, "Register clicked");
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);


                break;
        }
    }


    ///LoginTask

    private class LoginTask extends AsyncTask<String, Void, LoginResponse> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage(getResources()
                    .getString(R.string.login_progress_signing_in));
            mProgressDialog.show();
        }

        @Override
        protected LoginResponse doInBackground(String... params) {
            User registered = null;
            LoginResponse r = null;
            try {
                r = RestServerComunication.authenticate(username,password);
            } catch (Exception e) {
                Log.e(TAG, "Error login in: " + e.getMessage());
            }
            return r;
        }
        public   String getFormattedDate() {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat(); //called without pattern
            return df.format(c.getTime());
        }

        @Override
        protected void onPostExecute(LoginResponse result) {
            mProgressDialog.dismiss();
            if (result != null) {

                boolean failres = result.getToken().startsWith("LOGFAIL");
                if(failres == false) {
                    Toast.makeText(getBaseContext(),
                            R.string.success_login, Toast.LENGTH_LONG).show();

                    //Next time do not login - check is the token is old - then ask him to login - else directly take him to the main activity -

                     Date zdt;

                    SharedPreferences settings = getSharedPreferences(MaterialMainActivity.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(MaterialMainActivity.PREFS_USERNAME, username);
                    editor.putString(MaterialMainActivity.PREFS_ACCESS_TOKEN, result.getToken());
                    editor.putString(MaterialMainActivity.PREFS_PASSWORD, password);
                    editor.putString(MaterialMainActivity.LOGIN_DATE, getFormattedDate());
                    editor.commit();

                    //if settings returns a exception - go to settings exception else

                    HeritageAppDTO d = null;
                    // Spinner Drop down elements
                    try {
                        d = SettingsActivity.getCurrentAppInfo();
                        Set<HeritageCategory> categSet  = d.getCategorys();
                        List<String> heritageCategoriesStringList = new ArrayList<String>();
                        for(HeritageCategory c:categSet)
                        {
                            heritageCategoriesStringList.add(c.getCategoryName());

                        }
                        Intent i = new Intent(getApplicationContext(), MaterialMainActivity.class);
                        startActivity(i);

                    }
                    catch(Exception e)
                    {
                        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(i);
                    }



                    //finish();
                }

                else {
                    Toast.makeText(getBaseContext(), "Login Failure" + result.getToken(), Toast.LENGTH_LONG).show();
                }




            }
        }
    }


}
