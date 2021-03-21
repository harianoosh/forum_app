package com.ahari.midterm;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class DataServices {
    private static final long DELAY_MIN_MILLI_SECS = 500;
    private static final long DELAY_MAX_MILLI_SECS = 1500;
    private static long USER_ID_COUNTER = 1;
    private static long FORUM_ID_COUNTER = 1;
    private static long COMMENT_ID_COUNTER = 1;

    private static HashMap<String, Account> accountsMap = new HashMap<String, Account>(){{
        put("a@a.com", new Account("Alice Smith", "a@a.com", "test123"));
        put("b@b.com", new Account("Bob Smith", "b@b.com", "test123"));
        put("c@c.com", new Account("Charles Smith", "c@c.com", "test123"));
    }};
    private static HashMap<String, Account> authTokenAccountsMap = new HashMap<String, Account>();
    private static final ArrayList<Forum> forums = new ArrayList<Forum>();
    private static final HashMap<Long, ArrayList<Comment>> comments = new HashMap<Long, ArrayList<Comment>>(){{
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, -4);
        Account account = accountsMap.get("a@a.com");

        //first example forum
        Forum forum = new Forum("Sports and Other",account, calendar.getTime(),"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Enim eu turpis egestas pretium aenean. Vitae ultricies leo integer malesuada nunc. In hac habitasse platea dictumst vestibulum. Turpis egestas pretium aenean pharetra. Ornare massa eget egestas purus viverra accumsan in. Iaculis at erat pellentesque adipiscing commodo elit at imperdiet. Donec adipiscing tristique risus nec feugiat in fermentum posuere urna. Lorem ipsum dolor sit amet consectetur adipiscing. Natoque penatibus et magnis dis parturient. Est ante in nibh mauris cursus mattis. Urna cursus eget nunc scelerisque viverra mauris in aliquam. Penatibus et magnis dis parturient montes nascetur ridiculus mus. Porta nibh venenatis cras sed felis eget. Id donec ultrices tincidunt arcu.");
        forums.add(forum);
        ArrayList<Comment> forumComments = new ArrayList<>();
        forum.getLikedBy().add(account);

        calendar.add(Calendar.HOUR, 1);
        account = accountsMap.get("b@b.com");
        Comment comment = new Comment("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lacinia at quis risus sed vulputate odio ut enim. ",
                account,calendar.getTime());
        forumComments.add(comment);
        forum.getLikedBy().add(account);

        calendar.add(Calendar.HOUR, 1);
        account = accountsMap.get("c@c.com");
        comment = new Comment("Quam lacus suspendisse faucibus interdum posuere lorem ipsum dolor. Blandit turpis cursus in hac habitasse. Leo integer malesuada nunc vel risus commodo viverra maecenas accumsan. Nibh tellus molestie nunc non blandit.",
                account,calendar.getTime());
        forumComments.add(comment);

        calendar.add(Calendar.HOUR, 1);
        account = accountsMap.get("a@a.com");
        comment = new Comment("Id nibh tortor id aliquet lectus proin nibh nisl. Leo in vitae turpis massa sed elementum tempus egestas. Iaculis urna id volutpat lacus laoreet non curabitur gravida. Sagittis id consectetur purus ut faucibus." ,
                account,calendar.getTime());
        forumComments.add(comment);

        forum.getLikedBy().add(account);

        put(forum.getForumId(), forumComments);



        //second example forum
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, -2);
        account = accountsMap.get("b@b.com");
        forum = new Forum("Fishing Spots", account, calendar.getTime(),"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Est lorem ipsum dolor sit amet consectetur.");
        forums.add(forum);
        forumComments = new ArrayList<>();
        forum.getLikedBy().add(account);

        calendar.add(Calendar.MINUTE, 15);
        account = accountsMap.get("a@a.com");
        comment = new Comment("Risus viverra adipiscing at in tellus. Turpis egestas sed tempus urna et pharetra pharetra massa. Pharetra sit amet aliquam id diam maecenas ultricies mi. Risus at ultrices mi tempus imperdiet nulla. Sed lectus vestibulum mattis ullamcorper velit.",
                account,calendar.getTime());
        forumComments.add(comment);

        calendar.add(Calendar.MINUTE, 15);
        account = accountsMap.get("c@c.com");
        comment = new Comment("At quis risus sed vulputate odio. At ultrices mi tempus imperdiet nulla malesuada pellentesque elit eget. Turpis egestas sed tempus urna et pharetra. Suspendisse ultrices gravida dictum fusce ut placerat orci. Netus et malesuada fames ac turpis. Morbi tristique senectus et netus.",
                account,calendar.getTime());
        forumComments.add(comment);

        calendar.add(Calendar.MINUTE, 15);
        account = accountsMap.get("a@a.com");
        comment = new Comment("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Pharetra vel turpis nunc eget lorem dolor sed viverra." ,
                account,calendar.getTime());
        forumComments.add(comment);
        forum.getLikedBy().add(account);
        put(forum.getForumId(), forumComments);

    }};

    public static AuthResponse login(String email, String password) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        if(email == null || email.isEmpty() ){
            throw new RequestException("Email cannot be empty!");
        } else {
            Account account = accountsMap.get(email.toLowerCase());
            if(account == null){
                throw new RequestException("Email/Account not found!");
            } else {
                if(!account.getPassword().equals(password)){
                    throw new RequestException("Email or Password do not match!");
                } else {
                    String token =  UUID.randomUUID().toString();
                    authTokenAccountsMap.clear();
                    authTokenAccountsMap.put(token, account);
                    return new AuthResponse(account, token);
                }
            }
        }
    }

    public static AuthResponse register(String name, String email, String password) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        if(name == null || name.isEmpty()){
            throw new RequestException("Name cannot be empty!");
        } else if(email == null || email.isEmpty()){
            throw new RequestException("Email cannot be empty!");
        } else if(password == null || password.isEmpty() ){
            throw new RequestException("Password cannot be empty!");
        } else {
            if(accountsMap.containsKey(email.toLowerCase())){
                throw new RequestException("Email already used please use another email!");
            } else {
                Account account = new Account(name, email.trim().toLowerCase(), password);
                accountsMap.put(email.trim().toLowerCase(), account);
                String token = UUID.randomUUID().toString();
                authTokenAccountsMap.clear();
                authTokenAccountsMap.put(token, account);
                return new AuthResponse(account, token);
            }

        }
    }

    public static Account getAccount(String token) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        if(token == null){
            throw new RequestException("Token is required!");
        } else {
            Account account = authTokenAccountsMap.get(token);
            if(account == null){
                throw new RequestException("Invalid token account not found!");
            } else {
                return account;
            }
        }
    }

    public static ArrayList<Forum> getAllForums(String token) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        Account account = checkAccount(token);
        return new ArrayList<>(forums);
    }

    public static Forum getForum(String token, long forumId) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        Account account = checkAccount(token);
        Forum forum = null;

        for (Forum f: forums) {
            if(f.getForumId() == forumId){
                forum = f;
                break;
            }
        }

        if(forum != null){
            return forum;
        } else {
            throw new RequestException("Invalid forumId, unable to find this forum");
        }
    }

    public static ArrayList<Comment> getForumComments(String token, long forumId) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        Account account = checkAccount(token);
        if(comments.containsKey(forumId)){
            return new ArrayList<Comment>(comments.get(forumId));
        }
        throw new RequestException("Invalid forumId, unable to find this forum");
    }

    public static Forum createForum(String token, String title, String description) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        Account account = checkAccount(token);

        Forum forum = new Forum(title, account,new Date(),description);
        forums.add(forum);
        comments.put(forum.getForumId(), new ArrayList<Comment>());
        return forum;
    }

    public static Comment createComment(String token, long forumId, String commentText) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        Account account = checkAccount(token);

        ArrayList<Comment> forumComments = comments.get(forumId);

        if(forumComments != null){
            Comment comment = new Comment(commentText, account, new Date());
            forumComments.add(comment);
            comments.put(forumId, forumComments);
            return comment;
        }

        throw new RequestException("Invalid forumId, unable to find this forum");
    }

    public static void likeForum(String token, long forumId) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        Account account = checkAccount(token);
        Forum forum = null;

        for (Forum f: forums) {
            if(f.getForumId() == forumId){
                forum = f;
                break;
            }
        }

        if(forum != null){
            forum.getLikedBy().add(account);
        } else {
            throw new RequestException("Invalid forumId, unable to find this forum");
        }
    }

    public static void unLikeForum(String token, long forumId) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        Account account = checkAccount(token);
        Forum forum = null;

        for (Forum f: forums) {
            if(f.getForumId() == forumId){
                forum = f;
                break;
            }
        }

        if(forum != null){
            forum.getLikedBy().remove(account);
        } else {
            throw new RequestException("Invalid forumId, unable to find this forum");
        }
    }

    public static void deleteForum(String token, long forumId) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        Account account = checkAccount(token);
        Forum forum = null;

        for (Forum f: forums) {
            if(f.getForumId() == forumId){
                forum = f;
                break;
            }
        }

        if(forum != null){
            if(forum.getCreatedBy().getUid() == account.getUid()){
                forums.remove(forum);
                comments.remove(forumId);
                return;
            } else {
                throw new RequestException("Unable to delete forum that was created by another user.");
            }
        }
        throw new RequestException("Invalid forumId, unable to find this forum");
    }

    public static void deleteComment(String token, long forumId, long commentId) throws RequestException {
        addSomeDelay(DELAY_MAX_MILLI_SECS, DELAY_MIN_MILLI_SECS); //delay to emulate the network delay.
        Account account = checkAccount(token);

        ArrayList<Comment> forumComments = comments.get(forumId);

        if(forumComments != null){
            Comment comment = null;
            for (Comment c: forumComments) {
                if(c.getCommentId() == commentId){
                    comment = c;
                    break;
                }
            }

            if(comment != null){
                if(comment.getCreatedBy().getUid() == account.getUid()){
                    forumComments.remove(comment);
                    comments.put(forumId, forumComments);
                    return;
                } else {
                    throw new RequestException("Unable to delete comment that was created by another user.");
                }
            }
        }
        throw new RequestException("Invalid commentId, unable to find this comment");
    }

    private static void addSomeDelay(long millisMax, long millisMin){

        Random rand = new Random();

        long millis = (Math.abs(rand.nextLong()) % (millisMax - millisMin)) + millisMin;
        Log.d("demo", "addSomeDelay: " + millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class Account implements Serializable {
        private String name, email, password;
        long uid;
        public Account(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.uid = USER_ID_COUNTER++;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Account account = (Account) o;
            return uid == account.uid;
        }

        @Override
        public int hashCode() {
            return (int) (uid % (1024*1024));
        }

        @Override
        public String toString() {
            return "Account{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    ", uid=" + uid +
                    '}';
        }
    }

    public static class Forum implements Serializable{
        private String title;
        private Account createdBy;
        private Date createdAt;
        private String description;
        private HashSet<Account> likedBy;
        private long forumId;

        public Forum(String title, Account createdBy, Date createdAt, String description) {
            this.title = title;
            this.createdBy = createdBy;
            this.createdAt = createdAt;
            this.description = description;
            this.likedBy = new HashSet<>();
            this.forumId = FORUM_ID_COUNTER++;
        }

        public long getForumId() {
            return forumId;
        }

        public String getTitle() {
            return title;
        }

        public Account getCreatedBy() {
            return createdBy;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public String getDescription() {
            return description;
        }

        public HashSet<Account> getLikedBy() {
            return likedBy;
        }

        @Override
        public String toString() {
            return "Forum{" +
                    "title='" + title + '\'' +
                    ", createdBy=" + createdBy +
                    ", createdAt=" + createdAt +
                    ", description='" + description + '\'' +
                    ", likedBy=" + likedBy +
                    ", forumId=" + forumId +
                    '}';
        }
    }

    public static class Comment implements Serializable{
        String text;
        Account createdBy;
        Date createdAt;
        long commentId;

        public Comment(String text, Account createdBy, Date createdAt) {
            this.text = text;
            this.createdBy = createdBy;
            this.createdAt = createdAt;
            this.commentId = COMMENT_ID_COUNTER++;
        }

        public long getCommentId() {
            return commentId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Account getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Account createdBy) {
            this.createdBy = createdBy;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "Comment{" +
                    "text='" + text + '\'' +
                    ", createdBy=" + createdBy +
                    ", createdAt=" + createdAt +
                    ", commentId=" + commentId +
                    '}';
        }
    }

    public static class AuthResponse implements Serializable{
        Account account;
        String token;

        public AuthResponse(Account account, String token) {
            this.account = account;
            this.token = token;
        }

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class RequestException extends Exception {
        public RequestException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static Account checkAccount(String token) throws RequestException {
        if(token == null){
            throw new RequestException("Token is required!");
        } else {
            Account account = authTokenAccountsMap.get(token);
            if(account == null){
                throw new RequestException("Invalid token account not found!");
            } else {
                return account;
            }
        }
    }
}