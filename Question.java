package quizapp;

public class Question {
    public String question;
    public String[] options;
    public int correctAnswer; // index 0-3
    public String category;

    public Question(String question, String[] options, int correctAnswer, String category) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
    }
}
