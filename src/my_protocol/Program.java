package my_protocol;

import framework.IMACProtocol;
import framework.MACChallengeClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Entry point of the program. Starts the client and links the used MAC
 * protocol.
 *
 * @author Jaco ter Braak & Frans van Dijk, University of Twente
 * @version 2018-03-03
 */
public class Program {

    // Change to your group authentication token
    private static String groupToken = "4f991543-5d14-4576-9979-3684596697be";

    // Change to your protocol implementation
    private static IMACProtocol protocol = new MyProtocol();

    // Challenge server address
    private static String serverAddress = "networkingchallenges.ewi.utwente.nl";

    // Challenge server port
    private static int serverPort = 8003;

    // Randomly generate random ID, used by MyProtocol.
    public static int clientID = new Random().nextInt(100);

    /*
     *
     *
     *
     *
     *
     *
     *
     * DO NOT EDIT BELOW THIS LINE
     */
    public static void main(String[] args) {
        MACChallengeClient client = null;
        try {
            System.out.println("[FRAMEWORK] Starting client... ");

            // Create the client
            client = new MACChallengeClient(serverAddress, serverPort, groupToken);

            System.out.println("[FRAMEWORK] Done.");

            // Set protocol
            client.setListener(protocol);

            System.out.println("[FRAMEWORK] Press Enter to start the simulation...");
            System.out
                    .println("[FRAMEWORK] (Simulation will also be started automatically if another client in the group issues the start command)");

            boolean startCommand = false;
            InputStream inputStream = new BufferedInputStream(System.in);
            while (!client.isSimulationStarted() && !client.isSimulationFinished()) {
                if (!startCommand && inputStream.available() > 0) {
                    client.requestStart();
                    startCommand = true;
                }
                Thread.sleep(10);
            }

            System.out.println("[FRAMEWORK] Simulation started!");

            // Wait until the simulation ends
            while (!client.isSimulationFinished()) {
                Thread.sleep(10);
            }

            System.out
                    .println("[FRAMEWORK] Simulation stopped! Check your performance on the server web interface.");

        } catch (IOException e) {
            System.out.print("[FRAMEWORK ERROR] Could not start the client, because: ");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("[FRAMEWORK ERROR] Operation interrupted.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.print("[FRAMEWORK ERROR] Unexpected Exception: ");
            e.printStackTrace();
        } finally {
            if (client != null) {
                System.out.print("[FRAMEWORK] Shutting down client... ");
                client.stop();
                System.out.println("[FRAMEWORK] Done.");
            }
        }
    }
}
