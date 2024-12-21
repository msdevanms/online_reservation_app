package com.tcs.authservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "authtokens")
public class Authtoken {
    @Id
    @Column(name = "token", nullable = false, length = 50)
    private String token;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "creationtime")
    private Instant creationtime;

    @Column(name = "expirytime", nullable = false)
    private Integer expirytime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(Instant creationtime) {
        this.creationtime = creationtime;
    }

    public Integer getExpirytime() {
        return expirytime;
    }

    public void setExpirytime(Integer expirytime) {
        this.expirytime = expirytime;
    }

    @Override
    public String toString() {
        return "Authtoken{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", creationtime=" + creationtime +
                ", expirytime=" + expirytime +
                '}';
    }
}