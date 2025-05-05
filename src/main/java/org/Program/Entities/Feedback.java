package org.Program.Entities;

public class Feedback {
    private int instructorId;
    private int studentId;
    private String subjectLine;
    private String messageText;
    private String dateTimeSent;

    public Feedback(int instructorId, int studentId, String subjectLine, String messageText, String dateTimeSent) {
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
    public String getDateTimeSent() { return dateTimeSent;}
}