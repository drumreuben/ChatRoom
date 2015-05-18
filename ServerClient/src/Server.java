import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by Reuben on 5/16/2015.
 * Standalone program that handles the creation of client threads and sockets
 */
public class Server {

    //default port
    static final int DEFAULT_PORT = 900;

    //mapping of sockets to DataOutputStreams
    private final Hashtable outputStreams = new Hashtable();

    //output for the server
    TextArea log = new TextArea();

    /**
     * Main routine
     * takes a specific port as a command line argument, or uses the default one
     * Usage: java Server, java Server [port]
     */
    public static void main(String[] args){
        //default setting
        int port = DEFAULT_PORT;
        //get the port number from command line if user gives one as an argument
        if(args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe){
                System.out.println("java Server, java Server [port]");
                System.exit(-1);
            }
        }
        //creates the server object
        new Server(port);
    }

    /**
     * the window in which the server logs information
     */
    public JFrame makeServerWindow(){
        JFrame window = new JFrame("window");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(new Dimension(500, 500));
        window.add(log);
        return window;
    }

    /**
     * logs a message in the window
     */
    public void log(String message){
        log.append(message + "\n");
    }


    /**
     * listens on a specified port and creates a thread for any clients
     * who attempt connection on that port
     */
    public Server (int port){
        //makes the visible server component
        JFrame window = makeServerWindow();
        window.setVisible(true);
        try {
            listen(port);
        } catch (IOException e){
            e.printStackTrace();
            log("Something went wrong establishing client connection");
            System.exit(-1);
        }
    }

    /**
     * loops forever, accepting new connections and creating
     * output streams and threads to deal with them
     */
    public void listen (int port) throws IOException {
        //creates the server socket
        ServerSocket serverSocket = new ServerSocket(port);
        //logs server creation
        log("Listening on " + serverSocket);
        //keeps accepting connections forever
        while(true) {
            //establishes next connection
            Socket socket = serverSocket.accept();
            //logs connection
            log("Connection from " + socket);
            //creates DataOutputStream for writing data to the client
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            //saves the socket and its outputStream
            outputStreams.put(socket, out);
            //creates a new thread for the connection
            new ServerThread(this, socket);
        }
    }

    /**
     * Removes a socket and its corresponding outputStream from the server
     * Called when a server thread realizes that its connection to the client is dead
     */
    public void removeConnection(Socket s) {
        //synchronize to prevent interfering with sendToAll being called at the same time
        synchronized (outputStreams) {
            //logs the disconnection
            log("Removing connection " + s);
            //removes outputStream from hashtable
            outputStreams.remove(s);
            //closes stream
            try {
                s.close();
            } catch (IOException e){
                log("Failure to close " + s);
                e.printStackTrace();
            }
        }
    }

    public void sendDirectMessage(Socket s, String message){
        DataOutputStream out = (DataOutputStream)outputStreams.get(s);
        try{
            out.writeUTF(message);
        } catch (Exception e){}
    }

    /**
     * Sends a message to all clients
     */
    public void sendToAll(String message) {
        //synchronized to prevent errors caused by removeConnection being called concurrently
        synchronized (outputStreams) {
            //for each client
            for(Enumeration e = getOutputStreams(); e.hasMoreElements();) {
                //get the outputStream
                DataOutputStream out = (DataOutputStream)e.nextElement();
                //attempt to send message
                try {
                    out.writeUTF(message);
                } catch (IOException ie){
                    ie.printStackTrace();
                }
            }
        }
    }

    /**
     * gets an enumeration of all outputStreams, one for each connected client
     */
    public Enumeration getOutputStreams() {
        return outputStreams.elements();
    }

}
