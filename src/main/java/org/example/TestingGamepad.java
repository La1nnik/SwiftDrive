package org.example;

import net.java.games.input.*;
import swiftbot.SwiftBotAPI;

/*
Components of a playstation controller:
  A  id=A
  B  id=B
  X  id=X
  Y  id=Y
  Left Thumb  id=Left Thumb
  Right Thumb  id=Right Thumb
  Left Thumb 2  id=Left Thumb 2
  Right Thumb 2  id=Right Thumb 2
  Select  id=Select
  Start  id=Start
  Mode  id=Mode
  Left Thumb 3  id=Left Thumb 3
  Right Thumb 3  id=Right Thumb 3
  x  id=x
  y  id=y
  z  id=z
  rx  id=rx
  ry  id=ry
  rz  id=rz
 pov  id=pov
*/


public class TestingGamepad {

    static SwiftBotAPI swiftBot;


    public static void main(String[] args) throws InterruptedException {
        try {
            swiftBot = SwiftBotAPI.INSTANCE;
        } catch (Exception e) {
            System.out.println("\nI2C disabled!");
            System.exit(5);
        }

        UnderlightFader fader = new UnderlightFader(swiftBot);
        Thread faderThread = new Thread(fader);



        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();


        Controller gamepad = null;

        for (Controller c : controllers) {
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                gamepad = c;
                break;
            }
        }

        System.out.println("Using controller: " + gamepad.getName());

        /*
        System.out.println("Components:");
        for (Component comp : gamepad.getComponents()) {
            System.out.println("  " + comp.getName() + "  id=" + comp.getIdentifier().getName());
        }

        */

        if (gamepad == null) {
            System.out.println("No controller found!");
            return;
        }


        Component xAxis = gamepad.getComponent(Component.Identifier.Axis.X);
        Component yAxis = gamepad.getComponent(Component.Identifier.Axis.Y);



        if (xAxis == null || yAxis == null) {
            System.out.println("Left stick axes not found!");
            return;
        }

        float deadzone = 0.2f; // ignore tiny inputs

        float maxSpeed = 100;




        while (true) {
            // poll the controller to update values
            boolean ok;
            try {
                ok = gamepad.poll();
            } catch (Exception e) {
                System.out.println("Controller poll failed: " + e.getMessage());
                Thread.sleep(500);
                continue;
            }
            if (!ok) {
                System.out.println("Controller poll returned false (maybe disconnected).");
                Thread.sleep(500);
                continue;
            }




            float xValue = xAxis.getPollData();
            float yValue = yAxis.getPollData();


            float turn = xValue * Math.abs(xValue) * Math.abs(xValue);

            if (Math.abs(xValue) < deadzone) xValue = 0;
            if (Math.abs(yValue) < deadzone) yValue = 0;


            // Invert y-axis (pushing stick forward gives negative)
            yValue = -yValue;


            int leftWheel = (int) ((yValue + turn) * maxSpeed);
            int rightWheel = (int) ((yValue - turn) * maxSpeed);

            //clamping wheel speeds to -100 to 100
            leftWheel = Math.max(-100, Math.min(100, leftWheel));
            rightWheel = Math.max(-100, Math.min(100, rightWheel));


            swiftBot.startMove(leftWheel, rightWheel);


            //SHUTDOWN
            Component shareButton = gamepad.getComponent(Component.Identifier.Button.SELECT);
            Component dpadLeft = gamepad.getComponent(Component.Identifier.Axis.POV);

            if (dpadLeft != null && dpadLeft.getPollData() == Component.POV.LEFT){
                if (!fader.GetRunning()) {
                    System.out.println("Starting underlight fader.");
                    faderThread.start();
                }
                else if (fader.GetRunning()) {
                    System.out.println("Stopping underlight fader.");
                    fader.stop();
                }
            }

            if (shareButton != null && shareButton.getPollData() == 1.0f) {
                System.out.println("Share button pressed: shutting down.");
                swiftBot.stopMove();
                fader.stop();
                System.exit(0);
            }

            Thread.sleep(50);

        }
    }
}
