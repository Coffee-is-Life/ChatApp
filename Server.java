// Clay Patterson and Daniel Bothwell
// Java chat room server class

// initial import
// use bytearrayoutputstream to send files
import java.util.ArrayList;
import java.io.*;
import java.net.*;


public class Server{
	// need to create server object to be connected to by clients
	// use port 2120
	
	// keep track of all users connected to the server, along with their respective writers
	private static ArrayList<String> users;
	private static ArrayList<PrintWriter> userWriters;

	// server socket to be initialized later
	private ServerSocket chatroom;

	// constructer for server object to not have static references to each things
	private Server(){
		// initializes arrays and sets up chatroom server socket
		setup(2120);

		try{
			runServer(chatroom);			
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			shutdown();
		}

	}
	
	public static void main(String[] args){
		try{
			System.out.println(InetAddress.getLocalHost());
		} catch (Exception e){
			e.printStackTrace();
		}
		Server myChatRoom = new Server();
	}

	private void setup(int portNumber){
		// initializes user list and user writer list
		users = new ArrayList<String>();
		userWriters = new ArrayList<PrintWriter>();

		// attempt to initialize server on selected port
		try{
			chatroom = new ServerSocket(portNumber);			
		} catch (Exception e){	
			e.printStackTrace();
		}
	}
	// close socket
	private void shutdown(){
		try{
			chatroom.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	// loop to run chat server
	private void runServer(ServerSocket socket)throws IOException{
		// infinite loop to run until server is manually terminated
		while (true){
			// initialize new client and start its thread
			new NewClient(socket.accept()).start();
		}
	}
	// getter and setter/adder variants for when NewClient was a separate public class
	public ArrayList<String> getUsers(){
		return users;
	}
	public void addUser(String name){
		users.add(name);
	}
	public void addWriter(PrintWriter writer){
		userWriters.add(writer);
	}

	// private class for new client to enable each client thread to keep track of all
	// other client's writers
	private class NewClient extends Thread{
	private String name;
	private Socket socket;
	private BufferedReader incomingMessages;
	private PrintWriter outgoingMessages;

	// constructor for newClient, taking in the chatroom object
	public NewClient(Socket socket){
		// attach client to socket and chatroom
		this.socket = socket;
		System.out.println("New client called");
		run();
	}

	public void run(){
		try{
			// create buffered reader and writer for client
			incomingMessages = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outgoingMessages = new PrintWriter(socket.getOutputStream(), true);
			
			while(true){
				System.out.println("Enter Username");
				outgoingMessages.println("Enter username");
				name = incomingMessages.readLine();
				System.out.println(name);
				//
				if (name == null){
					outgoingMessages.println("\"\" is an invalid name.");
					return;
				}
				// synchronized (names) {
    //                 if (!names.contains(name)) {
    //                     names.add(name);
    //                     break;
    //                 }
    //             }
				if (!getUsers().contains(name)){
					// new user is entering chatroom, add them to the list and then break out of loop
					addUser(name);
					break;
				}
			}

			// welcome client to the chatroom
			// change this message
			outgoingMessages.println("Welcome to the big dick club, " + name);
			addWriter(outgoingMessages);

			while (true){
				String message = incomingMessages.readLine();
				if (message == null){
					// loop if no message is received
					return;
				}
				// write message to other users
				// needs to be in same class as server so we have the list of writers and can write to all of them
				// add functionality to distinguish between types of messages
				// default to global
				// maybe partially fill out the outgoing message as "/global" or something like that
				for (PrintWriter global : userWriters){
					global.println("/GLOBAL/" + name + ": " + message);
				}
			}


		} catch (IOException e){
			System.out.println(e);
		} finally {
			// client is leaving so clear saved data from arraylists
			if (name != null){
				users.remove(name);
			}
			if (outgoingMessages != null){
				userWriters.remove(outgoingMessages);
			}
			// terminate connection
			try{
				socket.close();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}






}
}