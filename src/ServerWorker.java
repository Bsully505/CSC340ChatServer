/******
* ServerWorker
* Author: Henok Ketsela, Bryan Sullivan, and Harrison Dominique
*
* This code provides a thread for the server to handle the protocol.
******/


import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerWorker extends Thread {

    // variables
    public  ArrayList<ServerWorker> clientell = new ArrayList<>();
    private final Socket client;
    private String Username = "";
    private static OutputStream output;
    private String RoomID = "0";

    // constructor
    // takes in a socket to establish a connection
    public ServerWorker(Socket client) throws IOException {
        this.client = client;
        output = client.getOutputStream();

    }

    @Override
    // thread will run the handleClient method
    public void run(){
        try{
            HandleClient(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param client
     * @throws IOException
     */
    // this method will read in the users input to determine what they want to do with the chat server
    public void HandleClient(Socket client) throws IOException {//this is for the input of each client
        output = client.getOutputStream();
        InputStream input = client.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String line;
        // while loop checking if the client is not closed and the user input is not null
        while(!client.isClosed() && (line = in.readLine()) != null){

            String NewLiner = line.toUpperCase();
            if(line.contains(" ")){

                NewLiner = line.split(" ")[0].toUpperCase();
            }

            //  Switch used in order to read in the user input
            // based on what they enter a protocol will happen which we broke down into methods
            switch(NewLiner){
                case "ENTER":
                    this.EnterName(line);
                    break;
                case "TRANSMIT":
                    this.Transmit(line);
                    break;
                case "JOIN":
                    this.JoinRoom(line);
                    break;
                case "EXIT":
                    this.Exit(line);
                    break;
                case "PRINT":
                    this.Print(line);
                    break;
                    // if the user does not type in a certain protocol we will reply not a valid protocol
                default:
                    output = client.getOutputStream();
                    output.write("Not a valid protocol \n".getBytes() );

            }

            // prints out what the user said to the system
            String msg = this.Username + " Typed: " + line+ "\n";
            System.out.println(msg);//this is the thing that is printing onto my terminal

        }
        // prints out who is left the server and closes the socket connection
        System.out.println(Username + " has left");
        client.close();


    }
    // synchronized method for when a user want to join a room
    public synchronized void JoinRoom(String line) throws IOException {
        printer("EXITING " + Username + "\n", RoomID);
        RoomID = line.split(" ", 2)[1];
        printBackToSender("ACK JOIN " + RoomID + "\n");
        printer("Entering " + Username + "\n", RoomID);
    }

    // synchronized method for when a user want to enter their name
    public synchronized void EnterName(String line) throws IOException {
        line = line;
        // reads in their name and saves it as username
        try {
            Username = line.split(" ")[1];// i might need to double check this for if someone has a debug line which includes 2 colens
            printer("ACK ENTER "+ Username + "\n",this.RoomID);
        }
        catch(ArrayIndexOutOfBoundsException e) {
            output.write("Not a valid protocol \n".getBytes() );
        }
    }
    // synchronized method for when a user wants to transmit a message
    public synchronized void Transmit(String line) throws IOException {
        line = line;
        // prints out the users message
        try {
            printer("NEWMESSAGE "+ Username +" "+line.split(" ",2)[1] + "\n",this.RoomID);
        }
        catch(ArrayIndexOutOfBoundsException e) {
            output.write("Not a valid protocol \n".getBytes() );
        }
    }
    // synchronized method for a user wanting to exit the chat server
    public synchronized void Exit(String line) throws IOException {
        line = line;
        // prints out what room their leaving and their name
        printer("EXITING "+Username+"\n",this.RoomID);
        System.out.println(Username + " HAS LEFT");
        // getting the current socket and closing the connection
        this.clientell = ChatServer.getClientell();
        clientell.remove(this);
        ChatServer.SetClientell(clientell);
        client.close();
    }
    // synchronized method
    public synchronized void Print(String line) throws IOException {
        line = line;

        for(ServerWorker i : clientell){
            System.out.print(clientell.indexOf(i) +": "+ clientell.get(clientell.indexOf(i)).Username+ " in room "+clientell.get(clientell.indexOf(i)).RoomID);
        }
    }


    // synchronized method used to write back to the user
    public synchronized void printBackToSender(String zed) throws IOException {
        output = client.getOutputStream();
        output.write(zed.getBytes());
    }

    // synchronized method used to handle all the printing of socket to the client
    public synchronized void printer(String zed,String RoomKey) throws IOException {// nobody can connect at this time
        this.clientell = ChatServer.getClientell();
        // for loop goes through the arraylist of serverworkers
        // writes out the message
        for(ServerWorker s: clientell){
            // checks if the server worker room id is equal to the Room id of where they want to output a message
            if(s.RoomID.equals(RoomKey)) {
                Socket temp = s.getSocket();
                OutputStream out = temp.getOutputStream();
                // prints out the message
                out.write(zed.getBytes());
            }
        }

    }

    // get method to return the socket
    public Socket getSocket(){
        return client;
    }

}
