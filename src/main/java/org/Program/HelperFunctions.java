package org.Program;

import org.Program.Entities.*;
import org.Program.Entities.Class;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

public class HelperFunctions {
    /**
     * verifies input validity; ensuring that the new user information matches specific formats.
     * also checks that email and phone number are unique.
     * @param firstName First Name, only upper or lower case characters, between 2 and 32 characters long
     * @param lastName Last Name, only upper or lower case characters, between 2 and 32 characters long
     * @param email email address must follow the format: [Something]@[Something].[Something]
     * @param phoneNumber must be a saudi phone number: starts with '05' or '+966'
     * @param password no conditions on the password yet.
     * @return a string containing the error message, and is null if there is no error.
     */
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

    /**
     * generates a Vector of Strings to display in JLists.
     * @param students a vector of student Objects
     * @return a Vector of Strings, each string containing an individual students full name and ID.
     */
    public static Vector<String> studentsToStringVector(Vector<Student> students){
        Vector<String> studentStringVector = new Vector<>();
        for(Student student: students)
            studentStringVector.add(String.format("\t%-32s\t(Student ID: %d)",
                    student.firstName + " " + student.lastName, student.id));
        return studentStringVector;
    }

    /**
     * generates a Vector of Strings to display in JLists.
     * @param classes a vector of class Objects
     * @return a Vector of Strings, each string containing an individual classes name and ID.
     */
    public static Vector<String> classesToStringVector(Vector<Class> classes){
        Vector<String> studentStringVector = new Vector<>();
        for(Class class_: classes)
            studentStringVector.add(String.format("\t%-32s\t(Class ID: %d)", class_.name, class_.id));
        return studentStringVector;
    }

    /**
     * Resizes an image given the filepath to that image.
     * @param imagePath the file path to the image to be resized.
     * @return the resized image.
     */
    public static ImageIcon resizeImage(String imagePath){
        Image image = new ImageIcon(imagePath)
                .getImage()
                .getScaledInstance(128, 128,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    /**
     * swaps the position of 2 elements in a vector
     * @param vector the vector containing the elements to be swapped.
     * @param indexA the index of the first element.
     * @param indexB the index of the second element.
     * @param <E> this function can work with vectors of any type.
     */
    public static <E> void swapElements(Vector<E> vector, int indexA, int indexB){
        E temp = vector.get(indexA);
        vector.set(indexA, vector.get(indexB));
        vector.set(indexB, temp);
    }

    /**
     * used by the GenerateQuiz function to ensure that the file does exist and is a valid format ('.doc', '.docx', '.ppt', '.pptx', or '.pdf')
     * @param filePath file path to the document to generate the quiz from.
     * @return boolean indicating if the file is valid.
     */
    public static boolean isValidFile(String filePath){
        File f = new File(filePath);
        return f.exists() && !f.isDirectory() && filePath.matches(".+(\\.doc|\\.docx|\\.ppt|\\.pptx|\\.pdf)$");
    }

    /**
     * @param filePath filepath to image.
     * @return boolean indicating if the file exists and is valid.
     */
    public static boolean isValidImage(String filePath){
        File f = new File(filePath);
        return f.exists() && !f.isDirectory() && filePath.matches(".+(\\.gif|\\.png|\\.jpeg)$");
    }

    /**
     * uses multiple other functions to generate the quiz:
     *      ensures the filepath is valid.
     *      Switches to a waiting page with a progress bar.
     *      runs the Quiz generation code on another thread.
     *
     * @param window the window object for the application.
     */
    public static void GenerateQuiz(Window window){
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(window);
        String filepath;

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filepath = fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            System.out.println("cancelled by user.");
            return;
        }

        Object[] options = {"Use sample LLM reply",
                            "Use API",
                            "Cancel"};
        String message = "Please by mindful of my API key. if you are testing the GUI, choose 'Use sample LLM reply'.";
        returnVal = JOptionPane.showOptionDialog(window, message, "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, 0);

        switch(returnVal){
            case 0 -> QuizGen.mode = 0;
            case 1 -> QuizGen.mode = 1;
            default -> {return;}
        }

        // if the filepath is valid, run the generateQuestions() on another thread.
        if(!isValidFile(filepath)) {
            JOptionPane.showMessageDialog(window, "Invalid File. Must be .doc, .docx, .ppt, .pptx, or .pdf",
                    "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        } // return if file is invalid

        window.switchPage(new WaitingPage(window));

        QuizGen.filePath = filepath;
        QuizGen.window = window;

        window.thread1 = new Thread(new QuizGen());
        window.thread1.start();
    }


    /**
     * Since labels don't have line wrapping, they will try to display the String, no matter how long, in a single line.
     * this function formats the string with html tokens('<html>', '<br/>') to add new line characters that work with labels.
     * @param string The String to split in multiple lines
     * @param maxCharsPerLine the maximum number of characters allowed in a line.
     * @return A Formatted String that can be displayed in labels and is split into multiple lines.
     */
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

    /**
     * Checks if a student can take a quiz:
     *      1. the quiz start time has to have started.
     *      2. the quiz end time must not have been passed.
     *      3. the student must not have taken the quiz before.
     * @param quiz the Quiz object of the quiz of interest.
     * @param studentId the ID of the student.
     * @return A string containing an error message with the reason the student can't take the quiz, or null if the student can take the quiz.
     */
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

    /**
     * many functions in this program return strings to show the result of a given operation,
     * if there is an error the string will contain a description of the error for the user.
     * if there is no error the string will be null.
     * this function will only show the error dialog and the error message if there is an error.
     * furthermore, this function will return true if there is an error to inform the calling function.
     * @param message the error message String.
     * @param window the window of the program.
     * @return true if there is an error.
     */
    public static boolean showDialogIfError(String message, Window window){ // todo: the error dialog contains two buttons there should only be one.
        if(message == null) return false;                   // if the message is null, there is no error: don't show error dialog and return false.
        int response = JOptionPane.showConfirmDialog(window,
                                                     message,
                                                "Operation Failed",
                                                     JOptionPane.OK_OPTION,
                                                     JOptionPane.ERROR_MESSAGE);
        return true; // if there is an error, return true to indicate that.
    }

    /**
     * Gets the status of a students quiz, there are for possible statuses:
     *  1. Quiz time hasn't started yet.
     *  2. Student hasn't completed Quiz yet. (if the student didn't take the quiz, but there is still time)
     *  3. Student hasn't completed Quiz yet. (if the student didn't take the quiz, and the quiz end date has passed)
     *  4. null (the student completed the quiz)
     *
     *  this function is used by the 'QuizDisplayTable()' function, which will pass a quiz and its corresponding
     *  submission for the student if the submission is null the student hasn't taken the quiz yet.
     *
     * @param quiz a Quiz object representing the quiz of interest.
     * @param submission a submission object representing the student submission for that quiz. Can be null.
     * @return A String containing the status, or null if the student took the quiz.
     */
    public static String StudentQuizStatus(Quiz quiz, Submission submission){
        Date currentTime = new Date();

        if(submission != null) // student completed quiz.
            return null;

        if(quiz.startDateTime.after(currentTime))
            return "Quiz time hasn't started yet.";

        if(quiz.endDateTime.after(currentTime))
            return "Student hasn't completed Quiz yet."; // student didn't take quiz, but quiz is still available.

        return "Student didn't take Quiz."; // if the quiz end time passed and student didn't complete it.
    }

    /**
     * uses other function to handle the grading of a submission:
     *  1. get the answers from the submissionId
     *  2. grades the mcq questions. and stores the overall result: how many answers correct over how many questions total.
     *  3. create a formatted string that containing all the essay questions and answers and grading instruction for the LLM.
     *  4. pass the string along with the hash of the quiz to the LLM in the QuizGen class
     *  5. parses the LLM's reply and updates essayQuestion objects with the grades and justifications for the grades.
     *  6. updates the Essay Answers and submission tables in the database with the student marks.
     * @param submissionId the ID of the submission to be graded.
     */
    public static void gradeSubmission(int submissionId){
        int[] marks = Database.getMCQMarks(submissionId);

        System.out.printf("The students grade for the mcq questions: %d / %d\n", marks[0], marks[1]);
        Vector<EssayQuestion> questions = Database.getEssayAnswers(submissionId);

        if(questions.size() != 0) {
            String questionsAndAnswers = formatEssayAnswersString(questions);
            String LLMReply = QuizGen.gradeEssay(submissionId, questionsAndAnswers);

            if(LLMReply != null) {
                gradeParser(LLMReply, questions);
                for (EssayQuestion question : questions) {
                    marks[1] += 2;
                    if (question.grade.matches("full credit")) marks[0] += 2;
                    else if (question.grade.matches("partial credit")) marks[0] += 1;
                }
                Database.UpdateEssayGrades(questions, submissionId);
            }
        }

        Database.gradeSubmission(submissionId, marks);
    }

    /**
     * creates a formatted String for the LLM to grade the students Answers for the Essay Questions.
     * @param questions A vector of EssayQuestion objects containing the student answer text.
     * @return A formatted String containing the essay question text, the Students answer, and instructions for grading
     */
    public static String formatEssayAnswersString(Vector<EssayQuestion> questions){
        StringBuilder str = new StringBuilder(Constants.gradingTemplate);
        str.append("\n");
        int count = 0;
        for(EssayQuestion question: questions){
            str.append(String.format("\nEssay Question %d:\"%s\"\nStudent Answer for question %d: \"%s\"\n",
                    count, question.questionText, count++, question.studentAnswer));
        }
        return str.toString();
    }

    /**
     * Reads the LLM's Grading reply and Extracts the grades and grade justifications for each essay question.
     * NOTE: this function returns by reference: the values will be stored in the 'grade' and 'gradeJustification'
     * attributes of the EssayQuestion objects of the 'questions' vector.
     * @param gradeReply the LLM Reply containing the grades.
     * @param questions the EssayQuestion Objects, where the grades and grade justifications will be stored.
     */
    public static void gradeParser(String gradeReply, Vector<EssayQuestion> questions){
        Vector<String> essayGrades = new Vector<>(Arrays.asList(gradeReply.split("\n")));
        for(int i = essayGrades.size() - 1; i >= 0; i--)
            if(essayGrades.get(i).isBlank())
                essayGrades.removeElementAt(i);

        for(int i = 0; i < questions.size(); i++){
            String[] elements = essayGrades.get(i).split("\\|");
            if(elements[0].matches("<full credit>")) questions.get(i).grade = "<full credit>";
            else if (elements[0].matches("<partial credit>")) questions.get(i).grade = "<partial credit>";
            else if (elements[0].matches("<no credit>")) questions.get(i).grade = "<no credit>";
            questions.get(i).gradeJustification = elements[1];
        }
    }
}
