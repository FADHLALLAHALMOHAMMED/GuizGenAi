package org.Program.Entities;


import java.util.Date;

public class Submission {
    public int submissionId;
    public Date dateSubmitted;
    public int studentMark;
    public int fullMark;
    public Submission(int submissionId, Date dateSubmitted, int studentMark, int fullMark){
        this.submissionId = submissionId;
        this.dateSubmitted = dateSubmitted;
        this.studentMark = studentMark;
        this.fullMark = fullMark;
    }
}
