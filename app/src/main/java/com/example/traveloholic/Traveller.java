package com.example.traveloholic;

public class Traveller {
        public String Name,Email,Password;

        public Traveller() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Traveller(String name, String email, String password) {
            Name = name;
            Email = email;
            Password = password;
        }
    }
