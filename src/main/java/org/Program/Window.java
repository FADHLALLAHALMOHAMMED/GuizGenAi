package org.Program;

import org.Program.Entities.*;
import org.Program.Entities.Class;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.Vector;

public class Window extends JFrame{
    private Page currentPage;
    private Class currentClass;
    private User user;
    public Thread thread1;
    public Quiz quiz;
    Window(){
        this.setTitle("QuizGenAI");
        this.setIconImage((new ImageIcon("FancyR.png").getImage()));
        this.setSize(1200, 800);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.switchPage(new StartPage(this));
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public void switchPage(Page page){
        if(currentPage != null)
            this.remove(currentPage);
        currentPage = page;
        this.add(page);
        this.setVisible(true);
    }

    public void setUser(User user){this.user = user;}
    public User getUser(){return this.user;}
    public Page getPage(){return this.currentPage;}
    public Class getCurrentClass() {return currentClass;}
    public void setCurrentClass(Class currentClass) {this.currentClass = currentClass;}

}

