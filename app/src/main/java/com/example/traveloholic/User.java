package com.example.traveloholic;

class User {
    public String Name,Address,Gender,Email,Password,DOB,url;
    public Long ph1,ph2;
    public Integer Age;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String address, String gender, String email, String password, String DOB, Long ph1, Long ph2, Integer age) {
        Name = name;
        Address = address;
        Gender = gender;
        Email = email;
        Password = password;
        this.DOB = DOB;
        this.ph1 = ph1;
        this.ph2 = ph2;
        Age = age;
    }
}
