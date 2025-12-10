
import swiftbot.SwiftBotAPI;

public class UnderlightFader implements Runnable
{
    private final SwiftBotAPI swiftBot;

    private volatile boolean running = false;
    private volatile boolean shutdown = false;

    public boolean isRunning() {
        return running;
    }


    public UnderlightFader(SwiftBotAPI api) {
        this.swiftBot = api;
    }


    public synchronized void setRunning(boolean value) {
        running = value;
        if (running) {
            notify();
        }
    }

    public synchronized void shutdown() {
        running = false;
        shutdown = true;
        notify(); // wake up the thread if waiting
    }

    @Override
    public void run() {

        int[][] colors = {
                {255, 0, 0},    // red
                {0, 255, 0},    // green
                {0, 0, 255},    // blue
                {255, 255, 0},  // yellow
                {255, 0, 255},  // magenta
                {0, 255, 255}   // cyan
        };

        int fadeSteps = 100;

        while (!shutdown) {

            synchronized (this) {
                while (!running && !shutdown) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {}
                }
            }


            // Fade loop only runs while ON
            for (int i = 0; i < colors.length && running && !shutdown; i++) {
                int[] start = colors[i];
                int[] end = colors[(i + 1) % colors.length];

                for (int step = 0; step < fadeSteps && running && !shutdown; step++) {

                    float t = (float) step / fadeSteps;
                    int r = (int) (start[0] + (end[0] - start[0]) * t);
                    int g = (int) (start[1] + (end[1] - start[1]) * t);
                    int b = (int) (start[2] + (end[2] - start[2]) * t);

                    swiftBot.fillUnderlights(new int[]{r, g, b});

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        if (shutdown) return;
                    }
                }
            }
        }

        swiftBot.disableUnderlights();
    }
}
