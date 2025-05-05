package org.Program.Entities;

import java.util.Date;

public class Feedback {
    private int instructorId;
    private int studentId;
    private String subjectLine;
    private String messageText;
    private Date dateTimeSent;

    public Feedback(int instructorId, int studentId, String subjectLine, String messageText, Date dateTimeSent) {
        this.instructorId = instructorId;
        this.studentId = studentId;
        this.subjectLine = subjectLine;
        this.messageText = messageText;
        this.dateTimeSent = dateTimeSent;
    }
    public int getInstructorId() { return instructorId; }
    public int getStudentId() { return studentId; }
    public String getSubjectLine() { return subjectLine; }
    public String getMessageText() { return messageText; }
    public Date getDateTimeSent() { return dateTimeSent;}
}