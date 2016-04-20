package morse;

import java.io.IOException;
import static morse.MorseConstant.morseLetters;
import static morse.MorseConstant.morseNumbers;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @class MorseServer
 * @author Kevin Wu
 * @date April 01 2016
 */
public class MorseServer {

    private Client[] clients;
    private ServerSocket server;
    private ExecutorService runGame;
    private Lock morseLock;
    private Condition otherClientConnected;

    // set up Morse server
    public MorseServer() {
        clients = new Client[2]; // create array with two clients

        // create ExecutorService with a thread for each client
        runGame = Executors.newFixedThreadPool(2);
        morseLock = new ReentrantLock(); // create lock

        // condition variable for both clients being connected
        otherClientConnected = morseLock.newCondition();

        try {
            server = new ServerSocket(MorseConstant.SERVER_PORT, 2); // set up ServerSocket
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.exit(1);
        }
    }

    // wait for two connections so communication can be started
    public void execute() {
        // wait for each client to connect
        for (int i = 0; i < clients.length; i++) {
            try {
                clients[i] = new Client(server.accept(), i);
                runGame.execute(clients[i]); // execute client runnable
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }
        }

        morseLock.lock(); // lock application to signal second client

        // first client was suspended to wait for second client
        try {
            otherClientConnected.signal(); // signal first client
        } finally {
            morseLock.unlock(); // unlock after signalling first client
        }
    }

    public void translate(String phrase, int clientNumber) {
        morseLock.lock(); // lock so only one client can proceed

        try {
            String morseCode = ""; // initialize Morse code of phrase
            // loop through the string
            for (int i = 0; i < phrase.length(); i++) {
                char alpha = phrase.charAt(i);
                // if the character is a number, access the number array
                if (Character.isDigit(alpha)) {
                    morseCode += morseNumbers[alpha - '0'] + " ";
                }
                // if the character is a letter, access the letter array
                if (Character.isLetter(alpha)) {
                    morseCode += morseLetters[Character.toUpperCase(alpha) - 'A'] + " ";
                }
                // if the character is a space, output two extra spaces
                if (alpha == ' ') {
                    morseCode += "   ";
                }
            }

            // let other client know the morseCode
            if (clientNumber == 0) {
                clients[1].showMorseCode(morseCode);
            } else {
                clients[0].showMorseCode(morseCode);
            }
        } finally {
            morseLock.unlock(); // unlock application to let other client go
        }
    }

    private class Client implements Runnable {

        private Socket connection;
        private Scanner input;
        private Formatter output;
        private int clientID;

        // set up client
        public Client(Socket socket, int number) {
            clientID = number; // store number
            connection = socket; // store socket

            try {
                // obtain i/o stream
                input = new Scanner(connection.getInputStream());
                output = new Formatter(connection.getOutputStream());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }
        }

        // send message containing other client's phrase
        public void showMorseCode(String morseCode) {
            output.format("%s\n", morseCode); // send Morse-code message
            output.flush();
        }

        // control thread's execution
        public void run() {
            // if client 0, wait for another client to connect
            if (clientID == 0) {
                morseLock.lock(); // lock to wait for second client
                try {
                    otherClientConnected.await(); // wait for client 1
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                } finally {
                    morseLock.unlock(); // unlock after second client
                }
            }

            while (true) {
                if (input.hasNextLine()) {
                    translate(input.nextLine(), clientID); // translate
                }
            }
        }
    }
}
