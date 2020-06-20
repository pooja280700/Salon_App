package com.example.salonapp;

public class BookingData {

    String email;
    String name;
    String service;
    String Time;


    public BookingData( String email, String name, String service, String time) {

        this.email = email;
        this.name = name;
        this.service = service;
        Time = time;
    }



    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getService() {
        return service;
    }

    public String getTime() {
        return Time;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setTime(String time) {
        Time = time;
    }
}
