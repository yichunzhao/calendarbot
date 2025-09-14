package com.ynz.ai.calendarbot.service;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class AppointmentRequest implements Serializable {

    @NotBlank
    private String clientName;

    @NotBlank
    private String clientContact;

    @NotBlank
    private String service;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime time;

    public AppointmentRequest() {
    }

    public AppointmentRequest(String clientName, String clientContact, String service,
                              LocalDate date, LocalTime time) {
        this.clientName = clientName;
        this.clientContact = clientContact;
        this.service = service;
        this.date = date;
        this.time = time;
    }

    // --- getters & setters ---
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientContact() {
        return clientContact;
    }

    public void setClientContact(String clientContact) {
        this.clientContact = clientContact;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentRequest that = (AppointmentRequest) o;
        return Objects.equals(clientName, that.clientName)
                && Objects.equals(clientContact, that.clientContact)
                && Objects.equals(service, that.service)
                && Objects.equals(date, that.date)
                && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientName, clientContact, service, date, time);
    }

    @Override
    public String toString() {
        return "AppointmentRequest{" +
                "clientName='" + clientName + '\'' +
                ", clientContact='" + clientContact + '\'' +
                ", service='" + service + '\'' +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}
