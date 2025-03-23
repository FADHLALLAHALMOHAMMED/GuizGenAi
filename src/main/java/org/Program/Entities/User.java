package org.Program.Entities;

public abstract class User {
    public int id;
    public String email;
    public String firstName;
    public String lastName;
    public String phoneNumber;

    public User(int id, String firstName, String lastName, String email, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
