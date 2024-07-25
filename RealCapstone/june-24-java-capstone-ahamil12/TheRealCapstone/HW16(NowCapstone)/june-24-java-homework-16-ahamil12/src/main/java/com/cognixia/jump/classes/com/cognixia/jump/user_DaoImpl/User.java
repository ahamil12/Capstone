package com.cognixia.jump.user_DaoImpl;

public class User {
    private int user_id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String role;


    public User(int user_id, String name, String username, String password, String email, String role) {
        this.user_id = user_id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;

    }

    public int getId() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return role;
    }
}
