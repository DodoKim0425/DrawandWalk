package com.example.drawandwalk;

import java.io.Serializable;

public class DrawLocation implements Serializable {

        private Double latitude, longitude;

        public DrawLocation(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
