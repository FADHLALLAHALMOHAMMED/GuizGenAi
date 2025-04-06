package org.Program.Entities;

import java.util.Date;
import java.util.Vector;

public class Quiz {
    public int id;
    public String title;
    public Date startDateTime;
    public Date endDateTime;
    public int instructorId;
    public Vector<Question> questions;
    public Quiz(int id, String title, Date startDateTime, Date endDateTime, int instructorId) {
        this.id = id;
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.instructorId = instructorId;
    }
}
