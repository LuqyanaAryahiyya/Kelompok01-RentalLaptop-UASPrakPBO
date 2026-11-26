package com.rentallaptop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Akun {

    private String id;
    private String username;
    private String password;
    private String nama;

    protected Akun(String id, String username, String password, String nama) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nama = nama;
    }

    public void login(String username, String password) throws LoginGagalException {
        if (username == null || password == null
                || !this.username.equals(username)
                || !this.password.equals(password)) {
            throw new LoginGagalException("Username atau password salah.");
        }
    }

    public abstract String getPeran();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean cocokPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
