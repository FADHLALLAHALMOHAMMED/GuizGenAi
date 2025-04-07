package org.Program;

import org.Program.Entities.Class;
import org.Program.Entities.Question;
import org.Program.Entities.Quiz;
import org.Program.Entities.Student;

import com.github.jaiimageio.impl.plugins.gif.GIFImageMetadata;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.LINE_START;


/** this file includes the class "GUI_Elements" which contains functions that simplify the repeated creation of Components
 *  e.g. rather than instantiating a button, then setting its font, color, size, etc. you can just call the function
 *  and the function will do all those things for you and return the button.
 *  These types of functions are called "Factory Functions".
 *  Factory functions also have the added benefit of ensuring consistency in the program: all buttons are going to look the same.
 *  furthermore, if we want to change something about the all buttons in the program, for example, all we need is to
 *  change the factory function that produces the buttons.
* */
public class GUI_Elements {
    public final static String TEXT_FONT = "Montserrat";
    public final static Color BUTTON_COLOR = new Color(44, 44, 44);
    public final static Dimension BUTTON_SIZE = new Dimension(160, 50);
    public final static EmptyBorder BUTTON_MARGIN = new EmptyBorder(20, 20, 20, 20);
    public final static Color APP_BACKGROUND = new Color(118, 39, 255);
    public final static Color TEXT_FOREGROUND = new Color(255, 255, 255);
    public final static Color SECONDARY_BACKGROUND = new Color(255, 247, 209);
    public final static Color WARNING_BACKGROUND = new Color(223, 0, 0);
    public final static Border BLACK_BORDER = BorderFactory.createLineBorder(new Color(223, 0, 0));


    public final static Dimension textFieldSize1 = new Dimension(300, 30);

    public static JPanel panel() {

        JPanel panel = new JPanel();
        panel.setBackground(APP_BACKGROUND);
        panel.setForeground(TEXT_FOREGROUND);
        
        return panel;
    }

    public static JPanel panel(LayoutManager layout) {

        JPanel panel = new JPanel(layout);
        panel.setBackground(APP_BACKGROUND);
        panel.setForeground(TEXT_FOREGROUND);
        
        return panel;
    }

    public static JRadioButton radioButton(String text) {

        JRadioButton button = new JRadioButton(text);
        button.setBackground(APP_BACKGROUND);
        button.setForeground(TEXT_FOREGROUND);

        return button;
    }

    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(TEXT_FONT, Font.BOLD, 14));
        label.setForeground(new Color(255, 255, 255));
        return label;
    }

    public static JTextField textField(){
        JTextField textField = new JTextField();
        textField.setPreferredSize(textFieldSize1);
        return textField;
    }

    public static JButton button(String text){
        JButton button = new JButton(text);

        // aesthetics
        button.setForeground(new Color(255,255,255));
        button.setBackground(BUTTON_COLOR);
        button.setPreferredSize(BUTTON_SIZE);
        button.setBorder(BUTTON_MARGIN);
        button.setFocusable(false);

        return button;
    }
    
}

/** this file also includes components that extend Swing components.
 *  These are specialized components we designed for the purposes of our program.
 *  for example, the Question panel in going to be displayed to students when taking quizzes,
 *  and will display a question: the Text of the question, the answerChoices, the radio button for selecting a choice.
 * */

class LabeledTextField extends JPanel{
    JTextField textField = GUI_Elements.textField();
    LabeledTextField(String labelText){
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0; c.gridy = 0;

        JLabel label = new JLabel(labelText);
        this.add(label, c); c.gridy++;
        this.add(textField, c);
    }
    public String getText(){return textField.getText();}
}

// Displayed to students when attempting a quiz. displays the components of a single question.
// includes the label for the question test, radio buttons to display and select the question choices.
// used by the "QuizScrollPane" class, which displays all the questions(question panels).
class QuestionPanel extends JPanel{
    Vector<JRadioButton> answerButtons = new Vector<>();
    QuestionPanel(Question question){
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = LINE_START;

        JLabel questionLabel = new JLabel(HelperFunctions.toMultiLine(question.questionText, 80));
        this.add(questionLabel, c); c.gridy++; c.gridy++;

        char answerNotation = 'A';
        JRadioButton button;
        ButtonGroup answerButtonGroup = new ButtonGroup();

        for(String choice : question.answerChoices){
            String choiceText = String.format("%c) %s", answerNotation++, choice);
            button = new JRadioButton(HelperFunctions.toMultiLine(choiceText, 64));

            answerButtonGroup.add(button);
            answerButtons.add(button);
            this.add(button, c); c.gridy++;
        }
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x28557a)));
    }

    public int getChoice(){
        for(int i = 0; i < 4; i++)
            if (answerButtons.get(i).isSelected()) return i;
        return -1;
    }
}

// shown to students when attempting a quiz
// displays a scrollPane with all the questions, where each individual question is represented by a "QuestionPanel".
class QuizScrollPane extends JScrollPane {
    Vector<QuestionPanel> questionPanels = new Vector<>();
    QuizScrollPane(Vector<Question> questions){
        this.setPreferredSize(new Dimension(700, 750));
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel quizPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = LINE_START; c.gridx = c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);

        for(Question question: questions){
            QuestionPanel questionPanel = new QuestionPanel(question);
            questionPanels.add(questionPanel);
            quizPanel.add(questionPanel, c); c.gridy++;
        }

        this.setViewportView(quizPanel);
    }

    public Vector<Integer> getAnswersArray() {
        Vector<Integer> answersArray = new Vector<>();
        for (QuestionPanel question : this.questionPanels)
            answersArray.add(question.getChoice());
        return answersArray;
    }
}

// represents a panel to edit an individual question. and is displayed to instructors.
// the EditQuizScrollPane generates, contains, and displays multiple instances of this class. (an instance for each question)
// allows instructors to change the question text, answer choices, and set the correct answer.
class EditQuestionPane extends JPanel implements ActionListener {
    EditQuizScrollPane parent; // the scroll pane that will contain and manage this EditQuestionPane instance.
    int questionIndex; // the index(order) of this pane in its parent scroll pane.
    JTextArea questionTextArea;
    Vector<JTextField> answers = new Vector<>();
    Vector<JRadioButton> correctAnswerSelectors = new Vector<>();
    JButton moveUpButton = GUI_Elements.button("Move up ^");
    JButton moveDownButton = GUI_Elements.button("Move Down V");
    JButton deleteButton = GUI_Elements.button("Delete Question");
    boolean first; // a flag for if this is the first question in the questions page
    boolean last; // if this is the last question.

    // takes a question object to display, the scroll pane parent container, and the index of the question in the container.
    EditQuestionPane(Question question, EditQuizScrollPane parent, int questionIndex){
        super(new GridBagLayout());
        this.questionIndex = questionIndex;
        this.parent = parent;
        this.first = questionIndex == 0;
        this.last = questionIndex == parent.numOfQuestions - 1;

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = LINE_START; c.gridwidth = 2;

        JLabel questionTextLabel = new JLabel("(" + questionIndex + ") Question Text:"); c.gridwidth = 1;
        JLabel correctAnswerLabel = new JLabel("<html>Correct<br/>Answer:</html>");
        JLabel answerChoicesLabel = new JLabel("Answer Choices Text:");

        // placing labels
        c.gridx = 0; c.gridy = 0;
        this.add(questionTextLabel, c); c.gridy = 2;
        this.add(correctAnswerLabel, c); c.gridx = 1;
        this.add(answerChoicesLabel, c);

        // placing QuestionText Area
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;

        questionTextArea = new JTextArea(question.questionText);
        questionTextArea.setMaximumSize(new Dimension(400, 120));
        questionTextArea.setPreferredSize(new Dimension(400, 120));
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        this.add(questionTextArea, c);
        c.gridwidth = 1;

        // placing radio buttons
        c.gridx = 0; c.gridy = 3; c.anchor = CENTER;
        ButtonGroup buttonGroup = new ButtonGroup();
        for(char i = 'A'; i < 'E'; i++){
            JRadioButton correctAnswerSelectorButton = new JRadioButton(" " + i + " ");
            buttonGroup.add(correctAnswerSelectorButton);
            correctAnswerSelectors.add(correctAnswerSelectorButton);
            this.add(correctAnswerSelectorButton, c); c.gridy++;
        }
        correctAnswerSelectors.get(question.correctAnswerIndex).setSelected(true);

        // placing text fields
        c.gridx = 1; c.gridy = 3; c.anchor = LINE_START;
        for(String answerChoiceText: question.answerChoices){
            JTextField answerChoiceTextField = GUI_Elements.textField();
            answerChoiceTextField.setText(answerChoiceText);
            answerChoiceTextField.setSize(80, 20);
            answers.add(answerChoiceTextField);
            this.add(answerChoiceTextField, c); c.gridy++;
        }

        // placing Buttons
        c.gridx = 3; c.gridy = 2; c.gridheight = 2;
        moveUpButton.addActionListener(this);
        moveDownButton.addActionListener(this);
        deleteButton.addActionListener(this);

        if(!first) this.add(moveUpButton, c); // only display the move up button if this isn't the first question pane.

        c.gridy = 4;
        if(!last) this.add(moveDownButton, c); // only display the move down button if this isn't the last question pane.

        c.gridy = 6;
        deleteButton.setBackground(new Color(192, 64, 64));
        this.add(deleteButton, c);

        this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x28557a)));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == moveUpButton)
            parent.switchPanes(this.questionIndex, this.questionIndex-1);
        else if(e.getSource() == moveDownButton)
            parent.switchPanes(this.questionIndex, this.questionIndex+1);
        else if(e.getSource() == deleteButton){
            parent.delete(this.questionIndex);
        }
    }

    // returns a Question object updated with the edits from the user.
    public Question getQuestion(){
        Vector<String> answerChoices = new Vector<>();
        for(JTextField answerChoiceTextField: answers)
            answerChoices.add(answerChoiceTextField.getText());

        int correctAnswerIndex = -1;
        for(int i = 0; i < 4; i++)
            if(correctAnswerSelectors.get(i).isSelected()){
                correctAnswerIndex = i;
                break;
            }

        return new Question(questionTextArea.getText(), answerChoices, correctAnswerIndex);
    }

    // the parent (EditQuizScrollPane) container class calls this function after adding, deleting, moving questions.
    // to update the graphics of the EditQuestionPane: add or remove moveUp/moveDown Buttons, update question index label.
    public void update(int newQuestionIndex){
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = LINE_START; c.gridx = 3; c.gridy = 2; c.gridheight = 2;
        int lastIndex = parent.numOfQuestions - 1; // the index of the last question pane in the parent container.

        if(first && newQuestionIndex != 0){    // if this used to be the first panel, but is no longer the first.
            this.add(moveUpButton, c);             // and the move up button.
            first = false;

        } else if(!first && newQuestionIndex == 0){ // if this wasn't the first panel, but became the first.
            this.remove(moveUpButton);          // remove the move up button, since it's already the highest panel.
            first = true;

        } else if(last && newQuestionIndex != lastIndex){ // if this used to be the last panel, But no longer is:
            c.gridy = 4;
            this.add(moveDownButton, c);
            last = false;

        } else if(!last && newQuestionIndex == lastIndex){ // if panel is now the last one
            this.remove(moveDownButton);
            last = true;
        }
        questionIndex = newQuestionIndex;
        this.updateUI();
    }
}

// displayed to instructor when editing a quiz: hold multiple "EditQuestionPane" panels each displaying a single question.
// allows user the add, edit, remove, and move questions
class EditQuizScrollPane extends JScrollPane{
    Vector<EditQuestionPane> editQuestionPanes = new Vector<>();
    int numOfQuestions;
    JPanel editQuizPanel = new JPanel(new GridBagLayout());
    EditQuizScrollPane(Vector<Question> questions){
        this.setPreferredSize(new Dimension(700, 750));
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        numOfQuestions = questions.size();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = LINE_START; c.gridy = 0;
        c.weightx = 1.0;
        c.insets = new Insets(10, 10, 10, 10);

        int i = 0;
        for(Question question: questions){                      // create editQuestionPanel instance for every question.
            EditQuestionPane editQuestionPanel = new EditQuestionPane(question, this, i++);
            editQuestionPanes.add(editQuestionPanel);           // add panel to vector for ease of access later.
            editQuizPanel.add(editQuestionPanel, c); c.gridy++; // add to the GUI.
        }
        c.weighty = 1.0;
        editQuizPanel.add(Box.createVerticalGlue(), c);

        this.setViewportView(editQuizPanel);
    }
    public void switchPanes(int paneIndex1, int paneIndex2){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = LINE_START;
        c.insets = new Insets(10, 10, 10, 10);

        editQuizPanel.remove(editQuestionPanes.get(paneIndex1)); // remove both components for layout
        editQuizPanel.remove(editQuestionPanes.get(paneIndex2));

        c.gridy = paneIndex1;
        editQuizPanel.add(editQuestionPanes.get(paneIndex2), c); // add the second one in place of the first.
        c.gridy = paneIndex2;
        editQuizPanel.add(editQuestionPanes.get(paneIndex1), c); // and the first in place of the second.


        editQuestionPanes.get(paneIndex1).update(paneIndex2); // update both components (GUI and attributes)
        editQuestionPanes.get(paneIndex2).update(paneIndex1);

        // also swap the location of the elements in the vector to maintain consistency.
        HelperFunctions.swapElements(editQuestionPanes, paneIndex1, paneIndex2);

        this.editQuizPanel.updateUI();
        this.repaint();
    }

    // delete a editQuestionPanel from the scroll bar.
    // called by a editQuestionPanel to delete itself, after delete button is pressed.
    public void delete(int index){ // index of panel to delete
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.gridy = index;

        editQuizPanel.remove(editQuestionPanes.get(index));
        for(int i = index; i < numOfQuestions - 1; i++){
            editQuizPanel.add(editQuestionPanes.get(i + 1), c); c.gridy++;  // shift the panel's position in the scroll panel to overwrite the panel we want to delete.
            editQuestionPanes.set(i, editQuestionPanes.get(i + 1));         // also shift the panel's position in the vector to maintain consistency.
            editQuestionPanes.get(i).update(i);                             // update the editQuestionPanel's attributes and GUI
        }
        editQuestionPanes.removeElementAt(numOfQuestions-1);           // the shifting operation duplicates the last element, get rid of the duplicate.
        numOfQuestions--;                                                    // decrement the size since we deleted a question.

        if(index == numOfQuestions)                                          // treat edge case that happens if you delete the last panel
            editQuestionPanes.get(numOfQuestions - 1).update(numOfQuestions - 1);

        this.editQuizPanel.updateUI();
        this.repaint();
    }

    // called with a new question is added.
    public void addQuestionPanel(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        numOfQuestions++;
        c.gridy = numOfQuestions - 1;

        EditQuestionPane questionPane = new EditQuestionPane(new Question(), this, numOfQuestions - 1); // create a question pane
        editQuestionPanes.add(questionPane);                  // add to the list of question panes
        this.editQuizPanel.add(questionPane, c);              // add to the GUI

        // update the question pane before (to add the move down button, since it is no longer the last Question pane)
        editQuestionPanes.get(numOfQuestions - 2).update(numOfQuestions - 2);
        this.editQuizPanel.updateUI();
        this.repaint();
    }

    // called when the user wants to save the changes to the questions: reads and returns the updated/edited questions.
    public Vector<Question> getQuestions(){
        Vector<Question> questions = new Vector<>();
        for(EditQuestionPane questionPane: editQuestionPanes)
            questions.add(questionPane.getQuestion());
        return questions;
    }
}

// displays the information for a single class, including ID, name, and icon.
class ClassPanel extends JPanel{

    Class class_;

    ClassPanel(Class class_) {

        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.anchor = CENTER;
        this.setBorder(new EmptyBorder(20, 40, 20, 40));

        this.class_ = class_;
        this.setPreferredSize(new Dimension(200, 250));
        JLabel className = new JLabel(class_.name);
        JLabel classIcon = new JLabel(class_.classIcon);
        JLabel classId = new JLabel(String.format("Class ID: %d", class_.id));
        this.add(classIcon, c); c.gridy++;
        this.add(className, c); c.gridy++;
        this.add(classId, c);
    }
}

// displays multiple ClassPanel to the user, where each ClassPanel holds the information for a single class.
// allows user the click on a class panel to go to the class's "ManageClassPage".
class ClassesPane extends JPanel implements MouseListener {
    Window window;
    Vector<ClassPanel> classPanels = new Vector<>();
    ClassesPane(Window window){
        this.window = window;
        int instructorId = window.getUser().id;
        Vector<Class> classes = Database.getInstructorClasses(instructorId);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
//        this.setPreferredSize(new Dimension(600, 500));
//        this.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        for(Class class_ : classes){
            ClassPanel classPanel = new ClassPanel(class_);
            classPanels.add(classPanel);
            this.add(classPanel);
            classPanel.addMouseListener(this);
        }
    }


    public void mouseClicked(MouseEvent e) {
        for(ClassPanel classPanel : classPanels){
            if(e.getSource() == classPanel){
                window.setCurrentClass(classPanel.class_);
                window.switchPage(new ManageClassPage(window));
                return;
            }
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

//
class ManageStudentsListPanel extends JPanel implements ActionListener, ListSelectionListener {
    Window window;
    Class class_;
    JList<String> studentList;
    Vector<Student> students;
    private final JButton goBackButton = GUI_Elements.button("Go back");

    ManageStudentsListPanel(Window window) {

        super(new GridBagLayout());

        class_ = window.getCurrentClass();
        this.students = Database.getStudentsInClass(class_.id);
        this.window = window;
        this.setBackground(GUI_Elements.SECONDARY_BACKGROUND);

        JLabel manageStudentsLabel = GUI_Elements.label("Manage Students");
        manageStudentsLabel.setForeground(Color.BLACK);
        manageStudentsLabel.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 20));
        JLabel descriptionLabel = GUI_Elements.label("Select one or more students to manaage:");
        descriptionLabel.setForeground(Color.BLACK);

        JPanel studentsListPanel = GUI_Elements.panel(new GridBagLayout());
        studentsListPanel.setBackground(Color.WHITE);

        goBackButton.setBackground(GUI_Elements.WARNING_BACKGROUND);
        goBackButton.addActionListener(this);


        studentList = new JList<>(HelperFunctions.studentsToStringVector(students));
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setVisibleRowCount(15);
        studentList.addListSelectionListener(this);

        JScrollPane scrollPane = new JScrollPane(studentList);

        
        
        
        // Grid Management --------------------------------------------------------------------------
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = c.gridx = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 10, 0, 10);
        
        // ---------------
        GridBagConstraints innerC = new GridBagConstraints();
        innerC.gridx = 0;
        innerC.gridy = 0;
        innerC.weightx = 1.0;
        innerC.weighty = 1.0;
        innerC.fill = GridBagConstraints.BOTH;

        studentsListPanel.add(scrollPane, innerC);
        // ---------------

        c.insets = new Insets(20, 10, 0, 10);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        this.add(manageStudentsLabel, c);
        
        c.insets = new Insets(0, 10, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.gridy++;
        this.add(descriptionLabel, c);
        
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridy++;
        this.add(studentsListPanel, c);
        
        c.insets = new Insets(0, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy++;
        this.add(goBackButton, c);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == goBackButton)
            window.switchPage(new InstructorHomePage(window));
    }

    @Override
    // Called when an item is selected or deselected from the list, displays how many students are selected.
    public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) return;
        Student selectedStudent = students.get(studentList.getSelectedIndex());
        window.switchPage(new ManageStudentPage(window, selectedStudent, class_));
    }

    // called when a student is added or removed from the class, to display updated information.
    public void update(){
        students = Database.getStudentsInClass(window.getCurrentClass().id);
        studentList.setListData(HelperFunctions.studentsToStringVector(students));
    }
}

class AddStudentListPanel extends JPanel implements ActionListener, ListSelectionListener {
    Window window;
    JButton addStudentsButton = GUI_Elements.button("Add Selected Students");
    JButton cancelButton =  GUI_Elements.button("Cancel");
    JLabel selectionCountLabel = new JLabel("You have not selected any students");
    JList<String> studentList;
    Vector<Student> students;
    AddStudentListPanel(Window window){
        super(new GridBagLayout());
        this.students = Database.getStudentsNotInClass(window.getCurrentClass().id);
        this.window = window;
        this.setBackground(GUI_Elements.SECONDARY_BACKGROUND);


        JLabel manageStudentsLabel = GUI_Elements.label("Add Students");
        manageStudentsLabel.setForeground(Color.BLACK);
        manageStudentsLabel.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 20));
        JLabel descriptionLabel = GUI_Elements.label("Select one or more students to add to your class:");
        descriptionLabel.setForeground(Color.BLACK);

        JPanel studentsListPanel = GUI_Elements.panel(new GridBagLayout());
        studentsListPanel.setBackground(Color.WHITE);

        cancelButton.setBackground(GUI_Elements.WARNING_BACKGROUND);
        cancelButton.addActionListener(this);
        addStudentsButton.addActionListener(this);

        studentList = new JList<>(HelperFunctions.studentsToStringVector(students));
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        studentList.setVisibleRowCount(15);
        studentList.addListSelectionListener(this);

        JScrollPane scrollPane = new JScrollPane(studentList);

        
        
        
        // Grid Management --------------------------------------------------------------------------
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = c.gridx = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 10, 0, 10);
        
        // ---------------
        GridBagConstraints innerC = new GridBagConstraints();
        innerC.gridx = 0;
        innerC.gridy = 0;
        innerC.weightx = 1.0;
        innerC.weighty = 1.0;
        innerC.fill = GridBagConstraints.BOTH;
        
        studentsListPanel.add(scrollPane, innerC);
        // ---------------
        
        c.gridwidth = 2;
        c.insets = new Insets(20, 10, 0, 10);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        this.add(manageStudentsLabel, c);
        
        c.insets = new Insets(0, 10, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.gridy++;
        this.add(descriptionLabel, c);
        
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridy++;
        this.add(studentsListPanel, c);
        
        c.gridwidth = 1;
        c.insets = new Insets(0, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy++;
        this.add(addStudentsButton, c);
        c.gridx++;
        this.add(cancelButton, c);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == addStudentsButton){

            int[] selectionIndices = studentList.getSelectedIndices();
            if(selectionIndices.length != 0){

                Vector<Student> selectedStudents = new Vector<>();
                for(int index: selectionIndices)
                    selectedStudents.add(students.get(index));

                Database.addStudentsToClass(selectedStudents, window.getCurrentClass().id);
                window.getPage().update();
            } else {
                System.out.println("You have no selected any students to add to the class.");
            }

        } else if(e.getSource() == cancelButton){
            window.setCurrentClass(null);
            window.switchPage(new InstructorHomePage(window));
        }
    }

    @Override
    // Called when an item is selected or deselected from the list, displays how many students are selected.
    public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) return;
        int[] selectionIndices = studentList.getSelectedIndices();
        switch (selectionIndices.length){
            case 0 -> selectionCountLabel.setText("You have not selected any students");
            case 1 -> selectionCountLabel.setText("One student selected");
            default -> selectionCountLabel.setText(String.format("%d students selected", selectionIndices.length));
        }
        selectionCountLabel.updateUI();
    }

    // called when a student is added or removed from the class, to display updated information.
    public void update(){
        students = Database.getStudentsNotInClass(window.getCurrentClass().id);
        studentList.setListData(HelperFunctions.studentsToStringVector(students));
    }
}


class RemoveStudentListPanel extends JPanel implements ActionListener, ListSelectionListener {
    Window window;
    JButton removeStudentsButton = GUI_Elements.button("Remove Selected Students");
    JButton cancelButton =  GUI_Elements.button("Cancel");
    JLabel selectionCountLabel = new JLabel("You have not selected any students");
    JList<String> studentList;
    Vector<Student> students;
    RemoveStudentListPanel(Window window) {
        
        super(new GridBagLayout());
        this.students = Database.getStudentsInClass(window.getCurrentClass().id);
        this.window = window;
        this.setBackground(GUI_Elements.SECONDARY_BACKGROUND);

        JLabel manageStudentsLabel = GUI_Elements.label("Add Students");
        manageStudentsLabel.setForeground(Color.BLACK);
        manageStudentsLabel.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 20));
        JLabel descriptionLabel = GUI_Elements.label("Select one or more students to add to your class:");
        descriptionLabel.setForeground(Color.BLACK);

        JPanel studentsListPanel = GUI_Elements.panel(new GridBagLayout());
        studentsListPanel.setBackground(Color.WHITE);

        cancelButton.setBackground(GUI_Elements.WARNING_BACKGROUND);
        cancelButton.addActionListener(this);
        removeStudentsButton.addActionListener(this);

        studentList = new JList<>(HelperFunctions.studentsToStringVector(students));
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        studentList.setVisibleRowCount(15);
        studentList.addListSelectionListener(this);

        JScrollPane scrollPane = new JScrollPane(studentList);

        
        
        
        // Grid Management --------------------------------------------------------------------------
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = c.gridx = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 10, 0, 10);
        
        // ---------------
        GridBagConstraints innerC = new GridBagConstraints();
        innerC.gridx = 0;
        innerC.gridy = 0;
        innerC.weightx = 1.0;
        innerC.weighty = 1.0;
        innerC.fill = GridBagConstraints.BOTH;
        
        studentsListPanel.add(scrollPane, innerC);
        // ---------------
        
        c.gridwidth = 2;
        c.insets = new Insets(20, 10, 0, 10);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        this.add(manageStudentsLabel, c);
        
        c.insets = new Insets(0, 10, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.gridy++;
        this.add(descriptionLabel, c);
        
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridy++;
        this.add(studentsListPanel, c);

        c.fill = GridBagConstraints.CENTER;
        c.gridy++;
        this.add(selectionCountLabel, c);
        
        c.gridwidth = 1;
        c.insets = new Insets(0, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy++;
        this.add(removeStudentsButton, c);
        c.gridx++;
        this.add(cancelButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == removeStudentsButton){

            int[] selectionIndices = studentList.getSelectedIndices();
            if(selectionIndices.length != 0){

                Vector<Student> selectedStudents = new Vector<>();
                for(int index: selectionIndices)
                    selectedStudents.add(students.get(index));

                Database.removeStudentsFromClass(selectedStudents, window.getCurrentClass().id);
                window.getPage().update();
            } else {
                System.out.println("You have no selected any students to add to the class.");
            }

        } else if(e.getSource() == cancelButton){
            window.setCurrentClass(null);
            window.switchPage(new InstructorHomePage(window));
        }
    }

    @Override
    // Called when an item is selected or deselected from the list, displays how many students are selected.
    public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) return;
        int[] selectionIndices = studentList.getSelectedIndices();
        switch (selectionIndices.length){
            case 0 -> selectionCountLabel.setText("You have not selected any students");
            case 1 -> selectionCountLabel.setText("One student selected");
            default -> selectionCountLabel.setText(String.format("%d students selected", selectionIndices.length));
        }
        selectionCountLabel.updateUI();
    }

    // called when a student is added or removed from the class, to display updated information.
    public void update(){
        this.students = Database.getStudentsInClass(window.getCurrentClass().id);
        this.studentList.setListData(HelperFunctions.studentsToStringVector(students));
    }
}

//class classesList extends JList<String> implements ListSelectionListener{
//    JLabel selectionCountLabel;
//    classesList(Vector<Class> classes){
//        Vector<String> classStrings = new Vector<>();
//
//        for(Class class_: classes)
//            classStrings.add(String.format("\t%-32s\t(Class ID: %d)", class_.name, class_.id));
//
//        this.setListData(classStrings);
//        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        this.addListSelectionListener(this);
//    }
//    public void setSelectionCountLabel(JLabel selectionCountLabel) {
//         this.selectionCountLabel = selectionCountLabel;
//    }
//
//    @Override
//    public void valueChanged(ListSelectionEvent e) {
//        if(selectionCountLabel != null) {
//            int[] selectionIndices = this.getSelectedIndices();
//            switch (selectionIndices.length) {
//                case 0 -> selectionCountLabel.setText("You have not selected any students");
//                case 1 -> selectionCountLabel.setText("One student selected");
//                default -> selectionCountLabel.setText(String.format("%d students selected", selectionIndices.length));
//            }
//        }
//    }
//}

class QuizSettingsPanel extends JPanel implements ListSelectionListener{
    LabeledTextField quizTitleTextField = new LabeledTextField("Quiz Title:");
    DatePicker startDatePicker = new DatePicker();
    DatePicker endDatePicker = new DatePicker();
    Vector<Class> classes;
    JList<String> classesList;
    JLabel selectionCountLabel = new JLabel("You have not selected any students");
    QuizSettingsPanel(Window window){
        super(new GridBagLayout());
        this.setPreferredSize(new Dimension(400, 400));
        GridBagConstraints c = new GridBagConstraints();
        c.insets  = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0; c.gridy = 0;

        classes = Database.getInstructorClasses(window.getUser().id);
        Vector<String> classesStrings = HelperFunctions.classesToStringVector(classes);

        classesList = new JList<>(classesStrings);
        classesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        classesList.addListSelectionListener(this);

        this.add(quizTitleTextField, c); c.gridy++;
        this.add(new JLabel("Quiz Start Date"), c); c.gridy++;
        this.add(startDatePicker,c ); c.gridy++;
        this.add(new JLabel("Quiz End Date"), c); c.gridy++;
        this.add(endDatePicker,c ); c.gridy++;
        this.add(classesList, c); c.gridy++;
        this.add(selectionCountLabel, c);
    }
    public String getQuizTitle(){return this.quizTitleTextField.getText();}
    public Date getQuizStartDate(){return this.startDatePicker.getDateTime();}
    public Date getQuizEndDate(){return this.endDatePicker.getDateTime();}
    public Vector<Class> getSelectedClasses(){
        Vector<Class> selectedClasses = new Vector<>();
        for(int classIndex: this.classesList.getSelectedIndices())
            selectedClasses.add(classes.get(classIndex));
        return selectedClasses;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) return;
        if(selectionCountLabel != null) {
            int[] selectionIndices = classesList.getSelectedIndices();
            switch (selectionIndices.length) {
                case 0 -> selectionCountLabel.setText("You have not selected any classes");
                case 1 -> selectionCountLabel.setText("One class selected");
                default -> selectionCountLabel.setText(String.format("%d classes selected", selectionIndices.length));
            }
        }
    }
}

class LabeledComboBox extends JPanel{
    JComboBox<String> comboBox;
    LabeledComboBox(String[] listElements, String listLabel, ItemListener itemListener){
        super(new BorderLayout());
        add(new JLabel(listLabel), BorderLayout.NORTH);
        comboBox = new JComboBox<>(listElements);
        comboBox.setMaximumRowCount(12);
        comboBox.addItemListener(itemListener);
        add(comboBox, BorderLayout.SOUTH);
    }

    public int getSelectedItemIndex(){return comboBox.getSelectedIndex();}
    public void updateListItems(String[] listElements){
        comboBox.removeAllItems();
        for(String element: listElements)
            comboBox.addItem(element);
    }
}

class DatePicker extends JPanel implements ItemListener{
    LabeledComboBox yearList = new LabeledComboBox(Constants.YEARS, "year", this);
    LabeledComboBox monthList = new LabeledComboBox(Constants.MONTHS, "month", this);
    LabeledComboBox dayList = new LabeledComboBox(Constants.DAYS_31, "day", this);
    LabeledComboBox hourList = new LabeledComboBox(Constants.HOURS, "hour", this);
    LabeledComboBox minuteList = new LabeledComboBox(Constants.MINUTES, "minute", this);
    DatePicker(){
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);

        this.add(yearList, c); c.gridx++;
        this.add(monthList, c); c.gridx++;
        this.add(dayList, c); c.gridx++;
        this.add(hourList, c); c.gridx++;
        this.add(minuteList, c);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource() == monthList.comboBox && e.getStateChange() == ItemEvent.SELECTED)
            switch(monthList.getSelectedItemIndex()){
                case 0, 2, 4, 6, 7, 9, 11 ->  dayList.updateListItems(Constants.DAYS_31);
                case 3, 5, 8, 10 -> dayList.updateListItems(Constants.DAYS_30);
                case 1 -> dayList.updateListItems(Constants.DAYS_29);
            }
    }

    public Date getDateTime(){
        return new GregorianCalendar(yearList.getSelectedItemIndex() + 2025,
                monthList.getSelectedItemIndex(),
                dayList.getSelectedItemIndex() + 1,
                hourList.getSelectedItemIndex(),
                minuteList.getSelectedItemIndex() * 5).getTime();
    }
}

class QuizSelectionScrollPane extends JScrollPane implements MouseListener{
    Vector<QuizPanel> quizPanels = new Vector<>();
    Vector<Quiz> quizzes;
    Window window;
    QuizSelectionScrollPane(Window window, Vector<Quiz> quizzes){
        this.setPreferredSize(new Dimension(700, 750));
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.quizzes = quizzes;
        this.window = window;

        JPanel quizzesSelectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);

        for(Quiz quiz : quizzes){
            QuizPanel quizPanel = new QuizPanel(quiz);
            quizPanel.addMouseListener(this);
            quizPanels.add(quizPanel);
            quizzesSelectionPanel.add(quizPanel, c); c.gridy++;
        }

        this.setViewportView(quizzesSelectionPanel);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        for(QuizPanel quizPanel: quizPanels){
            if(e.getSource() == quizPanel){

                String result = HelperFunctions.StudentCanTakeQuiz(quizPanel.quiz, window.getUser().id);
                if(HelperFunctions.showDialogIfError(result, window)) return;
                quizPanel.quiz.questions = Database.getQuizQuestions(quizPanel.quiz);
                window.quiz = quizPanel.quiz;
                window.switchPage(new QuizPage(window));
            }
        }
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

class QuizPanel extends JPanel{
    Quiz quiz;
    QuizPanel(Quiz quiz){
        super(new GridBagLayout());
        this.setPreferredSize(new Dimension(400, 150));
        this.quiz = quiz;
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = c.gridy = 0;
        c.anchor = CENTER;
        this.add(new JLabel(quiz.title), c);
        c.anchor = LINE_START; c.gridy++;
        this.add(new JLabel(String.format("Start Time: %s", quiz.startDateTime.toString())), c);
        c.gridy++;
        this.add(new JLabel(String.format("End Time: %s", quiz.endDateTime.toString())), c);
    }
}

// this class allows for components to be inserted into a table.
// I tried using the JTable defined in Swing, but it's too complicated. I found that defining the class myself is easier.
class Table extends JPanel{
    GridBagConstraints c = new GridBagConstraints();
    Table(Vector<String> headers){
        this();
        this.setHeaders(headers);
    }
    Table(){
        super(new GridBagLayout());
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
    }

    public void insert(Component component, int row, int col){
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, GUI_Elements.BUTTON_COLOR),
                new EmptyBorder(5, 5, 5, 5)
        ));
        containerPanel.setOpaque(false);
        containerPanel.add(component, BorderLayout.CENTER);
        c.gridx = col; c.gridy = row + 1;
        this.add(containerPanel, c);
    }

    public void setBackground(Color color){
        this.setOpaque(true);
        super.setBackground(color);
    }

    public void setHeaders(Vector<String> headers){
        c.gridy = c.gridx = 0;
        for(String header: headers) {

            JPanel containerPanel = new JPanel(new BorderLayout());
            containerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, GUI_Elements.BUTTON_COLOR),
                    new EmptyBorder(5, 5, 5, 5)));

            containerPanel.setOpaque(true);
            containerPanel.setBackground(new Color(0x28557a));

            JLabel label = new JLabel(header, SwingConstants.CENTER);
            label.setForeground(Color.WHITE);

            containerPanel.add(label, BorderLayout.CENTER);

            this.add(containerPanel, c);
            c.gridx++;
        }
    }
}

class QuizDisplayTable extends JPanel implements ActionListener{
    Window window;
    Class class_;
    Student student;
    Vector<Quiz> quizzes;
    Vector<Quiz> completedQuizzes = new Vector<>();
    Vector<JButton> buttons = new Vector<>();
    QuizDisplayTable(Window window, Student student, Class class_){
        this.window = window;
        this.class_ = class_;
        this.student = student;
        quizzes = Database.getQuizzesByClass(class_.id);
        Vector<String> headers = new Vector<>(Arrays.asList("ID", "Title", "Start Date", "End Date", "Action"));

        Table table = new Table(headers);
        table.setBackground(new Color(238,238,228));
        int row = 0;
        int col = 0;
        for(Quiz quiz: quizzes){

            table.insert(new JLabel(Integer.toString(quiz.id), SwingConstants.CENTER), row, col++);
            table.insert(new JLabel(quiz.title, SwingConstants.CENTER), row, col++);
            table.insert(new JLabel(quiz.startDateTime.toString(), SwingConstants.CENTER), row, col++);
            table.insert(new JLabel(quiz.endDateTime.toString(), SwingConstants.CENTER), row, col++);

            String status = HelperFunctions.StudentQuizStatus(quiz, student.id); // retrieve completion status: not started, completed, not completed
            if(status == null){                 // means the student Completed the quiz
                JButton button = GUI_Elements.button("View");
                button.addActionListener(this);
                completedQuizzes.add(quiz);
                buttons.add(button);
                table.insert(button, row++, col);
            } else {                            // the student hasn't completed the quiz, reason stored in the "status" string.
                table.insert(new JLabel(status), row++, col);
            }
            col = 0;
        }
        this.add(table);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(int i = 0; i < buttons.size(); i++){
            if(e.getSource() == buttons.get(i)){
                window.switchPage(new ViewStudentSubmissionPage(window, student, completedQuizzes.get(i), class_));
            }
        }
    }
}

class QuizSubmissionDisplay extends JPanel{
    QuizSubmissionDisplay(int studentId, int quizId){
        Vector<Question> questionsAndAnswers = Database.getStudentAnswers(quizId, studentId);
        Vector<String> headers = new Vector<>(Arrays.asList("ID", "Question Text", "Students Answer", "Correct Answer", "status"));

        Table table = new Table(headers);
        table.setBackground(new Color(238,238,228));
        int row = 0;
        int col = 0;
        for(Question question: questionsAndAnswers){
            table.insert(new JLabel(Integer.toString(question.id), SwingConstants.CENTER), row, col++);
            table.insert(textArea(question.questionText), row, col++);
            table.insert(textArea(question.answerChoices.get(question.selectedAnswerIndex)), row, col++);
            table.insert(textArea(question.answerChoices.get(question.correctAnswerIndex)), row, col++);
            String answerIsCorrect = question.correctAnswerIndex == question.selectedAnswerIndex ? "Correct" : "Incorrect";
            table.insert(new JLabel(answerIsCorrect, SwingConstants.CENTER), row++, col);
            col = 0;
        }
        this.add(table);
    }

    public JTextArea textArea(String text){
        JTextArea textArea = new JTextArea(text);
        textArea.setMaximumSize(new Dimension(200, 100));
        textArea.setPreferredSize(new Dimension(200, 100));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
}

