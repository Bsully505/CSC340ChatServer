# CSC340ChatServer
Authors: Dylan Irwin, Jack Zemlanicky, Henok Ketsela, Bryan Sullivan, and Harrison Dominique

Instructions on how to compile and run both the server and client code.
1) Open file and compile 
2) run ChatServer 
3) run multiple ChatClients

A breakdown of the work that each member of the team did, what parts were contributed by that
individual.

1) Bryan, Henok - created and worked on ChatServer and ServerWorker. Can find more details in the commenting of the code to see what tasks were completed.

2) Jack Zemlanicky - worked on ChatClient.java- added protocol and functionality for changing usernames, room names, window close functionality, and commented the code to further describe what each method does. Added dialog boxes to handle user errors when choosing their name and room name. Also assisted Dylan with the initial establishment of connection for the client to the server.

3) Dylan: Worked on ChatClient.java- created establishConnection and recieveServerMsg methods. The establishConnection method does exactly what it's name suggests, begins the connection to the server (ChatSever). The recieveServerMsg method takes the server's output and relays it onto the clients GUI in a neat, readable, fashion. Also assisted Jack with the username and room name changing functionality.

4) Harrison - worked mostly on ChatServer and also synchronized functions in both ChatServer and ChatClient

A description of how the tasks were divided out among the members.

1) Bryan, Henok - wanted to work together on the server
2) Jack, Dylan - worked together on the client-side. 
3) Harrison - was remote and couldn't meet in person so contributed to certain tasks that needed to be done. 
