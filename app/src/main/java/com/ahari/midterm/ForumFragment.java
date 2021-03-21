package com.ahari.midterm;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/*
    MidTerm
    Full Name of Student: Anoosh Hari
 */

public class ForumFragment extends Fragment {

    private static final String RESPONSE = "RESPONSE";
    private static final String FORUM = "FORUM";

    TextView title;
    TextView description;
    TextView author;
    TextView commentCounts;
    EditText comment;
    Button post;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    DataServices.AuthResponse response;
    DataServices.Forum forum;
    ForumFragmentAdapter adapter;
    LinearLayoutManager layoutManager;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

    ArrayList<DataServices.Comment> comments = new ArrayList<>();


    public ForumFragment(DataServices.AuthResponse response, DataServices.Forum forum) {
        this.response = response;
        this.forum = forum;
    }

    public static ForumFragment newInstance(DataServices.AuthResponse response, DataServices.Forum forum) {
        ForumFragment fragment = new ForumFragment(response, forum);
        Bundle args = new Bundle();
        args.putSerializable(RESPONSE, response);
        args.putSerializable(FORUM, forum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            response = (DataServices.AuthResponse) getArguments().getSerializable(RESPONSE);
            forum = (DataServices.Forum) getArguments().getSerializable(FORUM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        recyclerView = view.findViewById(R.id.forumRecyclerView);
        title = view.findViewById(R.id.forumTitle);
        description = view.findViewById(R.id.forumDescription);
        author = view.findViewById(R.id.forumAuthor);
        commentCounts = view.findViewById(R.id.forumCommentSize);
        comment = view.findViewById(R.id.forumCommentBox);
        post = view.findViewById(R.id.forumPost);

        getActivity().setTitle(getString(R.string.forum_title));

        adapter = new ForumFragmentAdapter();
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.loading));

        title.setText(forum.getTitle());
        description.setText(forum.getDescription());
        author.setText(forum.getCreatedBy().getName());

        new ForumCommentsAsyncTask().execute();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateCommentAsyncTask().execute(new String[]{forum.getForumId()+"", comment.getText().toString()});
            }
        });

        return view;
    }

    class ForumFragmentAdapter extends RecyclerView.Adapter<ForumFragmentHolder> {

        @NonNull
        @Override
        public ForumFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.comment_list_item, parent, false);
            ForumFragmentHolder holder = new ForumFragmentHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ForumFragmentHolder holder, int position) {
            DataServices.Comment comment = comments.get(position);
            holder.box.setBackground(getResources().getDrawable(R.drawable.border));
            holder.comment.setText(comment.getText());
            holder.date.setText(dateFormat.format(comment.getCreatedAt()));
            holder.author.setText(comment.getCreatedBy().getName());
            holder.commentId = comment.getCommentId();
            if (comment.getCreatedBy().getName().equalsIgnoreCase(response.getAccount().getName())) {
                holder.delete.setImageDrawable(getResources().getDrawable(R.drawable.rubbish_bin));
            } else {
                holder.delete.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return comments != null ? comments.size() : 0;
        }
    }

    class ForumFragmentHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView comment;
        TextView date;
        ImageView delete;
        ConstraintLayout box;
        long commentId;

        public ForumFragmentHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.commentAuther);
            comment = itemView.findViewById(R.id.commentDescription);
            date = itemView.findViewById(R.id.commentDate);
            delete = itemView.findViewById(R.id.deleteCommentIcon);
            box = itemView.findViewById(R.id.commentBox);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DeleteAsyncTask().execute(new Long[]{forum.getForumId(), commentId});
                }
            });
        }
    }

    class ForumCommentsAsyncTask extends AsyncTask<Long, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            post.setEnabled(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getActivity(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Long... data) {
            long forumId = forum.getForumId();
            try {
                comments.clear();
                comments.addAll(DataServices.getForumComments(response.token, forumId));
                publishProgress(getString(R.string.fetching_comment_successful));
            } catch (DataServices.RequestException e) {
                publishProgress(getString(R.string.error)+" " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            post.setEnabled(true);
            commentCounts.setText(comments.size() + " "+getResources().getString(R.string.comments));
            adapter.notifyDataSetChanged();
        }
    }

    class DeleteAsyncTask extends AsyncTask<Long[], String, String> {
        boolean isDeleteFailed = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            post.setEnabled(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getActivity(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Long[]... data) {
            long forumId = data[0][0];
            long commentId = data[0][1];
            try {
                DataServices.deleteComment(response.token, forumId, commentId);
                publishProgress(getString(R.string.delete_comment_successful));
            } catch (DataServices.RequestException e) {
                isDeleteFailed = true;
                publishProgress(getString(R.string.error)+" " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            post.setEnabled(true);
            if (!isDeleteFailed) {
                new ForumCommentsAsyncTask().execute();
            }
        }
    }

    class CreateCommentAsyncTask extends AsyncTask<String[], String, String> {
        DataServices.Comment comment;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            post.setEnabled(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getActivity(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String[]... data) {
            long forumId = new Long(data[0][0]);
            String commentText = data[0][1];
            try {
                comment = DataServices.createComment(response.token, forumId, commentText);
                publishProgress(getString(R.string.create_comment_successful));
            } catch (DataServices.RequestException e) {
                publishProgress(getString(R.string.error)+" " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            post.setEnabled(true);
            if (comment != null) {
                new ForumCommentsAsyncTask().execute();
                ForumFragment.this.comment.setText("");
            }
        }
    }
}