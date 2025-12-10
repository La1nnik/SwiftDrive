
import net.java.games.input.*;
import swiftbot.SwiftBotAPI;

public class Main
{

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
        faderThread.start();

        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        Controller gamepad = null;
        for (Controller c : controllers) {
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                gamepad = c;
                break;
            }
        }

        if (gamepad == null) {
            System.out.println("No controller found.");
            return;
        }

        Component xAxis = gamepad.getComponent(Component.Identifier.Axis.X);
        Component yAxis = gamepad.getComponent(Component.Identifier.Axis.Y);
        Component shareButton = gamepad.getComponent(Component.Identifier.Button.SELECT);
        Component dpadLeft = gamepad.getComponent(Component.Identifier.Axis.POV);

        float deadzone = 0.2f;
        final float maxSpeed = 100;

        boolean lastDpadLeft = false;

        while (true) {

            if (!gamepad.poll()) {
                System.out.println("Controller disconnected.");
                exitGracefully(fader, faderThread);
            }

            float xValue = xAxis.getPollData();
            float yValue = yAxis.getPollData();

            if (Math.abs(xValue) < deadzone) xValue = 0;
            if (Math.abs(yValue) < deadzone) yValue = 0;

            yValue = -yValue;

            float turn = xValue * Math.abs(xValue) * Math.abs(xValue);

            int leftWheel = (int) ((yValue + turn) * maxSpeed);
            int rightWheel = (int) ((yValue - turn) * maxSpeed);

            leftWheel = Math.max(-100, Math.min(100, leftWheel));
            rightWheel = Math.max(-100, Math.min(100, rightWheel));

            swiftBot.startMove(leftWheel, rightWheel);


            // ---- TOGGLE UNDERLIGHT ----

            boolean dpadLeftPressed =
                    (dpadLeft != null && dpadLeft.getPollData() == Component.POV.LEFT);

            if (dpadLeftPressed && !lastDpadLeft) {

                if (!fader.isRunning()) {
                    System.out.println("Starting underlight fader.");
                    fader.setRunning(true);

                } else {
                    System.out.println("Stopping underlight fader.");
                    fader.setRunning(false);
                    swiftBot.disableUnderlights();
                }
            }

            lastDpadLeft = dpadLeftPressed;


            // ---- SHUTDOWN ----

            if (shareButton != null && shareButton.getPollData() == 1.0f) {
                System.out.println("Share button pressed: shutting down.");

                exitGracefully(fader, faderThread);
            }

            Thread.sleep(50);
        }

    }





     static void exitGracefully(UnderlightFader fader, Thread faderThread) throws InterruptedException {
        swiftBot.stopMove();
        fader.shutdown();
        faderThread.join();
        System.exit(0);
    }

}
