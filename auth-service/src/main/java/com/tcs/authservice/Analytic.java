package com.tcs.authservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalTime;

@Entity
@Table(name = "analytics")
public class Analytic {
    @Id
    @Column(name = "objectid", nullable = false, length = 50)
    private String objectid;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "principal", length = 50)
    private String principal;

    @Column(name = "timestamp")
    private LocalTime timestamp;

    @Column(name = "description", length = 50)
    private String description;

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}