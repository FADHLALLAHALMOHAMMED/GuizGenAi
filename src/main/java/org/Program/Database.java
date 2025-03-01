package org.Program;

import ai.djl.huggingface.translator.QuestionAnsweringTranslator;
import org.Program.Entities.*;
import org.Program.Entities.Class;

import java.sql.*;
import java.util.Vector;

/*      this class acts as an intermediary between the program and the database.
*       methods in this class act as an interface to simplify communication between the database and the program.
* */

public class Database {
    private static Connection connection;
    public static Boolean connect(String databaseURL, String user, String password){
        try{
            connection = DriverManager.getConnection(databaseURL, user, password);
            System.out.println("Connection established successfully.");
            return true;
        } catch (SQLException e){
            System.out.println("Failed to establish connection with SQL server." + e);
        }
        return false;
    }

    public static int checkUser(String email, String phoneNumber){
        String emailQuery = String.format("""
                    select (email) from instructors where email = "%s"
                    union all
                    select (email) from students where email = "%s"
                    """, email, email);

        String phoneNumberQuery = String.format("""
                    Select PhoneNumber from instructors where PhoneNumber = "%s"
                    union all
                    select PhoneNumber from students where PhoneNumber = "%s"
                    """, phoneNumber, phoneNumber);

        try {
            ResultSet resultSet = connection.createStatement().executeQuery(emailQuery);
            if (resultSet.next()){
                System.out.println("Email already registered.");
                return 0;
            }

            resultSet = connection.createStatement().executeQuery(phoneNumberQuery);
            if (resultSet.next()){
                System.out.println("PhoneNumber already registered");
                return 1;
            }

        }catch(SQLException e){
            System.out.println("Failed to verify Email and phone number: " + e);
        }
        return 2;
    }

    public static void register(String firstName, String lastName, String email, String phoneNumber, String password, int accountType){
        String registrationUpdate;
        try{
            if(accountType == 0) {
                registrationUpdate = String.format("""
                                 insert into instructors(firstName, lastName, Email, Password, PhoneNumber)
                                 value("%s", "%s", "%s", "%s", "%s");
                                """,
                        firstName, lastName, email, phoneNumber, password);
            } else {
                registrationUpdate = String.format("""
                                 insert into students(firstName, lastName, Email, Password, PhoneNumber)
                                 value("%s", "%s", "%s", "%s", "%s");
                                """,
                        firstName, lastName, email, phoneNumber, password);
            }
            connection.createStatement().executeUpdate(registrationUpdate);

        }catch(SQLException e){
            System.out.println("Failed to register user: " + e);
        }
    }

    public static User login(String email, String password){
        String instructorLoginQuery = String.format("""
                     select instructorId, firstName, LastName, Email, PhoneNumber from Instructors
                     where email = "%s" and password = "%s";
                    """,
                email, password);

        String studentLoginQuery = String.format("""
                     select StudentId, firstName, LastName, Email, PhoneNumber, Points from Students
                     where email = "%s" and password = "%s";
                    """,
                email, password);

        try {
            // search the instructors table for matching username and password, if found, return user.
            ResultSet resultSet = connection.createStatement().executeQuery(instructorLoginQuery);
            if(resultSet.next()) return readInstructor(resultSet);

            // search the students table for matching user and password, if found, return user.
            resultSet = connection.createStatement().executeQuery(studentLoginQuery);
            if(resultSet.next()) return readStudent(resultSet);

            return null; // if no match was found for the users login credentials, return null.

        } catch(SQLException e){
            System.out.println("Failed to retrieve Login: " + e.getMessage());
        }
        return null; // control will only reach here is an error occurred.
    }

    private static Instructor readInstructor(ResultSet resultSet) throws SQLException{
        return new Instructor( // extract the info, then create and return instructor object.
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5)
        );
    }

    private static Student readStudent(ResultSet resultSet) throws SQLException{
        return new Student( // extract the info, then create and return instructor object.
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getInt(6)
        );
    }

    private static Class readClass(ResultSet resultSet) throws SQLException {
        return new Class(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3)
        );
    }

    private static Quiz readQuiz(ResultSet resultSet) throws SQLException {
        return new Quiz(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getTimestamp(3),
                resultSet.getTimestamp(4),
                resultSet.getInt(5)
        );
    }

    private static Question readQuestion(ResultSet resultSet) throws SQLException {
        return new Question(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6)
        );
    }

    public static String newClass(String className, String iconPath, Window window){
        int instructorId = window.getUser().id;
        if(!className.matches("^([a-zA-Z0-9]| ){3,48}$")) return "invalid class name.";

        try {
            PreparedStatement ps;

            ps = connection.prepareStatement("Select (ClassName) from classes where classname = ?");
            ps.setString(1, className);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()) return "Class Name must be unique.";

            String newClassUpdate = "insert into classes(ClassName, Instructor, classIconPath) value(?, ?, ?)";
            ps = connection.prepareStatement(newClassUpdate);
            ps.setString(1, className);
            ps.setInt(2, instructorId);
            ps.setString(3, iconPath);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Operation failed: an error has occurred with our server.";
        }
        return null;
    }

    public static Vector<Class> getInstructorClasses(int instructorId){
        Vector<Class> classes = new Vector<>();
        String getClassesQuery = String.format("""
                select ClassId, ClassName, classIconPath from classes where instructor = %d;
                """, instructorId);

        try{
            ResultSet resultSet = connection.createStatement().executeQuery(getClassesQuery);

            while(resultSet.next())
                classes.add(readClass(resultSet));

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return classes;
    }

    public static Vector<Student> getStudentsNotInClass(int classID){
        Vector<Student> students = new Vector<>();
        String studentQuery = """
                select studentId, FirstName, LastName, email, phoneNumber, Points from students
                where studentId not in (select student from students_classes where class = ?);
                """;

        try{
            PreparedStatement ps = connection.prepareStatement(studentQuery);
            ps.setInt(1, classID);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next())
                students.add(readStudent(resultSet));

        }catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return students;
    }

    public static Vector<Student> getStudentsInClass(int classID){
        Vector<Student> students = new Vector<>();
        String studentQuery = """
                select studentId, FirstName, LastName, email, phoneNumber, Points from students
                where studentId in (select student from students_classes where class = ?);
                """;

        try{
            PreparedStatement ps = connection.prepareStatement(studentQuery);
            ps.setInt(1, classID);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next())
                students.add(readStudent(resultSet));

        }catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return students;
    }

    public static void addStudentsToClass(Vector<Student> students, int classId){
        StringBuilder addStudentsUpdate = new StringBuilder("insert into students_classes values ");
        for(Student student: students) addStudentsUpdate.append("(?, ?), ");
        addStudentsUpdate.setLength(addStudentsUpdate.length() - 2);

        try{
            PreparedStatement ps = connection.prepareStatement(addStudentsUpdate.toString());
            int fieldIndex = 1;
            for(Student student: students){
                ps.setInt(fieldIndex++, student.id);
                ps.setInt(fieldIndex++, classId);
            }
            ps.executeUpdate();

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void removeStudentsFromClass(Vector<Student> students, int classId){
        if(students.isEmpty()) return;
        StringBuilder removeStudentsUpdate = new StringBuilder("delete from students_Classes where ");
        for(Student student: students) removeStudentsUpdate.append("(student = ? and class = ?) or ");
        removeStudentsUpdate.setLength(removeStudentsUpdate.length() - 4);

        try{
            PreparedStatement ps = connection.prepareStatement(removeStudentsUpdate.toString());
            int fieldIndex = 1;
            for(Student student: students){
                ps.setInt(fieldIndex++, student.id);
                ps.setInt(fieldIndex++, classId);
            }

            ps.executeUpdate();

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static String addQuiz(String title, java.util.Date startTime, java.util.Date endTime,
                                 Vector<Class> classes, Vector<Question> questions, int instructorId){
        if(classes.isEmpty()) return "No class selected: Quiz must be assigned to at least one class.";

        // test if start or end date is in the past
        Date CurrentDateTime = new Date(System.currentTimeMillis());
        if(startTime.before(CurrentDateTime) || endTime.before(CurrentDateTime))
            return "Invalid Start or End Time: Quiz start or end time cannot be in the past.";
        // test if the quiz ends before is starts
        if(endTime.before(startTime))
            return "Invalid Start or End Time: Quiz end time cannot be before its start time.";

        try {
            // test if title is unique
            PreparedStatement ps = connection.prepareStatement("select * from Quizzes where Title = ?;");
            ps.setString(1, title);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next())
                return "A Quiz with the same Title already exists: quiz titles must be unique.";

            // add Quiz to database
            ps = connection.prepareStatement("insert into Quizzes (title, startDateTime, endDateTime, Instructor) value(?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setTimestamp(2, new Timestamp(startTime.getTime()));
            ps.setTimestamp(3, new Timestamp(endTime.getTime()));
            ps.setInt(4, instructorId);
            ps.executeUpdate();

            // connect Quiz with classes
            resultSet = ps.getGeneratedKeys();
            resultSet.next();
            int quizId = resultSet.getInt(1);

            addQuestions(questions, quizId);

            ps = connection.prepareStatement("insert into classes_quizzes (class, quiz) values (?, ?)");
            for(Class class_: classes) {
                ps.setInt(1, class_.id);
                ps.setInt(2, quizId);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void addQuestions(Vector<Question> questions, int quizId){
        try{
            String SQL_Update = """
                    insert into Questions (AnswerChoice0, AnswerChoice1, AnswerChoice2, AnswerChoice3, QuestionText, CorrectAnswer, quiz)
                    values(?, ?, ?, ?, ?, ?, ?)
                    """;
            PreparedStatement ps = connection.prepareStatement(SQL_Update);
            for(Question question: questions){
                for(int i = 0; i < 4; i++)
                    ps.setString(i+1, question.answerChoices.get(i));
                ps.setString(5, question.questionText);
                ps.setInt(6, question.correctAnswerIndex);
                ps.setInt(7, quizId);
                ps.addBatch();
            }
            ps.executeBatch();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static Vector<Question> getQuizQuestions(Quiz quiz){
        Vector<Question> questions = new Vector<>();
        try{
            PreparedStatement ps = connection.prepareStatement("""
                    select * from Questions
                    where quiz = ?;
                    """);

            ps.setInt(1, quiz.id);

            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next())
                questions.add(readQuestion(resultSet));

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return questions;
    }

    public static Vector<Quiz> getQuizzesByStudent(int studentId){
        Vector<Quiz> quizzes = new Vector<>();
        String quizzesQuery = """
                select * from Quizzes where QuizId in
                (select Quiz from classes_quizzes where class in
                (select Class from Students_Classes where student = ?))
                """;
        try{

            PreparedStatement ps = connection.prepareStatement(quizzesQuery);
            ps.setInt(1, studentId);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next())
                quizzes.add(readQuiz(resultSet));

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return quizzes;
    }

    public static void RecordAnswers(){}

    public static boolean studentCompleted(int quizId, int studentId){
        try{
            PreparedStatement ps = connection.prepareStatement("""
                    select student from answers
                    where student = ?
                    and question in
                    (Select QuestionId from questions where quiz = ?)
                    limit 1;
                    """);
            ps.setInt(1, studentId);
            ps.setInt(2, quizId);
            ResultSet resultSet = ps.executeQuery();

            return resultSet.next();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static Vector<Quiz> getQuizzesByClass(int classId){
        Vector<Quiz> quizzes = new Vector<>();

        try{
            PreparedStatement ps = connection.prepareStatement("""
                    select * from quizzes where quizId in
                    (select quiz from classes_quizzes where class = ?)
                    """);
            ps.setInt(1, classId);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next())
                quizzes.add(readQuiz(resultSet));

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return quizzes;
    }

    public static void saveAnswers(Vector<Question> questions, Vector<Integer> answerIndices, int studentId){

        try{
            PreparedStatement ps = connection.prepareStatement("insert into answers values(?, ?, ?)");
            for(int i = 0; i < questions.size(); i++){
                ps.setInt(1, studentId);
                ps.setInt(2, questions.get(i).id);
                ps.setInt(3, answerIndices.get(i));
                ps.addBatch();
            }
            ps.executeBatch();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static Vector<Question> getStudentAnswers(int quizId, int studentId){
        Vector<Question> questions = new Vector<>();
        try{
            PreparedStatement ps = connection.prepareStatement("""
                    select QuestionId, QuestionText, AnswerChoice0, AnswerChoice1,
                    AnswerChoice2, AnswerChoice3, CorrectAnswer, AnswerChoice from
                    Questions QuizQuestions join Answers studentAnswers
                    on quizQuestions.questionId = studentAnswers.question
                    where quizQuestions.quiz = ? and studentAnswers.student = ?;
                    """);
            ps.setInt(1, quizId);
            ps.setInt(2, studentId);

            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                Question question = readQuestion(resultSet);
                question.correctAnswerIndex = resultSet.getInt(7);
                question.selectedAnswerIndex = resultSet.getInt(8);
                questions.add(question);
            }

        }catch(SQLException e){
            System.out.println(e);
        }
        return questions;
    }
}
