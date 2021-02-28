/**
 * the first thread always communicates with the latest one
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


    public void HandleClient(Socket client) throws IOException {//this is for the input of each client
         output = client.getOutputStream();
        InputStream input = client.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String line;
        while((line = in.readLine()) != null){
            if("quit".equalsIgnoreCase(line)){
                break;
            }
            //String subStr = line.substring(6);
            //new ChatServer(subStr);


            String msg = client.getInetAddress()+ ": You Typed: " + line+ "\n";
            System.out.println(msg);//this is the thing that is printing onto my terminal

            printer(msg);
            //output.write(msg.getBytes());
        }
        System.out.println("someone has left");
        client.close();


    }
//    public static void PrintSomething(String zed) throws IOException {
//        OutputStream output = client.getOutputStream();
//        output.write(zed.getBytes());
//    }

    public static void ChangeUserName(){

    }

    public  void printer(String zed) throws IOException {
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
