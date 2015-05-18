import javax.swing.*;
import java.awt.*;
import javax.swing.UIManager.*;

/**
 * Created by Reuben on 5/16/2015.
 * creates a new JFrame that the client panel resides in
 */
public class runClient {

    static final int DEFAULT_PORT = 900;

    /**
     * Main method
     * Usage: string hostname, int port
     * Usage: string hostname
     */
    public static void main(String[] args){

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        //gets client userName
        String username = promptUserName();
        new Client(username);
    }

    //prompts the client to enter a username
    private static String promptUserName(){
        return JOptionPane.showInputDialog("Please enter a username");
    }

}
