package com.example.android.nomaklerapp.Modal.User;


public class User {


    private String name;
    private String email;
    private long joinTime_millis;
    private String token;

    public User(String name, String email) {
        this.name = name;
        this.email = email;

        joinTime_millis = System.currentTimeMillis();
        token = this.email + "_" + joinTime_millis;
    }

    public String getUserName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public long getJoinTime_millis() {
        return joinTime_millis;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof User)){
            return false;
        }
        User otherUser = (User) obj;
        return otherUser.getToken().equals(this.token);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name + " - Email: ");
        builder.append(email + " - token: ");
        builder.append(token + " - joined: ");
        builder.append(joinTime_millis);

        return builder.toString();
    }
}
