package org.Program;

import org.Program.Entities.*;
import org.Program.Entities.Class;

import javax.swing.*;
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
    public final static Color buttonColor1 = new Color(44, 44, 44);
    public final static Dimension buttonSize1 = new Dimension(160, 50);
    public final static EmptyBorder buttonMargin1 = new EmptyBorder(20, 20, 20, 20);
    public final static String TEXT_FONT = "Montserrat";

    public final static Dimension textFieldSize1 = new Dimension(300, 30);

    public static JPanel panel() {

        JPanel panel = new JPanel();
        panel.setBackground(Page.APP_BACKGROUND);
        panel.setForeground(Page.TEXT_FOREGROUND);

        return panel;
    }

    public static JPanel panel(LayoutManager layout) {

        JPanel panel = new JPanel(layout);
        panel.setBackground(Page.APP_BACKGROUND);
        panel.setForeground(Page.TEXT_FOREGROUND);

        return panel;
    }

    public static JRadioButton radioButton(String text) {

        JRadioButton button = new JRadioButton(text);
        button.setBackground(Page.APP_BACKGROUND);
        button.setForeground(Page.TEXT_FOREGROUND);

        return button;
    }

    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(GUI_Elements.TEXT_FONT, Font.BOLD, 14));
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
        button.setForeground(new Color(255,  255, 255));
        button.setBackground(buttonColor1);
        button.setPreferredSize(buttonSize1);
        button.setBorder(buttonMargin1);
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
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0; c.gridy = 0;

        JLabel label = new JLabel(labelText);
        this.add(label, c); c.gridy++;
        this.add(textField, c);
    }
    public String getText(){return textField.getText();}
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

        JLabel questionLabel = new JLabel(HelperFunctions.toMultiLine(question.questionText, 80));
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
        c.anchor = LINE_START; c.gridy = 2;

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
        // this border helps separate the questions. It is 2 lines one above and one below each question.
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x28557a)));
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
        c.anchor = LINE_START; c.gridy = 2;

        studentAnswer = new JTextArea();
        studentAnswer.setMaximumSize(new Dimension(420, 120));
        studentAnswer.setPreferredSize(new Dimension(420, 120));
        studentAnswer.setLineWrap(true);
        studentAnswer.setWrapStyleWord(true);
        this.add(studentAnswer, c);
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
    JButton moveUpButton = GUI_Elements.button("Move up ^");
    JButton moveDownButton = GUI_Elements.button("Move Down V");
    JButton deleteButton = GUI_Elements.button("Delete Question");
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

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = LINE_START;

        // placing Buttons
        c.gridx = 3; c.gridy = 0; c.gridheight = 2;
        moveUpButton.addActionListener(this);
        moveDownButton.addActionListener(this);
        deleteButton.addActionListener(this);

        if(!first) this.add(moveUpButton, c); // only display the move up button if this isn't the first question pane.

        c.gridy = 2;
        if(!last) this.add(moveDownButton, c); // only display the move down button if this isn't the last question pane.

        c.gridy = 4;
        deleteButton.setBackground(new Color(192, 64, 64));
        this.add(deleteButton, c);

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
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = LINE_START; c.gridx = 3; c.gridy = 0; c.gridheight = 2;
        int lastIndex = parent.numOfQuestions - 1; // the index of the last question pane in the parent container.

        if(first && newQuestionIndex != 0){    // if this used to be the first panel, but is no longer the first.
            this.add(moveUpButton, c);             // and the move up button.
            first = false;

        } else if(!first && newQuestionIndex == 0){ // if this wasn't the first panel, but became the first.
            this.remove(moveUpButton);          // remove the move up button, since it's already the highest panel.
            first = true;

        } else if(last && newQuestionIndex != lastIndex){ // if this used to be the last panel, But no longer is:
            c.gridy = 2;
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

/**
 *  used to display and allow editing of an individual Multiple Choice Question for an instructor.
 *  includes a JTextArea to edit the Text of the Question
 *  4 JText Fields to Edit the Text of the 4 answer choices
 *  4 radio buttons to set which of the 4 answer choices is the correct one.
 */
class EditMCQuestionPane extends EditQuestionPane{
    Vector<JTextField> answers = new Vector<>();    // a vector of JTextFields Storing tha answer Choices Text.
    Vector<JRadioButton> correctAnswerSelectors = new Vector<>(); // storing the radio buttons for setting the correct answer.
    EditMCQuestionPane(MCQuestion question, EditQuizScrollPane parent, int questionIndex) {
        super(question, parent, questionIndex);
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

        questionTextArea = new JTextArea(question.questionText); // the display and allow editing of the question Text.
        questionTextArea.setMaximumSize(new Dimension(420, 120));
        questionTextArea.setPreferredSize(new Dimension(420, 120));
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        this.add(questionTextArea, c);
        c.gridwidth = 1;

        // placing radio buttons
        c.gridx = 0; c.gridy = 3; c.anchor = CENTER;
        ButtonGroup buttonGroup = new ButtonGroup();
        for(char i = 'A'; i < 'E'; i++){    // place the radio buttons (by default, the correct one as specified by the LLM Reply is selected)
            JRadioButton correctAnswerSelectorButton = new JRadioButton(" " + i + " ");
            buttonGroup.add(correctAnswerSelectorButton);
            correctAnswerSelectors.add(correctAnswerSelectorButton);
            this.add(correctAnswerSelectorButton, c); c.gridy++;
        }
        correctAnswerSelectors.get(question.correctAnswerIndex).setSelected(true);

        // placing text fields
        c.gridx = 1; c.gridy = 3; c.anchor = LINE_START;
        for(String answerChoiceText: question.answerChoices){ // place the answer choice JTextFields with the text
            JTextField answerChoiceTextField = GUI_Elements.textField();
            answerChoiceTextField.setText(answerChoiceText);
            answerChoiceTextField.setSize(80, 20);
            answers.add(answerChoiceTextField);
            this.add(answerChoiceTextField, c); c.gridy++;
        }

        this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x28557a)));
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
        c.anchor = LINE_START; c.gridwidth = 2;

        JLabel questionTextLabel = new JLabel("(" + questionIndex + ") Question Text:"); c.gridwidth = 1;

        // placing labels
        c.gridx = 0; c.gridy = 0;
        this.add(questionTextLabel, c);

        // placing QuestionText Area
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;  c.gridheight = 5;

        questionTextArea = new JTextArea(question.questionText);
        questionTextArea.setMaximumSize(new Dimension(420, 300));
        questionTextArea.setPreferredSize(new Dimension(420, 300));
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        this.add(questionTextArea, c);
        c.gridwidth = 1;
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

    /**
     * handles deletion of a panel, and updating the UI of the other panels to reflect the necessary changes.
     * @param index: index of the panel to be deleted.
     */
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

        editQuestionPanes.get(numOfQuestions - 1).update(numOfQuestions - 1);

        this.editQuizPanel.updateUI();
        this.repaint();
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
        this.add(className, c); c.gridy++;
        this.add(classIcon, c); c.gridy++;
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
    JButton backButton =  GUI_Elements.button("Back");
    Class class_;
    JList<String> studentList;
    Vector<Student> students;
    ManageStudentsListPanel(Window window){
        super(new BorderLayout());

        class_ = window.getCurrentClass();
        this.students = Database.getStudentsInClass(class_.id);
        this.window = window;

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.gridy = c.gridx = 0;

        // label panel
        JPanel labelPanel = new JPanel(new GridBagLayout());
        labelPanel.add(GUI_Elements.label("Manage Students"), c); c.gridy++;
        JLabel descriptionLabel = new JLabel("Select one student to manage");
        labelPanel.add(descriptionLabel, c);

        // list
        studentList = new JList<>(HelperFunctions.studentsToStringVector(students));
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setVisibleRowCount(12);
        studentList.addListSelectionListener(this);

        // button panel
        c.gridx = 0; c.gridy = 1;
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        backButton.addActionListener(this);
        backButton.setBackground(new Color(192, 64, 64));

        buttonPanel.add(backButton, c);


        // add panels
        this.add(labelPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(studentList), BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton)
            window.switchPage(new InstructorHomePage(window));
    }


    /**
     *  handles the logic of opening a 'ManageStudentPage' when a student in the list is selected
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        // when an item is selected in a list, 2 events happen simultaneously: a deselection and a selection event.
        if(e.getValueIsAdjusting()) return;         // returns if the event is a deselection event.
        Student selectedStudent = students.get(studentList.getSelectedIndex());
        window.switchPage(new ManageStudentPage(window, selectedStudent, class_));
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
        super(new BorderLayout());
        this.students = Database.getStudentsNotInClass(window.getCurrentClass().id);
        this.window = window;

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.gridy = c.gridx = 0;

        // label panel
        JPanel labelPanel = new JPanel(new GridBagLayout());
        labelPanel.add(GUI_Elements.label("Add Students"), c); c.gridy++;
        JLabel descriptionLabel = new JLabel("Select one or more students to add to your class");
        labelPanel.add(descriptionLabel, c);

        // list
        studentList = new JList<>(HelperFunctions.studentsToStringVector(students));
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        studentList.setVisibleRowCount(12);
        studentList.addListSelectionListener(this);

        // button panel
        c.gridx = 0; c.gridy = 1;
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        addStudentsButton.addActionListener(this);
        cancelButton.addActionListener(this);
        cancelButton.setBackground(new Color(192, 64, 64));

        buttonPanel.add(addStudentsButton, c); c.gridx++;
        buttonPanel.add(cancelButton, c);

        c.gridy = c.gridx = 0;
        c.gridwidth = 2;
        buttonPanel.add(selectionCountLabel, c);

        // add panels
        this.add(labelPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(studentList), BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
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
    RemoveStudentListPanel(Window window){
        super(new BorderLayout());
        this.students = Database.getStudentsInClass(window.getCurrentClass().id);
        this.window = window;

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.gridy = c.gridx = 0;

        // label panel
        JPanel labelPanel = new JPanel(new GridBagLayout());
        labelPanel.add(GUI_Elements.label("Remove Students"), c); c.gridy++;
        JLabel descriptionLabel = new JLabel("Select one or more students to remove from your class");
        labelPanel.add(descriptionLabel, c);

        // list
        studentList = new JList<>(HelperFunctions.studentsToStringVector(students));
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        studentList.setVisibleRowCount(12);
        studentList.addListSelectionListener(this);

        // button panel
        c.gridx = 0; c.gridy = 1;
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        removeStudentsButton.addActionListener(this);
        cancelButton.addActionListener(this);
        cancelButton.setBackground(new Color(192, 64, 64));

        buttonPanel.add(removeStudentsButton, c); c.gridx++;
        buttonPanel.add(cancelButton, c);

        c.gridy = c.gridx = 0;
        c.gridwidth = 2;
        buttonPanel.add(selectionCountLabel, c);

        // add panels
        this.add(labelPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(studentList), BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
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
class QuizSettingsPanel extends JPanel implements ListSelectionListener{
    LabeledTextField quizTitleTextField = new LabeledTextField("Quiz Title:");
    DatePicker startDatePicker = new DatePicker();
    DatePicker endDatePicker = new DatePicker();
    Vector<Class> classes;      // a list of all this instructor's classes
    JList<String> classesList;  // classes list in the form of a string to display in the list.
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

/**
 * displays the information of a single quiz to the student including its title, start and end times
 */
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
                BorderFactory.createMatteBorder(1, 1, 1, 1, GUI_Elements.buttonColor1),
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
                    BorderFactory.createMatteBorder(1, 1, 1, 1, GUI_Elements.buttonColor1),
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
class QuizDisplayTable extends JPanel implements ActionListener{
    Window window;
    Class class_;
    Student student;
    Vector<Quiz> quizzes = new Vector<>();
    Vector<Submission> submissions = new Vector<>();
    Vector<JButton> buttons = new Vector<>();

    /**
     * @param window the window object of the program
     * @param student the student about whom the information will be displayed in the table.
     * @param class_ the class whose quizzes will be displayed in the table.
     */
    QuizDisplayTable(Window window, Student student, Class class_){
        this.window = window;
        this.class_ = class_;
        this.student = student;

        // retrieves a vector of quizzes and a vector of the students submissions for a specific student in a specific class.
        // notice that the function returns by reference -not by value- because we need to get 2 values.
        // take note that the quizzes and submissions are, and must be, of the same size for the following code to function.
        // check the documentation of the 'getQuizzesByClass()' function for more info.
        Database.getQuizzesByClass(class_.id, student.id, quizzes, submissions);
        Vector<String> headers = new Vector<>(Arrays.asList("ID", "Title", "Start Date", "End Date", "Action"));

        Table table = new Table(headers);
        table.setBackground(new Color(238,238,228));
        int row = 0;
        int col = 0;
        for(int i = 0; i < quizzes.size(); i++){
            Quiz quiz = quizzes.get(i);
            Submission submission = submissions.get(i);

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
                window.switchPage(new ViewStudentSubmissionPage(window, student, quizzes.get(i), submissions.get(i), class_));
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
    /**
     * @param submissionId the ID of the submission whose information will be displayed.
     */
    QuizSubmissionDisplay(int submissionId){
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
                table.insert(textArea(eq.gradeJustification), row, col++);
                table.insert(new JLabel(eq.grade, SwingConstants.CENTER), row++, col);
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
        textArea.setEditable(false);
        textArea.setOpaque(false);
        JScrollPane textAreaScroll = new JScrollPane(textArea);
        textAreaScroll.setPreferredSize(new Dimension(205, 100));
        textAreaScroll.setOpaque(false);
        textAreaScroll.getViewport().setOpaque(false);
        textAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        textAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return textAreaScroll;
    }
}

