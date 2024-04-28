package com.bus_tours_ex.apps.bustours.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Organizator {

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("last_name")
        @Expose
        private String lastName;

        @SerializedName("regular_number")
        @Expose
        private String regularNumber;

        @SerializedName("email")
        @Expose
        private String email;

        @SerializedName("whatsapp_number")
        @Expose
        private String whatsappNumber;

        @SerializedName("tg_tag")
        @Expose
        private String tgTag;

        @SerializedName("viber_number")
        @Expose
        private String viberNumber;

        @SerializedName("trip_id")
        @Expose
        private String tripId;

        public Organizator(String name, String lastName, String regularNumber,
                           String email, String whatsappNumber, String tgTag, String viberNumber) {
            this.name = name;
            this.lastName = lastName;
            this.regularNumber = regularNumber;
            this.email = email;
            this.whatsappNumber = whatsappNumber;
            this.tgTag = tgTag;
            this.viberNumber = viberNumber;
        }

        // Getters


        public String getName() {
            return name;
        }

        public String getLastName() {
            return lastName;
        }

        public String getRegularNumber() {
            return regularNumber;
        }

        public String getEmail() {
            return email;
        }

        public String getWhatsappNumber() {
            return whatsappNumber;
        }

        public String getTgTag() {
            return tgTag;
        }

        public String getViberNumber() {
            return viberNumber;
        }

        public String getTripId() {
            return tripId;
        }
    }
