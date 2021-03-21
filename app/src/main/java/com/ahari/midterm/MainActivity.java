package com.ahari.midterm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/*
    MidTerm
    Full Name of Student: Anoosh Hari
 */

public class MainActivity extends AppCompatActivity implements LoginFragment.ILoginListener,
                                                               CreateNewAccountFragment.ICreateNewAccountListener,
                                                               ForumsFragment.IForumsFragmentListener,
                                                               NewForumFragment.INewForumFragmentListener {
    private String LOGIN_FRAGMENT = "LOGIN_FRAGMENT";
    private String FORUMS_FRAGMENT = "FORUMS_FRAGMENT";
    private String CREATE_NEW_ACCOUNT_FRAGMENT = "CREATE_NEW_ACCOUNT_FRAGMENT";
    private String NEW_FORUM_FRAGMENT = "NEW_FORUM_FRAGMENT";
    private String FORUM_FRAGMENT = "FORUM_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, LoginFragment.newInstance(), LOGIN_FRAGMENT)
                .commit();
    }

    @Override
    public void launchForumsFragment(DataServices.AuthResponse response) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ForumsFragment.newInstance(response), FORUMS_FRAGMENT)
                .commit();
    }

    @Override
    public void launchLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance(), LOGIN_FRAGMENT)
                .commit();
    }

    @Override
    public void launchNewForumFragment(DataServices.AuthResponse response) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, NewForumFragment.newInstance(response), NEW_FORUM_FRAGMENT)
                .addToBackStack(FORUMS_FRAGMENT)
                .commit();
    }

    @Override
    public void launchForumFragment(DataServices.AuthResponse response, DataServices.Forum forum) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ForumFragment.newInstance(response, forum), FORUM_FRAGMENT)
                .addToBackStack(FORUMS_FRAGMENT)
                .commit();
    }

    @Override
    public void launchCreateNewAccountFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, CreateNewAccountFragment.newInstance(), CREATE_NEW_ACCOUNT_FRAGMENT)
                .commit();
    }

    @Override
    public void launchForumFragmentBack() {
        getSupportFragmentManager().popBackStack();
    }
}