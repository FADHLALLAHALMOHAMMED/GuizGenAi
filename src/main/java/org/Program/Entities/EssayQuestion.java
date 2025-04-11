package org.Program.Entities;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.util.Vector;

public class EssayQuestion extends Question {
    public String studentAnswer;
    public String grade = "ungraded";
    public String gradeJustification;
    public EssayQuestion (){
        this.questionText = "";
    }
    public EssayQuestion(String questionText) {
        this.questionText = questionText;
    }

    public EssayQuestion(int id, String questionText) {
        this.id = id;
        this.questionText = questionText;
    }

    public EssayQuestion(Vector<String> questionElements) {
        this(questionElements.get(0));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(this.questionText).append("\n");
        if(grade != null)
            str.append("Grade: ").append(this.grade).append("\n");
        if(gradeJustification != null)
            str.append("Justification for grade: ").append(this.gradeJustification).append("\n");
        return str.toString();
    }
}
