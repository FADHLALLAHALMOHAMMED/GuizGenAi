package org.Program;

import org.Program.Entities.*;
import org.Program.Entities.Class;

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
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.LAST_LINE_END;
import static java.awt.GridBagConstraints.LINE_START;
import static java.awt.GridBagConstraints.NONE;


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

    public static JPanel secondaryPanel(LayoutManager layout) {

        JPanel panel = new JPanel(layout);
        panel.setBackground(SECONDARY_BACKGROUND);
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

    public static JPasswordField passwordField(){
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(textFieldSize1);
        return passwordField;
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

/*
   this file also includes components that extend Swing components.
   These are specialized components we designed for the purposes of our program.
   for example, the Question panel in going to be displayed to students when taking quizzes,
   and will display a question: the Text of the question, the answerChoices, the radio button for selecting a choice.
 */

/**
 * a simple component that return a Text field with a label above and to the left of the text field.
 * created to simplify the process of making this commonly used structure.
 */
class LabeledTextField extends JPanel{
    JTextField textField = GUI_Elements.textField();
    LabeledTextField(String labelText){
        super(new GridBagLayout());
        this.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0; c.gridy = 0;

        this.add(GUI_Elements.label(labelText), c); c.gridy++;
        this.add(textField, c);
    }
    public String getText(){return textField.getText();}
}


class LabeledPasswordField extends JPanel{
    JPasswordField passwordField = GUI_Elements.passwordField();
    LabeledPasswordField(String labelText){
        super(new GridBagLayout());
        this.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0; c.gridy = 0;

        this.add(GUI_Elements.label(labelText), c); c.gridy++;
        this.add(passwordField, c);
    }
    public String getText(){return new String(passwordField.getPassword());}
}

/**
 * a simple component that return a comboBox with a label above and to the left of the comboBox.
 * created to simplify the process of making this commonly used structure.
 */
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

    /**
     * used to update the elements in the combo box with a new set of elements.
     * specifically for date picker, where we update the values of the 'Day' comboBox depending on the month selected.
     * @param listElements the elements to be displayed in the comboBox.
     */
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

        yearList.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        monthList.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        dayList.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        hourList.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        minuteList.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
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

/* --------------------------------------------------------------------------------------------------------------------
   The following classes (QuizScrollPane, QuestionPanel, MCQuestionPanel, and EssayQuestionPanel) are used to display the
   Quiz and its Questions To students attempting a quiz.
   MCQuestionPanel and EssayQuestionPanel are specialized components for displaying Individual MCQ and Essay Questions respectively.
   And are both subclasses for the abstract class QuestionPanel, which implements their common features.

   QuizScrollPane can display any number of MCQuestionPanels or EssayQuestionPanels, and in any order.
   QuizScrollPane displays the entire quiz in a scrollable format.
 */

/**
 * An abstract super class of MCQuestionPanel and EssayQuestionPanel that implements the common features of both types.
 */
abstract class QuestionPanel extends JPanel{
    /**
     * @param question: the question that will be displayed by this QuestionPane.
     */
    QuestionPanel(Question question){
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = LINE_START;

        JLabel questionLabel = new JLabel(HelperFunctions.toMultiLine(question.questionText, 150));
        this.add(questionLabel, c); c.gridy++; c.gridy++;

        this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x28557a)));
    }

    /**
     * and abstract method that must be implemented by the 2 child classes of this class.
     * @return a Question object of this panels question with the students answer stored in its attributes.
     */
    public abstract Question getAnswer();
}

/**
 *  displays an individual Multiple Choice Question to students taking the quiz.
 *  displays 4 radio buttons to allow for the selection of one answer choice.
 */
class MCQuestionPanel extends QuestionPanel{
    MCQuestion question;        // the question displayed by this panel
    Vector<JRadioButton> answerButtons = new Vector<>(); // the radio buttons for the 4 answer choices
    MCQuestionPanel(MCQuestion question){
        super(question);
        this.question = question;
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridy = 2;
        c.weightx = 1.0;

        char answerNotation = 'A';
        JRadioButton button;
        ButtonGroup answerButtonGroup = new ButtonGroup();

        for(String choice : question.answerChoices){
            String choiceText = String.format("%c) %s", answerNotation++, choice);      // to place the 'A)', 'B)', etc. symbols before the answer choices.
            button = new JRadioButton(HelperFunctions.toMultiLine(choiceText, 64));

            answerButtonGroup.add(button);
            answerButtons.add(button);
            this.add(button, c); c.gridy++;
        }
        // this border helps separate the questions.
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x28557a)));
    }

    /**
     * the selectedAnswer is represented by an integer ( 0 <= selectedAnswer <= 3 or -1 if the student didn't select an answer)
     * @return An MCQuestion Object with the answer selected by the students stored in the 'selectedAnswer' attribute
     */
    public MCQuestion getAnswer(){
        this.question.selectedAnswer = -1;
        for(int i = 0; i < 4; i++)
            if (answerButtons.get(i).isSelected())
                this.question.selectedAnswer = i;
        return this.question;
    }
}

/**
 * Displays an individual essay question to students taking the quiz.
 * includes a text box to allow students to write their answer for the question.
 */
class EssayQuestionPanel extends QuestionPanel{
    EssayQuestion question; // the question displayed by this questionPanel
    JTextArea studentAnswer; // the text area where the student will write the answer for the essay question
    EssayQuestionPanel(EssayQuestion question){
        super(question);
        this.question = question;
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.LINE_START; 
        c.gridy = 2;
        c.weightx = 1.0;

        studentAnswer = new JTextArea();
        studentAnswer.setMaximumSize(new Dimension(420, 120));
        studentAnswer.setPreferredSize(new Dimension(420, 120));
        studentAnswer.setLineWrap(true);
        studentAnswer.setWrapStyleWord(true);
        c.fill = GridBagConstraints.BOTH;
        this.add(studentAnswer, c);

        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x28557a)));

    }

    /**
     * @return An EssayQuestion Object of this Panel's question, with the student's answer stored in the StudentAnswer attribute of the question.
     */
    public EssayQuestion getAnswer(){
        this.question.studentAnswer = studentAnswer.getText();
        return this.question;
    }
}

/**
 * Shown to Students taking the quiz.
 * displays a scrollPane with all the questions, where each individual question is represented by an 'MCQuestionPanel' or
 * an 'EssayQuestionPanel' depending on the type of question.
 */
class QuizScrollPane extends JScrollPane {
    Vector<QuestionPanel> questionPanels = new Vector<>();
    QuizScrollPane(Vector<Question> questions){
        this.setPreferredSize(new Dimension(700, 750));
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel quizPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.anchor = LINE_START; c.gridx = c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);

        for(Question question: questions){
            QuestionPanel questionPanel = question instanceof MCQuestion ?
                                            new MCQuestionPanel((MCQuestion) question) :
                                            new EssayQuestionPanel((EssayQuestion) question);
            questionPanels.add(questionPanel);
            quizPanel.add(questionPanel, c); c.gridy++;
        }

        this.setViewportView(quizPanel);
    }

    /**
     * Used when a student submits his answers for a quiz, and return's all the student's answers.
     * @return  A vector of Questions where each individual Question object stores the students answer for that question.
     */
    public Vector<Question> getAnswersArray() {
        Vector<Question> answersArray = new Vector<>(); // to store the Question object(Which store the student's answers).
        for (QuestionPanel question : this.questionPanels)  // iterate over all the questionPanels in the quiz.
            answersArray.add(question.getAnswer()); // retrieve the Question object and store in the vector.
        return answersArray;
    }
}

/* --------------------------------------------------------------------------------------------------------------------
   The following classes (EditQuizScrollPane, EditQuestionPane, EditMCQuestionPane, and EditEssayQuestionPane) are used
   to display the auto generated quiz to the Instructor and allow him to edit the questions.

   EditMCQuestionPane and EditEssayQuestionPane are specialized components for displaying and editing Individual MCQ and Essay Questions respectively
   And are both subclasses for the abstract class EditQuestionPane, which implements their common features.

   EditQuizScrollPane can display any number of MCQuestionPanels or EssayQuestionPanels, and allows for reordering, deleting, and adding questions.
 */

/**
 *  an abstract super class of MCQuestionPanel and EssayQuestionPanel that implements their common features, such as:
 *  buttons to delete the EditQuestionPane or move it the up or down.
 *  and an update function to update the UI depending on the position of the question in the 'EditQuizScrollPane'.
 */
abstract class EditQuestionPane extends JPanel implements ActionListener {
    EditQuizScrollPane parent; // the scroll pane that will contain and manage this EditQuestionPane instance.
    Question question;
    int questionIndex; // the index(order) of this pane in its parent scroll pane.
    JTextArea questionTextArea;
    JButton moveUpButton = GUI_Elements.button("Move up");
    JButton moveDownButton = GUI_Elements.button("Move Down");
    JButton deleteButton = GUI_Elements.button("Delete Question");
    private final JPanel buttonsPanel;
    private final GridBagConstraints c = new GridBagConstraints();
    boolean first; // a flag for if this is the first question in the questions page
    boolean last; // if this is the last question.

    // takes a question object to display, the scroll pane parent container, and the index of the question in the container.

    /**
     * @param question the Question object of the question displayed in this component.
     * @param parent the EditQuizScrollPane component that will contain this EditQuestionPane.
     * @param questionIndex the position of this EditQuestionPane in the EditQuizScrollPane parent component.
     */
    EditQuestionPane(Question question, EditQuizScrollPane parent, int questionIndex){
        super(new GridBagLayout());
        this.questionIndex = questionIndex;
        this.question = question;
        this.parent = parent;
        this.first = questionIndex == 0;
        this.last = questionIndex == parent.numOfQuestions - 1;
        this.setBackground(GUI_Elements.SECONDARY_BACKGROUND);

        
        moveUpButton.addActionListener(this);
        moveDownButton.addActionListener(this);
        deleteButton.addActionListener(this);


        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = CENTER;
        c.fill = NONE;
        c.gridx = 4; c.gridy = 3;
        c.weightx = 0;
        c.weighty = 0;
        
        // buttons panel ------------------------------------------------------------------------------
        buttonsPanel = GUI_Elements.panel(new GridBagLayout());
        buttonsPanel.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        
        
        
        if(!first) {
            c.insets = new Insets(10, 10, 5, 10);
            buttonsPanel.add(moveUpButton, c); // only display the move up button if this isn't the first question pane.
        }
        
        
        if(!last) {
            c.insets = new Insets(0, 10, 5, 10);
            c.gridy++;
            buttonsPanel.add(moveDownButton, c); // only display the move down button if this isn't the last question pane.m
        }
        
        c.gridy++;
        c.insets = new Insets(0, 10, 10, 10);
        deleteButton.setBackground(new Color(192, 64, 64));
        buttonsPanel.add(deleteButton, c);

        c.gridx = 4; c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 10, 5);

        this.add(buttonsPanel, c);
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x28557a)));
    }

    /**
     * abstract method that must be implemented by this class's subclasses.
     * @return An update Question object with any edits made to the question.
     */
    public abstract Question getQuestion();

    /**
     * event handling for deleting or moving a question up or down in the quiz.
     * @param e the event to be processed
     */
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

    /**
     * the parent (EditQuizScrollPane) container class calls this function after adding, deleting, moving questions.
     * to add or remove the 'move up' and 'move down' buttons depending on the question's new position in the EditQuizScrollPane.
     * @param newQuestionIndex: the new position(order) of this panel in the EditQuizScrollPane after the update.
     */
    public void update(int newQuestionIndex){

        int lastIndex = parent.numOfQuestions - 1; // the index of the last question pane in the parent container.
        
        c.gridx = 4; c.gridy = 3;

        if(first && newQuestionIndex != 0){    // if this used to be the first panel, but is no longer the first.
            c.insets = new Insets(10, 10, 5, 10);
            buttonsPanel.add(moveUpButton, c);             // and the move up button.
            first = false;

        } else if(!first && newQuestionIndex == 0){ // if this wasn't the first panel, but became the first.
            buttonsPanel.remove(moveUpButton);          // remove the move up button, since it's already the highest panel.
            first = true;

        } else if(last && newQuestionIndex != lastIndex){ // if this used to be the last panel, But no longer is:
            c.insets = new Insets(0, 10, 5, 10);
            c.gridy = 4;
            buttonsPanel.add(moveDownButton, c);
            last = false;

        } else if(!last && newQuestionIndex == lastIndex){ // if panel is now the last one
            buttonsPanel.remove(moveDownButton);
            last = true;
        }
        questionIndex = newQuestionIndex;
        this.updateUI();
    }
}

/**
 *  used to display and allow editing of an individual Multiple Choice Question for an instructor.
 *  includes a JTextArea to edit the Text of the Question
 *  4 JText Fields to Edit the Text of the 4 answer choices
 *  4 radio buttons to set which of the 4 answer choices is the correct one.
 */
class EditMCQuestionPane extends EditQuestionPane {
    Vector<JTextField> answers = new Vector<>();    // a vector of JTextFields Storing tha answer Choices Text.
    Vector<JRadioButton> correctAnswerSelectors = new Vector<>(); // storing the radio buttons for setting the correct answer.

    EditMCQuestionPane(MCQuestion question, EditQuizScrollPane parent, int questionIndex) {
        super(question, parent, questionIndex);
        
        JLabel questionTextLabel = new JLabel("Question " + questionIndex);
        JLabel correctAnswerLabel = new JLabel("Correct Answer");
        JLabel answerChoicesLabel = new JLabel("Answer Choices:");

        // text Areas
        questionTextArea = new JTextArea(question.questionText); // the display and allow editing of the question Text.
        questionTextArea.setMaximumSize(new Dimension(420, 120));
        questionTextArea.setPreferredSize(new Dimension(420, 120));
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);


        // radiobuttons panel -------------------------------------------------------------------------
        JPanel radioButtonsPanel = GUI_Elements.panel(new GridBagLayout());
        radioButtonsPanel.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        GridBagConstraints radioC = new GridBagConstraints();
        radioC.gridx = 0; radioC.gridy = 0;
        radioC.weightx = 1.0;
        radioC.weighty = 1.0;
        radioC.anchor = GridBagConstraints.WEST;
        radioC.insets = new Insets(5, 10, 5, 5);
        
        radioButtonsPanel.add(correctAnswerLabel, radioC);
        
        // placing radio buttons
        radioC.gridy++;
        radioC.anchor = GridBagConstraints.CENTER;
        ButtonGroup buttonGroup = new ButtonGroup();
        for (char i = 'A'; i < 'E'; i++) {
            JRadioButton correctAnswerSelectorButton = GUI_Elements.radioButton(" " + i + " ");
            correctAnswerSelectorButton.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
            correctAnswerSelectorButton.setForeground(Color.BLACK);
            buttonGroup.add(correctAnswerSelectorButton);
            correctAnswerSelectors.add(correctAnswerSelectorButton);
            radioButtonsPanel.add(correctAnswerSelectorButton, radioC);
            radioC.gridy++;
        }
        
        // choices text fields panel ---------------------------------------------------------------------
        JPanel textFieldsPanel = GUI_Elements.panel(new GridBagLayout());
        textFieldsPanel.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        GridBagConstraints textFieldsC = new GridBagConstraints();
        textFieldsC.gridx = 0; textFieldsC.gridy = 0;
        textFieldsC.weightx = 1.0;
        textFieldsC.weighty = 1.0;
        textFieldsC.anchor = GridBagConstraints.WEST;
        textFieldsC.insets = new Insets(5, 5, 5, 5);
        
        textFieldsPanel.add(answerChoicesLabel, textFieldsC);
        
        // placing text fields
        textFieldsC.insets = new Insets(0, 0, 5, 0);
        textFieldsC.fill = GridBagConstraints.HORIZONTAL;
        textFieldsC.gridy++;
        for (String answerChoiceText : question.answerChoices) {
            JTextField answerChoiceTextField = GUI_Elements.textField();
            answerChoiceTextField.setText(answerChoiceText);
            answerChoiceTextField.setPreferredSize(new Dimension(500, 25)); // Adjusted size
            answers.add(answerChoiceTextField);
            textFieldsPanel.add(answerChoiceTextField, textFieldsC);
            textFieldsC.gridy++;
        }
        


        // placing labels
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        c.insets = new Insets(10, 10, 5, 10);
        c.gridx = 0;
        c.gridy = 0;
        
        
        this.add(questionTextLabel, c);
        
        c.insets = new Insets(0, 10, 5, 5);
        c.gridy++;
        c.gridwidth = 4;
        c.anchor = GridBagConstraints.CENTER;
        this.add(questionTextArea, c);
        
        c.gridwidth = 1;
        c.insets = new Insets(5, 10, 10, 5);
        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        this.add(radioButtonsPanel, c);
        
        c.gridx++;
        c.weightx = 10;
        this.add(textFieldsPanel, c);

        
        
        
        
        c.gridy++;
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x28557a)));
    }

    /**
     * Used to save edits made by the Instructor for an individual Question
     * @return a Question object with the edits made by the Instructor.
     */
    public Question getQuestion(){
        Vector<String> answerChoices = new Vector<>();
        for(JTextField answerChoiceTextField: answers) // iterate over the JTextFields and store the Strings of the answer choices
            answerChoices.add(answerChoiceTextField.getText());

        int correctAnswerIndex = -1;
        for(int i = 0; i < 4; i++)  // iterate over the radio buttons and find the selected one (representing the correct answer choice)
            if(correctAnswerSelectors.get(i).isSelected()){
                correctAnswerIndex = i;
                break;
            }
        return new MCQuestion(questionTextArea.getText(), answerChoices, correctAnswerIndex); // create an updated Question object.
    }
}

/**
 *  used to display and allow editing of an individual Essay Question for an instructor.
 *  Only includes a JTextArea to edit the Text of the Question
 */
class EditEssayQuestionPane extends EditQuestionPane{
    EditEssayQuestionPane(EssayQuestion question, EditQuizScrollPane parent, int questionIndex) {
        super(question, parent, questionIndex);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;

        JLabel questionTextLabel = new JLabel("Question " + questionIndex);

        // placing labels
        c.gridx = 0; c.gridy = 0;
        this.add(questionTextLabel, c);

        // placing QuestionText Area
        c.gridx = 0; c.gridy = 1;
        c.gridwidth = 4;
        c.gridheight = 5;

        questionTextArea = new JTextArea(question.questionText);
        questionTextArea.setMaximumSize(new Dimension(420, 300));
        questionTextArea.setPreferredSize(new Dimension(420, 300));
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        this.add(questionTextArea, c);
        c.gridwidth = 1;
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x28557a))); // Remove top border   this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x28557a)));

    }

    /**
     * Used to save edits made by the Instructor for an individual Question
     * @return a Question object with the edits made by the Instructor.
     */
    public Question getQuestion(){
        return new EssayQuestion(questionTextArea.getText());
    }
}

// displayed to instructor when editing a quiz: hold multiple "EditQuestionPane" panels each displaying a single question.
// allows user the add, edit, remove, and move questions

/**
 * displayed to instructor when editing a quiz: hold multiple 'EditMCQuestionPane' and 'EditMCQuestionPane' panels each
 * panel displaying an individual Question of its corresponding type.
 * allows the instructor the add, edit, remove, and move questions.
 */
class EditQuizScrollPane extends JScrollPane{

    Vector<EditQuestionPane> editQuestionPanes = new Vector<>();
    int numOfQuestions;
    JPanel editQuizPanel = GUI_Elements.panel(new GridBagLayout());

    EditQuizScrollPane(Vector<Question> questions){
        this.setPreferredSize(new Dimension(700, 750));
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        editQuizPanel.setBackground(GUI_Elements.SECONDARY_BACKGROUND);

        numOfQuestions = questions.size();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = LINE_START; c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        int i = 0;
        for(Question question: questions){                      // create editQuestionPanel instance for every question.
            EditQuestionPane editQuestionPanel = question instanceof MCQuestion ?
                    new EditMCQuestionPane((MCQuestion) question, this, i++) :
                    new EditEssayQuestionPane((EssayQuestion) question, this, i++);

            editQuestionPanes.add(editQuestionPanel);           // add panel to vector for ease of access later.
            editQuizPanel.add(editQuestionPanel, c); c.gridy++; // add to the GUI.
        }
        c.weighty = 1.0;
        editQuizPanel.add(Box.createVerticalGlue(), c);

        this.setViewportView(editQuizPanel);
    }

    /**
     * moving a panel up simply means switching its position with the position of the panel before it.
     * both the 'move up' and the 'move down' buttons in the EditQuestionPane class use this function to move.
     * @param paneIndex1: the index position of the first EditQuestionPane to be switched.
     * @param paneIndex2: the index position of the second EditQuestionPane to be switched.
     */
    public void switchPanes(int paneIndex1, int paneIndex2) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = LINE_START; // Ensure consistent alignment
        c.fill = GridBagConstraints.HORIZONTAL; // Ensure components fill horizontally
        c.insets = new Insets(10, 10, 10, 10); // Consistent insets for spacing

        // Remove both components from the layout
        editQuizPanel.remove(editQuestionPanes.get(paneIndex1));
        editQuizPanel.remove(editQuestionPanes.get(paneIndex2));

        // Re-add the components in swapped positions
        c.gridy = paneIndex1;
        editQuizPanel.add(editQuestionPanes.get(paneIndex2), c);
        c.gridy = paneIndex2;
        editQuizPanel.add(editQuestionPanes.get(paneIndex1), c);

        // Update the question panes
        editQuestionPanes.get(paneIndex1).update(paneIndex2);
        editQuestionPanes.get(paneIndex2).update(paneIndex1);

        HelperFunctions.swapElements(editQuestionPanes, paneIndex1, paneIndex2);

        editQuizPanel.revalidate();
        editQuizPanel.repaint();
    }

    /**
     * handles deletion of a panel, and updating the UI of the other panels to reflect the necessary changes.
     * @param index: index of the panel to be deleted.
     */
    public void delete(int index) { // index of panel to delete
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);


        editQuizPanel.remove(editQuestionPanes.get(index));


        for (int i = index; i < numOfQuestions - 1; i++) {
            c.gridy = i;
            editQuizPanel.add(editQuestionPanes.get(i + 1), c);
            editQuestionPanes.set(i, editQuestionPanes.get(i + 1));
            editQuestionPanes.get(i).update(i);
        }


        editQuestionPanes.removeElementAt(numOfQuestions - 1);
        numOfQuestions--;

        editQuestionPanes.get(numOfQuestions - 1).update(numOfQuestions - 1);

        //
        editQuizPanel.revalidate();
        editQuizPanel.repaint();
    }

    /**
     * to add a new EditQuestionPane to the last position in the EditQuizScrollPane.
     * @param type: type of the EditQuestionPane to add: 1 for EditMCQuestionPane, 2 for EditEssayQuestionPane.
     */
    public void addQuestionPanel(int type){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        numOfQuestions++;
        c.gridy = numOfQuestions - 1;

        EditQuestionPane questionPane = type == 1 ?
                new EditMCQuestionPane(new MCQuestion(), this, numOfQuestions - 1) :
                new EditEssayQuestionPane(new EssayQuestion(), this, numOfQuestions - 1);

        editQuestionPanes.add(questionPane);                  // add to the list of question panes
        this.editQuizPanel.add(questionPane, c);              // add to the GUI

        // update the question pane before (to add the move down button, since it is no longer the last Question pane)
        editQuestionPanes.get(numOfQuestions - 2).update(numOfQuestions - 2);
        this.editQuizPanel.updateUI();
        this.repaint();
    }

    /**
     * called when the user wants to save the changes to the questions: reads and returns the updated/edited questions.
     * @return a vector of Question Objects, all updated to reflect edits made by the Instructor.
     */
    public Vector<Question> getQuestions(){
        Vector<Question> questions = new Vector<>();
        for(EditQuestionPane questionPane: editQuestionPanes)
            questions.add(questionPane.getQuestion());
        return questions;
    }
}

/**
 *  displays the information for a single class, including ID, name, and icon.
 */
class ClassPanel extends JPanel{
    Class class_;
    ClassPanel(Class class_){
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.anchor = CENTER;
        this.setBackground(new Color(64, 128, 192));
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

/**
 *  displays multiple ClassPanels to the user, where each ClassPanel holds the information for a single class.
 *  allows user the click on a class panel to go to the class's "ManageClassPage".
 */
class ClassesPane extends JPanel implements MouseListener {
    Window window;
    Vector<ClassPanel> classPanels = new Vector<>();
    ClassesPane(Window window){
        this.window = window;
        int instructorId = window.getUser().id;
        Vector<Class> classes = Database.getInstructorClasses(instructorId);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

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

/* ---------------------------------------------------------------------------------------------------------------------
   The following 3 classes(ManageStudentsListPanel, AddStudentsListPanel, and RemoveStudentsListPanel) display to the
   instructor in the 'ManageClassPage' Page, to allow his to manage, add, or remove student from his class.
 */

/**
 *  Shows the instructor a list of all the students in a specific class.
 *  Clicking on a students name in the list with take the instructor to the 'ManageStudentPage()' for that student.
 *  also includes a button to go back to instructors home page.
 */
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


    /**
     *  handles the logic of opening a 'ManageStudentPage' when a student in the list is selected
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        // when an item is selected in a list, 2 events happen simultaneously: a deselection and a selection event.
        if(e.getValueIsAdjusting()) return;         // returns if the event is a deselection event.
        window.setCurrentStudent(students.get(studentList.getSelectedIndex()));
        window.setCurrentClass(class_);
        window.switchPage(new ManageStudentPage(window));
    }

    /**
     *  This method gets called when a student is added or removed from the class to ensure that the displayed
     *  students update dynamically to accurately reflect the students in the class.
     */
    public void update(){
        students = Database.getStudentsInClass(window.getCurrentClass().id);
        studentList.setListData(HelperFunctions.studentsToStringVector(students));
    }
}

/**
 *  Shows the instructor a list of all the students that are not in a specific class.
 *  allows the selection of multiple students, and adding them to the class by clicking the 'addStudentsButton' button.
 *  includes a button to go back to the instructor's home page.
 */
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

    /**
     *  dynamically update a label informing the instructor of how many student in the list are selected.
     */
    @Override
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


    /**
     *  This method gets called when a student is added or removed from the class to ensure that the displayed
     *  students update dynamically to accurately reflect the students in the class.
     */
    public void update(){
        students = Database.getStudentsNotInClass(window.getCurrentClass().id);
        studentList.setListData(HelperFunctions.studentsToStringVector(students));
    }
}

/**
 *  Shows the instructor a list of all the students in a specific class.
 *  allows an instructor to select multiple students from the list and remove them by clicking the 'removeStudentsButton' button.
 */
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

    /**
     *  dynamically update a label informing the instructor of how many student in the list are selected.
     */
    @Override
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

    /**
     *  This method gets called when a student is added or removed from the class to ensure that the displayed
     *  students update dynamically to accurately reflect the students in the class.
     */
    public void update(){
        this.students = Database.getStudentsInClass(window.getCurrentClass().id);
        this.studentList.setListData(HelperFunctions.studentsToStringVector(students));
    }
}

/**
 *  allows the instructor to set general exam parameters, such as: exam title, quiz start end times, and classes taking this quiz.
 */
class QuizSettingsPanel extends JPanel implements ListSelectionListener {

    JPanel quizTitlePanel = GUI_Elements.secondaryPanel(new GridBagLayout());
    JPanel startQuizDatePanel = GUI_Elements.secondaryPanel(new GridBagLayout());
    JPanel endQuizDatePanel = GUI_Elements.secondaryPanel(new GridBagLayout());
    JPanel classSelectionPanel = GUI_Elements.secondaryPanel(new GridBagLayout());
    JLabel quizTitleLabel = GUI_Elements.label("Quiz Title");
    JLabel startDateLabel = GUI_Elements.label("Quiz Start Date");
    JLabel endDateLabel = GUI_Elements.label("Quiz End Date");
    JTextField quizTitleTextField = GUI_Elements.textField();
    DatePicker startDatePicker = new DatePicker();
    DatePicker endDatePicker = new DatePicker();
    Vector<Class> classes;      // a list of all this instructor's classes
    JList<String> classesList;  // classes list in the form of a string to display in the list.
    JLabel selectionCountLabel = new JLabel("You have not selected any students");


    QuizSettingsPanel(Window window) {

        super(new GridBagLayout());
        this.setPreferredSize(new Dimension(400, 400));
        this.setBackground(GUI_Elements.SECONDARY_BACKGROUND);

        classes = Database.getInstructorClasses(window.getUser().id);
        Vector<String> classesStrings = HelperFunctions.classesToStringVector(classes);

        classesList = new JList<>(classesStrings);
        classesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        classesList.addListSelectionListener(this);

        startDatePicker.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        endDatePicker.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        quizTitleLabel.setForeground(Color.BLACK);
        startDateLabel.setForeground(Color.BLACK);
        endDateLabel.setForeground(Color.BLACK);
        selectionCountLabel.setForeground(Color.BLACK);



        // Grid Management -------------------------------------------------------------------
        GridBagConstraints c = new GridBagConstraints();
        c.insets  = new Insets(10, 10, 5, 10);
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;

        GridBagConstraints innerC = new GridBagConstraints();
        innerC.gridx = 0; innerC.gridy = 0;
        innerC.weightx = 1.0;
        innerC.weighty = 1.0;
        innerC.anchor = GridBagConstraints.LINE_START;
        innerC.fill = GridBagConstraints.NONE;

        innerC.insets = new Insets(0, 0, 5, 0);
        quizTitlePanel.add(quizTitleLabel, innerC);
        innerC.gridy++;
        innerC.insets = new Insets(0, 0, 0, 0);
        quizTitlePanel.add(quizTitleTextField, innerC);

        innerC.gridy = 0;
        startQuizDatePanel.add(startDateLabel, innerC);

        innerC.gridy++;
        innerC.fill = GridBagConstraints.HORIZONTAL;
        startQuizDatePanel.add(startDatePicker, innerC);

        innerC.gridy = 0;
        innerC.fill = GridBagConstraints.NONE;
        endQuizDatePanel.add(endDateLabel, innerC);
        innerC.gridy++;
        innerC.fill = GridBagConstraints.HORIZONTAL;
        endQuizDatePanel.add(endDatePicker, innerC);

        innerC.fill = GridBagConstraints.NONE;
        innerC.anchor = GridBagConstraints.CENTER;
        innerC.gridy = 0;
        classSelectionPanel.add(selectionCountLabel, innerC);
        innerC.fill = GridBagConstraints.BOTH;
        innerC.gridy++;
        classSelectionPanel.add(classesList, innerC);


        c.insets = new Insets(10, 10, 10, 5);
        this.add(quizTitlePanel, c);

        c.insets  = new Insets(5, 10, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        c.gridy++;
        this.add(startQuizDatePanel, c);

        c.insets  = new Insets(5, 10, 10, 5);
        c.gridy++;
        this.add(endQuizDatePanel, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 4;
        c.insets = new Insets(10, 5, 10, 40);
        this.add(classSelectionPanel, c);
    }
    public String getQuizTitle(){return this.quizTitleTextField.getText();}
    public Date getQuizStartDate(){return this.startDatePicker.getDateTime();}
    public Date getQuizEndDate(){return this.endDatePicker.getDateTime();}

    /**
     *  The selected classes are from the 'classesList' which allows multiple interval selection.
     * @return a vector of Class objects representing the classes selected to take this quiz.
     */
    public Vector<Class> getSelectedClasses(){
        Vector<Class> selectedClasses = new Vector<>();
        for(int classIndex: this.classesList.getSelectedIndices())
            selectedClasses.add(classes.get(classIndex));
        return selectedClasses;
    }

    /**
     * dynamically updates a label with the count of the classes selected by the instructor.
     * @param e the event that characterizes the change.
     */
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

/**
 *  displays a list of quizzes to the student, and allowing him to click on a quiz to take it.
 */
class QuizSelectionScrollPane extends JScrollPane implements MouseListener{
    Vector<QuizPanel> quizPanels = new Vector<>();
    Vector<Quiz> quizzes;
    Window window;

    /**
     * @param window the window object for the program.
     * @param quizzes a vector of Quizzes to display.
     */
    QuizSelectionScrollPane(Window window, Vector<Quiz> quizzes){
        this.setPreferredSize(new Dimension(700, 750));
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.quizzes = quizzes;
        this.window = window;


        JPanel quizzesSelectionPanel = new JPanel(new GridBagLayout());
        quizzesSelectionPanel.setBackground(GUI_Elements.SECONDARY_BACKGROUND);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(5, 5, 10, 5);


        if (quizzes.isEmpty()) {

            JLabel emptyLabel = new JLabel("There are no quizzes to display", SwingConstants.CENTER);
            emptyLabel.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 15));

            c.anchor = GridBagConstraints.CENTER;
            quizzesSelectionPanel.add(emptyLabel, c);
        }
        else {

            for(Quiz quiz : quizzes){

                QuizPanel quizPanel = new QuizPanel(quiz);
                quizPanel.setBackground(Color.WHITE);
                quizPanel.addMouseListener(this);
                quizPanels.add(quizPanel);
                quizzesSelectionPanel.add(quizPanel, c);
                c.gridy++;
            }
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

/**
 * displays the information of a single quiz to the student including its title, start and end times
 */
class QuizPanel extends JPanel{
    Quiz quiz;
    QuizPanel(Quiz quiz){
        super(new GridBagLayout());
        this.setPreferredSize(new Dimension(400, 150));
        this.quiz = quiz;


        // Grid Management -----------------------------------------------------------------
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 1.0;

        JLabel titleLabel = new JLabel(quiz.title);
        titleLabel.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 20));
        this.add(titleLabel, c);

        c.gridy++;
        this.add(new JLabel(String.format("Date:  %s", quiz.startDateTime.toString().split(" ")[0])), c);
        c.gridy++;
        this.add(new JLabel(String.format("Start Time:  %s", quiz.startDateTime.toString().split(" ")[1])), c);
        c.gridy++;
        this.add(new JLabel(String.format("End Time:  %s", quiz.endDateTime.toString().split(" ")[1])), c);
    }
}

/**
 * this class allows for components to be inserted into a table.
 * I tried using the JTable defined in Swing, but it's too complicated. I found that defining the class myself is easier.
 */
class Table extends JPanel{
    GridBagConstraints c = new GridBagConstraints();

    /**
     * @param headers vector of strings representing the header of the table, will be displayed above the first row.
     */
    Table(Vector<String> headers){
        this();
        this.setHeaders(headers);
    }
    Table(){
        super(new GridBagLayout());
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
    }

    /**
     * @param component the component to be added.
     * @param row the row at which the component will be added. (the first row after the headers is row 0)
     * @param col the column at which the component will be added.
     */
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

    /**
     * places the headers in their appropriate places.
     * @param headers a vector of Strings that represent the header of the table.
     */
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

/**
 * uses the Table class defined earlier to display information to the instructor regarding a certain students status with the quizzes.
 * displays the quiz information, and the completion status of each quiz in a specific class for a specific student.
 * includes a button next to quizzes completed by the student that allows the instructor to navigate to the
 *  'ViewStudentSubmissionPage' to view the student's submission.
 */
class QuizDisplayTable extends JPanel implements ActionListener{ // requires student, optionally needs class
    Window window;
    Student student;
    Vector<Quiz> quizzes;
    Vector<Submission> submissions;
    Vector<JButton> buttons = new Vector<>();

    /**
     * @param window the window object of the program
     * @param quizzes the student about whom the information will be displayed in the table.
     * @param submissions the class whose quizzes will be displayed in the table.
     */
    QuizDisplayTable(Window window, Vector<Quiz> quizzes, Vector<Submission> submissions){
        this.window = window;
        this.quizzes = quizzes;
        this.student = window.getCurrentStudent();
        this.submissions = submissions;
        Vector<String> headers = new Vector<>(Arrays.asList("ID", "Title", "Start Date", "End Date", "Action"));
        this.setBackground(GUI_Elements.APP_BACKGROUND);

        Table table = new Table(headers);
        int row = 0;
        int col = 0;
        for(int i = 0; i < quizzes.size(); i++){
            Quiz quiz = quizzes.get(i);
            Submission submission = submissions.get(i);

            table.setBorder(BorderFactory.createLineBorder(GUI_Elements.APP_BACKGROUND));
            table.insert(new JLabel(Integer.toString(quiz.id), SwingConstants.CENTER), row, col++);
            table.insert(new JLabel(quiz.title, SwingConstants.CENTER), row, col++);
            table.insert(new JLabel(quiz.startDateTime.toString(), SwingConstants.CENTER), row, col++);
            table.insert(new JLabel(quiz.endDateTime.toString(), SwingConstants.CENTER), row, col++);

            String status = HelperFunctions.StudentQuizStatus(quiz, submission); // retrieve completion status: not started, completed, not completed
            if(status == null){                 // means the student Completed the quiz
                JButton button = GUI_Elements.button("View");
                button.addActionListener(this);
                buttons.add(button);
                table.insert(button, row++, col);
            } else {                            // if the student hasn't completed the quiz, more info is stored in the "status" string.
                table.insert(new JLabel(status), row++, col);
                buttons.add(null);
            }
            col = 0;
        }
        this.add(table);
    }

    /**
     * takes the instructor to the corresponding submission's page upon clicking the 'view' button for a submission.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        for(int i = 0; i < buttons.size(); i++){
            if(e.getSource() == buttons.get(i)){
                window.setCurrentStudent(student);
                window.setCurrentSubmission(submissions.get(i));
                window.quiz = quizzes.get(i);
                window.switchPage(new ViewStudentSubmissionPage(window));
            }
        }
    }
}

/**
 * uses the Table class defined earlier to display a student's submission to the instructor.
 * displays both Multiple choice questions and essay questions
 * includes information such as student's answer, grade for answer, justification for grade, etc.
 */
class QuizSubmissionDisplay extends JPanel{
    public final String[] grades = {"<no credit>", "<partial credit>", "<full credit>"};
    public Vector<EssayQuestion> essayQuestions = new Vector<>();
    public Vector<JTextArea> justifications = new Vector<>();
    public Vector<JComboBox<String>> essayGrades = new Vector<>();

    /**
     * @param submissionId the ID of the submission whose information will be displayed.
     * @param editable whether the user should be allowed to edit essay question grades, and grade justifications.
     */
    QuizSubmissionDisplay(int submissionId, boolean editable){
        Vector<Question> questionsAndAnswers = Database.getStudentAnswers(submissionId);
        Vector<String> headers = new Vector<>(Arrays.asList("Number", "Question Type", "Question Text", "Students Answer", "Correct Answer / Grade Justification", "Mark / Grade"));

        Table table = new Table(headers);
        table.setBackground(new Color(238,238,228));
        int row = 0;
        int col = 0;
        for(Question question: questionsAndAnswers){
            table.insert(new JLabel(Integer.toString(row+1), SwingConstants.CENTER), row, col++);
            table.insert(new JLabel(question instanceof MCQuestion ? "MCQ" : "Essay",
                    SwingConstants.CENTER), row, col++);
            table.insert(textArea(question.questionText), row, col++);
            if(question instanceof MCQuestion mcq) {
                table.insert(textArea(mcq.answerChoices.get(mcq.selectedAnswer)), row, col++);
                table.insert(textArea(mcq.answerChoices.get(mcq.correctAnswerIndex)), row, col++);
                String answerIsCorrect = mcq.correctAnswerIndex == mcq.selectedAnswer ? "Correct" : "Incorrect";
                table.insert(new JLabel(answerIsCorrect, SwingConstants.CENTER), row++, col);
            } else if(question instanceof EssayQuestion eq){
                table.insert(textArea(eq.studentAnswer), row, col++);

                JTextArea justificationTextArea = new JTextArea(eq.gradeJustification);
                JComboBox<String> gradeComboBox =  new JComboBox<>(grades);

                gradeComboBox.setOpaque(false);
                gradeComboBox.setFocusable(false);

                table.insert(textArea(justificationTextArea), row, col++);
                table.insert(gradeComboBox, row++, col);

                int defaultSelection = 0;
                if(eq.grade.equalsIgnoreCase(grades[2])) defaultSelection = 2;
                else if(eq.grade.equalsIgnoreCase(grades[1])) defaultSelection = 1;
                gradeComboBox.setSelectedIndex(defaultSelection);

                if(editable){       // if displayed to the instructor;
                    essayQuestions.add(eq);
                    justifications.add(justificationTextArea);
                    essayGrades.add(gradeComboBox);
                    justificationTextArea.setEditable(true);
                } else {            // if displayed to the student.
                    gradeComboBox.setEditable(false);
                    justificationTextArea.setEditable(false);
                }
            }
            col = 0;
        }
        this.add(table);
    }

    /**
     * used to display large blocks of text in a scrollable format(if needed) in the table.
     * @param text the block of text to be displayed.
     * @return a JScrollPane containing a text area with the block of text.
     */
    public JScrollPane textArea(String text){
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        JScrollPane textAreaScroll = new JScrollPane(textArea);
        textAreaScroll.setPreferredSize(new Dimension(205, 100));
        textAreaScroll.setOpaque(false);
        textAreaScroll.getViewport().setOpaque(false);
        textAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        textAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return textAreaScroll;
    }

    public JScrollPane textArea(JTextArea textArea){
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        JScrollPane textAreaScroll = new JScrollPane(textArea);
        textAreaScroll.setPreferredSize(new Dimension(205, 100));
        textAreaScroll.setOpaque(false);
        textAreaScroll.getViewport().setOpaque(false);
        textAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        textAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return textAreaScroll;
    }

    public Vector<EssayQuestion> getEditedQuestions(){
        for(int i = 0; i < essayQuestions.size(); i++){
            essayQuestions.get(i).gradeJustification = justifications.get(i).getText();
            essayQuestions.get(i).grade = grades[essayGrades.get(i).getSelectedIndex()];
        }
        return essayQuestions;
    }
}

