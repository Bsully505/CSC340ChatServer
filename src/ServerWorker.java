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
        while((line = in.readLine()) != null){

            String NewLiner = line;
            if(line.contains(" ")){

                 NewLiner = line.split(" ")[0].toUpperCase();
            }
            //transmit hello my name is bob -> [transmit , hello my name is bob] [0] = transmit while [1] = hello my name is bob
            switch(NewLiner){
                case "ENTER":
                    //create a synchronis function for these cases
                    Username = line.split(" ")[1];// i might need to double check this for if someone has a debug line which includes 2 colens
                    printer("ACK ENTER "+ Username + "\n",this.RoomID);
                    break;
                case "TRANSMIT":
                    printer("NEWMESSAGE "+ Username +" "+line.split(" ",2)[1] + "\n",this.RoomID);
                    break;
                case "JOIN":
                    //i might need to create a userinput case
                    printer("EXITING "+Username,RoomID);
                    RoomID = line.split(" ", 2)[1];
                    printBackToSender("ACK JOIN "+ RoomID);
                    printer("Entering "+Username,RoomID);



                    //i need to create a join room
                    break;
                case "EXIT": // this is not working
                    printer("EXITING "+Username+"\n",this.RoomID);
                    System.out.println(Username + " HAS LEFT");
                    this.clientell = ChatServer.getClientell();
                    System.out.println(client.getOutputStream());
                    System.out.println("this reaches here");
                    System.out.print(clientell.contains(this));
                    System.out.println(clientell.remove(this));
                    ChatServer.SetClientell(clientell);
                    client.close();

                    break;

                case "PRINT":
                    for(ServerWorker i : clientell){
                        System.out.println(clientell.indexOf(i));
                    }
                    break;
                default:
                    output = client.getOutputStream();
                    output.write("Not a valid protocol \n".getBytes() );

            }

            String msg = client.getInetAddress()+ ": You Typed: " + line+ "\n";
            System.out.println(msg);//this is the thing that is printing onto my terminal

        }
        System.out.println("someone has left");
        client.close();


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
