package org.janastu.heritageapp.geoheritagev2.client.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.RestServerComunication;
import org.janastu.heritageapp.geoheritagev2.client.pojo.MResponseToken;
import org.janastu.heritageapp.geoheritagev2.client.pojo.User;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener{


    private static final String TAG = "RegisterActivity" ;
    String username,password, passwordagain,emailId;
    Integer residentstatus, agestatus;
    String  specialmessage;
    RadioGroup rgResidentStatus;
    RadioGroup rgAge;
    TextView error;
    Button registerBtn;


    EditText usernameTxtView, passwordTxtView,passwordagainTxtView, emailTextView,specialmessageTextView;
    private String resStatus;
    private String age;


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_register);
        setContentView(R.layout.activity_design_registration_screen);

        usernameTxtView = (EditText) findViewById(R.id.username);
        passwordTxtView = (EditText) findViewById(R.id.password); //password
        passwordagainTxtView = (EditText) findViewById(R.id.passwordagain); //passwordagain
        emailTextView = (EditText) findViewById(R.id.email); //email
        specialmessageTextView = (EditText) findViewById(R.id.specialmessage); //email
        registerBtn = (Button) findViewById(R.id.register_button);
        rgResidentStatus = (RadioGroup) findViewById(R.id.rgResidentStatus);
        rgAge = (RadioGroup) findViewById(R.id.rgAge);
        registerBtn.setOnClickListener(this);
//Username should always be small letters - no caps allowed
        usernameTxtView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toLowerCase())) {
                    s = s.toLowerCase();
                    usernameTxtView.setText(s);
                }
                usernameTxtView.setSelection(usernameTxtView.getText().length());
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so


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

        switch (v.getId()) {
            case R.id.register_button:
                if(usernameTxtView.getText()==null || usernameTxtView.getText().toString().isEmpty())
                {
                    Toast.makeText(getBaseContext(),
                            "UserName is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }

                if(emailTextView.getText()==null || emailTextView.getText().toString().isEmpty())
                {
                    Toast.makeText(getBaseContext(),
                            "Email Text  is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }
                if(passwordTxtView.getText()==null || passwordTxtView.getText().toString().isEmpty())
                {
                    Toast.makeText(getBaseContext(),
                            "Password Text  is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }

                if(passwordagainTxtView.getText()==null || passwordagainTxtView.getText().toString().isEmpty())
                {
                    Toast.makeText(getBaseContext(),
                            "Passwordagain  Text  is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }
                if(passwordagainTxtView.getText()==null || passwordagainTxtView.getText().toString().isEmpty())
                {
                    Toast.makeText(getBaseContext(),
                            "Passwordagain  Text  is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }

                if(!isValidEmail(emailTextView.getText().toString()))
                {
                    Toast.makeText(getBaseContext(),
                            "Email is Invalid", Toast.LENGTH_LONG).show();
                    return ;
                }

                username = usernameTxtView.getText().toString();
                password = passwordTxtView.getText().toString();
                passwordagain = passwordagainTxtView.getText().toString();
                if(passwordagain.compareTo(password) != 0)
                {
                    Toast.makeText(getBaseContext(),
                            "Password Mismatch", Toast.LENGTH_LONG).show();
                    return ;
                }
                //check if the
                emailId = emailTextView.getText().toString();
                emailId = emailId.trim();
                specialmessage = specialmessageTextView.getText().toString();


                RadioButton selectRadio2 = (RadioButton) findViewById(rgResidentStatus
                        .getCheckedRadioButtonId());

                RadioButton selectRadio = (RadioButton) findViewById(rgAge
                        .getCheckedRadioButtonId());
                if(selectRadio2 == null ||selectRadio == null )
                {

                    Toast.makeText(getBaseContext(),
                            "Age or Resident Status  Text  is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }

                {
                    if(selectRadio2.getText().toString()==null ||selectRadio2.getText().toString().toString().isEmpty())
                    {
                        Toast.makeText(getBaseContext(),
                                "Resident Status  Text  is Empty", Toast.LENGTH_LONG).show();
                        return ;
                    }

                    if(selectRadio.getText().toString()==null ||selectRadio.getText().toString().toString().isEmpty())
                    {
                        Toast.makeText(getBaseContext(),
                                "Age  Text  is Empty", Toast.LENGTH_LONG).show();
                        return ;
                    }
                }
                 resStatus = selectRadio2.getText().toString();

                 Log.d(TAG, "resStatus string "+resStatus);


                 age = selectRadio.getText().toString();



                Log.d(TAG, "logging with "+"username"+username + "password"+password);
                //Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                new RegisterTask().execute(username, password);

                //i.putExtra("userName",username.getText().toString());
                //startActivity(i);
                //finish();
                break;
        }


    }

    private class RegisterTask extends AsyncTask<String, Void, MResponseToken> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(RegisterActivity.this);
            mProgressDialog.setMessage(getResources()
                    .getString(R.string.login_progress_signing_in));
            mProgressDialog.show();
        }

        @Override
        protected MResponseToken doInBackground(String... params) {
            User registered = null;
            MResponseToken r = null;
            try {
                r = RestServerComunication.registerUser(username,password, emailId, resStatus, age, specialmessage);
               // r = RestServerComunication.registerUser();
            } catch (Exception e) {
                Log.e("RegisterActvituy", "Error login in: " + e.getMessage());
            }
            return r;
        }

        @Override
        protected void onPostExecute(MResponseToken result) {
            mProgressDialog.dismiss();
            if (result != null) {

                boolean res = result.getStatus().startsWith("OK");
                if(res) {
                    Toast.makeText(getBaseContext(),
                            "Registration Successful" + " ID: " + result.getUserId(), Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Registration Failure "+result.getMessage() +"with Status" +" - "+result.getStatus(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
