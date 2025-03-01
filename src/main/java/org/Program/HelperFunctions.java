package org.Program;

import org.Program.Entities.Class;
import org.Program.Entities.Quiz;
import org.Program.Entities.Student;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.Vector;

public class HelperFunctions {
    public static String ValidateRegistration(String firstName, String lastName,
                                               String email, String phoneNumber, String password){
        phoneNumber = phoneNumber.replaceAll("(\\(|\\)|-| |\\.)", "");
        // validate firstName and last name
        if(!firstName.matches("^[a-zA-Z]{2,32}$")) return "Invalid First Name.";
        if(!lastName.matches("^[a-zA-Z]{2,32}$")) return "Invalid Last Name.";
        if(!email.matches("^.{3,24}@.{2,16}\\..{2,8}$")) return "Invalid Email Address.";
        if(!phoneNumber.matches("^(\\+966|0)5[0-9]{8}$")) return "invalid Phone Number";

        // TODO: add password requirements
        switch(Database.checkUser(email, phoneNumber)){
            case 0: return "Email Already Registered.";
            case 1: return "Phone Number Already Registered.";
        }
        return null;
    }

    public static Vector<String> studentsToStringVector(Vector<Student> students){
        Vector<String> studentStringVector = new Vector<>();
        for(Student student: students)
            studentStringVector.add(String.format("\t%-32s\t(Student ID: %d)",
                    student.firstName + " " + student.lastName, student.id));
        return studentStringVector;
    }

    public static Vector<String> classesToStringVector(Vector<Class> classes){
        Vector<String> studentStringVector = new Vector<>();
        for(Class class_: classes)
            studentStringVector.add(String.format("\t%-32s\t(Class ID: %d)", class_.name, class_.id));
        return studentStringVector;
    }

    public static ImageIcon resizeImage(String imagePath){
        Image image = new ImageIcon(imagePath)
                .getImage()
                .getScaledInstance(128, 128,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    public static <E> void swapElements(Vector<E> vector, int indexA, int indexB){
        E temp = vector.get(indexA);
        vector.set(indexA, vector.get(indexB));
        vector.set(indexB, temp);
    }

    public static boolean isValidFile(String filePath){
        File f = new File(filePath);
        return f.exists() && !f.isDirectory() && filePath.matches(".+(\\.doc|\\.docx|\\.ppt|\\.pptx|\\.pdf)$");
    }

    public static boolean isValidImage(String filePath){
        File f = new File(filePath);
        return f.exists() && !f.isDirectory() && filePath.matches(".+(\\.gif|\\.png|\\.jpeg)$");
    }

    public static void GenerateQuiz(String filePath, Window window){
        // if the filepath is valid, run the generateQuestions() on another thread.
        if(!isValidFile(filePath)) {
            JOptionPane.showMessageDialog(window, "Invalid File. Must be .doc, .docx, .ppt, .pptx, or .pdf",
                    "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        } // return if file is invalid

        window.switchPage(new WaitingPage(window));

        MCQGen.filePath = filePath;
        MCQGen.window = window;

        window.thread1 = new Thread(new MCQGen());
        window.thread1.start();
    }

    public static String toMultiLine(String string, int maxCharsPerLine){
        StringBuilder str = new StringBuilder(string.replace("\n", " "));
        int c = 0;  // the index of the last new line char.
        int b = 0;  // the last confirmed safe place to add a new line char.
        int a = str.indexOf(" ", b+1); // lookahead, to test if valid place to add new line char
        while(a != -1){
            if(a - c > maxCharsPerLine){ // if a - c exceeds the maxCharsPerLine -> too many chars per line.
                c = b;  // set c to equal b (the last confirmed safe index to add new line)
                str.setCharAt(c, '\n'); // add new line at b (which is also b in this case)
            }
            b = a;  // if a - c > maxCharsPerLine -> it is safe to insert \n char at a, so store it in b.
            a = str.indexOf(" ", b+1); // take one more step.
        }

        str.insert(0, "<html>");
        str.append("</html>");

        return str.toString().replace("\n", "<br/>");
    }

    public static String StudentCanTakeQuiz(Quiz quiz, int studentId){
        Date currentTime = new Date(System.currentTimeMillis());
        if(quiz.startDateTime.after(currentTime))
            return "This Quiz has not started yet.";
        if(quiz.endDateTime.before(currentTime))
            return "This Quiz has ended.";
        if(Database.studentCompleted(quiz.id, studentId))
            return "You already took this quiz.";
        return null;
    }

    // many functions in this program return strings to show the result of a given operation,
    // if there is an error the string will contain a description of the error for the user.
    // if there is no error the string will be null.
    // this function will only show the error dialog and the error message if there is an error.
    // furthermore, this function will return true if there is an error to inform the calling function.
    public static boolean showDialogIfError(String message, Window window){
        if(message == null) return false;                   // if the message is null, there is no error: don't show error dialog.
        int response = JOptionPane.showConfirmDialog(window,
                                                     message,
                                                "Operation Failed",
                                                     JOptionPane.OK_OPTION,
                                                     JOptionPane.ERROR_MESSAGE);
        return true; // if there is an error, return true to indicate that.
    }

    public static String StudentQuizStatus(Quiz quiz, int studentId){
        Date currentTime = new Date();

        if(quiz.startDateTime.after(currentTime))
            return "Quiz time hasn't started yet.";

        if(Database.studentCompleted(quiz.id, studentId)) // student completed quiz.
            return null;

        if(quiz.endDateTime.after(currentTime))
            return "Student hasn't completed Quiz yet."; // student didn't take quiz, but quiz is still available.

        return "Student didn't take Quiz."; // if the quiz end time passed and student didn't complete it.
    }
}




