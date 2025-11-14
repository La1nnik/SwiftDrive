package org.example;

import swiftbot.SwiftBotAPI;

public class UnderlightFader implements Runnable
{
    private final SwiftBotAPI swiftBot;

    private volatile boolean running = true;

    public boolean GetRunning()
    {
        return running;
    }

    public UnderlightFader(SwiftBotAPI api)
    {
        this.swiftBot = api;
    }

    public void stop()
    {
        running = false;
    }

    @Override
    public void run(){
        int[][] colors = {
                {255, 0, 0},    // red
                {0, 255, 0},    // green
                {0, 0, 255},    // blue
                {255, 255, 0},  // yellow
                {255, 0, 255},  // magenta
                {0, 255, 255}   // cyan
        };

        int fadeSteps = 100;


        while (running){
            for(int i = 0; i < colors.length; i++){
                int[] start = colors[i];
                int[] end = colors[(i + 1) % colors.length];

                for(int step = 0; step < fadeSteps && running; step++){
                    float t = (float) step / fadeSteps;
                    int r = (int) (start[0] + (end[0] - start[0]) * t);
                    int g = (int) (start[1] + (end[1] - start[1]) * t);
                    int b = (int) (start[2] + (end[2] - start[2]) * t);

                    int[] rgb = {r, g, b};
                    swiftBot.fillUnderlights(rgb);

                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        }
    }
}
