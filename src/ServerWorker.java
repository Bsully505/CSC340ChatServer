/**
 * the first thread always communicates with the latest one
 * testing
 * efficiancy mulpile
 * he recommend one the name two the socket and input stream and the output stream and the room num
 */


import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerWorker extends Thread {

    public  ArrayList<ServerWorker> clientell = new ArrayList<>();
    private final Socket client;
    private String Username = "";
    private static OutputStream output;
    private String RoomID = "0";


    public ServerWorker(Socket client) throws IOException {
        this.client = client;
        output = client.getOutputStream();

    }

    @Override
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
    public void HandleClient(Socket client) throws IOException {//this is for the input of each client
         output = client.getOutputStream();
        InputStream input = client.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String line;
        while(!client.isClosed() && (line = in.readLine()) != null){

            String NewLiner = line;
            if(line.contains(" ")){

                 NewLiner = line.split(" ")[0].toUpperCase();
            }
            //transmit hello my name is bob -> [transmit , hello my name is bob] [0] = transmit while [1] = hello my name is bob
            switch(NewLiner){
                case "ENTER":
                    this.EnterName(line);
                    break;
                case "TRANSMIT":
                    this.Transmit(line);
                    break;
                case "JOIN":
                    this.JoinRoom(line);
                    //i need to create a join room
                    break;
                case "EXIT": // this is not working
                    this.Exit(line);
                    break;
                case "PRINT":
                    this.Print(line);
                    break;
                default:
                    output = client.getOutputStream();
                    output.write("Not a valid protocol \n".getBytes() );

            }

            String msg = this.Username + " Typed: " + line+ "\n";
            System.out.println(msg);//this is the thing that is printing onto my terminal

        }
        System.out.println("someone has left");
        client.close();


    }
    public synchronized void JoinRoom(String line) throws IOException {
        printer("EXITING " + Username + "\n", RoomID);
        RoomID = line.split(" ", 2)[1];
        printBackToSender("ACK JOIN \n" + RoomID);
        printer("Entering " + Username + "\n", RoomID);
    }

    public synchronized void EnterName(String line) throws IOException {
        line = line;
        Username = line.split(" ")[1];// i might need to double check this for if someone has a debug line which includes 2 colens
        printer("ACK ENTER "+ Username + "\n",this.RoomID);

    }
    public synchronized void Transmit(String line) throws IOException {
        line = line;
        printer("NEWMESSAGE "+ Username +" "+line.split(" ",2)[1] + "\n",this.RoomID);
    }

    public synchronized void Exit(String line) throws IOException {
        line = line;
        printer("EXITING "+Username+"\n",this.RoomID);
        System.out.println(Username + " HAS LEFT");
        this.clientell = ChatServer.getClientell();
        clientell.remove(this);
        ChatServer.SetClientell(clientell);
        client.close();
    }

    public synchronized void Print(String line) throws IOException {
        line = line;
        for(ServerWorker i : clientell){
            System.out.print(clientell.indexOf(i) +": "+ clientell.get(clientell.indexOf(i)).Username+ " in room "+clientell.get(clientell.indexOf(i)).RoomID);
        }
    }


//find out who i have to send it too
    //array list
    public synchronized void printBackToSender(String zed) throws IOException {
        output = client.getOutputStream();
        output.write(zed.getBytes());
    }
    public synchronized void printer(String zed,String RoomKey) throws IOException {// nobody can connect at this time
        this.clientell = ChatServer.getClientell();
        for(ServerWorker s: clientell){
            if(s.RoomID == RoomKey) {
                Socket temp = s.getSocket();
                OutputStream out = temp.getOutputStream();
                out.write(zed.getBytes());
            }
        }

    }

    public Socket getSocket(){
        return client;
    }

}


