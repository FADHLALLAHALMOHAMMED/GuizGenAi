package org.Program.Entities;

import java.util.Date;

public class Student extends User {
    public int points;
    public Date lastLogin;
    public Student(int id, String firstName, String lastName, String email, String phoneNumber, int points, Date lastLogin) {
        super(id, firstName, lastName, email, phoneNumber);
        this.lastLogin = lastLogin;
        this.points = points;
    }
}
