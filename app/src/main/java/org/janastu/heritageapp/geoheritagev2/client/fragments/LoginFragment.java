package org.janastu.heritageapp.geoheritagev2.client.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.PlusOneButton;

import org.janastu.heritageapp.geoheritagev2.client.LoginResponse;
import org.janastu.heritageapp.geoheritagev2.client.MaterialMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.R;
import org.janastu.heritageapp.geoheritagev2.client.SimpleMainActivity;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.LoginService;
import org.janastu.heritageapp.geoheritagev2.client.fragments.services.LoginServiceImpl;
import org.janastu.heritageapp.geoheritagev2.client.pojo.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PlusOneButton mPlusOneButton;
    private static final String TAG = "LoginFragment";
    String LOGGER ="LoginFragment" ;
    EditText usernameTxtView, passwordTxtView;
    String username, password;
    TextView error;
    Button loginButton;

    Button registerButton;
    LoginService loginService;

    private OnLoginFragmentInteractionListener mListener;
    Context             context;

    public LoginFragment() {
        // Required empty public constructor

        loginService = new LoginServiceImpl();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //Find the +1 button
        mPlusOneButton = (PlusOneButton) view.findViewById(R.id.plus_one_button);


        usernameTxtView = (EditText) view.findViewById(R.id.userName);
        passwordTxtView = (EditText) view.findViewById(R.id.password);



        loginButton = (Button) view.findViewById(R.id.btnlogin);
        registerButton = (Button) view.findViewById(R.id.btn_Register);
        //   error = (TextView) findViewById(R.id.error);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        return view;
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


        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
//        mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLoginFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {


        super.onAttach(context);

        this.context = context;
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        this.context = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLoginFragmentInteraction(Uri uri);
        public void onLoginSuccess(String username, String password, LoginResponse r, String d);
        public void onLoginFailure(LoginResponse result);
        public void onLoginDateStillValid();
    }

    ////Login Task

    private class LoginTask extends AsyncTask<String, Void, LoginResponse> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getResources()
                    .getString(R.string.login_progress_signing_in));
            mProgressDialog.show();
        }

        @Override
        protected LoginResponse doInBackground(String... params) {
            User registered = null;
            LoginResponse r = null;
            try {
                r =  loginService.login(username,password);
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
                    Toast.makeText(context,
                            R.string.success_login, Toast.LENGTH_LONG).show();
                    mListener.onLoginSuccess(username, password,result,getFormattedDate());
                }
                else {
                    mListener.onLoginFailure(result);
                }
            }
        }
    }


}
