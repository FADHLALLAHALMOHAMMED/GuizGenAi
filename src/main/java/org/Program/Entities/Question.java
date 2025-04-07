package org.Program.Entities;

import java.util.List;
import java.util.Vector;
public class Question {
    public int id;
    public String questionText;

    public Question(){}
    public Question(int id, String questionText){
        this.id = id;
        this.questionText = questionText;
    }

    /**
     *      how questions are processed:
     *      The LLM is instructed to separate the questions using the "<Question>" token.
     *      therefore, we can use the .split() function to separate the questions using that token as a delimiter.
     *      the String[] questions array contains a string for each individual question.
     *
     *      then, we split each of the individual question strings using the newline character as a delimiter:
     *          if the resulting array contains only 1 element, then we can assume is an essay question.
     *          if the array contains 5 elements(question text and 4 answer choices), it's a multiple choice question.
     *
     *      either way, then we use the appropriate function to create the question objects for the Strings.
     * @param LLMReply: the Reply of the LLM, containing the all the questions of the quiz in the form of a string.
     * @return A vector of Question Objects containing the questions of the quiz.
     *
     */
    public static Vector<Question> process(String LLMReply){ // rework this such that if an exception occurred while processing a question string, we simply skip that question.
        String[] questions = LLMReply.split("<question>");
        for(int i = 0; i < questions.length; i++)
            questions[i] = questions[i].trim();

        Vector<Question> questionList = new Vector<>();
        for(String question: questions){
            if(question.isBlank()) continue;

            Vector<String> questionElements = new Vector<>(List.of(question.split("\n")));
            if(questionElements.size() == 5)
                questionList.add(new MCQuestion(questionElements));
            else if(questionElements.size() < 3)
                questionList.add(new EssayQuestion(questionElements));
        }
        return questionList;
    }
}

