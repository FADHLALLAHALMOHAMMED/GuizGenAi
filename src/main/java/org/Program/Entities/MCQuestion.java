package org.Program.Entities;

import org.Program.HelperFunctions;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class MCQuestion extends Question {
    public Vector<String> answerChoices;
    public int correctAnswerIndex;
    public int selectedAnswer;

    public MCQuestion(int id, String questionText, String answerChoice0, String answerChoice1, String answerChoice2, String answerChoice3) {
        super(id, questionText);
        this.answerChoices = new Vector<>(Arrays.asList(answerChoice0, answerChoice1, answerChoice2, answerChoice3));
    }

    public MCQuestion(String questionText, Vector<String> answerChoices, int correctAnswerIndex) {
        this.questionText = questionText;
        this.answerChoices = answerChoices;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public MCQuestion() {
        this.questionText = "";
        this.answerChoices = new Vector<>(List.of(new String[]{"", "", "", ""}));
        this.correctAnswerIndex = -1;
    }

    public MCQuestion(Vector<String> questionElements) {
        Random random = new Random();
        // separate the question text for the answer choices after storing is elsewhere.
        String questionText = questionElements.get(0);
        questionElements.removeElementAt(0);

        // the correct answer is a string that has the token "<correct>" at the end of it.
        // returns the index of the correct answer, and removes the "<correct>" token from the string.
        this.correctAnswerIndex = correctAnswerIndex(questionElements);
        int randomNumber = random.nextInt(4);
        // sometimes the LLM doesn't randomize the position of the correct choice, so I did this to ensure randomness.
        HelperFunctions.swapElements(questionElements, correctAnswerIndex, randomNumber);
        this.correctAnswerIndex = randomNumber;
        this.questionText = questionText;
        this.answerChoices = questionElements;
    }

    public static int correctAnswerIndex(Vector<String> answers) {
        int index = -1;
        for (int i = 0; i < answers.size(); i++)
            if (answers.get(i).matches(".+<correct>")) {
                index = i;
                break;
            }
        if (index == -1 || index > 3)
            System.out.println("Failed to find the correct answer.");

        String correctAnswer = answers.get(index); // get the string of the correct answer.
        correctAnswer = correctAnswer.substring(0, correctAnswer.length() - 9).trim(); // remove the token.
        answers.set(index, correctAnswer); // put edited version of the correct answer back into the vector.
        return index;
    }

    public String toString() {
        StringBuilder str = new StringBuilder(this.questionText);
        str.append("\n");
        char AlphaNotation = 'A';
        for (String answer : this.answerChoices)
            str.append(String.format("%c) %s\n", AlphaNotation++, answer));
        str.append(String.format("Correct answer Index = %d\n", correctAnswerIndex));
        return str.toString();
    }
}
