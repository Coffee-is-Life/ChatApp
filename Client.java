// Clay Patterson and Daniel Bothwell
// Java chat room client class
// initial client copied from online - will need to alter and add functionality
// it actually doesnt work so need to revamp entirely.
// Server seemingly works
// initial import
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.Socket;

public class Client{
	// connect to server by providing port/socket #

	BufferedReader incomingMessages;
	PrintWriter outgoingMessages;
	JFrame frame = new JFrame("Chatter");
	JTextField textField = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);

	public Client(){
		textField.setEditable(false);
		messageArea.setEditable(false);

		frame.getContentPane().add(textField, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.pack();

		textField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				outgoingMessages.println(textField.getText());
				//use this to change between different modes of typing
				textField.setText("");
			}
		});
	}

	private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Prompt for and return the desired screen name.
     */
    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 2120);
        incomingMessages = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        outgoingMessages = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
        	System.out.println("Entered while loop");
            String line = incomingMessages.readLine();
            if (line.startsWith("SUBMITNAME")) {
                outgoingMessages.println(getName());
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            }
        }
    }

    /**
     * Runs the client as an application with a closeable frame.
     */
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }



}