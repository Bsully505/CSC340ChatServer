/**
 * the first thread always communicates with the latest one
 * testing
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
     * what i am going to have to add is the username for the client
     * @param client
     * @throws IOException
     */
    public void HandleClient(Socket client) throws IOException {//this is for the input of each client
         output = client.getOutputStream();
        InputStream input = client.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String line;
        while((line = in.readLine()) != null){
            //input could be Transmit Message:, EnterName:
            //Exit or join room, ack join room, ACK EnterName,
            //
            // NewMessage Name Message
            //enter name
            //this is where the
            String NewLine = line;
            String NewLiner = line;
            if(line.contains(":")){
                 NewLine = line.split(":")[0].toUpperCase();
                 NewLiner = line.split(" ")[0].toUpperCase();
            }
            switch(NewLiner){
                case "ENTER":
                    //create a synchronis function for these cases
                    Username = line.split(" ")[1];// i might need to double check this for if someone has a debug line which includes 2 colens
                    printer("ACK ENTER "+ Username);
                    break;
                case "TRANSMIT":
                    printer("NEWMESSAGE "+ Username + line.split(" ")[1]);
                    break;
                case "JOIN":
                    //i need to create a join room
                    break;
                case "EXIT":
                    printer("EXITING "+Username+":");
                    System.out.println(Username + " HAS LEFT");
                    client.close();
                    break;
                default:
                    output.write("Not a valid protocol \n".getBytes() );

            }
//            switch(NewLine){
//                case "ENTER NAME":
//                    //create a synchronis function for these cases
//                    Username = line.split(":")[1];// i might need to double check this for if someone has a debug line which includes 2 colens
//                    printer("ACK ENTER NAME:"+ Username);
//                    break;
//                case "TRANSMIT MESSAGE":
//                    printer("NEWMESSAGE "+ Username + line.split(":")[1]);
//                    break;
//                case "JOIN ROOM":
//                    //i need to create a join room
//                    break;
//                case "EXIT":
//                    printer("EXITING "+Username+":");
//                    System.out.println(Username + " HAS LEFT");
//                    client.close();
//                    break;
//                default:
//                    output.write("Not a valid protocol \n".getBytes() );
//
//            }




            String msg = client.getInetAddress()+ ": You Typed: " + line+ "\n";
            System.out.println(msg);//this is the thing that is printing onto my terminal

            //printer(msg);
            //output.write(msg.getBytes());
        }
        System.out.println("someone has left");
        client.close();


    }


    public static void ChangeUserName(){

    }

    public synchronized void printer(String zed) throws IOException {
        this.clientell = ChatServer.getClientell();
        for(ServerWorker s: clientell){
            Socket temp = s.getSocket();
            OutputStream out = temp.getOutputStream();
            out.write(zed.getBytes());
        }

    }

    public Socket getSocket(){
        return client;
    }
}
