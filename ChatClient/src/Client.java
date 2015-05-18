import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Reuben on 5/16/2015.
 * A chat client
 */
public class Client implements Runnable {

    //Default port number
    public static final String DEFAULT_PORT = "900";

    //socket used to connect to the server
    private Socket socket;

    //input and output streams
    private DataInputStream in;
    private DataOutputStream out;

    //keeps track of whether the user is currently connected to a server
    boolean connected = false;

    //chat userName of the client
    private String username;

    //the gui of the client
    ClientGUI gui;

    //constructor
    public Client(String username) {
        //sets client userName
        this.username = username;
        //creates the gui
        gui = new ClientGUI(this);
        gui.showMessage("Welcome to SimpleChat! You can connect to servers in the options menu.\n Currently, the server is hosted on 98.204.68.177 - Have fun!");

    }

    //background thread used to get incoming messages
    public void run() {
        try {
            //constantly looks for incoming messages on the input stream
            while(true){
               //gets next message
                String message = in.readUTF();
                //prints message to text window
                gui.showMessage(message);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //logs a message in the text area
    public void log(String message){
        gui.showMessage(message);
    }

    //connects to a server
    public void connect(){
        //makes sure user isn't already in a room
        if(connected){
            gui.showMessage("Please close current connection before joining a new room");
            return;
        }
        //gets the host string
        String host = gui.promptHost();
        //checks for user cancellation
        if(host == null){
            return;
        }
        //gets port string
        String port = gui.promptPort();
        //checks for user cancellation
        if(port == null){
            return;
        }
        //port is guaranteed to be parsable by promptPort method
        int portNumber = Integer.parseInt(port);
        //connects to server using host and portNumber
        connectToServer(host, portNumber);
    }

    //disconnected from a server
    public void disconnect(){
        //checks if user is currently in a room
        if(!connected){
            gui.showMessage("You are not currently in a chat room");
            return;
        }
        try {
            gui.clearText();
            socket.close();
            gui.showMessage("Disconnected");
            connected = false;
        } catch (IOException io){
            gui.showMessage("An error occurred while disconnecting from server");
            io.printStackTrace();
        }
    }

    //creates socket and I/O streams for a server connection
    public boolean connectToServer(String host, int port){
        try {
            //establishes connection
            socket = new Socket(host, port);
            //logs successful connection
            gui.clearText();
            log("Connected to " + socket);
            //establishes input and output streams
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            //starts the background thread for receiving messages
            new Thread(this).start();
            connected = true;
            return true;
        } catch (IOException e){
            gui.showMessage("An error occurred while connecting to the server. ");
            e.printStackTrace();
            return false;
        }
    }

    //sends a message
    public void sendMessage(String message){
        //checks whether the user is on a server
        if(connected) {
            //sends the message
            processMessage(message);
        } else {
            //informs the user that they are not on a server
            log("You are not in a chat room. Use the connect button to join a room.");
            gui.clearInput();
        }
    }

    //processes a message and sends it to the server
    private void processMessage(String message){
        try {
            //send message to server
            out.writeUTF(username + ": " + message);
            gui.clearInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //sets username
    public void setUsername(String username){
        try{
            processMessage("changed name to " + username);
        } catch (NullPointerException e){
            gui.showMessage("changed name to " + username);
        }
        this.username = username;
    }
}
