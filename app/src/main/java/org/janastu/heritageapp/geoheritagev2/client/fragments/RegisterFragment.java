package org.janastu.heritageapp.geoheritagev2.client.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.janastu.heritageapp.geoheritagev2.client.RestServerComunication;
import org.janastu.heritageapp.geoheritagev2.client.pojo.MResponseToken;
import org.janastu.heritageapp.geoheritagev2.client.pojo.User;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import org.janastu.heritageapp.geoheritagev2.client.R;

import com.google.android.gms.plus.PlusOneButton;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link org.janastu.heritageapp.geoheritagev2.client.fragments.RegisterFragment.OnRegisterFragmentInteractionListener interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    private static final String TAG = "REGISTER";
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
  //  private PlusOneButton mPlusOneButton;

    private OnRegisterFragmentInteractionListener mListener;

    ////GUI

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

    Context ctx ;
    ///GUI

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        usernameTxtView = (EditText)  view.findViewById(R.id.username);
        passwordTxtView = (EditText)  view.findViewById(R.id.password); //password
        passwordagainTxtView = (EditText)  view.findViewById(R.id.passwordagain); //passwordagain
        emailTextView = (EditText)  view.findViewById(R.id.email); //email
        specialmessageTextView = (EditText)  view.findViewById(R.id.specialmessage); //email
        registerBtn = (Button)  view.findViewById(R.id.btn_Register);
        rgResidentStatus = (RadioGroup)  view.findViewById(R.id.rgResidentStatus);
        rgResidentStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.radioResident:
                        resStatus="Resident";
                        Log.d(TAG, "resident");
                        break;
                    case R.id.radioVisitor:
                        resStatus="Visitor";
                        Log.d(TAG, "visitor");
                        break;

                }
            }
        });

        rgAge = (RadioGroup)  view.findViewById(R.id.rgAge);

        rgAge.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.radioButtonChild:
                        age="Child";
                        Log.d(TAG, "Child");
                        break;
                    case R.id.radioButtonYouth:
                        age="Youth";
                        Log.d(TAG, "Youth");
                        break;
                    case R.id.radioButtonAdult:
                        age="Adult";
                        Log.d(TAG, "Adult");
                        break;
                    case R.id.radioButtonElder:
                        age="Elder";
                        Log.d(TAG, "Elder");
                        break;

                }
            }
        });

        ctx = getActivity().getApplicationContext();
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
        //mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRegisterFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegisterFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btn_Register:
                if(usernameTxtView.getText()==null || usernameTxtView.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "UserName is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }

                if(emailTextView.getText()==null || emailTextView.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity() ,
                            "Email Text  is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }
                if(passwordTxtView.getText()==null || passwordTxtView.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity() ,
                            "Password Text  is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }

                if(passwordagainTxtView.getText()==null || passwordagainTxtView.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity() ,
                            "Passwordagain  Text  is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }
                if(passwordagainTxtView.getText()==null || passwordagainTxtView.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity() ,
                            "Passwordagain  Text  is Empty", Toast.LENGTH_LONG).show();
                    return ;
                }

                if(!isValidEmail(emailTextView.getText().toString()))
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Email is Invalid", Toast.LENGTH_LONG).show();
                    return ;
                }

                username = usernameTxtView.getText().toString();
                password = passwordTxtView.getText().toString();
                passwordagain = passwordagainTxtView.getText().toString();
                if(passwordagain.compareTo(password) != 0)
                {
                    Toast.makeText(getActivity(),
                            "Password Mismatch", Toast.LENGTH_LONG).show();
                    return ;
                }
                //check if the
                emailId = emailTextView.getText().toString();
                emailId = emailId.trim();
                specialmessage = specialmessageTextView.getText().toString();


            {
                if(resStatus == null)
                {
                    Toast.makeText(getActivity() ,
                            "Select Resident Status", Toast.LENGTH_LONG).show();
                    return ;
                }
                if(resStatus.isEmpty() )
                {
                    Toast.makeText(getActivity() ,
                            "Select Resident Status", Toast.LENGTH_LONG).show();
                    return ;
                }


                if(age == null)
                {
                    Toast.makeText(getActivity() ,
                            "Select Age Group", Toast.LENGTH_LONG).show();
                    return ;
                }
                if(age.isEmpty() )
                {
                    Toast.makeText(getActivity() ,
                            "Select Age Group", Toast.LENGTH_LONG).show();
                    return ;
                }
            }











            Log.d(TAG, "logging with "+"username"+username + "password"+password);
            //Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
            new RegisterTask().execute(username, password);

            //i.putExtra("userName",username.getText().toString());
            //startActivity(i);
            //finish();
            break;
        }

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
    public interface OnRegisterFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRegisterFragmentInteraction(Uri uri);
    }



    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    /////TASk
    private class RegisterTask extends AsyncTask<String, Void, MResponseToken> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(getActivity());
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
                    Toast.makeText(getActivity().getBaseContext(),
                            "Registration Successful" + " ID: Pls Login " + result.getUserId(), Toast.LENGTH_LONG).show();

                    mListener.onRegisterFragmentInteraction(null);

                }
                else
                {
                    Toast.makeText(getActivity().getBaseContext(), "Registration Failure "+result.getMessage() +"with Status" +" - "+result.getStatus(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    //TASK
}
