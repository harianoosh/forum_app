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

public class CreateNewAccountFragment extends Fragment {

    private DataServices.AuthResponse response;

    public CreateNewAccountFragment() {

    }

    public static CreateNewAccountFragment newInstance() {
        CreateNewAccountFragment fragment = new CreateNewAccountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText name;
    EditText email;
    EditText password;
    Button submit;
    TextView cancel;
    ICreateNewAccountListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_new_account, container, false);
        name = view.findViewById(R.id.createNewFragementName);
        email = view.findViewById(R.id.createNewFragementEmail);
        password = view.findViewById(R.id.createNewFragementPassword);
        submit = view.findViewById(R.id.createNewFragementSubmit);
        cancel = view.findViewById(R.id.createNewFragementCancel);

        getActivity().setTitle(getString(R.string.create_new_account_title));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String[] details = new String[3];
                    details[0] = name.getText().toString();
                    details[1] = email.getText().toString();
                    details[2] = password.getText().toString();

                    if (details[0].isEmpty()) {
                        throw new Exception(getString(R.string.name_error));
                    }

                    if (details[1].isEmpty()) {
                        throw new Exception(getString(R.string.email_error));
                    }

                    if (details[2].isEmpty()) {
                        throw new Exception(getString(R.string.password_error));
                    }
                    new CreateNewAccountAsyncTask().execute(details);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.launchLoginFragment();
            }
        });
        return view;
    }

    class CreateNewAccountAsyncTask extends AsyncTask<String[], String, DataServices.AuthResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submit.setEnabled(false);
            cancel.setVisibility(View.INVISIBLE);
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
                response = DataServices.register(details[0], details[1], details[2]);
                publishProgress(getString(R.string.registration_successful));
            } catch (DataServices.RequestException e) {
                response = null;
                publishProgress("Error: "+e.getMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(DataServices.AuthResponse response) {
            super.onPostExecute(response);
            submit.setEnabled(true);
            cancel.setVisibility(View.VISIBLE);
            if (response != null && response.account != null){
                listener.launchForumsFragment(response);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ICreateNewAccountListener){
            listener = (ICreateNewAccountListener) context;
        }
    }

    interface ICreateNewAccountListener{
        void launchForumsFragment(DataServices.AuthResponse response);
        void launchLoginFragment();
    }
}