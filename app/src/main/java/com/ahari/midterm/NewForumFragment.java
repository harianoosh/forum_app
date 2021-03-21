package com.ahari.midterm;

import android.app.ProgressDialog;
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

public class NewForumFragment extends Fragment {

    private static final String RESPONSE = "RESPONSE";

    Button submit;
    EditText forumTitle;
    EditText forumDescription;
    TextView cancel;
    ProgressDialog progressDialog;
    DataServices.AuthResponse response;

    public NewForumFragment(DataServices.AuthResponse response) {
        this.response = response;
    }

    public static NewForumFragment newInstance(DataServices.AuthResponse response) {
        NewForumFragment fragment = new NewForumFragment(response);
        Bundle args = new Bundle();
        args.putSerializable(RESPONSE, response);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            response = (DataServices.AuthResponse) getArguments().getSerializable(RESPONSE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_forum, container, false);
        forumTitle = view.findViewById(R.id.newForumTitle);
        forumDescription = view.findViewById(R.id.newForumDescription);
        submit = view.findViewById(R.id.newForumSubmit);
        cancel = view.findViewById(R.id.newForumCancel);

        getActivity().setTitle(getString(R.string.new_forum_title));

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.loading));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewFragmentAsyncTask().execute();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.launchForumFragmentBack();
            }
        });

        return view;
    }

    class NewFragmentAsyncTask extends AsyncTask<Long, String, String> {
        DataServices.Forum forum;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            submit.setEnabled(false);
            cancel.setVisibility(View.INVISIBLE);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getActivity(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Long... data) {
            try {
                String title = forumTitle.getText().toString();
                String description = forumDescription.getText().toString();
                if (title.isEmpty()) {
                    throw new Exception(getString(R.string.forum_title_error));
                }

                if (description.isEmpty()) {
                    throw new Exception(getString(R.string.forum_description_error));
                }
                forum = DataServices.createForum(response.token, title, description);
                publishProgress(getString(R.string.create_forum_successful));
            } catch (Exception e) {
                publishProgress(getString(R.string.error)+" " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            submit.setEnabled(true);
            cancel.setVisibility(View.VISIBLE);
            if (forum != null) {
                listener.launchForumFragmentBack();
            }
        }
    }

    INewForumFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof INewForumFragmentListener){
            listener = (INewForumFragmentListener) context;
        }
    }

    interface INewForumFragmentListener{
        void launchForumFragmentBack();
    }
}