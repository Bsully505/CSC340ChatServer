import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
// variables
    public  static ArrayList<ServerWorker> clientell = new ArrayList<>();
    public static int port;

    // setting the port to 1518
    public  ChatServer(){
        this.port = 1518;
    }



    public void run(){
        try {
          // creates a ServerSocket
            ServerSocket serverSocket = new ServerSocket(port);
            // continues to keep accepting sockets
            while(true){

                System.out.println("accepting Socket...");
                Socket ClientSocket = serverSocket.accept();
                System.out.println("Socket accepted");
                // makes a new serverworker once a socket been accepted
                ServerWorker s = new ServerWorker(ClientSocket);
               // add the server worker to the arraylist
                clientell.add(s);
                // starts the serverwork(thread)
                s.start();

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
// main method to run the server
    public static void main(String[] args) {
        ChatServer cS = new ChatServer();
        cS.run();




    }

    // returns the clientell
    public static ArrayList<ServerWorker> getClientell(){
        return clientell;
    }
    //sets the clientell
    public static void SetClientell(ArrayList<ServerWorker> newest){
        clientell = newest;
    }


}
