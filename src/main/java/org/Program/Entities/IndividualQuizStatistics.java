package org.Program.Entities;
public class IndividualQuizStatistics {
    String questionType;
    String questionText;
    int percentageOfCorrectChoice;

    int percentageOfFullCredit;
    int percentageOfPartialCredit;
    int percentageOfNoCredit;

    public IndividualQuizStatistics(String questionType, String questionText, double percentageOfCorrectChoice) {
        this.questionType = questionType;
        this.questionText = questionText;
        this.percentageOfCorrectChoice = (int) (percentageOfCorrectChoice * 100);
    }

    public IndividualQuizStatistics(String quistionType, String quistionText, double percentageOfFullCredit, double percentageOfPartialCredit, double percentageOfNoCredit) {
        this.questionType = quistionType;
        this.questionText = quistionText;

        this.percentageOfFullCredit = (int) (percentageOfFullCredit * 100);
        this.percentageOfPartialCredit = (int) (percentageOfPartialCredit * 100);
        this.percentageOfNoCredit = (int) (percentageOfNoCredit * 100);
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public int getPercentageOfCorrectChoice() {
        return percentageOfCorrectChoice;
    }

    public int getPercentageOfFullCredit() {
        return percentageOfFullCredit;
    }

    public int getPercentageOfPartialCredit() {
        return percentageOfPartialCredit;
    }

    public int getPercentageOfNoCredit() {
        return percentageOfNoCredit;
    }
}
