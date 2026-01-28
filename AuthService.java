package quizapp;

import java.io.*;
import java.util.Scanner;

public class AuthService {

    private static final String FILE_NAME = "users.txt";

    public static boolean loginOrRegister(Scanner sc) {
        System.out.println("===== LOGIN MENU =====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1: return login(sc);
            case 2: return register(sc);
            default: return false;
        }
    }

    private static boolean register(Scanner sc) {
        try {
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            if (userExists(username)) {
                System.out.println("❌ Username already exists!");
                return false;
            }

            FileWriter fw = new FileWriter(FILE_NAME, true);
            fw.write(username + "," + password + "\n");
            fw.close();

            System.out.println("✅ Registration successful. Welcome " + username + "!");
            return true;

        } catch (IOException e) {
            System.out.println("Error during registration.");
            return false;
        }
    }

    private static boolean login(Scanner sc) {
        try {
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            File file = new File(FILE_NAME);
            if (!file.exists()) {
                System.out.println("❌ No users found. Please register first.");
                return false;
            }

            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String[] data = fileScanner.nextLine().split(",");
                if (data[0].equals(username) && data[1].equals(password)) {
                    System.out.println("✅ Login successful!");
                    fileScanner.close();
                    return true;
                }
            }
            fileScanner.close();

            System.out.println("❌ Invalid credentials!");
            return false;

        } catch (Exception e) {
            System.out.println("Error during login.");
            return false;
        }
    }

    private static boolean userExists(String username) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String[] data = sc.nextLine().split(",");
            if (data[0].equals(username)) {
                sc.close();
                return true;
            }
        }
        sc.close();
        return false;
    }
}
