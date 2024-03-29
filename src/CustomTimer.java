import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer {
    private Timer timer;
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;

    public CustomTimer() {
        timer = new Timer();
        isRunning = false;
    }

    public void start() {
        if (!isRunning) {
            startTime = System.nanoTime() - elapsedTime;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    elapsedTime = System.nanoTime() - startTime;
                    System.out.println("Elapsed Time: " + elapsedTime + " nanoseconds");
                }
            }, 0, 1000000000); // Update every second (in nanoseconds)
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            timer = new Timer();
            isRunning = false;
        }
    }

    public void resume() {
        if (!isRunning) {
            start();
        }
    }

    public void end() {
        stop();
        elapsedTime = 0;
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime(); // start timer

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long stopTime = System.nanoTime();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long resumeTime = System.nanoTime();
        long duration1 = (resumeTime - stopTime);
        long endTime = System.nanoTime() - duration1;

        long duration = (endTime - startTime);

        System.out.println("Sudoku solved in " + duration + " nanoseconds.");

        CustomTimer customTimer = new CustomTimer();
        customTimer.start();

        // Simulate some time passing
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        customTimer.stop();

        // Simulate resuming the timer
        customTimer.resume();

        // Simulate some more time passing
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // End the timer
        customTimer.end();
    }
}