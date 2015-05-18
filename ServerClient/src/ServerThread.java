import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Reuben on 5/16/2015.
 * A class to one connection between a client and the server
 */
public class ServerThread extends Thread {

    //the server using the thread
    Server server;

    //the client socket the connection is being made with
    Socket socket;


    //constructor
    public ServerThread (Server server, Socket socket) {
        this.server = server;
        this.socket = socket;

        //starts the thread
        start();
    }

    //main input and output loop
    public void run() {
        try {
            //creates a data stream for the communication
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //runs until connection is closed
            while(true) {
                //reads a message from the client
                String message = in.readUTF();
                //distributes message to all other clients, and logs it
                server.log("Sending message " + message);
                server.sendToAll(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //removes a connection from the server if it is closed
            server.removeConnection(socket);
        }
    }
}
