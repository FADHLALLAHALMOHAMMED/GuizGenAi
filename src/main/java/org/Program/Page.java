package org.Program;

import org.Program.Entities.*;
import org.Program.Entities.Class;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Date;
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

    // private ArrayList<JPanel> panelsList = new ArrayList<>();
    private final Icon appImage = new ImageIcon(getClass().getResource("/images/app_name.png"));
    private final JLabel appImageLabel;
    private final JButton logoutButton;
    public final JLabel pageTitle;
    protected final JPanel headerPanel; // for headers (applied in all pages)
    protected final JPanel contentPanel; // for contents in all pages

    Page(Window window) {

        this.window = window;
        this.setBackground(GUI_Elements.APP_BACKGROUND);
        this.setLayout(new BorderLayout());


        // Header panel ------------------------------------------------------------------------------
        headerPanel = GUI_Elements.panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        headerPanel.setBackground(GUI_Elements.APP_BACKGROUND);

        // managing app_name image
        Image img = ((ImageIcon) appImage).getImage();
        img = img.getScaledInstance((int)(img.getWidth(null) / 1.2), (int)( img.getHeight(null) / 1.2), Image.SCALE_SMOOTH);

        Icon scaledAppImg = new ImageIcon(img);
        appImageLabel = new JLabel(scaledAppImg);

        pageTitle = GUI_Elements.label("");
        pageTitle.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 28));

        // managing the 'logout' label
        if (this instanceof StartPage || this instanceof RegisterPage || this instanceof LoginPage) {
            logoutButton = null;
        }
        else {

            logoutButton = GUI_Elements.button("Logout");
            logoutButton.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 20));
            logoutButton.setBackground(GUI_Elements.APP_BACKGROUND);
            logoutButton.setForeground(Color.RED);

            logoutButton.setFocusPainted(false);
            logoutButton.setBorderPainted(false);
            logoutButton.setOpaque(true);
            logoutButton.setContentAreaFilled(false);


            logoutButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if(HelperFunctions.confirmUserAction("Are you sure you want to logout?", window)) return;
                    window.switchPage(new StartPage(window));
                }
            });

        }



        // Content panel ----------------------------------------------------------------------------

        contentPanel = GUI_Elements.panel(new GridBagLayout());
        contentPanel.setBackground(GUI_Elements.APP_BACKGROUND);
        
        // Grid Management -----------------------------------------------------------------------------
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(23, 23, 23, 20);
        headerPanel.add(appImageLabel, gbc);
        gbc.gridx++;


        gbc.anchor = GridBagConstraints.CENTER;
        headerPanel.add(pageTitle, gbc);
        
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        if (! (this instanceof StartPage || this instanceof RegisterPage || this instanceof LoginPage)) {
            headerPanel.add(logoutButton, gbc);
        }

        this.add(headerPanel, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);
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
        contentPanel.setLayout(new GridBagLayout());

        JLabel titleLabel = GUI_Elements.label("Welcome to QuizGenAi");
        titleLabel.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 40));
        loginButton = GUI_Elements.button("Login");
        registerButton = GUI_Elements.button("Register");

        loginButton.addActionListener(this);
        registerButton.addActionListener(this);

        gbc.gridy = 0; gbc.gridx = 0;

        gbc.insets = new Insets(20, 10, 50, 10);
        contentPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(loginButton, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(registerButton, gbc);

        contentPanel.setVisible(true);
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
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;

        // Image Panel -------------------------------------------------------------------
        JPanel imagePanel = GUI_Elements.panel(new GridBagLayout());

        imageLogo = new ImageIcon(getClass().getResource("/images/logo.png"));
        logoLabel = new JLabel(imageLogo);

        // Register Panel -------------------------------------------------------------------
        JPanel registerPanel = GUI_Elements.panel(new GridBagLayout());

        createAccountButton.addActionListener(this);
        startPageButton.addActionListener(this);

        //radioTypePanel
        JPanel radioButtonPanel = GUI_Elements.panel();
        ButtonGroup accountTypeRadioGroup = new ButtonGroup();
        radioButtonPanel.setLayout(new GridBagLayout());

        accountTypeRadioGroup.add(instructorRadioButton);
        accountTypeRadioGroup.add(studentRadioButton);

        radioButtonPanel.add(instructorRadioButton);
        radioButtonPanel.add(studentRadioButton);

        // empty panel (to organize look)
        JPanel emptyPanel = GUI_Elements.panel(new GridBagLayout());

        //Grid Manager ------------------------------------------------------------------

        //register panel grid
        GridBagConstraints registerConstraints = new GridBagConstraints();
        registerConstraints.gridx = 0; registerConstraints.gridy = 0;
        registerConstraints.weightx = 1.0; registerConstraints.weighty = 0;
        registerConstraints.fill = GridBagConstraints.HORIZONTAL;
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
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0; c.gridy = 0;
        imagePanel.add(logoLabel, c);

        //loginPage grid
        c.gridx = 0; c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 7;
        contentPanel.add(imagePanel, c);
        c.gridx = 1; c.gridy = 0;
        c.weightx = 1;
        contentPanel.add(registerPanel, c);
        c.gridx = 2; c.gridy = 0;
        c.weightx = 7;
        contentPanel.add(emptyPanel, c);
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
            Database.register(firstName, lastName, email, phoneNumber, password, accountType);
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
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;

        // Image Panel -------------------------------------------------------------------
        JPanel imagePanel = GUI_Elements.panel(new GridBagLayout());

        imageLogo = new ImageIcon(getClass().getResource("/images/logo.png"));
        logoLabel = new JLabel(imageLogo);

        // Login Panel -------------------------------------------------------------------
        JPanel loginPanel = GUI_Elements.panel(new GridBagLayout());
        loginPanel.setBackground(GUI_Elements.APP_BACKGROUND);

        JLabel emailLabel = GUI_Elements.label("Email");
        JLabel passwordLabel = GUI_Elements.label("Password");

        loginButton.addActionListener(this);
        backButton.addActionListener(this);

        // empty panel (to organize look)
        JPanel emptyPanel = GUI_Elements.panel(new GridBagLayout());

        //Grid Manager ------------------------------------------------------------------

        //login panel grid
        GridBagConstraints loginConstraints = new GridBagConstraints();
        loginConstraints.gridx = 0; loginConstraints.gridy = 0;
        loginConstraints.weightx = 1.0; loginConstraints.weighty = 0;
        loginConstraints.fill = GridBagConstraints.HORIZONTAL;
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
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0; c.gridy = 0;
        imagePanel.add(logoLabel, c);

        //loginPage grid
        c.gridx = 0; c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 7;
        contentPanel.add(imagePanel, c);
        c.gridx = 1; c.gridy = 0;
        c.weightx = 1;
        contentPanel.add(loginPanel, c);
        c.gridx = 2; c.gridy = 0;
        c.weightx = 7;
        contentPanel.add(emptyPanel, c);
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
    private final JLabel descriptionLabel;
    private final JPanel cardsPanel;
    private final JScrollPane cardsScrollPane;
    private final Vector<ClassPanel> classPanels = new Vector<>();
    private final JPanel buttonsPanel;
    private final JButton newClassButton;
    private final JButton generateQuizButton;
    private final JButton editUserInfoButton;

    InstructorHomePage(Window window) {
        super(window);
        window.setCurrentClass(null);
        contentPanel.setLayout(new GridBagLayout());
        
        pageTitle.setText("Instructor Home Page");
        
        descriptionLabel = GUI_Elements.label(String.format(
            "Welcome, %s %s, to QuizGenAI",
            window.getUser().firstName, window.getUser().lastName
            )
        );


        //Classes Panel ------------------------------------------------------------------------------
        cardsPanel = GUI_Elements.panel(new GridBagLayout());

        GridBagConstraints cardsConstraints = new GridBagConstraints();
        cardsConstraints.fill = GridBagConstraints.NONE;
        cardsConstraints.insets = new Insets(10, 10, 10, 10);

        int columns = 4; // num of cards per row
        Dimension cardSize = new Dimension(250, 200);


        int instructorId = window.getUser().id;
        Vector<Class> classes = Database.getInstructorClasses(instructorId);

        for (int i = 0; i < classes.size(); i++) {

            ClassPanel classPanel = new ClassPanel(classes.get(i));
            classPanel.setPreferredSize(cardSize);
            classPanel.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
            classPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            classPanels.add(classPanel);

            cardsConstraints.gridx = i % columns;
            cardsConstraints.gridy = i / columns;

            classPanel.addMouseListener(
                new MouseListener() {
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        window.setCurrentClass(classPanel.class_);
                        window.switchPage(new ManageClassPage(window));
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    

                }
            );
            cardsPanel.add(classPanel, cardsConstraints);
        }


        // classes pane
        cardsScrollPane = new JScrollPane(cardsPanel);
        cardsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Buttons panel ---------------------------------------------------------------------------------------
        buttonsPanel = GUI_Elements.panel(new GridBagLayout());

        newClassButton = GUI_Elements.button("Create new class");
        generateQuizButton = GUI_Elements.button("Generate quiz");
        editUserInfoButton = GUI_Elements.button("Update Profile Info");

        newClassButton.addActionListener(this);
        generateQuizButton.addActionListener(this);
        editUserInfoButton.addActionListener(this);

        // Grid manager ------------------------------------------------------------------------------------------
        GridBagConstraints homeConstraints = new GridBagConstraints();
        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        homeConstraints.gridx = 0;
        homeConstraints.gridy = 0;
        homeConstraints.fill = GridBagConstraints.BOTH;
        buttonsConstraints.gridx = 0;
        buttonsConstraints.gridy = 0;
        buttonsConstraints.fill = GridBagConstraints.HORIZONTAL;

        buttonsConstraints.weightx = 1.0;
        buttonsConstraints.weighty = 1.0;
        buttonsConstraints.insets = new Insets(10, 23, 23, 10);
        buttonsPanel.add(newClassButton, buttonsConstraints);
        buttonsConstraints.gridx++;
        buttonsPanel.add(generateQuizButton, buttonsConstraints);
        buttonsConstraints.gridx++;
        buttonsPanel.add(editUserInfoButton, buttonsConstraints);
        homeConstraints.insets = new Insets(23, 23, 23, 23);

        homeConstraints.fill = GridBagConstraints.NONE;
        homeConstraints.anchor = GridBagConstraints.CENTER;
        contentPanel.add(descriptionLabel, homeConstraints);
        
        homeConstraints.fill = GridBagConstraints.BOTH;
        homeConstraints.anchor = GridBagConstraints.WEST;
        homeConstraints.gridy++;
        homeConstraints.weighty = 2.0;
        homeConstraints.weightx = 1.0;
        contentPanel.add(cardsScrollPane, homeConstraints);
        homeConstraints.gridy++;
        homeConstraints.weightx = 1.0;
        homeConstraints.weighty = 1.0;
        contentPanel.add(buttonsPanel, homeConstraints);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == generateQuizButton){
            HelperFunctions.GenerateQuiz(window);
        } else if(e.getSource() == newClassButton){
            window.switchPage(new CreateNewClassPage(window));
        } else if(e.getSource() == editUserInfoButton){
            window.switchPage(new EditUserInfoPage(window));
        }
    }
}

class StudentHomePage extends Page{
    JButton viewSubmissions = GUI_Elements.button("View all Submissions");
    JButton viewQuizzesButton = GUI_Elements.button("View all Quizzes");
    JButton editUserInfoButton = GUI_Elements.button("Update Profile Info");
    private final JLabel descriptionLabel;
    private final JPanel cardsPanel;
    private final JScrollPane cardsScrollPane;
    private final Vector<ClassPanel> classPanels = new Vector<>();
    private final JPanel buttonsPanel;

    StudentHomePage(Window window){
        super(window);
        window.setCurrentStudent((Student)window.getUser());
        window.setCurrentClass(null);
        contentPanel.setLayout(new GridBagLayout());

        pageTitle.setText("Student Home Page");

        descriptionLabel = GUI_Elements.label(String.format(
                        "Welcome, %s %s, to QuizGenAI",
                        window.getUser().firstName, window.getUser().lastName
                )
        );

        //Classes Panel ------------------------------------------------------------------------------
        cardsPanel = GUI_Elements.panel(new GridBagLayout());

        GridBagConstraints cardsConstraints = new GridBagConstraints();
        cardsConstraints.fill = GridBagConstraints.NONE;
        cardsConstraints.insets = new Insets(10, 10, 10, 10);

        int columns = 4; // num of cards per row
        Dimension cardSize = new Dimension(250, 200);


        int instructorId = window.getUser().id;
        Vector<Class> classes = Database.getStudentClasses(instructorId);

        for (int i = 0; i < classes.size(); i++) {

            ClassPanel classPanel = new ClassPanel(classes.get(i));
            classPanel.setPreferredSize(cardSize);
            classPanel.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
            classPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            classPanels.add(classPanel);

            cardsConstraints.gridx = i % columns;
            cardsConstraints.gridy = i / columns;

            classPanel.addMouseListener(
                    new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            window.setCurrentClass(classPanel.class_);
                            window.switchPage(new StudentClassPage(window));
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {}
                        @Override
                        public void mouseExited(MouseEvent e) {}
                        @Override
                        public void mousePressed(MouseEvent e) {}
                        @Override
                        public void mouseReleased(MouseEvent e) {}
                    }
            );
            cardsPanel.add(classPanel, cardsConstraints);
        }


        // classes pane
        cardsScrollPane = new JScrollPane(cardsPanel);
        cardsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Buttons panel ---------------------------------------------------------------------------------------
        buttonsPanel = GUI_Elements.panel(new GridBagLayout());

        viewQuizzesButton.addActionListener(this);
        viewSubmissions.addActionListener(this);
        editUserInfoButton.addActionListener(this);

        // Grid manager ------------------------------------------------------------------------------------------
        GridBagConstraints homeConstraints = new GridBagConstraints();
        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        homeConstraints.gridx = 0;
        homeConstraints.gridy = 0;
        homeConstraints.fill = GridBagConstraints.BOTH;
        buttonsConstraints.gridx = 0;
        buttonsConstraints.gridy = 0;
        buttonsConstraints.fill = GridBagConstraints.HORIZONTAL;



        buttonsConstraints.weightx = 1.0;
        buttonsConstraints.weighty = 1.0;
        buttonsConstraints.insets = new Insets(10, 23, 23, 10);
        buttonsPanel.add(viewQuizzesButton, buttonsConstraints);
        buttonsConstraints.gridx++;
        buttonsPanel.add(viewSubmissions, buttonsConstraints);
        buttonsConstraints.gridx++;
        buttonsPanel.add(editUserInfoButton, buttonsConstraints);
        homeConstraints.insets = new Insets(23, 23, 23, 23);

        homeConstraints.fill = GridBagConstraints.NONE;
        homeConstraints.anchor = GridBagConstraints.CENTER;
        contentPanel.add(descriptionLabel, homeConstraints);

        homeConstraints.fill = GridBagConstraints.BOTH;
        homeConstraints.anchor = GridBagConstraints.WEST;
        homeConstraints.gridy++;
        homeConstraints.weighty = 2.0;
        homeConstraints.weightx = 1.0;
        contentPanel.add(cardsScrollPane, homeConstraints);
        homeConstraints.gridy++;
        homeConstraints.weightx = 1.0;
        homeConstraints.weighty = 1.0;
        contentPanel.add(buttonsPanel, homeConstraints);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == viewQuizzesButton) {
            window.switchPage(new StudentViewQuizzesPage(window));
        } else if(e.getSource() == viewSubmissions){
            window.switchPage(new StudentViewAllSubmissions(window));
        } else if(e.getSource() == editUserInfoButton){
            window.switchPage(new EditUserInfoPage(window));
        }
    }
}

class WaitingPage extends Page{
    
    
    JProgressBar progressBar = new JProgressBar();
    JButton cancelButton = GUI_Elements.button("Cancel Quiz Generation");
    JLabel loadingLabel = GUI_Elements.label("Loading...");


    WaitingPage(Window window){
        super(window);
        contentPanel.setLayout(new GridBagLayout());

        pageTitle.setText("Processing...");
        
        cancelButton.addActionListener(this);


        // Grid Management -----------------------------------------------------------------------
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0; c.gridx = 0;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.CENTER;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 400, 10, 400);
        
        
        contentPanel.add(loadingLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy++;
        contentPanel.add(progressBar, c);
        
        c.insets = new Insets(10, 400, 23, 400);
        c.gridy++;
        contentPanel.add(cancelButton, c);

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
    JLabel timeLeftLabel = GUI_Elements.label("Time left: 00:00:00");
    QuizPage(Window window){
        super(window);
        contentPanel.setLayout(new GridBagLayout());


        quizScrollPane = new QuizScrollPane(window.quiz.questions);
                
        backButton.addActionListener(this);
        submitButton.addActionListener(this);
        
        
        // Grid Management -----------------------------------------------------------
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1.0;

        c.gridwidth = 2;
        c.insets = new Insets(10, 23, 0, 23);
        contentPanel.add(timeLeftLabel, c);
        
        c.gridy++;
        c.weighty = 1.0;
        c.insets = new Insets(5, 23, 10, 23);
        c.fill = GridBagConstraints.BOTH;
        contentPanel.add(quizScrollPane, c);
        
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 23, 23, 5);
        c.gridwidth = 1;
        c.gridy++;
//        contentPanel.add(backButton, c);
//        c.gridx++;
        
        c.insets = new Insets(10, 5, 23, 23);
        contentPanel.add(submitButton, c);
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

            if(response == 1) return; // return from function and cancel submission if the user chooses the no option.

            int submissionId = Database.saveSubmission(quizScrollPane.getAnswersArray(), window.getUser().id, window.quiz.id);
            JOptionPane.showMessageDialog(window, "Quiz Submitted Successfully");
            window.switchPage(new StudentHomePage(window));
            HelperFunctions.gradeSubmission(submissionId, window.getUser().id);
        }
    }
}


class TestPage extends Page {
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
            editQuizScrollPane.addQuestionPanel(1);
    }
}

class CreateNewClassPage extends Page{
    String iconFilePath = "DefaultImages\\DefaultClassIcon.png";

    JTextField classNameField = GUI_Elements.textField();
    JLabel classNameLabel = GUI_Elements.label("Class Name");
    JButton createClassButton = GUI_Elements.button("Create Class");
    JButton selectIconButton = GUI_Elements.button("Select an Icon");
    JButton cancelButton = GUI_Elements.button("Cancel");

    JPanel localPanel = GUI_Elements.panel(new GridBagLayout());


    CreateNewClassPage(Window window){
        super(window);
        contentPanel.setLayout(new GridBagLayout());


        pageTitle.setText("Create New Class");

        cancelButton.setBackground(GUI_Elements.WARNING_BACKGROUND);
        cancelButton.setBorder(GUI_Elements.BLACK_BORDER);

        selectIconButton.setPreferredSize(new Dimension(150, GUI_Elements.textFieldSize1.height));
        createClassButton.setPreferredSize(new Dimension((GUI_Elements.textFieldSize1.width + selectIconButton.getPreferredSize().width + 20), GUI_Elements.BUTTON_SIZE.height));
        cancelButton.setPreferredSize(new Dimension((GUI_Elements.textFieldSize1.width + selectIconButton.getPreferredSize().width + 20), GUI_Elements.BUTTON_SIZE.height));

        selectIconButton.addActionListener(this);
        createClassButton.addActionListener(this);
        cancelButton.addActionListener(this);


        // Grid Managemenet -----------------------------------------------------------------------------------------

        GridBagConstraints contentPanelGBC = new GridBagConstraints();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0; gbc.gridy = 0;

        
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        localPanel.add(classNameLabel, gbc);
        
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy++;
        localPanel.add(classNameField, gbc);
        
        gbc.gridx++;
        localPanel.add(selectIconButton, gbc);
        

        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        localPanel.add(createClassButton, gbc);
        
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy++;
        localPanel.add(cancelButton, gbc);


        contentPanelGBC.gridx = 0; contentPanelGBC.gridy = 0;
        contentPanelGBC.fill = GridBagConstraints.BOTH;
        contentPanelGBC.anchor = GridBagConstraints.CENTER;
        contentPanel.add(localPanel, contentPanelGBC);


    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == selectIconButton) {

            JFileChooser fileChooser = new JFileChooser();

            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                if (HelperFunctions.isValidImage(filePath)) {
                    iconFilePath = filePath;
                }
                else {

                    JOptionPane.showMessageDialog(
                        window,
                        "The Icon file format must be: \".png\", \".gif\", or \".jpeg\".",
                        "Invalid Image",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        }
        else if (e.getSource() == cancelButton) {

            window.switchPage(new InstructorHomePage(window));
        }
        else if (e.getSource() == createClassButton) {

            String className = classNameField.getText();
            String operationResult = Database.newClass(className, iconFilePath, window);
            if (HelperFunctions.showDialogIfError(operationResult, window)) return;
            JOptionPane.showMessageDialog(
                    window,
                    "Class Created Successfully.",
                    "Successful Operation",
                    JOptionPane.INFORMATION_MESSAGE
            );
            window.switchPage(new InstructorHomePage(window));
        }
    }
}

class ManageClassPage extends Page{
    ManageStudentsListPanel manageStudentsList;
    AddStudentListPanel addStudentsList;
    RemoveStudentListPanel removeStudentsList;

    ManageClassPage(Window window) {
        super(window);
        contentPanel.setLayout(new GridBagLayout());

        // local panel --------------------------------------------------------------------------------
        JPanel localPanel = GUI_Elements.panel(new GridBagLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        manageStudentsList = new ManageStudentsListPanel(window);
        addStudentsList = new AddStudentListPanel(window);
        removeStudentsList = new RemoveStudentListPanel(window);

        

        tabbedPane.addTab("                              Manage Class                              ", manageStudentsList);
        tabbedPane.addTab("      Add Students to Class      ", addStudentsList);
        tabbedPane.addTab("     Remove Students from Class     ", removeStudentsList);
        tabbedPane.addTab("     Quiz Performance     ", new QuizAverageTab(window));

        // Set tab background colors
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setBackgroundAt(i, GUI_Elements.SECONDARY_BACKGROUND);
        }

        // Grid Management ---------------------------------------------------------------------------
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 123, 50, 123);

        localPanel.add(tabbedPane, c);
        contentPanel.add(localPanel, c);
    }


    @Override
    public void actionPerformed(ActionEvent e) {}
    public void update(){
        manageStudentsList.update();
        addStudentsList.update();
        removeStudentsList.update();
    }
}

class StudentClassPage extends Page{
    JButton backButton = GUI_Elements.button("Back");
    StudentClassPage(Window window){
        super(window);
        contentPanel.setLayout(new GridBagLayout());
        pageTitle.setText(window.getCurrentClass().name);
        // local panel --------------------------------------------------------------------------------
        JPanel localPanel = GUI_Elements.panel(new GridBagLayout());

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setPreferredSize(new Dimension(1000, 550));

        // for the pass submissions tab
        Vector<Quiz> quizzes = new Vector<>();
        Vector<Submission> submissions = new Vector<>();
        Database.getAllStudentSubmissionsInClass(window.getUser().id, window.getCurrentClass().id, quizzes, submissions);
        JScrollPane QuizDisplayScrollPane = new JScrollPane(new QuizDisplayTable(window, quizzes, submissions));
        QuizDisplayScrollPane.setBackground(Color.WHITE);
        // for the Class information tab
        JPanel classInformationPanel = new JPanel(new GridBagLayout());
        classInformationPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 5, 2, 5);
        c.gridx = 0; c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;

        Class class_ = window.getCurrentClass();
        classInformationPanel.add(new JLabel(class_.classIcon), c);  c.gridy++;
        classInformationPanel.add(GUI_Elements.label("Class Title:   " + class_.name), c); c.gridy++;
        classInformationPanel.add(GUI_Elements.label("Class Id:   " + class_.id), c); c.gridy++;
        classInformationPanel.add(GUI_Elements.label("Class Instructor:   "), c); c.gridy++; // todo: create a query that returns Instructors full  and count of students
        classInformationPanel.add(GUI_Elements.label("Number of students:   "), c); // todo: think of more relevant information to add here.

        // view ongoing Quizzes panel
        Vector<Quiz> ongoingQuizzes = Database.getStudentQuizzesInClass(window.getUser().id, window.getCurrentClass().id);
        QuizSelectionScrollPane QuizList = new QuizSelectionScrollPane(window, ongoingQuizzes);

        tabbedPane.addTab("        Class Information        ", classInformationPanel);
        tabbedPane.addTab("      View Trophies in Class     ", new StudentViewClassTrophies(window));
        tabbedPane.addTab("      View Feedback in Class     ", new StudentClassFeedbackTab(window));
        tabbedPane.addTab("       View Ongoing Quizzes      ", QuizList);
        tabbedPane.addTab("      View Past Submissions      ", QuizDisplayScrollPane);

        // Set tab background colors
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setBackgroundAt(i, GUI_Elements.SECONDARY_BACKGROUND);
        }

        // Grid Management ---------------------------------------------------------------------------
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 123, 50, 123);

        localPanel.add(tabbedPane, c); // todo: find a way to expand the tabbed Pane to fill the screen.
        contentPanel.add(localPanel, c); c.gridy++;

        c.fill = GridBagConstraints.NONE;
        contentPanel.add(backButton, c);
        backButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton){
            window.switchPage(new StudentHomePage(window));
        }
    }
}

class EditQuizPage extends Page{
    EditQuizScrollPane editQuizScrollPane = new EditQuizScrollPane(QuizGen.questions);
    JButton backButton = GUI_Elements.button("Go back");
    JButton saveButton = GUI_Elements.button("Save Changes");
    QuizSettingsPanel quizSettingsPanel = new QuizSettingsPanel(window);

    
    EditQuizPage(Window window) {
        
        super(window);
        


        JPanel localPanel = GUI_Elements.panel(new GridBagLayout());
        JTabbedPane editQuizTabbedPane = new JTabbedPane(JTabbedPane.TOP);

        editQuizTabbedPane.addTab("Quiz Questions", editQuizScrollPane);
        editQuizTabbedPane.addTab("Schedule Time", quizSettingsPanel);

        saveButton.addActionListener(this);
        backButton.addActionListener(this);



        // Grid Management ----------------------------------------------------------------------
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc. gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        
        localPanel.add(editQuizTabbedPane, gbc);
        
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 123, 10, 123);
        contentPanel.add(localPanel, gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets(10, 123, 23, 5);
        contentPanel.add(saveButton, gbc);
        gbc.gridx++;
        gbc.insets = new Insets(10, 5, 23, 123);
        contentPanel.add(backButton, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton){
            window.switchPage(new InstructorHomePage(window));

        } else if(e.getSource() == saveButton) {
            String result = Database.addQuiz(
                quizSettingsPanel.getQuizTitle(),
                quizSettingsPanel.getQuizStartDate(),
                quizSettingsPanel.getQuizEndDate(),
                quizSettingsPanel.getSelectedClasses(),
                editQuizScrollPane.getQuestions(),
                window.getUser().id,
                QuizGen.documentHash
            );

            if(HelperFunctions.showDialogIfError(result, window)) return;
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
    ManageStudentPage(Window window){
        super(window);
        Student student = window.getCurrentStudent();
        pageTitle.setText(student.firstName + " " + student.lastName);
        contentPanel.setLayout(new GridBagLayout());

        JPanel localPanel = new JPanel(new GridBagLayout());
        localPanel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;

        JTabbedPane tabs = new JTabbedPane();
        tabs.setPreferredSize(new Dimension(800, 600));

        JPanel studentInfoTab = new StudentInfoTab(window);
        JPanel submissionsTab = new InstructorViewStudentSubmissionsTab(window);
        JPanel feedbackTab = new InstructorFeedbackTab(window);
        JPanel trophiesTab = new InstructorViewStudentClassTrophies(window);

        tabs.addTab("Student Information", studentInfoTab);
        tabs.addTab("Student Submissions", submissionsTab);
        tabs.addTab("Student feedback", feedbackTab);
        tabs.addTab("Student Trophies", trophiesTab);

        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.setBackgroundAt(i, GUI_Elements.SECONDARY_BACKGROUND);
        }

//        contentPanel.add(tabs, c);
//        c.gridy++;
//        c.anchor = GridBagConstraints.CENTER;
//
//        c.insets = new Insets(10, 325, 10, 325);
//        backButton.addActionListener(this);
//        c.gridy++;
//        contentPanel.add(backButton, c);

        // Grid Management ---------------------------------------------------------------------------
        c.gridx = 0; c.gridy = 0;
        c.weightx = 6.0;
        c.weighty = 6.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 60, 10, 60);

        localPanel.add(tabs, c);
        contentPanel.add(localPanel, c);

        c.fill = GridBagConstraints.NONE;
        c.gridy++;
        c.insets = new Insets(10, 20, 20, 20);
        c.weightx = 1.0;
        c.weighty = 1.0;
        contentPanel.add(backButton, c);
        backButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton) {
            window.switchPage(new ManageClassPage(window));
        }
    }
}

class ViewStudentSubmissionPage extends Page{
    JButton backButton = GUI_Elements.button("Back");
    JButton saveButton = GUI_Elements.button("Save Changes");
    QuizSubmissionDisplay quizSubmissionDisplay;
    Quiz quiz;
    Student student;
    Submission submission;

    ViewStudentSubmissionPage(Window window){ // requires student, submission, and quiz
        super(window);
        this.setLayout(new GridBagLayout());
        this.quiz = window.quiz;
        this.student = window.getCurrentStudent();
        this.submission = window.getCurrentSubmission();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 5, 2, 5);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;

        JPanel infoPanel = new JPanel(new GridBagLayout());
        c.anchor = GridBagConstraints.LINE_START;
        infoPanel.add(new JLabel(String.format("Student name: %s %s", student.firstName, student.lastName)), c);
        c.gridy++;
        infoPanel.add(new JLabel(String.format("Student ID: %d", student.id)), c); c.gridy++;
        infoPanel.add(new JLabel(String.format("Quiz Title: %s", quiz.title)), c); c.gridy++;
        infoPanel.add(new JLabel(String.format("Quiz Id: %s", quiz.id)), c);

        c.insets = new Insets(5, 5, 10, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0; c.gridy = 0;

        this.add(GUI_Elements.label("Student Submission"), c); c.gridy++;

        c.anchor = GridBagConstraints.LINE_START;
        this.add(infoPanel, c); c.gridy++;

        c.anchor = GridBagConstraints.CENTER;
        quizSubmissionDisplay = new QuizSubmissionDisplay(submission.submissionId, window.getUser() instanceof Instructor);
        JScrollPane QuizDisplayScrollPane = new JScrollPane(quizSubmissionDisplay);
        QuizDisplayScrollPane.setPreferredSize(new Dimension(960, 400));
        this.add(QuizDisplayScrollPane, c); c.gridy++;

        backButton.addActionListener(this);
        saveButton.addActionListener(this);

        c.gridwidth = 1;
        if(window.getUser() instanceof Instructor) {
            this.add(saveButton, c);
            c.gridx++;
        }
        this.add(backButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton) {
            if(window.getUser() instanceof Instructor)
                window.switchPage(new ManageStudentPage(window));
            else if(window.getCurrentClass() == null)
                window.switchPage(new StudentHomePage(window));
            else
                window.switchPage(new StudentClassPage(window));
        }else if(e.getSource() == saveButton) {
            if(HelperFunctions.confirmUserAction("Are you sure you want to save changes?", window)) return;
            Database.UpdateEssayGrades(quizSubmissionDisplay.getEditedQuestions(), submission.submissionId);
            JOptionPane.showMessageDialog(window,
                    "Essay Questions updated successfully",
                    "Successful Operation",
                    JOptionPane.PLAIN_MESSAGE);
            window.switchPage(new ManageStudentPage(window));
        }
    }
}

class EditUserInfoPage extends Page implements ActionListener {
    LabeledTextField emailTextField;
    LabeledTextField phoneNumberTextField;
    LabeledPasswordField passwordField;
    LabeledPasswordField passwordField2;
    JButton backButton = GUI_Elements.button("Back");
    JButton saveButton = GUI_Elements.button("Save Changes");
    EditUserInfoPage(Window window){
        super(window);
        contentPanel.setLayout(new GridBagLayout());
        pageTitle.setText("Edit Account details");
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = 0; c.insets = new Insets(10, 10, 10, 10);
        c.gridwidth = 2; c.anchor = GridBagConstraints.LINE_START;

        JPanel fieldPanel = new JPanel(new GridBagLayout());
        JPanel buttonsPanel = new JPanel(new GridBagLayout());

        fieldPanel.setOpaque(false);
        buttonsPanel.setOpaque(false);

        fieldPanel.add(GUI_Elements.label(String.format("User ID:    %d", window.getUser().id)), c);
        c.gridy++;
        fieldPanel.add(GUI_Elements.label(String.format("Full Name:    %s %s", window.getUser().firstName, window.getUser().lastName)), c);
        c.gridy++;

        emailTextField = new LabeledTextField("Email Address");
        emailTextField.textField.setText(window.getUser().email);
        phoneNumberTextField = new LabeledTextField("Phone Number");
        phoneNumberTextField.textField.setText(window.getUser().phoneNumber);
        passwordField = new LabeledPasswordField("New Password");
        passwordField2 = new LabeledPasswordField("Repeat new Password");

        fieldPanel.add(emailTextField, c); c.gridy++;
        fieldPanel.add(phoneNumberTextField, c); c.gridy++;
        fieldPanel.add(passwordField, c); c.gridy++;
        fieldPanel.add(passwordField2, c); c.gridy++;


        // buttons
        c.anchor = GridBagConstraints.CENTER;
        backButton.addActionListener(this);
        saveButton.addActionListener(this);

        c.gridwidth = 1;
        buttonsPanel.add(backButton, c); c.gridx++;
        buttonsPanel.add(saveButton, c);

        // content panel
        contentPanel.add(fieldPanel, c); c.gridy++;
        contentPanel.add(buttonsPanel, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton){
            if(window.getUser() instanceof Instructor) window.switchPage(new InstructorHomePage(window));
            else if(window.getUser() instanceof  Student) window.switchPage(new StudentHomePage(window));
        } else if(e.getSource() == saveButton){
            if(HelperFunctions.confirmUserAction("Are you sure you want to save changes to your account's information?", window))
                return ;

            String newEmail = emailTextField.getText();
            String newPhoneNumber = phoneNumberTextField.getText();
            String newPassword = passwordField.getText();
            String newPassword2 = passwordField2.getText();

            String result = HelperFunctions.handleUserInfoEdit(window, newEmail, newPhoneNumber, newPassword, newPassword2);
            if(HelperFunctions.showDialogIfError(result, window)) return;
            JOptionPane.showMessageDialog(window,
                    "Your Account's information has been updated successfully",
                    "success", JOptionPane.INFORMATION_MESSAGE);

            if(window.getUser() instanceof Instructor) window.switchPage(new InstructorHomePage(window));
            else if(window.getUser() instanceof  Student) window.switchPage(new StudentHomePage(window));
        }
    }
}

class StudentViewAllSubmissions extends Page{
    JButton backButton = GUI_Elements.button("Back");
    Vector<Quiz> quizzes = new Vector<>();
    Vector<Submission> submissions = new Vector<>();
    StudentViewAllSubmissions(Window window){
        super(window);
        contentPanel.setLayout(new GridBagLayout());
        pageTitle.setText("All Past Submissions");
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 10, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0; c.gridy = 0;

        // retrieves a vector of quizzes and a vector of the students submissions for a specific student in a specific class.
        // notice that the function returns by reference -not by value- because we need to get 2 values.
        // take note that the quizzes and submissions are, and must be, of the same size for the following code to function.
        // check the documentation of the 'getQuizzesByClass()' function for more info.
        Database.getAllStudentSubmissions(window.getUser().id, quizzes, submissions);
        JScrollPane QuizDisplayScrollPane = new JScrollPane(new QuizDisplayTable(window, quizzes, submissions));

        c.anchor = GridBagConstraints.CENTER;
        contentPanel.add(QuizDisplayScrollPane, c); c.gridy++;

        backButton.addActionListener(this);
        contentPanel.add(backButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton){
            window.switchPage(new StudentHomePage(window));
        }
    }
}

class ComposeFeedbackPage extends Page {
    JTextField subjectField;
    JTextArea messageArea;
    int instructorId;
    int studentId;
    JButton sendButton;
    JButton backButton;
    public ComposeFeedbackPage(Window window, int instructorId, int studentId) {
        super(window);
        setLayout(new BorderLayout());
        this.instructorId = instructorId;
        this.studentId = studentId;
        subjectField = new JTextField();
        messageArea = new JTextArea();

        sendButton = GUI_Elements.button("Send");
        sendButton.addActionListener(this);

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(new JLabel("Subject:"));
        panel.add(subjectField);
        panel.add(new JLabel("Message:"));
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);
        add(sendButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton){
            window.switchPage(new ManageStudentPage(window));
        } else if(e.getSource() == sendButton) {
            String subject = subjectField.getText();
            String message = messageArea.getText();
            Database.sendFeedback(instructorId, studentId, subject, message);
            JOptionPane.showMessageDialog(this, "Message Sent!");
            window.switchPage(new ManageStudentPage(window));
        }
    }
}

class AwardTrophyPage extends Page {
    JTextArea descriptionTextArea = new JTextArea(4, 40);
    JLabel descriptionLabel = GUI_Elements.label("Enter Student Achievement");
    JButton awardTrophyButton = GUI_Elements.button("Award Trophy");
    JButton selectIconButton = GUI_Elements.button("Select Trophy Icon");
    JButton cancelButton = GUI_Elements.button("Cancel");
    JPanel localPanel = GUI_Elements.panel(new GridBagLayout());
    JFileChooser fileChooser = new JFileChooser();

    AwardTrophyPage(Window window) {
        super(window);
        contentPanel.setLayout(new GridBagLayout());
        pageTitle.setText("Award Trophy");

        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);

        cancelButton.setBackground(GUI_Elements.WARNING_BACKGROUND);
        cancelButton.setBorder(GUI_Elements.BLACK_BORDER);

        selectIconButton.setPreferredSize(new Dimension(150, GUI_Elements.textFieldSize1.height));
        awardTrophyButton.setPreferredSize(new Dimension((GUI_Elements.textFieldSize1.width + selectIconButton.getPreferredSize().width + 20), GUI_Elements.BUTTON_SIZE.height));
        cancelButton.setPreferredSize(new Dimension((GUI_Elements.textFieldSize1.width + selectIconButton.getPreferredSize().width + 20), GUI_Elements.BUTTON_SIZE.height));

        selectIconButton.addActionListener(this);
        awardTrophyButton.addActionListener(this);
        cancelButton.addActionListener(this);


        // Grid Managemenet -----------------------------------------------------------------------------------------

        GridBagConstraints contentPanelGBC = new GridBagConstraints();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;


        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        localPanel.add(descriptionLabel, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy++;
        localPanel.add(descriptionTextArea, gbc);

        gbc.gridx++;
        localPanel.add(selectIconButton, gbc);


        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        localPanel.add(awardTrophyButton, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy++;
        localPanel.add(cancelButton, gbc);


        contentPanelGBC.gridx = 0;
        contentPanelGBC.gridy = 0;
        contentPanelGBC.fill = GridBagConstraints.BOTH;
        contentPanelGBC.anchor = GridBagConstraints.CENTER;
        contentPanel.add(localPanel, contentPanelGBC);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == selectIconButton) {

            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                if (!HelperFunctions.isValidImage(filePath)) {
                    JOptionPane.showMessageDialog(
                            window,
                            "The Icon file format must be: \".png\", \".gif\", or \".jpeg\".",
                            "Invalid Image",
                            JOptionPane.ERROR_MESSAGE
                    );
                    fileChooser.setSelectedFile(null);
                }
            }
        } else if(e.getSource() == awardTrophyButton){
            if(fileChooser.getSelectedFile() == null){
                HelperFunctions.showDialogIfError("You must select an Image for the Trophy", window);
                return;
            }
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            Database.insertTrophy(window.getCurrentClass().id, window.getCurrentStudent().id,
                    filePath, descriptionTextArea.getText());
            JOptionPane.showMessageDialog(
                    window,
                    "Trophy awarded successfully.",
                    "Successful Operation",
                    JOptionPane.INFORMATION_MESSAGE
            );
            window.switchPage(new ManageStudentPage(window));
        } else if(e.getSource() == cancelButton){
            window.switchPage(new ManageStudentPage(window));
        }
    }
}

class ComposeNewMessagePage extends Page implements ActionListener{
    JTextField subjectField = GUI_Elements.textField();
    JTextArea messageArea = new JTextArea();
    JButton cancelButton = GUI_Elements.button("Cancel");
    JButton sendButton = GUI_Elements.button("Send Feedback");
    JPanel localPanel = GUI_Elements.panel(new GridBagLayout());
    ComposeNewMessagePage(Window window){
        super(window);
        contentPanel.setLayout(new GridBagLayout());
        pageTitle.setText("Compose Feedback Message");

        cancelButton.setBackground(GUI_Elements.WARNING_BACKGROUND);
        cancelButton.setBorder(GUI_Elements.BLACK_BORDER);

        sendButton.setPreferredSize(new Dimension(GUI_Elements.textFieldSize1.width, GUI_Elements.BUTTON_SIZE.height));
        cancelButton.setPreferredSize(new Dimension(GUI_Elements.textFieldSize1.width, GUI_Elements.BUTTON_SIZE.height));

        sendButton.addActionListener(this);
        cancelButton.addActionListener(this);

        JLabel subjectLineLabel = GUI_Elements.label("Subject Line:");
        JLabel messageTextLabel = GUI_Elements.label("Message Text:");

        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane textAreaScroll = new JScrollPane(messageArea);
        textAreaScroll.setPreferredSize(new Dimension(GUI_Elements.textFieldSize1.width, 200));
        textAreaScroll.setOpaque(false);
        textAreaScroll.getViewport().setOpaque(false);
        textAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        textAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // Grid Managemenet -----------------------------------------------------------------------------------------

        GridBagConstraints contentPanelGBC = new GridBagConstraints();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0; gbc.gridy = 0;


        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        localPanel.add(subjectLineLabel, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy++;
        localPanel.add(subjectField, gbc);


        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        localPanel.add(messageTextLabel, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy++;
        localPanel.add(textAreaScroll, gbc);


        gbc.gridx++;
        localPanel.add(sendButton, gbc);


        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        localPanel.add(sendButton, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy++;
        localPanel.add(cancelButton, gbc);


        contentPanelGBC.gridx = 0; contentPanelGBC.gridy = 0;
        contentPanelGBC.fill = GridBagConstraints.BOTH;
        contentPanelGBC.anchor = GridBagConstraints.CENTER;
        contentPanel.add(localPanel, contentPanelGBC);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == sendButton){
            if(subjectField.getText().equals("") || messageArea.getText().equals("")){
                HelperFunctions.showDialogIfError("Subject Line and Message Text must not be empty", window);
                return;
            }
            Database.sendFeedback(window.getUser().id, window.getCurrentStudent().id, subjectField.getText(), messageArea.getText());
            JOptionPane.showMessageDialog(
                    window,
                    "Feedback message successfully sent.",
                    "Successful Operation",
                    JOptionPane.INFORMATION_MESSAGE
            );
            window.switchPage(new ManageStudentPage(window));
        } else if(e.getSource() == cancelButton){
            window.switchPage(new ManageStudentPage(window));
        }
    }
}

class ViewQuizStatsPage extends Page{
    JButton backButton = GUI_Elements.button("Back");
    ViewQuizStatsPage(Window window){
        super(window);
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weighty = 6.0;
        gbc.insets = new Insets(10, 10, 10, 10);

        pageTitle.setText("Individual Quiz Statistics");

        java.util.List<IndividualQuizStatistics> mcqStatistics = Database.getMCQSAnswersPerQuiz(window.quiz.id);
        DisplayMCQStats mcqTable = new DisplayMCQStats(mcqStatistics);

        java.util.List<IndividualQuizStatistics> essayStatistics = Database.getEssayAnswersPerQuiz(window.quiz.id);
        DisplayEssayStats essayTable = new DisplayEssayStats(essayStatistics);
        JPanel localPanel = new JPanel(new GridBagLayout());
        localPanel.setOpaque(false);
        localPanel.add(mcqTable, gbc);
        gbc.gridy++;
        localPanel.add(essayTable, gbc);

        JScrollPane scrollPane = new JScrollPane(localPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        scrollPane.setOpaque(false);
        contentPanel.add(scrollPane, gbc); gbc.gridy++;
        gbc.weighty = 1.0;
        contentPanel.add(backButton, gbc);
        backButton.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton){
            window.switchPage(new ManageClassPage(window));
        }
    }
}

