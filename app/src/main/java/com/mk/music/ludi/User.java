package com.mk.music.ludi;

import java.io.Serializable;
public class User implements Serializable{
    private int id;
    private String username;
    private String password;

    public User() {
        super();
// TODO Auto-generated constructor stub
    }
    public User(String username, String password, int age, String sex) {
        super();
        this.username = username;
        this.password = password;

    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }

    @Override
    public String toString(){
//        return "User [username=" + username + ", password=" + password "]"";
        return "User [id=" + id + ", username=" + username + ", password="
                + password +  "]";
    }
}

//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class User extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user);
//    }
//}