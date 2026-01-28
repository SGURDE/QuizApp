package quizapp;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class QuizApp {

    static Scanner sc = new Scanner(System.in);

    static boolean fiftyUsed = false;
    static boolean skipUsed = false;

    static int correct = 0;
    static long totalTime = 0;

    static Map<String, Integer> categoryScore = new HashMap<>();
    static Map<String, Integer> categoryTotal = new HashMap<>();
    static Set<String> askedQuestions = new HashSet<>();

    static Question[][][] quizData;
    static final int TOTAL_QUESTIONS = 10;

    public static void main(String[] args) {

        System.out.println("DEBUG: Main method started");

        // Login/Register
        if (!AuthService.loginOrRegister(sc)) {
            System.out.println("Exiting application...");
            return;
        }

        loadQuestions();

        System.out.println("===== SUPERCHARGED QUIZ APP =====");
        System.out.println("Select Category:");
        System.out.println("1. Java");
        System.out.println("2. SQL");
        int choice = sc.nextInt() - 1;

        int difficulty = 0; // 0-Easy, 1-Medium, 2-Hard
        int streak = 0;

        List<Question> questions = getShuffledQuestions(choice, difficulty);
        int index = 0;

        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            while (questions.isEmpty()) {
                if (difficulty < 2) {
                    difficulty++;
                    questions = getShuffledQuestions(choice, difficulty);
                    index = 0;
                } else break;
            }

            if (questions.isEmpty()) break;

            Question q = questions.get(index++);
            askedQuestions.add(q.question);

            boolean isCorrect = askQuestion(q);

            if (isCorrect) streak++;
            else streak = 0;

            if (streak >= 2 && difficulty < 2) {
                difficulty++;
                questions = getShuffledQuestions(choice, difficulty);
                index = 0;
            }

            if (index >= questions.size()) {
                questions = getShuffledQuestions(choice, difficulty);
                index = 0;
            }
        }

        showSummary();
        runMockTests();
    }

    static boolean askQuestion(Question q) {
        final int TIME_LIMIT = 15;
        long start = System.currentTimeMillis();

        System.out.println("\n" + q.question);
        for (int i = 0; i < 4; i++)
            System.out.println((i + 1) + ". " + q.options[i]);
        System.out.println("Choose option (1-4) | 5: 50/50 | 6: Skip");
        System.out.println("⏱ You have " + TIME_LIMIT + " seconds");
        

        Integer userAns = TimedInput.getIntWithTimeout(TIME_LIMIT);

        long elapsed = (System.currentTimeMillis() - start) / 1000;
        totalTime += elapsed;

        if (elapsed < 2) {
            System.out.println(" Anti-Cheat Triggered: Answered too fast!");
            return false;
        }

        if (userAns == null) {
            System.out.println(" Time's up! Correct Answer: " + (q.correctAnswer + 1));
            categoryTotal.put(q.category, categoryTotal.getOrDefault(q.category, 0) + 1);
            return false;
        }

        if (userAns == 5) { // 50/50
            if (fiftyUsed) System.out.println("❌ 50/50 already used!");
            else {
                fiftyUsed = true;
                System.out.println("50/50 Activated: Remove 2 wrong options");
                int removed = 0;
                for (int i = 0; i < 4 && removed < 2; i++) {
                    if (i != q.correctAnswer) {
                        System.out.println("Removed Option: " + (i + 1));
                        removed++;
                    }
                }
            }
            return askQuestion(q);
        }

        if (userAns == 6) { // Skip
            if (skipUsed) System.out.println("❌ Skip already used!");
            else {
                skipUsed = true;
                System.out.println("⏭ Question Skipped!");
            }
            return false;
        }

        if (userAns - 1 == q.correctAnswer) {
            System.out.println("✅ Correct!");
            correct++;
            categoryScore.put(q.category, categoryScore.getOrDefault(q.category, 0) + 1);
        } else {
            System.out.println("❌ Wrong! Correct Answer: " + (q.correctAnswer + 1));
        }
        categoryTotal.put(q.category, categoryTotal.getOrDefault(q.category, 0) + 1);
        return userAns - 1 == q.correctAnswer;
    }

    static void showSummary() {
        System.out.println("\n===== QUIZ SUMMARY =====");
        System.out.println("Total Questions: " + askedQuestions.size());
        System.out.println("Correct Answers: " + correct);
        System.out.println("Time Taken: " + totalTime + " seconds");

        String strong = "", weak = "";
        double max = 0, min = 100;

        for (String cat : categoryTotal.keySet()) {
            double percent = (categoryScore.getOrDefault(cat, 0) * 100.0) / categoryTotal.get(cat);
            if (percent > max) { max = percent; strong = cat; }
            if (percent < min) { min = percent; weak = cat; }
        }

        System.out.println("Strongest Category: " + strong + " (" + max + "%)");
        System.out.println("Weakest Category: " + weak + " (" + min + "%)");
    }

    static List<Question> getShuffledQuestions(int category, int difficulty) {
        List<Question> list = new ArrayList<>();
        for (Question q : quizData[category][difficulty])
            if (!askedQuestions.contains(q.question))
                list.add(q);
        Collections.shuffle(list);
        return list;
    }

    static void runMockTests() {
        int simulations = 100;
        int totalCorrect = 0;
        int anomalies = 0;
        Random rand = new Random();

        for (int i = 0; i < simulations; i++) {
            int correctCount = 0;
            for (int q = 0; q < TOTAL_QUESTIONS; q++)
                if (rand.nextBoolean()) correctCount++;
            totalCorrect += correctCount;
            if (correctCount >= TOTAL_QUESTIONS - 1) anomalies++;
        }

        System.out.println("\n===== MOCK TEST RESULT =====");
        System.out.println("Simulations Run: " + simulations);
        System.out.println("Average Correct: " + (totalCorrect / (double) simulations));
        System.out.println("Anomalies Detected: " + anomalies);
    }

    static void loadQuestions() {
        quizData = new Question[2][3][3];

        // Java Easy
        quizData[0][0][0] = new Question("What is JVM?", new String[]{"Java Virtual Machine","Java Variable Method","Java Verified Mode","None"}, 0, "Java");
        quizData[0][0][1] = new Question("Which keyword prevents inheritance?", new String[]{"static","final","private","protected"}, 1, "Java");
        quizData[0][0][2] = new Question("Size of int?", new String[]{"2 bytes","4 bytes","8 bytes","Depends"}, 1, "Java");

        // Java Medium
        quizData[0][1][0] = new Question("Parent of all classes?", new String[]{"System","String","Object","Class"}, 2, "Java");
        quizData[0][1][1] = new Question("Which collection allows duplicates?", new String[]{"Set","Map","List","None"}, 2, "Java");
        quizData[0][1][2] = new Question("What is polymorphism?", new String[]{"Many forms","One form","Inheritance","Encapsulation"}, 0, "Java");

        // Java Hard
        quizData[0][2][0] = new Question("Which exception is unchecked?", new String[]{"IOException","SQLException","NullPointerException","ClassNotFound"}, 2, "Java");
        quizData[0][2][1] = new Question("Purpose of garbage collection?", new String[]{"Speed","Security","Memory management","Threading"}, 2, "Java");
        quizData[0][2][2] = new Question("Which keyword prevents inheritance?", new String[]{"static","final","private","protected"}, 1, "Java");

        // SQL Easy
        quizData[1][0][0] = new Question("SQL stands for?", new String[]{"Structured Query Language","Simple Query Language","Standard Query Language","None"}, 0, "SQL");
        quizData[1][0][1] = new Question("Which command retrieves data?", new String[]{"INSERT","UPDATE","SELECT","DELETE"}, 2, "SQL");
        quizData[1][0][2] = new Question("Primary key allows?", new String[]{"Duplicates","Null","Unique","All"}, 2, "SQL");

        // SQL Medium
        quizData[1][1][0] = new Question("Which clause filters rows?", new String[]{"WHERE","ORDER BY","GROUP BY","HAVING"}, 0, "SQL");
        quizData[1][1][1] = new Question("Which join returns all rows?", new String[]{"INNER","LEFT","RIGHT","FULL"}, 3, "SQL");
        quizData[1][1][2] = new Question("Which command removes table?", new String[]{"DELETE","DROP","TRUNCATE","REMOVE"}, 1, "SQL");

        // SQL Hard
        quizData[1][2][0] = new Question("Which normal form removes partial dependency?", new String[]{"1NF","2NF","3NF","BCNF"}, 1, "SQL");
        quizData[1][2][1] = new Question("Index improves?", new String[]{"Security","Speed","Memory","Backup"}, 1, "SQL");
        quizData[1][2][2] = new Question("Which constraint ensures uniqueness?", new String[]{"NOT NULL","CHECK","UNIQUE","DEFAULT"}, 2, "SQL");
    }
}
