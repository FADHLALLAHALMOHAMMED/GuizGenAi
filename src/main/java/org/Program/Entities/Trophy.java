package org.Program.Entities;

import org.Program.HelperFunctions;

import javax.swing.*;

public class Trophy {
    private int classId;
    private int studentId;
    private String imagePath;
    private String description;
    private ImageIcon trophyIcon;

    public Trophy(int classId, int studentId, String imagePath, String description) {
        this.classId = classId;
        this.studentId = studentId;
        this.imagePath = imagePath;
        this.trophyIcon = HelperFunctions.resizeImage(imagePath);
        this.description = description;
    }


    public int getClassId() { return classId; }
    public int getStudentId() { return studentId; }
    public String getImagePath() { return imagePath; }
    public String getDescription() { return description;}
    public ImageIcon getTrophyIcon() {return trophyIcon;}
}