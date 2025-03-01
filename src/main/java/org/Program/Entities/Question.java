package org.Program.Entities;

import org.Program.HelperFunctions;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
public class Question {
    public int id;
    public String questionText;
    public Vector<String> answerChoices;
    public int correctAnswerIndex;
    public int selectedAnswerIndex; // when viewing student answers it is useful to store the answer selected by the student with the question

    public Question(){
        this.questionText = "";
        this.answerChoices = new Vector<>(List.of(new String[]{"", "", "", ""}));
        this.correctAnswerIndex = -1;
    }
    public Question(int id, String questionText, String answerChoice0, String answerChoice1, String answerChoice2, String answerChoice3){
        this.id = id;
        this.questionText = questionText;
        this.answerChoices = new Vector<>(Arrays.asList(answerChoice0, answerChoice1, answerChoice2, answerChoice3));
    }
    public Question(String questionText, Vector<String> answerChoices, int correctAnswerIndex){
        this.questionText = questionText;
        this.answerChoices = answerChoices;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public static Vector<Question> process(String LLMReply){ // rework this such that if an exception occurred while processing a question string, we simply skip that question.
        String[] questions = LLMReply.split("<question>");
        for(int i = 0; i < questions.length; i++)
            questions[i] = questions[i].trim();
        Random random = new Random();
        Vector<Question> questionList = new Vector<>();
        for(String question: questions){
            if(question.isBlank()) continue;
            // the vector answerChoices contains 5 Strings: the first is the question string, and the rest are answers.
            Vector<String> answerChoices = new Vector<>(List.of(question.split("\n")));

            // remove the Question String from the vector after saving it elsewhere.
            String questionText = answerChoices.get(0);
            answerChoices.removeElementAt(0);

            // the language model has a habit of placing the correct answer in the first position.
            // so, we will swap the first answer(which is probably the correct one) with a random index.

            HelperFunctions.swapElements(answerChoices, 0, random.nextInt(4));

            // the correct answer is a string that has the token "<correct>" at the end of it.
            // returns the index of the correct answer, and removes the "<correct>" token from the string.
            int correctAnswerIndex = correctAnswerIndex(answerChoices);

            questionList.add(new Question(questionText, answerChoices, correctAnswerIndex));
        }
        return questionList;
    }

    public static int correctAnswerIndex(Vector<String> answers){
        int index = -1;
        for(int i = 0; i < answers.size(); i++)
            if(answers.get(i).matches(".+<correct>")){
                index = i;
                break;
            }
        if(index == -1 || index > 3)
            System.out.println("Failed to find the correct answer.");

        String correctAnswer = answers.get(index); // get the string of the correct answer.
        correctAnswer = correctAnswer.substring(0, correctAnswer.length() - 9).trim(); // remove the token.
        answers.set(index, correctAnswer); // put edited version of the correct answer back into the vector.
        return index;
    }

    public String toString(){
        StringBuilder str = new StringBuilder(this.questionText);
        str.append("\n");
        char AlphaNotation = 'A';
        for(String answer: this.answerChoices)
            str.append(String.format("%c) %s\n", AlphaNotation++, answer));
        str.append(String.format("Correct answer Index = %d\n", correctAnswerIndex));
        return str.toString();
    }
}

