package org.Program;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.Program.Entities.*;
import org.Program.Entities.Class;

import java.sql.*;
import java.util.Arrays;
import java.util.Objects;
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

    public static String addQuiz(String title, java.util.Date startTime, java.util.Date endTime, Vector<Class> classes,
                                 Vector<Question> questions, int instructorId, String embeddingsHash){
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
            ps = connection.prepareStatement("""
                insert into Quizzes (title, startDateTime, endDateTime, Instructor, embeddings)
                value(?, ?, ?, ?, ?);
                """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setTimestamp(2, new Timestamp(startTime.getTime()));
            ps.setTimestamp(3, new Timestamp(endTime.getTime()));
            ps.setInt(4, instructorId);
            ps.setString(5, embeddingsHash);
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

    /**
     *  there are 2 types of questions that we need to store, which makes updating the database a little tricky:
     *
     *  In the database there are 3 relations(tables): Questions, MCQuestions, and EssayQuestions.
     *  Questions is the supertype, and the other 2 are its subtypes.
     *  the relationship is a total, disjoint relationship, we learned this in the database class(lecture 6)
     *
     *  The Questions table stores the shared information between question types: ID, Question text, type, and quizId.
     *  The MCQuestions table stores the unique information of the MCQs: the 4 choices, and the index of the correct choice.
     *  The EssayQuestions table store the unique information of the Essay Questions: (no unique information as of now)
     *
     *  To add the questions to the database we first iterate over the questions and add the relevant information to the Questions table.
     *  Since the Ids are automatically assigned to questions by the database, we use the .getGeneratedKeys() function to retrieve the IDs.
     *  we then add the rest of the information of both question types separately, connecting them to the questions supertype using the IDs we retrieved.
     */
    public static void addQuestions(Vector<Question> questions, int quizId){
        try{
            String SQL_Question_Update = """
                    insert into Questions (QuestionType, QuestionOrder, QuestionText, quiz)
                    Values(?, ?, ?, ?)
                    """;
            String SQL_MCQ_Update = """
                    insert into MCQuestions (Question, AnswerChoice0, AnswerChoice1, AnswerChoice2, AnswerChoice3, CorrectAnswer)
                    values(?, ?, ?, ?, ?, ?)
                    """;
            String SQL_EQ_Update = """
                    Insert into EssayQuestions (Question)
                    Values(?)
                    """;

            int count = 1;
            // RETURN_GENERATED_KEYS is to retrieve the ID that were automatically assigned to the questions.
            PreparedStatement ps = connection.prepareStatement(SQL_Question_Update, Statement.RETURN_GENERATED_KEYS);

            for(Question question: questions){
                ps.setString(1, question instanceof MCQuestion ? "MCQ" : "Essay"); // store the type of the question.
                ps.setInt(2, count++); // <- The order of the questions in the quiz
                ps.setString(3, question.questionText);
                ps.setInt(4, quizId);
                ps.addBatch();
            }
            ps.executeBatch();

            ResultSet resultSet = ps.getGeneratedKeys(); // get the auto generated IDs
            Vector<Integer> questionIds = new Vector<>(questions.size()); // create a vector to store the IDs
            while(resultSet.next())
                questionIds.add(resultSet.getInt(1)); // iterate over the result set and store the IDs in the vector

            PreparedStatement MCQUpdates = connection.prepareStatement(SQL_MCQ_Update);
            PreparedStatement essayUpdates = connection.prepareStatement(SQL_EQ_Update);

            count = 0;
            for(Question question: questions){
                // if the question type is MCQ add its information to the batch of MCQ Updates to execute them later all at once.
                if(question instanceof MCQuestion MCQ) {

                    MCQUpdates.setInt(1, questionIds.get(count++));
                    for (int i = 0; i < 4; i++)
                        MCQUpdates.setString(i + 2, MCQ.answerChoices.get(i));
                    MCQUpdates.setInt(6, MCQ.correctAnswerIndex);
                    MCQUpdates.addBatch();

                } else if(question instanceof EssayQuestion EQ){ // if Essay question add its information to its relevant batch.
                    essayUpdates.setInt(1, questionIds.get(count++));
                    essayUpdates.addBatch();
                    // the essay questions don't have any unique information, but I included this just in case.
                }
            }

            MCQUpdates.executeBatch();
            essayUpdates.executeBatch();

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static int[] getMCQMarks(int submissionId){
        try{
            PreparedStatement ps = connection.prepareStatement("""
                    SELECT
                        s.SubmissionId,
                        SUM(CASE WHEN mcq.CorrectAnswer = mca.SelectedAnswer THEN 1 ELSE 0 END) AS correctAnswers,
                        COUNT(mca.Question) AS numOfQuestions
                    FROM Submissions s
                    JOIN MCAnswers mca ON mca.Submission = s.SubmissionId
                    JOIN MCQuestions mcq ON mcq.Question = mca.Question
                    WHERE s.SubmissionId = ?
                    """);
            ps.setInt(1, submissionId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return new int[]{rs.getInt(2), rs.getInt(3)};

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Vector<EssayQuestion> getEssayAnswers(int submissionId){
        Vector<EssayQuestion> questions = new Vector<>();
        try{
            PreparedStatement ps = connection.prepareStatement("""
                    select q.QuestionId, q.QuestionText, ea.AnswerText
                    from Questions q left join essayAnswers ea
                    on q.questionId = ea.Question
                    where q.QuestionType = "essay" and ea.submission = ?;
                    """);
            ps.setInt(1, submissionId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                EssayQuestion question = new EssayQuestion(
                        rs.getInt("QuestionId"),
                        rs.getString("QuestionText")
                );
                question.studentAnswer = rs.getString("AnswerText");
                questions.add(question);
            }

        }catch(SQLException e){
            System.out.println("Error" + e.getMessage());
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
                    select submissionId from submissions
                    where student = ? and quiz = ?
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

    public static void getQuizzesByClass(int classId, int studentId, Vector<Quiz> quizzes, Vector<Submission> submissions){
        try{
            PreparedStatement ps = connection.prepareStatement("""
                    select q.quizId, q.Title, q.StartDateTime, q.EndDateTime, q.instructor,
                    s.submissionId, s.timeSubmitted, s.StudentMark, s.FullMark
                    from quizzes q left join submissions s
                    on q.quizId = s.quiz and s.student = ?
                    where q.quizID in
                    (select quizId from classes_quizzes where class = ?)
                    """);
            ps.setInt(1, studentId);
            ps.setInt(2, classId);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()) {
                quizzes.add(readQuiz(resultSet));
                resultSet.getInt("submissionId");
                if(resultSet.wasNull()){
                    submissions.add(null);
                    continue;
                }
                submissions.add(readSubmission(resultSet));
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static int saveSubmission(Vector<Question> questions, int studentId, int quizId){
        int submissionId = -1;
        String submissionUpdate = """
                insert into Submissions (TimeSubmitted, quiz, student)
                values(?, ?, ?)
                """;
        String MCAnswersUpdate = """
                insert into MCAnswers (submission, selectedAnswer, Question)
                values(?, ?, ?)
                """;
        String essayAnswersUpdate = """
                insert into EssayAnswers (submission, AnswerText, Question)
                values(?, ?, ?)
                """;

        try{
            PreparedStatement ps = connection.prepareStatement(submissionUpdate, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, quizId);
            ps.setInt(3, studentId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(!rs.next())
                throw new RuntimeException("no keys");

            submissionId = rs.getInt(1);
            PreparedStatement MCUpdate = connection.prepareStatement(MCAnswersUpdate);
            PreparedStatement essayUpdate = connection.prepareStatement(essayAnswersUpdate);

            for(Question question: questions){
                if(question instanceof MCQuestion mcq){
                    MCUpdate.setInt(1, submissionId);
                    MCUpdate.setInt(2, mcq.selectedAnswer);
                    MCUpdate.setInt(3, mcq.id);
                    MCUpdate.addBatch();
                } else if(question instanceof EssayQuestion eq){;
                    essayUpdate.setInt(1, submissionId);
                    essayUpdate.setString(2, eq.studentAnswer);
                    essayUpdate.setInt(3, eq.id);
                    essayUpdate.addBatch();
                }
            }
            MCUpdate.executeBatch();
            essayUpdate.executeBatch();

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return submissionId;
    }

    public static Vector<Question> getQuizQuestions(Quiz quiz){
        Vector<Question> questions = new Vector<>();
        try{
            PreparedStatement ps = connection.prepareStatement("""
                    Select q.QuestionId, q.QuestionType, q.QuestionOrder, q.QuestionText,
                            mcq.AnswerChoice0, mcq.AnswerChoice1, mcq.AnswerChoice2, mcq.AnswerChoice3
                    from quizzes qz left join Questions q on qz.quizId = q.quiz
                    left join MCQuestions mcq on q.QuestionId = mcq.question
                    where qz.quizID = ?
                    order by q.QuestionOrder;
                    """);
            ps.setInt(1, quiz.id);
            ResultSet rs = ps.executeQuery();

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

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return questions;
    }

    public static Vector<Question> getStudentAnswers(int submissionId){
        Vector<Question> questions = new Vector<>();
        try{
            PreparedStatement ps = connection.prepareStatement("""
                    select q.questionId, q.questionType, q.questionText,
                    mcq.AnswerChoice0, mcq.AnswerChoice1, mcq.AnswerChoice2, mcq.AnswerChoice3,
                    mcq.correctAnswer, mca.selectedAnswer, ea.answerText, ea.grade, ea.justification
                    from questions q left join MCQuestions mcq on q.questionId = mcq.question
                    left join MCAnswers mca on q.QuestionId = mca.question
                    left join EssayAnswers ea on q.QuestionId = ea.Question
                    where mca.submission = ? or ea.submission = ?
                    """);
            ps.setInt(1, submissionId);
            ps.setInt(2, submissionId);

            ResultSet rs = ps.executeQuery();
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
        }catch(SQLException e){
            System.out.println(e);
        }
        return questions;
    }

    public static String getEmbeddingsBySubmission(int submissionId){
        try{
            PreparedStatement ps = connection.prepareStatement("""
                    select e.embeddings
                    from submissions s left join quizzes q
                    on s.quiz = q.quizId
                    left join embeddings e
                    on q.embeddings = e.hash
                    where submissionId = ?;
                    """);
            ps.setInt(1, submissionId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getString("Embeddings");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null; // todo: create a better exception handling system: if the execution reached here, the program fails.
    }

    public static void storeEmbeddings(InMemoryEmbeddingStore<TextSegment> embeddingStore, String hash){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    insert into embeddings (hash, embeddings) value(?, ?);
                    """);
            preparedStatement.setString(1, hash);
            preparedStatement.setObject(2, embeddingStore.serializeToJson());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    public static InMemoryEmbeddingStore<TextSegment> retrieveEmbeddings(String hash){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select embeddings from embeddings where hash = ?");
            preparedStatement.setString(1, hash);
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()){
                return InMemoryEmbeddingStore.fromJson(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
        return null;
    }

    public static void UpdateEssayGrades(Vector<EssayQuestion> questions, int submissionId){
        try {
            PreparedStatement ps = connection.prepareStatement("""
                    update EssayAnswers set grade = ?, justification = ?
                    where question = ? and submission = ?
                    """);

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

    public static void gradeSubmission(int submissionId, int[] marks){
        try {
            PreparedStatement ps = connection.prepareStatement("""
                    update submissions
                    set studentMark = ?, fullMark = ?
                    where submissionId = ?
                    """);

            ps.setInt(1, marks[0]);
            ps.setInt(2, marks[1]);
            ps.setInt(3, submissionId);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    public static void addPointsToStudent(int studentId, int amount){
        try {
            PreparedStatement ps = connection.prepareStatement("""
                    update Students
                    set points = points + "?
                    where studentId = ?;
                    """);

            ps.setInt(1, amount);
            ps.setInt(2, studentId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    public static void updateLastLogin(int studentId, java.util.Date newLastLogin){
        try {
            PreparedStatement ps = connection.prepareStatement("""
                    update Students
                    set lastLogin = ?
                    where studentId = ?
                    """);

            ps.setTimestamp(1, new Timestamp(newLastLogin.getTime()));
            ps.setInt(2, studentId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }
}
