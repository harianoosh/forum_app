package com.ahari.midterm;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;

/*
    MidTerm
    Full Name of Student: Anoosh Hari
 */

public class ForumsFragment extends Fragment {

    private static final String RESPONSE = "RESPONSE";

    private DataServices.AuthResponse response;

    Button logout;
    Button newForum;
    IForumsFragmentListener listener;
    ArrayList<DataServices.Forum> forums = new ArrayList<>();
    ForumsAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ProgressDialog progressDialog;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

    public ForumsFragment(DataServices.AuthResponse response) {
        this.response = response;
    }

    public static ForumsFragment newInstance(DataServices.AuthResponse response) {
        ForumsFragment fragment = new ForumsFragment(response);
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
        View view = inflater.inflate(R.layout.fragment_forums, container, false);
        logout = view.findViewById(R.id.forumsFragmentLogout);
        newForum = view.findViewById(R.id.forumsFragmentNewForum);
        recyclerView = view.findViewById(R.id.forumsFragmentRecyclerView);

        getActivity().setTitle(getString(R.string.forums_title));

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.loading));

        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new ForumsAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        new ForumsAsyncTask().execute();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.launchLoginFragment();
            }
        });

        newForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.launchNewForumFragment(response);
            }
        });
        return view;
    }

    class ForumsAsyncTask extends AsyncTask<String[], String, DataServices.AuthResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            logout.setEnabled(false);
            newForum.setEnabled(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getActivity(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected DataServices.AuthResponse doInBackground(String[]... strings) {
            try {
                forums.clear();
                forums.addAll(DataServices.getAllForums(response.token));
                publishProgress(getString(R.string.fetching_forums_successful));
            } catch (DataServices.RequestException e) {
                response = null;
                publishProgress(getString(R.string.error)+" " + e.getMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(DataServices.AuthResponse s) {
            super.onPostExecute(s);
            logout.setEnabled(true);
            newForum.setEnabled(true);
            progressDialog.dismiss();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IForumsFragmentListener) {
            listener = (IForumsFragmentListener) context;
        }
    }

    interface IForumsFragmentListener {
        void launchLoginFragment();

        void launchNewForumFragment(DataServices.AuthResponse response);

        void launchForumFragment(DataServices.AuthResponse response, DataServices.Forum forum);
    }

    class ForumsAdapter extends RecyclerView.Adapter<ForumsAdapterHolder> {

        @NonNull
        @Override
        public ForumsAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forums_list_item, parent, false);
            ForumsAdapterHolder holder = new ForumsAdapterHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ForumsAdapterHolder holder, int position) {
            DataServices.Forum forum = forums.get(position);
            holder.createdDate.setText(dateFormat.format(forum.getCreatedAt()));
            holder.forumDescription.setText(forum.getDescription());
            holder.createrName.setText(forum.getCreatedBy().getName());
            holder.title.setText(forum.getTitle());
            holder.outline.setBackground(getResources().getDrawable(R.drawable.border));
            holder.likes.setText(forum.getLikedBy().size() + " "+getResources().getString(R.string.likes));
            holder.forumId = forum.getForumId();
            holder.forum = forum;
            HashSet<DataServices.Account> likedUsers = forum.getLikedBy();
            if (likedUsers.contains(response.account)) {
                holder.likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.like_favorite));
                holder.isLiked = true;
            } else {
                holder.likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.like_not_favorite));
                holder.isLiked = false;
            }
            if (forum.getCreatedBy().getName().equalsIgnoreCase(response.account.getName())) {
                holder.deleteIcon.setVisibility(View.VISIBLE);
            } else {
                holder.deleteIcon.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return forums != null ? forums.size() : 0;
        }
    }

    class ForumsAdapterHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView createrName;
        TextView forumDescription;
        TextView likes;
        TextView createdDate;
        ImageView deleteIcon;
        ImageView likeIcon;
        ConstraintLayout outline;
        long forumId;
        boolean isLiked;
        DataServices.Forum forum;

        public ForumsAdapterHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            createrName = itemView.findViewById(R.id.itemForumCreaterName);
            forumDescription = itemView.findViewById(R.id.itemForumDescription);
            likes = itemView.findViewById(R.id.itemForumLikes);
            createdDate = itemView.findViewById(R.id.itemForumCreated);
            deleteIcon = itemView.findViewById(R.id.imageViewDelete);
            likeIcon = itemView.findViewById(R.id.imageViewLike);
            outline = itemView.findViewById(R.id.outlineBox);

            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DeleteAsyncTask().execute(forumId);
                }
            });

            likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long[] floats;
                    //1L and 2L are codes to delete in LikeAsyncTask (Utilizing one task for both like and unlike calls)
                    if (isLiked) {
                        floats = new Long[]{forumId, 1L};
                    } else {
                        floats = new Long[]{forumId, 2L};
                    }
                    new LikeAsyncTask().execute(floats);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.launchForumFragment(response, forum);
                }
            });
        }
    }

    class DeleteAsyncTask extends AsyncTask<Long, String, String> {
        boolean isDeleteFailed = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            logout.setEnabled(false);
            newForum.setEnabled(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getActivity(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Long... data) {
            long forumId = data[0];
            try {
                DataServices.deleteForum(response.token, forumId);
                publishProgress(getString(R.string.delete_forum_successful));
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
            logout.setEnabled(true);
            newForum.setEnabled(true);
            if (!isDeleteFailed) {
                new ForumsAsyncTask().execute();
            }
        }
    }

    class LikeAsyncTask extends AsyncTask<Long[], String, String> {
        boolean isLikeUnlikeFailed = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            logout.setEnabled(false);
            newForum.setEnabled(false);
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
            try {
                if (data[0][1] == 2L) {
                    DataServices.likeForum(response.token, forumId);
                } else if (data[0][1] == 1L) {
                    DataServices.unLikeForum(response.token, forumId);
                }
                publishProgress(getString(R.string.like_updated));
            } catch (DataServices.RequestException e) {
                isLikeUnlikeFailed = true;
                publishProgress(getString(R.string.error)+" " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            logout.setEnabled(true);
            newForum.setEnabled(true);
            if (!isLikeUnlikeFailed) {
                new ForumsAsyncTask().execute();
            }
        }
    }
}