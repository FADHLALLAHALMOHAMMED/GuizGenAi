package org.Program;

import org.Program.Entities.*;
import org.Program.Entities.Class;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 *      This file is for implementing pages. Each page is a class that extends the Page class which extends JFrame.
 *      Pages are created when needed and are added to the Window object(which is just a JFrame).
 *      Afterwards, when the user switches to another page, the page is disposed of, garbage collected, and the new
 *      page is created and added to the Window.
 *      Pages can use the attribute of the window object to get relevant information for loading the page, such as the current user.
 * **/

public abstract class Page extends JPanel implements ActionListener {


    Window window;
    public static Color APP_BACKGROUND = new Color(118, 39, 255);
    public static Color TEXT_FOREGROUND = new Color(255, 255, 255);
    private ArrayList<JPanel> panelsList = new ArrayList<>();

    Page(Window window) {

        this.window = window;
        this.setBackground(APP_BACKGROUND);
        // this.setForeground(APP_BACKGROUND);
    }


    public void updateAllPanels() {

        for (JPanel panel: panelsList) {
            panel.setBackground(Page.APP_BACKGROUND);


            for (Component comp: panel.getComponents()) {

                if (comp instanceof JRadioButton) {
                    comp.setBackground(APP_BACKGROUND);
                    comp.setForeground(TEXT_FOREGROUND);
                }
            }
            
        }


    }

    @Override
    public Component add(Component comp) {

        if (comp instanceof JPanel) {
            panelsList.add( (JPanel) comp);
        }

        updateAllPanels();

        //calling super method
        return super.add(comp);
    }

    @Override
    public void add(Component comp, Object constraints) {

        if (comp instanceof JPanel) {
            panelsList.add( (JPanel) comp);
        }

        updateAllPanels();

        //calling super method
        super.add(comp, constraints);
    }

    public void update() {

    }

}


class StartPage extends Page{
    JButton loginButton;
    JButton registerButton;
    StartPage(Window window) {
        super(window);
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(new GridBagLayout());

        JLabel titleLabel = GUI_Elements.label("Welcome to QuizGenAi");
        titleLabel.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 40));
        loginButton = GUI_Elements.button("Login");
        registerButton = GUI_Elements.button("Register");

        loginButton.addActionListener(this);
        registerButton.addActionListener(this);

        gbc.gridy = 0; gbc.gridx = 0;
        
        gbc.insets = new Insets(20, 10, 50, 10);
        this.add(titleLabel, gbc);
        
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridy++;
        gbc.fill = gbc.HORIZONTAL;
        this.add(loginButton, gbc);

        gbc.gridy++;
        gbc.fill = gbc.HORIZONTAL;
        this.add(registerButton, gbc);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == registerButton){
            window.switchPage(new RegisterPage(window));
        } else if(e.getSource() == loginButton){
            window.switchPage((new LoginPage(window)));
        }

    }
}

class RegisterPage extends Page{

    private JLabel firstName = GUI_Elements.label("First name");
    private JLabel lastName = GUI_Elements.label("Last name");
    private JLabel email = GUI_Elements.label("Email address");
    private JLabel newPassword = GUI_Elements.label("New password");
    private JLabel phoneNumber = GUI_Elements.label("Phone number");

    private JTextField firstNameField = GUI_Elements.textField();
    private JTextField lastNameField = GUI_Elements.textField();
    private JTextField emailField = GUI_Elements.textField();
    private JTextField newPasswordField = GUI_Elements.textField();
    private JTextField phoneNumberField = GUI_Elements.textField();

    private JRadioButton instructorRadioButton = GUI_Elements.radioButton("I'm an instructor");
    private JRadioButton studentRadioButton = GUI_Elements.radioButton("I'm a student");
    private JButton createAccountButton = GUI_Elements.button("Create New Account");
    private JButton startPageButton = GUI_Elements.button("Go To Start Page");
    private Icon imageLogo;
    private JLabel logoLabel;


    RegisterPage(Window window) {
        super(window);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;

        // Image Panel -------------------------------------------------------------------
        JPanel imagePanel = new JPanel(new GridBagLayout());

        imageLogo = new ImageIcon(getClass().getResource("images/logo.png"));
        logoLabel = new JLabel(imageLogo);
        
        // Register Panel -------------------------------------------------------------------
        JPanel registerPanel = new JPanel(new GridBagLayout());

        createAccountButton.addActionListener(this);
        startPageButton.addActionListener(this);

        //radioTypePanel
        JPanel radioButtonPanel = GUI_Elements.panel();
        ButtonGroup accountTypeRadioGroup = new ButtonGroup();
        radioButtonPanel.setLayout(new GridBagLayout());
        
        radioButtonPanel.add(instructorRadioButton);
        radioButtonPanel.add(studentRadioButton);

        // empty panel (to organize look)
        JPanel emptyPanel = new JPanel(new GridBagLayout());

        //Grid Manager ------------------------------------------------------------------
        
        //register panel grid
        GridBagConstraints registerConstraints = new GridBagConstraints();
        registerConstraints.gridx = 0; registerConstraints.gridy = 0;
        registerConstraints.weightx = 1.0; registerConstraints.weighty = 0;
        registerConstraints.fill = registerConstraints.HORIZONTAL;
        registerConstraints.insets = new Insets(5, 80, 5, 5);
        
        
        registerConstraints.gridwidth = 2;
        registerPanel.add(firstName, registerConstraints);
        registerConstraints.gridy++;
        registerPanel.add(firstNameField, registerConstraints);
        registerConstraints.gridy++;
        registerPanel.add(lastName, registerConstraints);
        registerConstraints.gridy++;
        registerPanel.add(lastNameField, registerConstraints);
        registerConstraints.gridy++;
        registerPanel.add(email, registerConstraints);
        registerConstraints.gridy++;
        registerPanel.add(emailField, registerConstraints);
        registerConstraints.gridy++;
        registerPanel.add(newPassword, registerConstraints);
        registerConstraints.gridy++;
        registerPanel.add(newPasswordField, registerConstraints);
        registerConstraints.gridy++;
        registerPanel.add(phoneNumber, registerConstraints);
        registerConstraints.gridy++;
        registerPanel.add(phoneNumberField, registerConstraints);


        registerConstraints.gridwidth = 1;
        registerConstraints.insets = new Insets(5, 5, 5, 5);
        registerConstraints.gridy++;
        radioButtonPanel.add(instructorRadioButton, registerConstraints);
        registerConstraints.gridy++;
        radioButtonPanel.add(studentRadioButton, registerConstraints);

        registerConstraints.insets = new Insets(5, 80, 5, 5);
        registerConstraints.gridy++;
        registerPanel.add(radioButtonPanel, registerConstraints);

        registerConstraints.gridy++;
        registerPanel.add(createAccountButton, registerConstraints);
        registerConstraints.gridx++;
        registerConstraints.insets = new Insets(5, 5, 5, 5);
        registerPanel.add(startPageButton, registerConstraints);
        
        //image panel grid
        c.anchor = c.CENTER;
        c.gridx = 0; c.gridy = 0;
        imagePanel.add(logoLabel, c);

        //loginPage grid
        c.gridx = 0; c.gridy = 0;
        c.fill = c.BOTH;
        c.weightx = 7;
        this.add(imagePanel, c);
        c.gridx = 1; c.gridy = 0;
        c.weightx = 1;
        this.add(registerPanel, c);
        c.gridx = 2; c.gridy = 0;
        c.weightx = 7;
        this.add(emptyPanel, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startPageButton) {
            window.switchPage(new StartPage(window));
        }else if(e.getSource() == createAccountButton){

            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String password = newPasswordField.getText();
            String phoneNumber = phoneNumberField.getText();

            String validity = HelperFunctions.ValidateRegistration(firstName, lastName, email, phoneNumber, password);
            if(validity != null){
                JOptionPane.showMessageDialog(window, validity, "Registration Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int accountType = instructorRadioButton.isSelected() ? 0 : 1;
            Database.register(firstName, lastName, email, password, phoneNumber, accountType);
            JOptionPane.showMessageDialog(window,
                    "You account has been created successfully.",
                    "Successful Account Registration",
                    JOptionPane.INFORMATION_MESSAGE);
            window.switchPage(new StartPage(window));
        }
    }
}

class LoginPage extends Page{
    private JButton loginButton = GUI_Elements.button("Log in");
    private JButton backButton = GUI_Elements.button("Go back");
    private JTextField emailTextField = GUI_Elements.textField();
    private JTextField passwordTextField = GUI_Elements.textField();
    private Icon imageLogo;
    private JLabel logoLabel;

    LoginPage(Window window){
        super(window);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;

        // Image Panel -------------------------------------------------------------------
        JPanel imagePanel = new JPanel(new GridBagLayout());

        imageLogo = new ImageIcon(getClass().getResource("images/logo.png"));
        logoLabel = new JLabel(imageLogo);
        
        // Login Panel -------------------------------------------------------------------
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(APP_BACKGROUND);

        JLabel emailLabel = GUI_Elements.label("Email");
        JLabel passwordLabel = GUI_Elements.label("Password");

        loginButton.addActionListener(this);
        backButton.addActionListener(this);

        // empty panel (to organize look)
        JPanel emptyPanel = new JPanel(new GridBagLayout());

        //Grid Manager ------------------------------------------------------------------
        
        //login panel grid
        GridBagConstraints loginConstraints = new GridBagConstraints();
        loginConstraints.gridx = 0; loginConstraints.gridy = 0;
        loginConstraints.weightx = 1.0; loginConstraints.weighty = 0;
        loginConstraints.fill = loginConstraints.HORIZONTAL;
        loginConstraints.insets = new Insets(5, 80, 5, 5);
        
        
        loginConstraints.gridwidth = 2;
        loginPanel.add(emailLabel, loginConstraints);
        loginConstraints.gridy++;
        loginPanel.add(emailTextField, loginConstraints);
        loginConstraints.gridy++;
        loginPanel.add(passwordLabel, loginConstraints);
        loginConstraints.gridy++;
        loginPanel.add(passwordTextField, loginConstraints);

        loginConstraints.gridwidth = 1;
        loginConstraints.insets = new Insets(20, 80, 5, 5);
        loginConstraints.gridy++;
        loginPanel.add(loginButton, loginConstraints);
        loginConstraints.gridx++;
        loginConstraints.insets = new Insets(20, 5, 5, 5);
        loginPanel.add(backButton, loginConstraints);
        
        //image panel grid
        c.anchor = c.CENTER;
        c.gridx = 0; c.gridy = 0;
        imagePanel.add(logoLabel, c);

        //loginPage grid
        c.gridx = 0; c.gridy = 0;
        c.fill = c.BOTH;
        c.weightx = 7;
        this.add(imagePanel, c);
        c.gridx = 1; c.gridy = 0;
        c.weightx = 1;
        this.add(loginPanel, c);
        c.gridx = 2; c.gridy = 0;
        c.weightx = 7;
        this.add(emptyPanel, c);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == loginButton){
            String email = emailTextField.getText();
            String password = passwordTextField.getText();

            User user = Database.login(email, password);
            if(user == null){
                JOptionPane.showMessageDialog(window,
                        "Incorrect Name or Password.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            window.setUser(user);
            if(user instanceof Instructor) window.switchPage(new InstructorHomePage(window));
            if(user instanceof Student) window.switchPage(new StudentHomePage(window));

        } else if(e.getSource() == backButton){
            window.switchPage(new StartPage(window));
        }
    }
}

class InstructorHomePage extends Page{
    JButton newClassButton = GUI_Elements.button("Create New Class");
    JButton generateQuizButton = GUI_Elements.button("Generate New Quiz");
    JButton logoutButton = GUI_Elements.button("Log out");
    InstructorHomePage(Window window){
        super(window);
        this.setLayout(new BorderLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        // labels
        JPanel labelPanel = new JPanel(new GridBagLayout());
        c.gridx = 0; c.gridy = 0;
        JLabel titleLabel = GUI_Elements.label("Instructor Home Page");
        JLabel descriptionLabel = new JLabel(String.format("Welcome, %s %s, to the QuizGenAI Program!",
                window.getUser().firstName, window.getUser().lastName));

        labelPanel.add(titleLabel, c); c.gridy++;
        labelPanel.add(descriptionLabel, c);
        this.add(BorderLayout.NORTH, labelPanel);

        //Buttons
        c.gridx = 0; c.gridy = 0;
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setPreferredSize(new Dimension(400, 300));
        newClassButton.addActionListener(this);
        generateQuizButton.addActionListener(this);
        logoutButton.addActionListener(this);

        buttonPanel.add(newClassButton, c); c.gridy++;
        buttonPanel.add(generateQuizButton, c); c.gridy++;
        buttonPanel.add(logoutButton, c);
        this.add(BorderLayout.CENTER, buttonPanel);

        // classes scroll panel
        this.add(BorderLayout.EAST, new ClassesPane(window));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == logoutButton){
            window.setUser(null);
            window.switchPage(new StartPage(window));

        } else if(e.getSource() == generateQuizButton){
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                HelperFunctions.GenerateQuiz(fileChooser.getSelectedFile().getAbsolutePath(), window);
            } else {
                System.out.println("cancelled by user.");
            }
        } else if(e.getSource() == newClassButton){
            window.switchPage(new CreateNewClassPage(window));
        }
    }
}

class StudentHomePage extends Page{
    JButton viewQuizzesButton = GUI_Elements.button("View Quizzes");
    JButton logoutButton = GUI_Elements.button("Log Out");
    StudentHomePage(Window window){
        super(window);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);

        this.add(GUI_Elements.label("Student Home Page")); c.gridy++;

        JLabel descriptionLabel = new JLabel(String.format("Welcome, %s %s, to the QuizGenAI Program!",
                window.getUser().firstName, window.getUser().lastName));
        this.add(descriptionLabel, c); c.gridy++;

        viewQuizzesButton.addActionListener(this);
        logoutButton.addActionListener(this);

        this.add(viewQuizzesButton, c); c.gridy++;
        this.add(logoutButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == viewQuizzesButton)
            window.switchPage(new StudentViewQuizzesPage(window));
        else if(e.getSource() == logoutButton){
            window.setUser(null);
            window.switchPage(new StartPage(window));
        }
    }
}

class WaitingPage extends Page{
    JProgressBar progressBar = new JProgressBar();
    JButton cancelButton = GUI_Elements.button("Cancel Quiz Generation");
    WaitingPage(Window window){
        super(window);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.gridy = 0; c.gridx = 0;

        // labels
        JLabel titleLabel = GUI_Elements.label("Instructor Home Page");
        this.add(titleLabel, c); c.gridy++;

        // progress bar
        this.add(progressBar, c); c.gridy++;

        // button
        this.add(cancelButton, c);
        cancelButton.addActionListener(this);
//        printButton.addActionListener(e -> System.out.println(MCQGen.Questions.size()));
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == cancelButton){
            window.thread1.interrupt();
            JOptionPane.showMessageDialog(window,
                    "You have cancelled the quiz generation operation.",
                    "Cancelled Operation",
                    JOptionPane.INFORMATION_MESSAGE);
            window.switchPage(new InstructorHomePage(window));
        }
    }
}

class QuizPage extends Page{
    QuizScrollPane quizScrollPane;
    JButton backButton = GUI_Elements.button("Back");
    JButton submitButton = GUI_Elements.button("Submit");
    QuizPage(Window window){
        super(window);
        this.setLayout(new BorderLayout());
        quizScrollPane = new QuizScrollPane(window.quiz.questions);
        this.add(quizScrollPane, BorderLayout.CENTER);

        // buttons
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.gridx = c.gridy = 0;
        c.gridwidth = 1;
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        backButton.addActionListener(this);
        submitButton.addActionListener(this);
        buttonPanel.add(backButton, c); c.gridx++;
        buttonPanel.add(submitButton, c);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton){
            window.switchPage(new StudentHomePage(window));
        } else if(e.getSource() == submitButton){
            int response = JOptionPane.showConfirmDialog(window,
                    "Are you Sure? this action is irreversible",
                    "Confirm Submission",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            System.out.println(response);
            if(response == 1) return; // return from function and cancel submission if the user chooses the no option.

            Vector<Integer> studentAnswers = quizScrollPane.getAnswersArray();
            Database.saveAnswers(window.quiz.questions, studentAnswers, window.getUser().id);
            JOptionPane.showMessageDialog(window, "Quiz Submitted Successfully");
            window.switchPage(new StudentHomePage(window));
        }
    }
}


class TestPage extends Page implements ActionListener{
    JButton addQuestionButton = GUI_Elements.button("Add new Question");
    EditQuizScrollPane editQuizScrollPane;
    TestPage(Window window) {
        super(window);
        this.setLayout(new BorderLayout());

        window.setUser(new Instructor(6, "a", "a" ,"a", "a"));
        ClassesPane panel = new ClassesPane(window);

//        JScrollPane scrollPane = new JScrollPane(panel);
//        scrollPane.setPreferredSize(new Dimension(600, 500));
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == addQuestionButton)
            editQuizScrollPane.addQuestionPanel();
    }
}

class CreateNewClassPage extends Page{
    String iconFilePath = "DefaultImages\\DefaultClassIcon.png";
    JLabel selectedIconLabel = new JLabel();
    JTextField classnameTextField = GUI_Elements.textField();
    JButton createClassButton = GUI_Elements.button("Create Class");
    JButton selectIconButton = GUI_Elements.button("Select Class Icon");
    JButton cancelButton = GUI_Elements.button("Cancel");
    CreateNewClassPage(Window window){
        super(window);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.gridwidth= 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(20, 10, 20, 10);

        this.add(GUI_Elements.label("Create New Class"), c);

        // class name Panel
        JPanel classNamePanel = new JPanel(new GridBagLayout());

        JLabel classNameLabel = new JLabel("Class Name: ");
        classNameLabel.setPreferredSize(new Dimension(100, 30));

        c.gridwidth= 1;
        classNamePanel.add(classNameLabel, c); c.gridx++;
        classNamePanel.add(classnameTextField, c);

        c.gridx = 0; c.gridy = 1;  c.gridwidth= 2;
        this.add(classNamePanel, c); c.gridy++;
        this.add(selectIconButton, c); c.gridx++;
        this.add(selectedIconLabel, c);

        selectIconButton.addActionListener(this);

        // Button Panel
        c.insets = new Insets(10, 10, 10, 10);
        c.gridx = 0; c.gridy = 0; c.gridwidth= 1;

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        cancelButton.setBackground(new Color(192, 64, 64));

        createClassButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonPanel.add(createClassButton, c); c.gridx++;
        buttonPanel.add(cancelButton, c);

        c.gridwidth = 2; c.gridx = 0; c.gridy = 3;
        this.add(buttonPanel, c);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == selectIconButton) {

            JFileChooser fileChooser = new JFileChooser();

            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if(HelperFunctions.isValidImage(filePath)){
                    iconFilePath = filePath;
                    selectedIconLabel.setText("Selected Image:" + filePath);
                } else {
                    JOptionPane.showMessageDialog(window,
                            "The Icon file format must be: \".png\", \".gif\", or \".jpeg\".",
                            "Invalid Image",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } else if(e.getSource() == cancelButton){
            window.switchPage(new InstructorHomePage(window));
        } else if(e.getSource() == createClassButton){
            String className = classnameTextField.getText();
            String operationResult = Database.newClass(className, iconFilePath, window);

            if(operationResult == null){
                JOptionPane.showMessageDialog(window,
                        "Class Created Successfully.",
                        "Successful Operation",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(window,
                    operationResult,
                    "Operation Failed",
                    JOptionPane.ERROR_MESSAGE);
        }

    }
}

class ManageClassPage extends Page{
    ManageStudentsListPanel manageStudentsList;
    AddStudentListPanel addStudentsList;
    RemoveStudentListPanel removeStudentsList;
    ManageClassPage(Window window){
        super(window);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(20, 10, 20, 10);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        manageStudentsList = new ManageStudentsListPanel(window);
        addStudentsList = new AddStudentListPanel(window);
        removeStudentsList = new RemoveStudentListPanel(window);

        tabbedPane.addTab("Manage Class", manageStudentsList);
        tabbedPane.addTab("Add Students to Class", addStudentsList);
        tabbedPane.addTab("Remove Students from Class", removeStudentsList);

        this.add(tabbedPane, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void update(){
        manageStudentsList.update();
        addStudentsList.update();
        removeStudentsList.update();
    }
}

class EditQuizPage extends Page{
    EditQuizScrollPane editQuizScrollPane = new EditQuizScrollPane(MCQGen.questions);
    JButton backButton = GUI_Elements.button("Back");
    JButton saveButton = GUI_Elements.button("Save Changes");
    QuizSettingsPanel quizSettingsPanel = new QuizSettingsPanel(window);
    EditQuizPage(Window window){
        super(window);
        // side panel
        JPanel sidePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 10, 10);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;

        // title label
        sidePanel.add(GUI_Elements.label("Edit Quiz"), c); c.gridy++;

        // settings panel
        sidePanel.add(quizSettingsPanel, c); c.gridy++;

        // buttons
        c.gridwidth = 1;
        backButton.addActionListener(this);
        saveButton.addActionListener(this);
        sidePanel.add(backButton, c); c.gridx++;
        sidePanel.add(saveButton, c); c.gridx++;

        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        sidePanel.add(Box.createVerticalGlue(), c);

        this.add(sidePanel, BorderLayout.EAST);
        this.add(editQuizScrollPane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton){
            window.switchPage(new InstructorHomePage(window));
        } else if(e.getSource() == saveButton){
            String result = Database.addQuiz(quizSettingsPanel.getQuizTitle(),
                                                quizSettingsPanel.getQuizStartDate(),
                                                quizSettingsPanel.getQuizEndDate(),
                                                quizSettingsPanel.getSelectedClasses(),
                                                editQuizScrollPane.getQuestions(),
                                                window.getUser().id);
            if(result != null) {
                JOptionPane.showMessageDialog(window,
                        result,
                        "Operation Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(window,
                    String.format("Quiz %s has been successfully added to the database.",
                            quizSettingsPanel.getQuizTitle()),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            window.switchPage(new InstructorHomePage(window));
        }
    }
}

class StudentViewQuizzesPage extends Page{

    StudentViewQuizzesPage(Window window){
        super(window);
        this.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        Vector<Quiz> quizzes = Database.getQuizzesByStudent(window.getUser().id);

        Vector<Quiz> upcomingQuizzes = new Vector<>();
        Vector<Quiz> ongoingQuizzes = new Vector<>();
        Vector<Quiz> pastQuizzes = new Vector<>();

        for(Quiz quiz: quizzes){
            Date currentTime = new Date(System.currentTimeMillis());
            if(quiz.startDateTime.after(currentTime))
                upcomingQuizzes.add(quiz);
            else if(quiz.endDateTime.before(currentTime))
                pastQuizzes.add(quiz);
            else
                ongoingQuizzes.add(quiz);
        }

        tabbedPane.addTab("Ongoing Quizzes", new QuizSelectionScrollPane(window, ongoingQuizzes));
        tabbedPane.addTab("Upcoming Quizzes", new QuizSelectionScrollPane(window, upcomingQuizzes));
        tabbedPane.addTab("Past Quizzes", new QuizSelectionScrollPane(window, pastQuizzes));
        this.add(tabbedPane, BorderLayout.CENTER);

        // buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = c.gridy = 0;

        JButton backButton = GUI_Elements.button("Back");
        backButton.addActionListener(this);
        buttonPanel.add(backButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        window.switchPage(new StudentHomePage(window));
    }
}

class ManageStudentPage extends Page{
    JButton backButton = GUI_Elements.button("Back");
    ManageStudentPage(Window window, Student student, Class class_){
        super(window);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 5, 2, 5);
        c.gridx = 0; c.gridy = 0;


        JPanel studentInfoPanel = new JPanel(new GridBagLayout());
        c.anchor = GridBagConstraints.LINE_START;
        studentInfoPanel.add(new JLabel(String.format("Student name: %s %s", student.firstName, student.lastName)), c);
        c.gridy++;
        studentInfoPanel.add(new JLabel(String.format("Student ID: %d", student.id)), c); c.gridy++;
        studentInfoPanel.add(new JLabel(String.format("Student Email: %s", student.email)), c); c.gridy++;
        studentInfoPanel.add(new JLabel(String.format("Phone Number: %s", student.phoneNumber)), c); c.gridy++;
        studentInfoPanel.add(new JLabel(String.format("Students Points: %d", student.points)), c);
        // todo: allow student Object to store last login date.

        c.insets = new Insets(5, 5, 10, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0; c.gridy = 0;

        this.add(GUI_Elements.label("manage Student"), c); c.gridy++;

        c.anchor = GridBagConstraints.LINE_START;
        this.add(studentInfoPanel, c); c.gridy++;

        c.anchor = GridBagConstraints.CENTER;
        JScrollPane QuizDisplayScrollPane = new JScrollPane(new QuizDisplayTable(window, student, class_));
        this.add(QuizDisplayScrollPane, c); c.gridy++;

        backButton.addActionListener(this);
        this.add(backButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton)
            window.switchPage(new ManageClassPage(window));
    }
}

class ViewStudentSubmissionPage extends Page{
    JButton backButton = GUI_Elements.button("Back");
    Class class_;
    Quiz quiz;
    Student student;

    ViewStudentSubmissionPage(Window window, Student student, Quiz quiz, Class class_){
        super(window);
        this.setLayout(new GridBagLayout());
        this.class_ = class_;
        this.quiz = quiz;
        this.student = student;

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 5, 2, 5);
        c.gridx = 0; c.gridy = 0;

        JPanel infoPanel = new JPanel(new GridBagLayout());
        c.anchor = GridBagConstraints.LINE_START;
        infoPanel.add(new JLabel(String.format("Student name: %s %s", student.firstName, student.lastName)), c);
        c.gridy++;
        infoPanel.add(new JLabel(String.format("Student ID: %d", student.id)), c); c.gridy++;
        infoPanel.add(new JLabel(String.format("Quiz Title: %s", quiz.title)), c); c.gridy++;
        infoPanel.add(new JLabel(String.format("Quiz Id: %s", quiz.id)), c); c.gridy++;
        infoPanel.add(new JLabel(String.format("Class: %s", class_.name)), c);

        c.insets = new Insets(5, 5, 10, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0; c.gridy = 0;

        this.add(GUI_Elements.label("Student Submission"), c); c.gridy++;

        c.anchor = GridBagConstraints.LINE_START;
        this.add(infoPanel, c); c.gridy++;

        c.anchor = GridBagConstraints.CENTER;
        JScrollPane QuizDisplayScrollPane = new JScrollPane(new QuizSubmissionDisplay(student.id, quiz.id));
        QuizDisplayScrollPane.setPreferredSize(new Dimension(760, 400));
        this.add(QuizDisplayScrollPane, c); c.gridy++;

        backButton.addActionListener(this);
        this.add(backButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton)
            window.switchPage(new ManageStudentPage(window, student, class_));
    }
}