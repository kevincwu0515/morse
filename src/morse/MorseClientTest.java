package morse;

import javax.swing.JFrame;

/**
 * @class MorseClientTest
 * @author Kevin Wu
 * @date April 01 2016
 */
public class MorseClientTest {

    public static void main(String[] args) {

        MorseClient mclient = new MorseClient();

        mclient.setSize(300, 410);
        mclient.setVisible(true);
        mclient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mclient.execute();

    }
}
