package org.example;

import net.java.games.input.*;
import swiftbot.SwiftBotAPI;





public class TestingGamepad {

    static SwiftBotAPI swiftBot;


    public static void main(String[] args) throws InterruptedException {
        try {
            swiftBot = SwiftBotAPI.INSTANCE;
        } catch (Exception e) {
            System.out.println("\nI2C disabled!");
            System.exit(5);
        }


        Event event = new Event();

        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        Controller gamepad = null;

        for (Controller c : controllers) {
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                gamepad = c;
                break;
            }
        }

        System.out.println("Using controller: " + gamepad.getName());

        if (gamepad == null) {
            System.out.println("No controller found!");
            return;
        }

        //swiftBot.move(100,100, 5000);

        System.exit(0);




    }

}
