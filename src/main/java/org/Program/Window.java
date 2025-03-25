package org.Program;

import org.Program.Entities.Class;
import org.Program.Entities.Question;
import org.Program.Entities.Quiz;
import org.Program.Entities.User;

import javax.swing.*;
import java.util.Date;
import java.util.Vector;
// todo: look into implementing a "last page" attribute, so that users can go back without needing to reload the page.
public class Window extends JFrame{
    private Page currentPage;
    private Class currentClass;
    private User user;
    public Thread thread1;
    public Quiz quiz;
    Window(){
        this.setTitle("Project Management System");
        this.setIconImage((new ImageIcon("FancyR.png").getImage()));
        this.setSize(1200, 800);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.switchPage(new StartPage(this));
        this.setVisible(true);
        this.setLocationRelativeTo(null);

//        this.switchPage(new TestPage(this));

//        Date date = new Date(System.currentTimeMillis());
//        quiz = new Quiz(1, "1", date, date, 1);
//        quiz.questions = Question.process(Constants.LLMReply);
//        this.switchPage(new QuizPage(this));
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

