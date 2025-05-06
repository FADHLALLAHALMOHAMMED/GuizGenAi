package org.Program.Entities;

import org.Program.HelperFunctions;

import javax.swing.*;
import java.awt.*;

public class Class {
    public int id;
    public int instructorId;
    public String name;
    public String iconPath;
    public ImageIcon classIcon;

    public Class(int id, String name, String iconPath, int instructorId){
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
        this.classIcon = HelperFunctions.resizeImage(iconPath);
        this.instructorId = instructorId;
    }
}
