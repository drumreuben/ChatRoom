import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Reuben on 5/17/2015.
 * The GUI for the chat client
 */
public class ClientGUI extends JFrame {

    //visual components of the panel
    private TextField textField = new TextField();
    private TextArea textArea = new TextArea();
    private JMenuBar menuBar = new JMenuBar();
    private JMenu options = new JMenu("Options");
    private JMenuItem connect = new JMenuItem("Connect");
    private JMenuItem disconnect = new JMenuItem("Disconnect");
    private JMenuItem changeName = new JMenuItem("Change Username");
    private JPanel panel = new JPanel();

    //default panel size
    public int width = 500;
    public int height = 500;

    //the client using the GUI
    private Client client;


    public ClientGUI(final Client client){
        //sets the client
        this.client = client;
        //configures the menu bar
        menuBar.add(options);
        //configures options menu
        options.add(connect);
        options.add(disconnect);
        options.add(changeName);
        //configures the text area component
        textArea.setEditable(false);
        //adds components to the panel using a border layout
        panel.setLayout(new BorderLayout());
        panel.add("Center", textArea);
        panel.add("South", textField);
        //adds components to the jFrame
        this.add(panel);
        this.setJMenuBar(menuBar);
        //configures JFrame
        this.setSize(new Dimension(width, height));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //shows JFrame
        this.setVisible(true);

        //action listener for the connect button
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.connect();
            }
        });

        //action listener for the disconnect button
        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.disconnect();
            }
        });

        //sends a message when the user types a line and hits return
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = e.getActionCommand();
                client.sendMessage(message);
            }
        });

        //change username listener
        changeName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setUsername(promptUserName());
            }
        });
    }

    //prompts the client to enter a host to connect to
    public static String promptHost(){
        return JOptionPane.showInputDialog("Enter the ip of the server you wish to connect to");
    }

    //prompts the client to enter the port number
    public static String promptPort(){
        String input;
        //loops until a valid input is given
        while(true){
            //gets port number as a string
            input = JOptionPane.showInputDialog("Enter the port number, or leave blank to use default (900)");
            //checks for cancellation
            if(input == null){
                return null;
            }
            //if user has hit okay and not enter text, default port number 900 is used.
            if(input.equals("")){
                return Client.DEFAULT_PORT;
            }
            try {
                Integer.parseInt(input);
                return input;
            } catch (NumberFormatException n){
                JOptionPane.showMessageDialog(null, "Port must be an integer or left empty");
            }
        }
    }

    //prompts the client to enter a username
    public static String promptUserName(){
        return JOptionPane.showInputDialog("Please enter a username");
    }

    //displays an incoming message
    public void showMessage(String message){
        textArea.append(message + "\n");
        //jiggles the window so that everything works
        //TODO this should really not be necessary
        this.setSize(width+1, height);
        this.setSize(width-1, height);
    }

    //clears textField text
    public void clearInput(){
        textField.setText(" ");
    }

    //clears textArea text
    public void clearText(){
        textArea.setText("");
    }
}
