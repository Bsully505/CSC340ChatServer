/******
 * ChatClient
 * Author: Christian Duncan
 * Updated by: Dylan Irwin, Jack Zemlanicky
 *
 * This code provides a basic GUI ChatClient.
 * It is a single frame made of 3 parts:
 *    A textbox for updated messages
 *    An input textbox for entering in messages to send
 *    A "send" button to send the current textbox material.
 ******/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ChatClient extends JFrame implements WindowListener{
    public static void main(String[] args) {
        // Create and start up the ChatClient Frame
        ChatClient frame = new ChatClient();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        //adds listener to the main frame so if the user closes the chat client
        //and a connection is established it sends a message to the server before closing
       
    }

    private JTextArea chatTextArea;
    private JTextArea sendTextArea;
    private Action nameAction;
    private Action roomNameAction;

    private String hostname = "127.0.0.1";  // Default is local host
    // REMEMBER TO CHANGE
    private int port = 1519;                // Default port is 1518
    private String userName = "<UNDEFINED>";
    private String roomName= "0";
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private String clientName="";
    private String clientMsg = "";
    private String switchRoom = "";

    /* Constructor: Sets up the initial look-and-feel */
    public ChatClient() {
        addWindowListener(this);
        JLabel label;  // Temporary variable for a label
        JButton button; // Temporary variable for a button

        // Set up the initial size and layout of the frame
        // For this we will keep it to a simple BoxLayout
        setLocation(100, 100);
        setPreferredSize(new Dimension(1000, 500));
        setTitle("CSC340 Chat Client");
        Container mainPane = getContentPane();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.setPreferredSize(new Dimension(1000, 500));

        // Set up the text area for receiving chat messages
        chatTextArea = new JTextArea(30, 80);
        chatTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        label = new JLabel("Chat Messages", JLabel.CENTER);
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        mainPane.add(label);
        mainPane.add(scrollPane);

        // Set up the text area for entering chat messages (to send)
        sendTextArea = new JTextArea(3, 80);
        sendTextArea.setEditable(true);
        scrollPane = new JScrollPane(sendTextArea);
        label = new JLabel("Message to Transmit", JLabel.CENTER);
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        mainPane.add(label);
        mainPane.add(scrollPane);
        // frame.addWindowListener(new java.awt.event.WindowAdapter() {
        //     @Override
        //     public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        //         //closeMessage(); 
        //         System.exit(0);
        //     }
        // });

        // Set up a button to "send" the chat message
        Action sendAction = new AbstractAction("Send") {
            public void actionPerformed(ActionEvent e) {
                // Send the message in the text area (if anything)
                // and clear the text area
                String message = sendTextArea.getText();
                if (message != null && message != "") {
                    sendMsg(message);
                    sendTextArea.setText("");  // Clear out the field
                }
                sendTextArea.requestFocus();  // Focus back on box
            }
        };
        sendAction.putValue(Action.SHORT_DESCRIPTION, "Push this to transmit your message to the server.");

        // ALT+ENTER will automatically trigger this button
        sendAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_ENTER);

        button = new JButton(sendAction);
        button.setAlignmentX(JButton.CENTER_ALIGNMENT);
        mainPane.add(button);

        // Set up Ctrl-Enter in JTextArea as a send option as well
        setupTextAreaSend(sendAction);

        // Set up a button to get a new user name (and transmit request to the server)
        nameAction = new AbstractAction("Change User Name") {
            public void actionPerformed(ActionEvent e) {
                // Get the new username and transmit to the server!
                changeUserName(userName);
            }
        };
        //calls method before the GUI appears so the user must enter a name before connecting to the server
        //this method is very similar to the changeUserName method with a small difference
        initialUserName(userName);
        nameAction.putValue(Action.SHORT_DESCRIPTION, "Push this to change your username.");
        button = new JButton(nameAction);
        button.setAlignmentX(JButton.CENTER_ALIGNMENT);
        mainPane.add(button);
        //set up a button to input a room name and send the name to the server
        roomNameAction = new AbstractAction("Change the room name") {
            public void actionPerformed(ActionEvent e) {
                // Get the new user name and transmit to the server!
                String newRoomName = roomName;
                boolean flag = true;
                while(flag && newRoomName != null){
                    newRoomName = JOptionPane.showInputDialog("Enter a room name. Current room: " + roomName);
                    if (newRoomName!=null&&followsTextProtocol(newRoomName)){
                        changeRoomName(newRoomName);
                        flag = false;
                    } else {
                        if (newRoomName != null) {
                            JOptionPane.showMessageDialog(null, "An invalid charcater was input, please only use AlphaNumerics.");
                        }
                    }
                }
            }
        };
        changeRoomName("0");
        roomNameAction.putValue(Action.SHORT_DESCRIPTION, "Push this to change the room you are in.");
        button = new JButton(roomNameAction);
        button.setAlignmentX(JButton.CENTER_ALIGNMENT);
        mainPane.add(button);


        // Setup the menubar
        setupMenuBar();
    }

    private void setupTextAreaSend(Action sendAction) {
        // Get InputMap and ActionMap for the sendTextArea
        InputMap inputMap = sendTextArea.getInputMap();
        ActionMap actionMap = sendTextArea.getActionMap();

        // Get the key used to send a message (for us, CTRL+ENTER)
        KeyStroke sendKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK);
        inputMap.put(sendKeyStroke, "SendText");

        // Add the send action for this key to the Text Area's ActionMap
        actionMap.put("SendText", sendAction);
    }

    private void setupMenuBar() {
        JMenuBar mbar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;
        Action menuAction;
        menu = new JMenu("Connection");

        // Menu item to change server IP address (or hostname really)
        menuAction = new AbstractAction("Change Server IP") {
            public void actionPerformed(ActionEvent e) {
                String newHostName = JOptionPane.showInputDialog("Please enter a server IP/Hostname.\nThis only takes effect after the next connection attempt.\nCurrent server address: " + hostname);
                if (newHostName != null && newHostName.length() > 0)
                    hostname = newHostName;
            }
        };
        menuAction.putValue(Action.SHORT_DESCRIPTION, "Change server IP address.");
        menuItem = new JMenuItem(menuAction);
        menu.add(menuItem);

        // Menu item to change the port to use
        menuAction = new AbstractAction("Change Server PORT") {
            public void actionPerformed(ActionEvent e) {
                String portName = JOptionPane.showInputDialog("Please enter a server PORT.\nThis only takes effect after the next connection attempt.\nCurrent port: " + port);
                if (portName != null && portName.length() > 0) {
                    try {
                        int p = Integer.parseInt(portName);
                        if (p < 0 || p > 65535) {
                            JOptionPane.showMessageDialog(null, "The port [" + portName + "] must be in the range 0 to 65535.", "Invalid Port Number", JOptionPane.ERROR_MESSAGE);
                        } else {
                            port = p;  // Valid.  Update the port
                        }
                    } catch (NumberFormatException ignore) {
                        JOptionPane.showMessageDialog(null, "The port [" + portName + "] must be an integer.", "Number Format Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        menuAction.putValue(Action.SHORT_DESCRIPTION, "Change server PORT.");
        menuItem = new JMenuItem(menuAction);
        menu.add(menuItem);

        // Menu item to create a connection
        menuAction = new AbstractAction("Connect to Server") {
            public void actionPerformed(ActionEvent e) {
                //Function Call establishConnection
                establishConnection();
                //sends initial JOIN and ENTER commands to the server when first connecting a client
                out.println("ENTER " + userName);
                out.println("JOIN " + roomName);
            }
        };
        menuAction.putValue(Action.SHORT_DESCRIPTION, "Connect to server.");
        menuItem = new JMenuItem(menuAction);
        menu.add(menuItem);

        mbar.add(menu);
        setJMenuBar(mbar);
    }

    //Method to connect to a server at a specific port
    public void establishConnection(){
        try {
            socket = new Socket(hostname, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("Yo, we're live");
            recieveServerMsg(in);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Changes the user name on the nameAction
    public void initialUserName(String newName) {
        newName = JOptionPane.showInputDialog("Please enter a user name. Current user name: " + userName);
        //if the input is alphanumeric, not blank, and is the right size, and the user does not initially press cancel or x, send it through
        if(newName!=null&&followsTextProtocol(newName)){
            userName=newName;
            nameAction.putValue(Action.NAME, "User Name: "+userName);
        }
        else{
            JOptionPane.showMessageDialog(null, "An invalid character was input, please only use AlphaNumerics.\nYou cannot cancel or close this box, either.");
            initialUserName(newName);
        }        
    }
    public void changeUserName(String newName) {
        newName = JOptionPane.showInputDialog("Please enter a user name. Current user name: " + userName);
        //check to see if user changes their mind and cancels the request
        if(newName==null)return;
        //if the input is both alphanumeric and not blank, send it through
        if(followsTextProtocol(newName)){
            userName=newName;
            nameAction.putValue(Action.NAME, "User Name: "+userName);
            if (out==null) {
                // When not connected to server
            } else {
                // When connected to server
                out.println("ENTER "+userName);
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "An invalid character was input, please only use AlphaNumerics.");
            changeUserName(newName);
        }        
    }
    //This method is only called if the name given by the user follows protocol
    public void changeRoomName(String newRoom) {
        roomName = newRoom;
        roomNameAction.putValue(Action.NAME, "Room name: " + roomName);
            if (out == null){
                //If we are not connected to the server yet, do not transmit anything
            } 
            else {
                out.println("JOIN " + roomName);
            }
    }

    // Message Sending
    public void sendMsg(String msg) {
        if (out == null) {
            // When not connected to server
        } else {
            // When connected to server, sends with the format TRANSMIT [username] message
            out.println("TRANSMIT "+ msg);
            postMessage("[" + userName + "]: " + msg);
        }
    }
    //Method to check if a given string contains only alphanumeric characters
    public static boolean isAlphaNumeric(String isItAllowed) {
        return isItAllowed != null && isItAllowed.matches("^[a-zA-Z0-9]*$");
    }
    public void recieveServerMsg(BufferedReader input) throws IOException {
        new Thread() {
          public void run() {
            boolean done = false;
            try {
                while(!done) {
                  String msg = input.readLine();
                  //postMessage(msg);
                  String firstSwitch = msg.split(" ")[1];
                  String secondSwitch = msg.split(" ")[0];
                  // Parsing the messages from server to look neater on GUI
                  switch(firstSwitch){
                    case "ENTER":
                      clientName = msg.split(" ")[2];
                      postMessage("<SERVER> " + clientName + " has entered the room");
                      break;
                    case "JOIN":
                      clientName = msg.split(" ")[2];
                      postMessage("<SERVER> " + clientName + " has entered the room");
                      break;
                    case "Entering":
                      clientName = msg.split(" ")[2];
                      switchRoom = msg.split(" ")[0];
                    default:
                  }
                  switch(secondSwitch) {
                    case "NEWMESSAGE":
                      clientName = msg.split(" ")[1];
                      clientMsg = msg.substring(msg.indexOf(" "), msg.length());
                      postMessage("[" + clientName + "]: " + clientMsg);
                      break;
                    case "EXITING":
                      clientName = msg.split(" ")[1];
                      postMessage("<SERVER> " + clientName + " has left the room");
                      break;

                    default:
                  }
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }.start();
      }
    //Method to check usernames and group names to see if they are up to protocol
    public static boolean followsTextProtocol(String isItAllowed){
        return !isItAllowed.equals("")&!isItAllowed.contains(" ")&&isItAllowed.length()<=16&&isAlphaNumeric(isItAllowed);
    }
    // Post a message on the main Chat Text Area (with a new line)
    public synchronized void postMessage(String message) {
        chatTextArea.append(message + "\n");
    }
    //sends the "EXIT" message to the server when the user closes out of the client
    public void closeMessage(){
        //if no connection is established, do nothing
        if(out==null)return;
        //if a connection is established, write to server
        else out.println("EXIT");
        
    }

    //you must go, your people need you
    //method to send "EXIT" protocol upon the client closing the chat completely
    //only sends to the server if it is actually connected to the server
    @Override
    public void windowClosing(WindowEvent e) {
        if(out==null)return;
        else out.println("EXIT");
    }
    //sorry guys, we don't need you
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}
