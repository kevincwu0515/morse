package morse;

/**
 * @class MorseConstant
 * @author Kevin Wu
 * @date April 01 2016
 */
public class MorseConstant {

    public static String SERVER_IP = "localhost";
    public static int SERVER_PORT = 10000;
    // the morse number from 0 to 1
    public static final String[] morseNumbers = {
        "-----", ".----", "..---", "...--", "....-",
        ".....", "-....", "--...", "---..", "----."
    };
    // the morse letters from a to z
    public static final String[] morseLetters = {
        ".-", "-...", "-.-.", "-..", ".",
        "..-.", "--.", "....", "..", ".---",
        "-.-", ".-..", "--", "-.", "---",
        ".--.", "--.-", ".-.", "...", "-",
        "..-", "...-", ".--", "-..-", "-.--",
        "--.."};
    // Morse-code numbers and letters

    public static final String[] morseCharacters = {"-----", ".----",
        "..---", "...--", "....-", ".....", "-....", "--...", "---..",
        "----.", ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....",
        "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-",
        ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.."};

    // normal English characters
    public static final String[] normalCharacters = {"0", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G",
        "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
        "U", "V", "W", "X", "Y", "Z"};
}
