package com.example.hfilproject;

import android.location.Location;

 public class SendLocation {
     private Location location;

    public SendLocation(Location mLocation) {
        this.location=mLocation;
    }

     public Location getLocation() {
         return location;
     }

     public void setLocation(Location location) {
         this.location = location;
     }
 }
