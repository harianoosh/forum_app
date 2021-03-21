package com.ahari.midterm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
    MidTerm
    Full Name of Student: Anoosh Hari
 */

public class LoginFragment extends Fragment {

    public LoginFragment() {

    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText email;
    EditText password;
    Button login;
    TextView createNewAccount;
    DataServices.AuthResponse response;
    ILoginListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        email = view.findViewById(R.id.loginFragmentEmail);
        password = view.findViewById(R.id.loginFragmentPassword);
        login = view.findViewById(R.id.loginFragmentLogin);
        createNewAccount = view.findViewById(R.id.loginFragmentCreateNewAccount);

        getActivity().setTitle(getString(R.string.login_title));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String[] details = new String[2];
                    details[0] = email.getText().toString();
                    details[1] = password.getText().toString();
                    if (details[0].isEmpty()) {
                        throw new Exception(getString(R.string.email_error));
                    }

                    if (details[1].isEmpty()) {
                        throw new Exception(getString(R.string.password_error));
                    }
                    new LoginAsyncTask().execute(details);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.launchCreateNewAccountFragment();
            }
        });
        return view;
    }

    class LoginAsyncTask extends AsyncTask<String[], String, DataServices.AuthResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            login.setEnabled(false);
            createNewAccount.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getActivity(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected DataServices.AuthResponse doInBackground(String[]... strings) {
            String[] details = strings[0];
            try {
                response = DataServices.login(details[0], details[1]);
                publishProgress(getString(R.string.login_successful));
            } catch (DataServices.RequestException e) {
                response = null;
                publishProgress(getString(R.string.error)+" " + e.getMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(DataServices.AuthResponse response) {
            super.onPostExecute(response);
            login.setEnabled(true);
            createNewAccount.setVisibility(View.VISIBLE);
            if (response != null && response.account != null) {
                listener.launchForumsFragment(response);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ILoginListener) {
            listener = (ILoginListener) context;
        }
    }

    interface ILoginListener {
        void launchForumsFragment(DataServices.AuthResponse response);

        void launchCreateNewAccountFragment();
    }
}