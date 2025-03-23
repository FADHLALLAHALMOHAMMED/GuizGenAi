package org.Program.Entities;
public class Student extends User {
    public int points;
    public Student(int id, String firstName, String lastName, String email, String phoneNumber, int points) {
        super(id, firstName, lastName, email, phoneNumber);
        this.points = points;
    }
}
