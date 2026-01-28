package quizapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public class TimedInput {

    public static Integer getIntWithTimeout(int seconds) {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<Integer> future = ex.submit(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line =reader.readLine();
                try {
                    return Integer.parseInt(line.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid number:");
                }
            }
        });

        try {
            return future.get(seconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            ex.shutdownNow();
        }
    }
}
