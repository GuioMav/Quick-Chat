//Libraries
import java.util.ArrayList; // Imports ArrayList for dynamic arrays
import java.util.List;      // Imports List interface
import javax.swing.*;       // Imports Swing for GUI components
import java.io.FileWriter;  // Imports FileWriter for writing files
import java.io.IOException; // Imports IOException for I/O errors
import java.io.File;        // Imports File class for file operations
import java.util.*;         // Imports all utility classes (e.g., Random, Scanner)
import com.google.gson.Gson;          // Imports Gson for JSON serialization/deserialization
import com.google.gson.GsonBuilder;   // Imports GsonBuilder for Gson configuration

public class Messages {

    // List to hold all message entries
    private List<MessageEntry> messages = new ArrayList<>();
    // Counter for the total number of messages
    private int totalMessages = 0;

    /**
     * Sets the total number of messages.
     * @param tm The new total number of messages.
     */
    public void setTotalMessages(int tm){
        this.totalMessages = tm; // Sets the total messages to the provided value
    }

    /**
     * Constructor for the Messages class.
     * Initializes message list and count, then loads saved messages.
     */
    public Messages() {
        this.messages = new ArrayList<>(); // Initializes the messages list
        this.totalMessages = 0;           // Initializes the total messages counter
        loadMessagesFromJson();           // Loads messages from a JSON file
    }

    /**
     * Generates a random 10-digit message ID.
     * @return A 10-digit message ID as a String.
     */
    public  String generateMessageID() {
        Random rand = new Random();     // Creates a Random object
        StringBuilder sb = new StringBuilder(); // Creates a StringBuilder
        while (sb.length() < 10) {      // Loops until 10 digits are generated
            sb.append(rand.nextInt(10)); // Appends a random digit
        }
        return sb.toString();           // Returns the ID
    }

    /**
     * Checks if a message ID is less than 10 characters long.
     * @param messageID The message ID to check.
     * @return True if the message ID's length is less than 10, false otherwise.
     */
    public boolean checkMessageID(String messageID){
        return messageID.length() < 10; // Checks if ID length is less than 10
    }

    /**
     * Validates a recipient's cell number.
     * Must be non-null and in the format "+27" followed by 9 digits.
     * @param recipient The recipient's cell number.
     * @return True if the recipient number is valid, false otherwise.
     */
    public boolean checkRecipient(String recipient) {
        // Checks if recipient is not null and matches the regex pattern for +27 followed by 9 digits
        return recipient != null && recipient.matches("\\+27\\d{9}");
    }

    /**
     * Creates a unique hash for a message.
     * Combines parts of the ID, message number, and message text.
     * @param id The message ID.
     * @param messageNum The sequential message number.
     * @param message The actual message text.
     * @return The message hash as a String.
     */
    public String createMessageHash(String id, int messageNum, String message) {
        String[] words = message.trim().split("\\s+"); // Splits message into words
        String firstWord = words.length > 0 ? words[0] : ""; // Gets the first word
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord; // Gets the last word
        // Constructs the hash and converts to uppercase
        return (id.substring(0, 2) + ":" + messageNum + ":" + firstWord + lastWord).toUpperCase();
    }

    /**
     * Presents options to the user for message action (Send, Store, Disregard).
     * @return The user's choice as a String.
     */
    public String sentMessage() {
        Object[] options = {"Send", "Store", "Disregard"}; // Defines dialog options
        // Shows a dialog and gets the user's choice
        int choice = JOptionPane.showOptionDialog(null, "What would you like to do with the message?",
                "Message Action", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        // Returns a string based on the choice
        switch (choice) {
            case 0: return "send";      // User chose "Send"
            case 1: return "store";     // User chose "Store"
            default: return "disregard"; // User chose "Disregard" or closed
        }
    }

    /**
     * Displays all stored messages in a dialog box.
     * Informs the user if no messages are stored.
     */
    public void printMessages() {
        if (messages.isEmpty()) { // Checks if message list is empty
            JOptionPane.showMessageDialog(null, "No messages have been sent yet.");
            return; // Exits if no messages
        }

        StringBuilder sb = new StringBuilder(); // Creates a StringBuilder
        for (int i = 0; i < messages.size(); i++) { // Loops through each message
            MessageEntry msg = messages.get(i); // Gets the current message
            // Appends message details to the StringBuilder
            sb.append("Message #").append(i + 1).append(":\n")
                    .append("ID: ").append(msg.MessageID).append("\n")
                    .append("Hash: ").append(msg.MessageHash).append("\n")
                    .append("Recipient: ").append(msg.Recipient).append("\n")
                    .append("Message: ").append(msg.Message).append("\n\n");
        }

        // Displays formatted messages in a dialog
        JOptionPane.showMessageDialog(null, sb.toString(), "Recent Messages", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Returns the current total count of messages.
     * @return The total number of messages.
     */
    public int returnTotalMessages() {
        return totalMessages;
    }

    /**
     * Stores a new message, increments count, and saves all messages to JSON.
     * @param id The message ID.
     * @param hash The message hash.
     * @param recipient The message recipient.
     * @param text The message text.
     */
    public void storeMessage(String id, String hash, String recipient, String text) {
        MessageEntry message = new MessageEntry(id, hash, recipient, text); // Creates a new MessageEntry
        messages.add(message); // Adds message to list
        totalMessages++;       // Increments total messages
        writeMessagesToJson(); // Writes messages to JSON file
    }

    /**
     * Writes the current list of messages to "messages.json" in a pretty format.
     */
    public void writeMessagesToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Creates Gson with pretty printing
        try (FileWriter writer = new FileWriter("messages.json")) { // Opens FileWriter
            gson.toJson(messages, writer); // Writes messages to JSON
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to JSON file: " + e.getMessage()); // Shows error
        }
    }

    /**
     * Loads messages from "messages.json" into the messages list.
     * Handles file not found or reading errors.
     */
    public void loadMessagesFromJson() {
        Gson gson = new Gson(); // Creates a Gson object
        try (Scanner scanner = new Scanner(new File("messages.json"))) { // Opens Scanner to read file
            StringBuilder json = new StringBuilder(); // Creates StringBuilder for JSON string
            while (scanner.hasNextLine()) {         // Reads each line
                json.append(scanner.nextLine());    // Appends line to StringBuilder
            }

            // Deserializes JSON into an array of MessageEntry
            MessageEntry[] loadedMessages = gson.fromJson(json.toString(), MessageEntry[].class);
            if (loadedMessages != null) { // If messages were loaded
                messages.addAll(Arrays.asList(loadedMessages)); // Adds loaded messages to list
                totalMessages = messages.size(); // Updates total messages count
            }

        } catch (IOException e) {
            System.out.println("No previous messages found or error reading file."); // Prints message
        }
    }
}