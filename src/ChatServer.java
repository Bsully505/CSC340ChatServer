//import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

    public  static ArrayList<ServerWorker> clientell = new ArrayList<>();
    public static int port;

    public  ChatServer(){
         this.port = 1518;
    }

    public ChatServer(String message) throws IOException {
        //ReadIn(message);
    }

    public void run(){
        try {
//            OutputStream output = client.getOutputStream();
//            InputStream input = client.getInputStream();
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){

                System.out.println("accepting Socket...");
                Socket ClientSocket = serverSocket.accept();
                System.out.println("Socket accepted");
                ServerWorker s = new ServerWorker(ClientSocket);
                clientell.add(s);


                s.start();



            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer cS = new ChatServer();
        cS.run();




    }

    public static ArrayList<ServerWorker> getClientell(){
        return clientell;
    }
    public static void SetClientell(ArrayList<ServerWorker> newest){
        clientell = newest;
    }


}
