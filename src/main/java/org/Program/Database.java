package org.Program;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.Program.Entities.*;
import org.Program.Entities.Class;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/*      this class acts as an intermediary between the program and the database.
*       methods in this class act as an interface to simplify communication between the database and the program.
* */

public class Database {
    public static final String databaseURL = "jdbc:mysql://localhost:3306/QuizGenAI";
    public static final String user = "root";
    public static final String password = "System";
    public static Connection getConnection(){
        try{
            return DriverManager.getConnection(databaseURL, user, password);
        } catch (SQLException e){
            System.out.println("Failed to establish connection with SQL server." + e);
        }
        throw new RuntimeException("Failed to establish connection");
    }

    public static int checkUser(String email, String phoneNumber){
        int result = 0;
        String emailDML = """
                    select (email) from instructors where email = ?
                    union all
                    select (email) from students where email = ?
                    """;

        String phoneNumberDML ="""
                    Select PhoneNumber from instructors where PhoneNumber = ?
                    union all
                    select PhoneNumber from students where PhoneNumber = ?
                    """;

        try (
                Connection connection = getConnection();
                PreparedStatement emailSQL = connection.prepareStatement(emailDML);
                PreparedStatement phoneNumberSQL = connection.prepareStatement(phoneNumberDML)
        ){

            emailSQL.setString(1, email);
            emailSQL.setString(2, email);

            try(ResultSet resultSet = emailSQL.executeQuery()){
                if (resultSet.next()) result |= 1;
            }

            phoneNumberSQL.setString(1, phoneNumber);
            phoneNumberSQL.setString(2, phoneNumber);

            try(ResultSet resultSet = phoneNumberSQL.executeQuery()){
                if (resultSet.next()) result |= 2;
            }

        }catch(SQLException e){
            System.out.println("Failed to verify Email and phone number: " + e);
        }
        return result;
    }

    public static void register(String firstName, String lastName, String email, String phoneNumber, String password, int accountType){
        String registrationDML = accountType == 0?
                                """
                                 insert into instructors(firstName, lastName, Email, Password, PhoneNumber)
                                 value(?, ?, ?, ?, ?);
                                """:
                                """
                                 insert into students(firstName, lastName, Email, Password, PhoneNumber)
                                 value(?, ?, ?, ?, ?);
                                """;
        try(
                Connection connection = getConnection();
                PreparedStatement registrationUpdate = connection.prepareStatement(registrationDML)
        ){
            registrationUpdate.setString(1, firstName);
            registrationUpdate.setString(2, lastName);
            registrationUpdate.setString(3, email);
            registrationUpdate.setString(4, password);
            registrationUpdate.setString(5, phoneNumber);

            registrationUpdate.executeUpdate();

        }catch(SQLException e){
            System.out.println("Failed to register user: " + e);
        }
    }

    public static User login(String email, String password){
        String instructorLoginDML = """
                     select instructorId, firstName, LastName, Email, PhoneNumber from Instructors
                     where email = ? and password = ?;
                    """;

        String studentLoginDML = """
                     select StudentId, firstName, LastName, Email, PhoneNumber, Points, lastLogin from Students
                     where email = ? and password = ?;
                    """;

        try (
                Connection connection = getConnection();
                PreparedStatement instructorLoginQuery = connection.prepareStatement(instructorLoginDML);
                PreparedStatement studentLoginQuery = connection.prepareStatement(studentLoginDML)
        ){
            // search the instructors table for matching username and password, if found, return user.
            instructorLoginQuery.setString(1, email);
            instructorLoginQuery.setString(2, password);

            try(ResultSet resultSet = instructorLoginQuery.executeQuery()){
                if(resultSet.next()) return readInstructor(resultSet);
            }

            // search the students table for matching user and password, if found, return user.
            studentLoginQuery.setString(1, email);
            studentLoginQuery.setString(2, password);

            try(ResultSet resultSet = studentLoginQuery.executeQuery()){
                if(resultSet.next()) {
                    Student student = readStudent(resultSet);
                    HelperFunctions.StudentLoginRewards(student.id, student.lastLogin);
                    return student;
                }
            }

        } catch(SQLException e){
            System.out.println("Failed to retrieve Login: " + e.getMessage());
        }
        return null; // if no match was found for the users login credentials or if an error occurred.
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
                resultSet.getInt(6),
                resultSet.getTimestamp(7)
        );
    }

    private static Class readClass(ResultSet resultSet) throws SQLException {
        return new Class(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getInt(4)
        );
    }

    private static Quiz readQuiz(ResultSet resultSet) throws SQLException {
        return new Quiz(
                resultSet.getInt("QuizId"),
                resultSet.getString("title"),
                resultSet.getTimestamp("startDateTime"),
                resultSet.getTimestamp("endDateTime"),
                resultSet.getInt("instructor")
        );
    }

    private static Submission readSubmission(ResultSet resultSet) throws SQLException {
            return new Submission(
                resultSet.getInt("submissionId"),
                resultSet.getTimestamp("TimeSubmitted"),
                resultSet.getInt("StudentMark"),
                resultSet.getInt("fullMark")
            );
    }

    public static Question readQuestion(ResultSet resultSet) throws SQLException{
        if(Objects.equals(resultSet.getString("QuestionType"), "MCQ"))
            return readMCQuestion(resultSet);
        return readEssayQuestion(resultSet);
    }

    private static MCQuestion readMCQuestion(ResultSet resultSet) throws SQLException {
        return new MCQuestion(
                resultSet.getInt("QuestionID"),
                resultSet.getString("QuestionText"),
                resultSet.getString("AnswerChoice0"),
                resultSet.getString("AnswerChoice1"),
                resultSet.getString("AnswerChoice2"),
                resultSet.getString("AnswerChoice3")
        );
    }

    private static EssayQuestion readEssayQuestion(ResultSet resultSet) throws SQLException {
        return new EssayQuestion(
                resultSet.getInt("QuestionID"),
                resultSet.getString("QuestionText")
        );
    }

    public static String newClass(String className, String iconPath, Window window){
        int instructorId = window.getUser().id;
        if(!className.matches("^([a-zA-Z0-9]| ){3,48}$")) return "invalid class name.";

        try (
                Connection connection = getConnection();
                PreparedStatement classQuery = connection.prepareStatement("Select (ClassName) from classes where classname = ?");
                PreparedStatement newClassUpdate = connection.prepareStatement("insert into classes(ClassName, Instructor, classIconPath) value(?, ?, ?)")
        ){

            classQuery.setString(1, className);

            try(ResultSet resultSet = classQuery.executeQuery()){
                if(resultSet.next()) return "Class Name must be unique.";
            }

            newClassUpdate.setString(1, className);
            newClassUpdate.setInt(2, instructorId);
            newClassUpdate.setString(3, iconPath);
            newClassUpdate.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Operation failed: an error has occurred with our server.";
        }
        return null;
    }

    public static Vector<Class> getInstructorClasses(int instructorId){
        Vector<Class> classes = new Vector<>();
        String getClassesDML = """
                select ClassId, ClassName, classIconPath, Instructor
                from classes
                where instructor = ?;
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement getClassesQuery = connection.prepareStatement(getClassesDML)
        ){

            getClassesQuery.setInt(1, instructorId);

            try (ResultSet resultSet = getClassesQuery.executeQuery()){
                while(resultSet.next())
                    classes.add(readClass(resultSet));
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return classes;
    }
    public static Vector<Class> getStudentClasses(int StudentId){
        Vector<Class> classes = new Vector<>();
        String getClassesDML = """
                select c.classId, c.classname, c.classIconPath, c.Instructor
                from classes c right join students_classes sc
                on c.ClassID = sc.Class
                where sc.student = ?;
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement getClassesQuery = connection.prepareStatement(getClassesDML)
        ){

            getClassesQuery.setInt(1, StudentId);

            try (ResultSet resultSet = getClassesQuery.executeQuery()){
                while(resultSet.next())
                    classes.add(readClass(resultSet));
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return classes;
    }

    public static Vector<Student> getStudentsNotInClass(int classID){
        Vector<Student> students = new Vector<>();
        String studentDML = """
                select studentId, FirstName, LastName, email, phoneNumber, Points, LastLogin from students
                where studentId not in (select student from students_classes where class = ?);
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(studentDML)
        ){

            ps.setInt(1, classID);

            try(ResultSet resultSet = ps.executeQuery()){
                while(resultSet.next())
                    students.add(readStudent(resultSet));
            }

        }catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return students;
    }

    public static Vector<Student> getStudentsInClass(int classID){
        Vector<Student> students = new Vector<>();
        String studentQuery = """
                select studentId, FirstName, LastName, email, phoneNumber, Points, LastLogin from students
                where studentId in (select student from students_classes where class = ?);
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(studentQuery)
        ){

            ps.setInt(1, classID);

            try(ResultSet resultSet = ps.executeQuery()){
                while(resultSet.next())
                    students.add(readStudent(resultSet));
            }

        }catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return students;
    }

    public static void addStudentsToClass(Vector<Student> students, int classId){
        if(students.isEmpty()) return;
        String addStudentsDML = "insert into students_classes values (?, ?)";

        try(
                Connection connection = getConnection();
                PreparedStatement addStudentsQuery = connection.prepareStatement(addStudentsDML)
        ){
            for(Student student: students){
                addStudentsQuery.setInt(1, student.id);
                addStudentsQuery.setInt(2, classId);
                addStudentsQuery.addBatch();
            }

            addStudentsQuery.executeBatch();

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void removeStudentsFromClass(Vector<Student> students, int classId){
        if(students.isEmpty()) return;
        StringBuilder removeStudentsDML = new StringBuilder("""
                delete from students_Classes
                where class = ? and student in (
                """);
        for(Student student: students)
            removeStudentsDML.append("?, ");
        removeStudentsDML.setCharAt(removeStudentsDML.length() - 2, ')');

        try(
                Connection connection = getConnection();
                PreparedStatement removeStudentsUpdate = connection.prepareStatement(removeStudentsDML.toString())
        ){

            int fieldIndex = 1;
            removeStudentsUpdate.setInt(fieldIndex++, classId);

            for(Student student: students)
                removeStudentsUpdate.setInt(fieldIndex++, student.id);

            removeStudentsUpdate.executeUpdate();

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static String addQuiz(String title, java.util.Date startTime, java.util.Date endTime, Vector<Class> classes,
                                 Vector<Question> questions, int instructorId, String embeddingsHash){

        if(classes.isEmpty()) return "No class selected: Quiz must be assigned to at least one class.";

        // test if start or end date is in the past
        Timestamp CurrentDateTime = new Timestamp(System.currentTimeMillis());
        if(startTime.before(CurrentDateTime) || endTime.before(CurrentDateTime))
            return "Invalid Start or End Time: Quiz start or end time cannot be in the past.";

        // test if the quiz ends before is starts
        if(endTime.before(startTime))
            return "Invalid Start or End Time: Quiz end time cannot be before its start time.";

        String matchingTitleDML = "select * from Quizzes where Title = ?";
        String addQuizDML = """
                insert into Quizzes (title, startDateTime, endDateTime, Instructor, embeddings, fullMark)
                value(?, ?, ?, ?, ?, ?);
                """;
        String assignClassesQuizzesDML = "insert into classes_quizzes (class, quiz) values (?, ?)";

        int fullMark = 0;
        for(Question question: questions){      // calculate the full mark on the quiz
            if(question instanceof MCQuestion) fullMark += 1; // each MCQ is worth 1 mark
            else fullMark += 2; // each essay is worth 2 marks
        }

        try (
                Connection connection = getConnection();
                PreparedStatement matchingTitleQuery = connection.prepareStatement(matchingTitleDML);
                PreparedStatement addQuizQuery = connection.prepareStatement(addQuizDML, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement assignClassesQuizzesUpdate = connection.prepareStatement(assignClassesQuizzesDML)
        ){
            // test if title is unique
            matchingTitleQuery.setString(1, title);

            try(ResultSet resultSet = matchingTitleQuery.executeQuery()){
                if(resultSet.next())
                    return "A Quiz with the same Title already exists: quiz titles must be unique.";
            }

            // add Quiz to database
            addQuizQuery.setString(1, title);
            addQuizQuery.setTimestamp(2, new Timestamp(startTime.getTime()));
            addQuizQuery.setTimestamp(3, new Timestamp(endTime.getTime()));
            addQuizQuery.setInt(4, instructorId);
            addQuizQuery.setString(5, embeddingsHash);
            addQuizQuery.setInt(6, fullMark);
            addQuizQuery.executeUpdate();

            // connect Quiz with classes
            try(ResultSet resultSet = addQuizQuery.getGeneratedKeys()){

                resultSet.next();
                int quizId = resultSet.getInt(1);

                addQuestions(questions, quizId);

                for(Class class_: classes) {
                    assignClassesQuizzesUpdate.setInt(1, class_.id);
                    assignClassesQuizzesUpdate.setInt(2, quizId);
                    assignClassesQuizzesUpdate.addBatch();
                }
                assignClassesQuizzesUpdate.executeBatch();

            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     *  there are 2 types of questions that we need to store, which makes updating the database a little tricky:
     *  In the database there are 3 relations(tables): Questions, MCQuestions, and EssayQuestions.
     *  Questions is the supertype, and the other 2 are its subtypes.
     *  the relationship is a total, disjoint relationship, we learned this in the database class(lecture 6)
     *  The Questions table stores the shared information between question types: ID, Question text, type, and quizId.
     *  The MCQuestions table stores the unique information of the MCQs: the 4 choices, and the index of the correct choice.
     *  The EssayQuestions table store the unique information of the Essay Questions: (no unique information as of now)
     *  To add the questions to the database we first iterate over the questions and add the relevant information to the Questions table.
     *  Since the Ids are automatically assigned to questions by the database, we use the .getGeneratedKeys() function to retrieve the IDs.
     *  we then add the rest of the information of both question types separately, connecting them to the questions supertype using the IDs we retrieved.
     */
    public static void addQuestions(Vector<Question> questions, int quizId){
        String addQuestionsDML = """
                    insert into Questions (QuestionType, QuestionOrder, QuestionText, quiz)
                    Values(?, ?, ?, ?)
                    """;
        String addMCQuestionsDML = """
                    insert into MCQuestions (Question, AnswerChoice0, AnswerChoice1, AnswerChoice2, AnswerChoice3, CorrectAnswer)
                    values(?, ?, ?, ?, ?, ?)
                    """;
        String addEssayQuestionsDML = """
                    Insert into EssayQuestions (Question)
                    Values(?)
                    """;
        try(
                Connection connection = getConnection();
                PreparedStatement addQuestionsUpdate = connection.prepareStatement(addQuestionsDML, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement addMCQuestionsUpdate = connection.prepareStatement(addMCQuestionsDML);
                PreparedStatement addEssayQuestionsUpdate = connection.prepareStatement(addEssayQuestionsDML)
        ){
            int count = 1;
            for(Question question: questions){
                addQuestionsUpdate.setString(1, question instanceof MCQuestion ? "MCQ" : "Essay"); // store the type of the question.
                addQuestionsUpdate.setInt(2, count++); // <- The order of the questions in the quiz
                addQuestionsUpdate.setString(3, question.questionText);
                addQuestionsUpdate.setInt(4, quizId);
                addQuestionsUpdate.addBatch();
            }
            addQuestionsUpdate.executeBatch();

            try(ResultSet resultSet = addQuestionsUpdate.getGeneratedKeys()) { // get the auto generated IDs
                Vector<Integer> questionIds = new Vector<>(questions.size()); // create a vector to store the IDs
                while (resultSet.next())
                    questionIds.add(resultSet.getInt(1)); // iterate over the result set and store the IDs in the vector

                count = 0;
                for (Question question : questions) {
                    // if the question type is MCQ add its information to the batch of MCQ Updates to execute them later all at once.
                    if (question instanceof MCQuestion MCQ) {

                        addMCQuestionsUpdate.setInt(1, questionIds.get(count++));
                        for (int i = 0; i < 4; i++)
                            addMCQuestionsUpdate.setString(i + 2, MCQ.answerChoices.get(i));
                        addMCQuestionsUpdate.setInt(6, MCQ.correctAnswerIndex);
                        addMCQuestionsUpdate.addBatch();

                    } else if (question instanceof EssayQuestion EQ) { // if Essay question add its information to its relevant batch.
                        addEssayQuestionsUpdate.setInt(1, questionIds.get(count++));
                        addEssayQuestionsUpdate.addBatch();
                        // the essay questions don't have any unique information, but I included this just in case.
                    }
                }

                addMCQuestionsUpdate.executeBatch();
                addEssayQuestionsUpdate.executeBatch();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static int[] getMCQMarks(int submissionId){
        String getMCQMarksDML = """
                    SELECT
                        s.SubmissionId,
                        SUM(CASE WHEN mcq.CorrectAnswer = mca.SelectedAnswer THEN 1 ELSE 0 END) AS correctAnswers,
                        COUNT(mca.Question) AS numOfQuestions
                    FROM Submissions s
                    JOIN MCAnswers mca ON mca.Submission = s.SubmissionId
                    JOIN MCQuestions mcq ON mcq.Question = mca.Question
                    WHERE s.SubmissionId = ?
                    """;
        try(
                Connection connection = getConnection();
                PreparedStatement getMCQMarksUpdate = connection.prepareStatement(getMCQMarksDML)
        ){

            getMCQMarksUpdate.setInt(1, submissionId);

            try(ResultSet rs = getMCQMarksUpdate.executeQuery()){
                rs.next();
                return new int[]{rs.getInt(2), rs.getInt(3)};
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Vector<EssayQuestion> getEssayAnswers(int submissionId){
        Vector<EssayQuestion> questions = new Vector<>();
        String getEssayAnswersDML = """
                    select q.QuestionId, q.QuestionText, ea.AnswerText
                    from Questions q left join essayAnswers ea
                    on q.questionId = ea.Question
                    where q.QuestionType = "essay" and ea.submission = ?;
                    """;
        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(getEssayAnswersDML)
        ){

            ps.setInt(1, submissionId);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    EssayQuestion question = new EssayQuestion(
                            rs.getInt("QuestionId"),
                            rs.getString("QuestionText")
                    );
                    question.studentAnswer = rs.getString("AnswerText");
                    questions.add(question);
                }
            }

        }catch(SQLException e){
            System.out.println("Error" + e.getMessage());
        }
        return questions;
    }

    public static Vector<Quiz> getQuizzesByStudent(int studentId){
        Vector<Quiz> quizzes = new Vector<>();
        String quizzesDML = """
                select * from Quizzes where QuizId in
                (select Quiz from classes_quizzes where class in
                (select Class from Students_Classes where student = ?))
                """;
        try(
                Connection connection = getConnection();
                PreparedStatement quizzesUpdate = connection.prepareStatement(quizzesDML)
        ){

            quizzesUpdate.setInt(1, studentId);

            try(ResultSet resultSet = quizzesUpdate.executeQuery()){
                while(resultSet.next())
                    quizzes.add(readQuiz(resultSet));

            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return quizzes;
    }

    public static boolean studentCompleted(int quizId, int studentId){
        String getCompletionStatusDML = """
                    select submissionId from submissions
                    where student = ? and quiz = ?
                    limit 1;
                """;
        try(
                Connection connection = getConnection();
                PreparedStatement getCompletionStatusQuery = connection.prepareStatement(getCompletionStatusDML)
        ){

            getCompletionStatusQuery.setInt(1, studentId);
            getCompletionStatusQuery.setInt(2, quizId);

            try(ResultSet resultSet = getCompletionStatusQuery.executeQuery()){
                return resultSet.next();
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static void getQuizzesByClass(int classId, int studentId, Vector<Quiz> quizzes, Vector<Submission> submissions){
        String getQuizzesDML = """
                SELECT
                    q.quizId,
                    q.Title,
                    q.StartDateTime,
                    q.EndDateTime,
                    q.instructor,
                    s.submissionId,
                    s.timeSubmitted,
                    s.StudentMark,
                    q.FullMark
                FROM
                    quizzes q
                LEFT JOIN
                    submissions s ON q.quizId = s.quiz AND s.student = ?
                INNER JOIN
                    classes_quizzes cq ON q.quizId = cq.quiz
                WHERE
                    cq.class = ?;
                    """;
        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(getQuizzesDML)
        ){

            ps.setInt(1, studentId);
            ps.setInt(2, classId);

            try(ResultSet resultSet = ps.executeQuery()){
                while(resultSet.next()) {
                    quizzes.add(readQuiz(resultSet));
                    resultSet.getInt("submissionId");
                    if(resultSet.wasNull()){
                        submissions.add(null);
                        continue;
                    }
                    submissions.add(readSubmission(resultSet));
                }
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static int saveSubmission(Vector<Question> questions, int studentId, int quizId){
        int submissionId;
        String submissionDML = """
                insert into Submissions (TimeSubmitted, quiz, student)
                values(?, ?, ?)
                """;
        String MCAnswersDML = """
                insert into MCAnswers (submission, selectedAnswer, Question)
                values(?, ?, ?)
                """;
        String essayAnswersDML = """
                insert into EssayAnswers (submission, AnswerText, Question)
                values(?, ?, ?)
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement submissionUpdate = connection.prepareStatement(submissionDML, Statement.RETURN_GENERATED_KEYS)
        ){

            submissionUpdate.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            submissionUpdate.setInt(2, quizId);
            submissionUpdate.setInt(3, studentId);
            submissionUpdate.executeUpdate();

            try(ResultSet rs = submissionUpdate.getGeneratedKeys()){
                if(!rs.next())
                    throw new RuntimeException("no keys");

                submissionId = rs.getInt(1);
                PreparedStatement MCUpdate = connection.prepareStatement(MCAnswersDML);
                PreparedStatement essayUpdate = connection.prepareStatement(essayAnswersDML);

                for(Question question: questions){
                    if(question instanceof MCQuestion mcq){
                        MCUpdate.setInt(1, submissionId);
                        MCUpdate.setInt(2, mcq.selectedAnswer);
                        MCUpdate.setInt(3, mcq.id);
                        MCUpdate.addBatch();
                    } else if(question instanceof EssayQuestion eq){
                        essayUpdate.setInt(1, submissionId);
                        essayUpdate.setString(2, eq.studentAnswer);
                        essayUpdate.setInt(3, eq.id);
                        essayUpdate.addBatch();
                    }
                }
                MCUpdate.executeBatch();
                essayUpdate.executeBatch();
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return submissionId;
    }

    public static Vector<Question> getQuizQuestions(Quiz quiz){
        Vector<Question> questions = new Vector<>();
        String getQuestionsDML = """
                    Select q.QuestionId, q.QuestionType, q.QuestionOrder, q.QuestionText,
                            mcq.AnswerChoice0, mcq.AnswerChoice1, mcq.AnswerChoice2, mcq.AnswerChoice3
                    from quizzes qz left join Questions q on qz.quizId = q.quiz
                    left join MCQuestions mcq on q.QuestionId = mcq.question
                    where qz.quizID = ?
                    order by q.QuestionOrder;
                    """;

        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(getQuestionsDML)
        ){
            ps.setInt(1, quiz.id);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    if(rs.getString("QuestionType").equalsIgnoreCase("MCQ")){
                        questions.add(new MCQuestion(
                                rs.getInt("QuestionId"),
                                rs.getString("QuestionText"),
                                rs.getString("AnswerChoice0"),
                                rs.getString("AnswerChoice1"),
                                rs.getString("AnswerChoice2"),
                                rs.getString("AnswerChoice3")));

                    }else if(rs.getString("QuestionType").equalsIgnoreCase("essay")){
                        questions.add(new EssayQuestion(
                                rs.getInt("QuestionId"),
                                rs.getString("QuestionText")));
                    }

                }
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return questions;
    }

    public static Vector<Question> getStudentAnswers(int submissionId){
        Vector<Question> questions = new Vector<>();
        String getAnswersDML = """
                    select q.questionId, q.questionType, q.questionText,
                    mcq.AnswerChoice0, mcq.AnswerChoice1, mcq.AnswerChoice2, mcq.AnswerChoice3,
                    mcq.correctAnswer, mca.selectedAnswer, ea.answerText, ea.grade, ea.justification
                    from questions q left join MCQuestions mcq on q.questionId = mcq.question
                    left join MCAnswers mca on q.QuestionId = mca.question
                    left join EssayAnswers ea on q.QuestionId = ea.Question
                    where mca.submission = ? or ea.submission = ?
                    """;
        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(getAnswersDML)
        ){

            ps.setInt(1, submissionId);
            ps.setInt(2, submissionId);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    if(rs.getString("questionType").equalsIgnoreCase("MCQ")){
                        MCQuestion question = readMCQuestion(rs);
                        question.correctAnswerIndex = rs.getInt("correctAnswer");
                        question.selectedAnswer = rs.getInt("selectedAnswer");
                        questions.add(question);
                    } else if(rs.getString("questionType").equalsIgnoreCase("Essay")){
                        EssayQuestion question = readEssayQuestion(rs);
                        question.grade = rs.getString("Grade");
                        question.gradeJustification = rs.getString("Justification");
                        question.studentAnswer = rs.getString("AnswerText");
                        questions.add(question);
                    }
                }
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return questions;
    }

    public static String getEmbeddingsBySubmission(int submissionId){
        String getEmbeddingsDML = """
                    select e.embeddings
                    from submissions s left join quizzes q
                    on s.quiz = q.quizId
                    left join embeddings e
                    on q.embeddings = e.hash
                    where submissionId = ?;
                    """;

        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(getEmbeddingsDML)
        ){
            ps.setInt(1, submissionId);

            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getString("Embeddings");
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null; // todo: create a better exception handling system: if the execution reached here, the program fails.
    }

    public static void storeEmbeddings(InMemoryEmbeddingStore<TextSegment> embeddingStore, String hash){
        String storeEmbeddingsDML = " insert into embeddings (hash, embeddings) value(?, ?)";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(storeEmbeddingsDML)
        ){

            preparedStatement.setString(1, hash);
            preparedStatement.setObject(2, embeddingStore.serializeToJson());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    public static InMemoryEmbeddingStore<TextSegment> retrieveEmbeddings(String hash){
        String getEmbeddingsDML = "select embeddings from embeddings where hash = ?";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getEmbeddingsDML)
        ){

            preparedStatement.setString(1, hash);

            try(ResultSet rs = preparedStatement.executeQuery()){
                if(rs.next())
                    return InMemoryEmbeddingStore.fromJson(rs.getString(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Embeddings retrieval from database failed.");
        }
        return null;
    }

    public static void UpdateEssayGrades(Vector<EssayQuestion> questions, int submissionId){
        String setEssayGradesDML = """
                    update EssayAnswers
                    set grade = ?, justification = ?
                    where question = ? and submission = ?
                    """;
        try (
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(setEssayGradesDML)
        ){

            for(EssayQuestion question: questions){
                ps.setString(1, question.grade);
                ps.setString(2, question.gradeJustification);
                ps.setInt(3, question.id);
                ps.setInt(4, submissionId);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    public static void gradeSubmission(int submissionId, int studentMark){
        String gradeSubmissionDML = """
                    update submissions
                    set studentMark = ?
                    where submissionId = ?
                    """;

        try (
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(gradeSubmissionDML)
        ){

            ps.setInt(1, studentMark);
            ps.setInt(2, submissionId);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    public static void addPointsToStudent(int studentId, int amount){
        String addPointsDML = """
                    update Students
                    set points = points + ?
                    where studentId = ?;
                    """;
        try (
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(addPointsDML)
        ){

            ps.setInt(1, amount);
            ps.setInt(2, studentId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    public static void updateLastLogin(int studentId, java.util.Date newLastLogin){
        String lastLoginDML = """
                    update Students
                    set lastLogin = ?
                    where studentId = ?
                    """;
        try (
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(lastLoginDML)
        ){

            ps.setTimestamp(1, new Timestamp(newLastLogin.getTime()));
            ps.setInt(2, studentId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    public static void updateEmailAndPhone(User user, String newEmail, String newPhone){
        String InstructorUpdateDML = """
                update Instructors
                set email = ?,
                phoneNumber = ?
                where InstructorId = ?
                """;
        String StudentUpdateDML = """
                update students
                set email = ?,
                phoneNumber = ?
                where StudentId = ?
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement emailAndPhoneUpdate = user instanceof Instructor ?
                        connection.prepareStatement(InstructorUpdateDML):
                        connection.prepareStatement(StudentUpdateDML)
        ){

            emailAndPhoneUpdate.setString(1, newEmail);
            emailAndPhoneUpdate.setString(2, newPhone);
            emailAndPhoneUpdate.setInt(3, user.id);

            emailAndPhoneUpdate.executeUpdate();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void updateUserPassword(User user, String password){
        String InstructorUpdateDML = """
                update Instructors
                set password = ?
                where InstructorId = ?
                """;
        String StudentUpdateDML = """
                update students
                set password = ?
                where StudentId = ?
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement emailAndPhoneUpdate = user instanceof Instructor ?
                        connection.prepareStatement(InstructorUpdateDML):
                        connection.prepareStatement(StudentUpdateDML)
        ){

            emailAndPhoneUpdate.setString(1, password);
            emailAndPhoneUpdate.setInt(2, user.id);

            emailAndPhoneUpdate.executeUpdate();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void getAllStudentSubmissions(int studentId, Vector<Quiz> quizzes, Vector<Submission> submissions){
        String getSubmissionsDML = """
                select q.quizId, q.Title, q.StartDateTime, q.EndDateTime, q.instructor,
                s.submissionId, s.timeSubmitted, s.StudentMark, q.FullMark
                from submissions s left Join Quizzes q
                on s.quiz = q.quizId
                where s.student = ?
                order by s.timeSubmitted desc
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(getSubmissionsDML)
        ){

            ps.setInt(1, studentId);

            try(ResultSet resultSet = ps.executeQuery()){
                while(resultSet.next()) {
                    quizzes.add(readQuiz(resultSet));
                    resultSet.getInt("submissionId");
                    if(resultSet.wasNull()){
                        submissions.add(null);
                        continue;
                    }
                    submissions.add(readSubmission(resultSet));
                }
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void getAllStudentSubmissionsInClass(int studentId, int classId, Vector<Quiz> quizzes, Vector<Submission> submissions){
        String getSubmissionsDML = """
                select q.quizId, q.Title, q.StartDateTime, q.EndDateTime, q.instructor,
                s.submissionId, s.timeSubmitted, s.StudentMark, q.FullMark
                from submissions s
                left Join Quizzes q on s.quiz = q.quizId
                left Join classes_quizzes cq on cq.quiz = q.quizId
                where s.student = ? and class = ?
                order by s.timeSubmitted desc
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(getSubmissionsDML)
        ){

            ps.setInt(1, studentId);
            ps.setInt(2, classId);

            try(ResultSet resultSet = ps.executeQuery()){
                while(resultSet.next()) {
                    quizzes.add(readQuiz(resultSet));
                    resultSet.getInt("submissionId");
                    if(resultSet.wasNull()){
                        submissions.add(null);
                        continue;
                    }
                    submissions.add(readSubmission(resultSet));
                }
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static Vector<Quiz> getStudentQuizzesInClass(int studentId, int classId){
        Vector<Quiz> quizzes = new Vector<>();
        String getSubmissionsDML = """
                select q.quizId, q.Title, q.StartDateTime, q.EndDateTime, q.instructor
                from Quizzes q right join classes_quizzes cq on q.quizId = cq.Quiz
                where now() between q.StartDateTime and Q.EndDateTime
                and q.quizId not in
                (Select quiz from Submissions
                where student = ?)
                and cq.class = ?
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(getSubmissionsDML)
        ){

            ps.setInt(1, studentId);
            ps.setInt(2, classId);

            try(ResultSet resultSet = ps.executeQuery()){
                while(resultSet.next())
                    quizzes.add(readQuiz(resultSet));
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return quizzes;
    }

    public static String qenerateTranscript(int studentId){
        StringBuilder str = new StringBuilder("""
                --------------------------------------------------------------------------------------------------------
                                                            Student Transcript
                --------------------------------------------------------------------------------------------------------
                                                            Student Information:
                
                """);
        String getStudentInfoDML = """
                select FirstName, Lastname, StudentId, Points, Email, PhoneNumber
                from Students where studentId = ?
                """;
        String generalQuizInfoDML = """
                select count(submissionId) as numOfQuizzes,
                avg(studentMark/fullMark) * 100 as avgGrade
                from submissions join quizzes
                on quizzes.QuizID = submissions.quiz
                where student = ?;
                """;
        String last5QuizzesDML = """
                select Title, studentMark, fullMark
                from submissions left join quizzes
                on submissions.quiz = quizzes.QuizID
                where student = ?
                order by timeSubmitted desc
                limit 5;
                """;
        String numOfClassesDML = """
                select count(*) as numOfClasses
                from students_classes
                where student = ?;
                """;
        String classesDML = """
                select c.ClassId, c.ClassName, i.firstName, i.LastName, i.email
                from Students_Classes sc left join classes c
                on sc.class = c.classId
                left join instructors i
                on c.Instructor = i.instructorId
                where sc.student = ?
                limit 10;
                """;

        try(
                Connection connection = getConnection();
                PreparedStatement studentInfoStatement = connection.prepareStatement(getStudentInfoDML);
                PreparedStatement quizInfoStatement = connection.prepareStatement(generalQuizInfoDML);
                PreparedStatement last5QuizzesStatement = connection.prepareStatement(last5QuizzesDML);
                PreparedStatement numOfClassesStatement = connection.prepareStatement(numOfClassesDML);
                PreparedStatement classesStatement = connection.prepareStatement(classesDML);
        ){

            studentInfoStatement.setInt(1, studentId);
            try(ResultSet resultSet = studentInfoStatement.executeQuery()){
                if(!resultSet.next()) return "";
                str.append(String.format("""
                        Student Name:  %s %s
                        StudentID:  %d
                        Points:  %d
                        Email Address:  %s
                        PhoneNumber:  %s
                       --------------------------------------------------------------------------------------------------------
                        """, resultSet.getString("FirstName"), resultSet.getString("LastName"),
                        resultSet.getInt("StudentID"),  resultSet.getInt("Points"),
                        resultSet.getString("Email"), resultSet.getString("PhoneNumber")));
            }

            quizInfoStatement.setInt(1, studentId);
            try(ResultSet resultSet = quizInfoStatement.executeQuery()){
                if(!resultSet.next()) return "";
                str.append(String.format("""
                         Number of quizzes taken:  %d
                         Average Quiz Performance:  %.2f
                        
                        
                         """, resultSet.getInt("numOfQuizzes"), resultSet.getDouble("avgGrade")));
            }

            last5QuizzesStatement.setInt(1, studentId);
            try(ResultSet resultSet = last5QuizzesStatement.executeQuery()){
                str.append(String.format("""
                        
                        --------------------------------------------------------------------------------------------------------
                         %-32s| %-12S | %s
                        --------------------------------------------------------------------------------------------------------
                         """, "Quiz Title", "Student Mark", "Full Mark"));
                while(resultSet.next()){
                    str.append(String.format("""
                         %-32s| %-12d | %d
                        """, resultSet.getString("Title"), resultSet.getInt("StudentMark"),
                            resultSet.getInt("FullMark")));
                }
                str.append("--------------------------------------------------------------------------------------------------------\n");
            }

            numOfClassesStatement.setInt(1, studentId);
            try(ResultSet resultSet = numOfClassesStatement.executeQuery()){
                if(!resultSet.next()) return "";
                str.append(String.format("\n\nNumber of Student's Classes: %d\n", resultSet.getInt("numOfClasses")));
            }

            classesStatement.setInt(1, studentId);
            try(ResultSet resultSet = classesStatement.executeQuery()){
                str.append(String.format("""
                        --------------------------------------------------------------------------------------------------------
                         %-4s | %-32s | %-32s | %S
                        --------------------------------------------------------------------------------------------------------
                         """, "ID", "Class Name", "Instructor Name", "Instructor Email"));
                while(resultSet.next()){
                    str.append(String.format("""
                          %-4d | %-32s | %-32s | %s
                         """, resultSet.getInt("ClassID"), resultSet.getString("ClassName"),
                            resultSet.getString("FirstName") + " " + resultSet.getString("LastName"),
                            resultSet.getString("Email")));
                }
                str.append("--------------------------------------------------------------------------------------------------------\n");
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return str.toString();
    }

    public static List<Trophy> getTrophiesForStudentInClass(int studentId, int classId) {
        List<Trophy> trophies = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM Trophies WHERE student = ? AND class = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            stmt.setInt(2, classId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                trophies.add(new Trophy(
                        rs.getInt("class"),
                        rs.getInt("student"),
                        rs.getString("imagepath"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trophies;
    }

    public static void insertTrophy(int classId, int studentId, String imagePath, String description) {
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO Trophies (class, student, imagepath, description) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, classId);
            stmt.setInt(2, studentId);
            stmt.setString(3, imagePath);
            stmt.setString(4, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Trophy> getAllTrophiesForStudent(int studentId) {
        List<Trophy> trophies = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM Trophies WHERE student = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                trophies.add(new Trophy(
                        rs.getInt("class"),
                        rs.getInt("student"),
                        rs.getString("imagepath"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trophies;
    }

    public static void sendFeedback(int instructorId, int studentId, String subjectLine, String messageText) {
        String query = "INSERT INTO Feedback (Instructor, Student, SubjectLine, MessageText, DateTimeSent) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, instructorId);
            stmt.setInt(2, studentId);
            stmt.setString(3, subjectLine);
            stmt.setString(4, messageText);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Feedback> getFeedbackForStudent(int studentId) {
        List<Feedback> feedbackList = new ArrayList<>();
        String query = "SELECT * FROM Feedback WHERE Student = ? ORDER BY DateTimeSent DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getInt("Instructor"),
                        rs.getInt("Student"),
                        rs.getString("SubjectLine"),
                        rs.getString("MessageText"),
                        rs.getTimestamp("DateTimeSent")
                );
                feedbackList.add(feedback);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    public static List<Feedback> getFeedbackForStudentInClass(int studentId, int instructorId) {
        List<Feedback> feedbackList = new ArrayList<>();
        String query = """
                SELECT * FROM Feedback
                WHERE Student = ? and instructor = ?
                ORDER BY DateTimeSent DESC
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getInt("Instructor"),
                        rs.getInt("Student"),
                        rs.getString("SubjectLine"),
                        rs.getString("MessageText"),
                        rs.getTimestamp("DateTimeSent")
                );
                feedbackList.add(feedback);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    public static List<IndividualQuizStatistics> getMCQSAnswersPerQuiz(int quizNumber){
        List<IndividualQuizStatistics> statistics = new ArrayList<>();
        String getMCQSAnswersPerQuizDML = """
                select q.questionText, q.questionType,
                AVG(case when mcq.correctAnswer = ma.selectedAnswer then 1 else 0 end) as CorrectPercent
                from MCAnswers ma left join Questions q
                left join mcquestions mcq on mcq.question = q.Questionid
                on ma.question = q.questionid
                left join Quizzes qz on q.quiz = qz.quizId
                where quiz = ?
                group by questionId;
                    """;
        try(
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(getMCQSAnswersPerQuizDML)
        ){

            ps.setInt(1, quizNumber);


            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) {
                    IndividualQuizStatistics stat = new IndividualQuizStatistics(
                            rs.getString("questionType"),
                            rs.getString("questionText"),
                            rs.getDouble("CorrectPercent")
                    );
                    statistics.add(stat);
                }
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return statistics;
    }


    public static List<IndividualQuizStatistics> getEssayAnswersPerQuiz(int quizNumber) {
        List<IndividualQuizStatistics> statistics = new ArrayList<>();
        String sql = """
        SELECT q.QuestionText, q.questionType,
               AVG(CASE WHEN ea.grade = '<full credit>' THEN 1 ELSE 0 END) AS FullCredit,
               AVG(CASE WHEN ea.grade = '<partial credit>' THEN 1 ELSE 0 END) AS PartialCredit,
               AVG(CASE WHEN ea.grade = '<no credit>' THEN 1 ELSE 0 END) AS NoCredit
        FROM essayquestions eq
        LEFT JOIN Questions q on eq.question = q.QuestionId
        LEFT JOIN essayanswers ea ON ea.question = q.questionId
        WHERE q.quiz = ?
        GROUP BY q.Questionid, q.QuestionText, q.questionType;
        """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, quizNumber);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    IndividualQuizStatistics stat = new IndividualQuizStatistics(
                            rs.getString("questionType"),
                            rs.getString("QuestionText"),
                            rs.getDouble("FullCredit"),
                            rs.getDouble("PartialCredit"),
                            rs.getDouble("NoCredit")
                    );
                    statistics.add(stat);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving essay answers: " + e.getMessage());
            e.printStackTrace();
        }
        return statistics;
    }

public static List<Quiz> getOverallQuizStatistics(int classId) {
        List<Quiz> statistics = new ArrayList<>();
        String sql = """
                SELECT q.QuizID, q.Title, COUNT(s.SubmissionId) count, AVG(s.studentmark) as avg, MAX(s.studentmark) as max, MIN(s.studentmark) as min
                FROM quizzes q
                JOIN classes_quizzes cq ON cq.Quiz = q.QuizID
                LEFT JOIN submissions s ON s.quiz = q.QuizID
                where q.quizId in (select quiz from classes_quizzes where class = ?)
                GROUP BY q.QuizID
                """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = new Quiz(rs.getInt("QuizID"));
                    quiz.title = rs.getString("Title");
                    quiz.average = rs.getDouble("avg");
                    quiz.count = rs.getInt("avg");
                    quiz.min = rs.getInt("avg");
                    quiz.max = rs.getInt("avg");
                    statistics.add(quiz);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving statistics: " + e.getMessage());
            e.printStackTrace();
        }
        return statistics;
    }
}
