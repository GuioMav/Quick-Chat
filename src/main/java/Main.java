//Libraries
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // Creates an instance of the userManager class to handle user-related operations
        UserManager user = new UserManager();
        // Creates an instance of the Messages class to handle message-related operations
        Messages m = new Messages();

        // Starts an infinite loop for the application's main menu
        while (true) {

            // Checks if a user is currently not logged in
            if (!user.isLoggedIn()) {
                // Displays the first menu (login/register/exit) to the user
                String choice = JOptionPane.showInputDialog(
                        "📱 Welcome to Quick Chat\n" +
                                "1. Register\n" +
                                "2. Login\n" +
                                "3. Exit\n" +
                                "Choose option:"
                );

                // Uses a switch statement to handle the user's choice from the first menu
                switch (choice) {
                    case "1":
                        user.register(); // Calls the register method from the userManager
                        break;
                    case "2":
                        user.login();    // Calls the login method from the userManager
                        break;
                    case "3":
                        JOptionPane.showMessageDialog(null, "👋 Goodbye!"); // Displays a goodbye message
                        System.exit(0); // Exits the application
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "❌ Invalid choice. Try again."); // Handles invalid input
                }
            } else {
                // This block executes if a user is logged in, displaying the second menu

                // Prompts the user to enter a message limit
                int messageLimit = Integer.parseInt(JOptionPane.showInputDialog("How many messages would you like to send?"));
                int sentCount = 0; // Initializes a counter for sent messages

                // Starts an inner loop for the logged-in user's menu
                while (true) {
                    // Displays the second menu options to the logged-in user
                    String menu = JOptionPane.showInputDialog("Choose an option:" +
                            "\n1) Send Messages" +
                            "\n2) Show recently sent messages" +
                            "\n3) View your Profile" +
                            "\n4) Quit");

                    // Uses a switch statement to handle the logged-in user's choice
                    switch (menu) {
                        case "1":
                            // Checks if the message limit has been reached
                            if (sentCount >= messageLimit) {
                                JOptionPane.showMessageDialog(null, "Message limit reached.");
                                break; // Exits the current case
                            }

                            // Generates a unique message ID
                            String messageID = m.generateMessageID();
                            // Prompts for the recipient's cell number
                            String recipient = JOptionPane.showInputDialog("Enter Recipient Cell Number (e.g. 0123456789):");
                            // Validates the recipient's number
                            if (!m.checkRecipient(recipient)) {
                                JOptionPane.showMessageDialog(null, "Invalid Recipient Number. Must start with +27 and be followed by 9 digits.");
                                break; // Exits the current case if invalid
                            }

                            // Prompts for the message text
                            String messageText = JOptionPane.showInputDialog("Enter your message (max 250 characters):");
                            // Validates message length (overall max)
                            if (messageText.length() > 250) {
                                JOptionPane.showMessageDialog(null, "Message too long. Max 250 characters.");
                                break; // Exits the current case if too long
                            }
                            // Validates message length (specific internal requirement, seems redundant with above but kept as per original code)
                            if (messageText.length() > 50) {
                                JOptionPane.showMessageDialog(null, "Please enter a message of less than 50 characters.");
                                break; // Exits the current case if too long
                            } else {
                                JOptionPane.showMessageDialog(null, "Message sent."); // Confirmation message
                            }

                            // Increments the total messages sent count
                            m.setTotalMessages(m.returnTotalMessages() + 1);
                            // Creates a hash for the message
                            String hash = m.createMessageHash(messageID, m.returnTotalMessages(), messageText);
                            // Determines the action (send, store, disregard) for the message
                            String action = m.sentMessage();

                            // Handles the message action
                            switch (action) {
                                case "send":
                                case "store":
                                    // Stores the message details
                                    m.storeMessage(messageID, hash, recipient, messageText);
                                    sentCount++; // Increments the sent messages counter for the current session
                                    // Displays message details and status
                                    JOptionPane.showMessageDialog(null,
                                            "Message ID: " + messageID + "\n" +
                                                    "Message Hash: " + hash + "\n" +
                                                    "Recipient: " + recipient + "\n" +
                                                    "Message: " + messageText + "\n" +
                                                    "Status: Message " + (action.equals("send") ? "Sent" : "Stored") + " Successfully");
                                    break;
                                case "disregard":
                                    JOptionPane.showMessageDialog(null, "Message was disregarded."); // Informs user message was disregarded
                                    break;
                            }
                            break; // Exits the "Send Messages" case

                        case "2":
                            m.printMessages(); // Calls method to display recently sent messages
                            break;

                        case "3":
                            user.viewProfile(); // Calls method to view user profile
                            break;
                        case "4":
                            // Displays total messages sent and exits the inner loop, returning to the main menu logic
                            JOptionPane.showMessageDialog(null, "You sent " + m.returnTotalMessages() + " message(s). Goodbye!");
                            return; // Exits the main method, terminating the application

                        default:
                            JOptionPane.showMessageDialog(null, "Invalid option."); // Handles invalid input
                            break;
                    }
                }
            }
        }
    }
}