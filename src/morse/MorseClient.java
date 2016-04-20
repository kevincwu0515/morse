package morse;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.Socket;
import java.io.IOException;
import static morse.MorseConstant.morseCharacters;
import static morse.MorseConstant.normalCharacters;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * @class MorseClient
 * @author Kevin Wu
 * @date April 01 2016
 */
public class MorseClient extends JFrame implements Runnable {

    // HashMap provides quick lookup for converting
    private final HashMap< String, String> morseToNormal;
    private JTextField inputField;
    private JTextArea displayArea;
    private Socket connection;
    private Scanner input;
    private Formatter output;

    // Set up user interface
    public MorseClient() {
        super("Morse Code Decoder");

        // populate lookup table
        morseToNormal = new HashMap<>();

        for (int i = 0; i < morseCharacters.length; ++i) {
            morseToNormal.put(morseCharacters[i], normalCharacters[i]);
        }

        inputField = new JTextField(); // text field to input messages
        add(inputField, BorderLayout.NORTH);
        inputField.addActionListener(
                (ActionEvent e) -> {
                    output.format("%s\n", inputField.getText());
                    output.flush(); // flush output to server
                    inputField.setText(""); // clear input
                }
        );

        displayArea = new JTextArea(); // text area for output
        displayArea.setEditable(false); // disable editing
        add(new JScrollPane(displayArea), BorderLayout.CENTER);
    }

    // make connection to server
    public void execute() {
        try {
            connection = new Socket(MorseConstant.SERVER_IP, MorseConstant.SERVER_PORT);

            // get i/o streams
            input = new Scanner(connection.getInputStream());
            output = new Formatter(connection.getOutputStream());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // create and start worker thread for this client
        ExecutorService worker = Executors.newFixedThreadPool(1);
        worker.execute(this); // execute client
    }

    @Override
    public void run() {
        // receive messages sent to client and output them
        while (true) {
            if (input.hasNextLine()) {
                processMessage(input.nextLine());
            }
        }
    }

    private void processMessage(final String morseCode) {
        // translate Morse code to normal characters
        final String message = translate(morseCode);

        // display strings in event-dispatching thread
        SwingUtilities.invokeLater(() -> {
            displayArea.append("\n\nMorse Code: \n" + morseCode + "\n\nNormal Text: \n" + message);
        }
        );
    }

    // translate Morse code phrase to normal text
    private String translate(String morseCode) {
        String result = ""; // result of translation
        int start = 0; // start location of next word
        int length = 0; // length of translated string
        int threeSpaces = morseCode.indexOf("   "); // find end letter
        String word; // string to hold Morse-code word

        // while not reach the end of Morse code
        while (length < morseCode.length()) {
            if (threeSpaces != -1) {
                word = morseCode.substring(start, threeSpaces);
                length = threeSpaces; // update length of translation
            } else {
                word = morseCode.substring(start, morseCode.length());
                length = morseCode.length(); // ends translation
            }

            // decode letters
            for (String letter : word.split("\\s+")) {
                result += decode(letter); // decode one letter
            }
            result += " "; // add space between words
            start = threeSpaces + 3; // update start location of next word
            threeSpaces = morseCode.indexOf("   ", start);
        }

        return result;
    }

    // decode Morse-code letter
    private String decode(String morseCode) {
        String normal = morseToNormal.get(morseCode);
        return normal == null ? "" : normal;
    }
}
