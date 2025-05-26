//Libraries
import com.google.gson.Gson; // Imports the Gson library for JSON serialization/deserialization
import com.google.gson.reflect.TypeToken; // Imports TypeToken for handling generic types with Gson

import javax.swing.*; // Imports Swing for GUI components
import java.io.*;     // Imports classes for input/output operations (File, Reader, Writer)
import java.lang.reflect.Type; // Imports Type for reflection operations
import java.util.HashMap; // Imports HashMap for storing users
import java.util.Map;     // Imports Map interface

public class UserManager {

    // A Map to store User objects, with usernames as keys
    private Map<String, User> users = new HashMap<>();
    // The file path where user data will be saved/loaded (JSON format)
    private final String filePath = "users.json";
    // An instance of Gson for converting Java objects to/from JSON
    private final Gson gson = new Gson();
    // An instance of the Validation class to perform input validation
    private final Validation validator = new Validation();

    // Variable to represent the currently logged-in user
    private User loggedInUser = null;

    //Constructor, each time a constructor for this class is created, it will load the currently registered users for login purposes
    public UserManager() {
        loadUsers(); // Calls the method to load users from the JSON file
    }

    /**
     * Handles the user registration process.
     * Prompts the user for details, validates them, and if successful,
     * creates a new User object and saves it.
     */
    public void register() {
        JOptionPane.showMessageDialog(null, "--- REGISTER ---"); // Displays a registration header

        // Prompts the user for registration details
        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");
        String name = JOptionPane.showInputDialog("Enter name:");
        String surname = JOptionPane.showInputDialog("Enter surname:");
        String cellphone = JOptionPane.showInputDialog("Enter cellphone (+27XXXXXXXXX):");

        // Validates the entered details using the Validation class
        String validationResult = validator.registerUser(username, password, cellphone);

        // Checks if the validation failed (i.e., the result message does not contain "User registered successfully")
        if (!validationResult.contains("User registered successfully")) {
            JOptionPane.showMessageDialog(null, "❌ Registration failed:\n" + validationResult); // Displays validation errors
            return; // Exits the method if validation fails
        }

        // Checks if the username already exists in the system
        if (users.containsKey(username)) {
            JOptionPane.showMessageDialog(null, "❌ Username already exists."); // Informs user if username is taken
            return; // Exits the method
        }

        // Creates a new User object and sets its properties
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setName(name);
        newUser.setSurname(surname);
        newUser.setCellphoneNumber(cellphone);
        users.put(username, newUser); // Adds the new user to the map
        saveUsers(); // Saves the updated user data to the JSON file

        JOptionPane.showMessageDialog(null, "✅ Registration successful!"); // Confirms successful registration
    }

    /**
     * Handles the user login process.
     * Allows a user up to 3 attempts to log in with correct credentials.
     */
    public void login() {
        JOptionPane.showMessageDialog(null, "--- LOGIN ---"); // Displays a login header

        int attempts = 3; // Initializes login attempts
        // Loops while attempts are remaining and no user is logged in
        while (attempts > 0 && loggedInUser == null) {
            String username = JOptionPane.showInputDialog("Enter username:"); // Prompts for username
            String password = JOptionPane.showInputDialog("Enter password:"); // Prompts for password

            // Attempts to log in the user using the Validation class
            boolean success = validator.loginUser(username, password, users);
            if (success) {
                loggedInUser = users.get(username); // Sets the loggedInUser if login is successful
                JOptionPane.showMessageDialog(null, "✅ " + validator.returnLoginStatus()); // Displays success message
                break; // Exits the loop on successful login
            } else {
                attempts--; // Decrements attempts on failure
                JOptionPane.showMessageDialog(null, "❌ Incorrect credentials. Attempts left: " + attempts); // Informs about remaining attempts
            }
        }

        // If after all attempts, no user is logged in
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(null, "❌ Login failed. Returning to main menu."); // Informs about login failure
        }
    }

    /**
     * Handles the user logout process.
     * If a user is logged in, it logs them out and clears the loggedInUser.
     */
    public void logout() {
        if (loggedInUser != null) { // Checks if a user is currently logged in
            JOptionPane.showMessageDialog(null, "👋 " + loggedInUser.getName() + " logged out."); // Displays logout message
            loggedInUser = null; // Clears the logged-in user
        } else {
            JOptionPane.showMessageDialog(null, "⚠️ You are not logged in."); // Informs if no user is logged in
        }
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return loggedInUser != null; // Returns true if loggedInUser is not null
    }

    /**
     * Returns the currently logged-in User object.
     * @return The User object of the logged-in user, or null if no user is logged in.
     */
    public User getLoggedInUser() {
        return loggedInUser; // Returns the current logged-in user
    }

    /**
     * Displays the profile information of the currently logged-in user.
     */
    public void viewProfile() {
        if (loggedInUser != null) { // Checks if a user is logged in
            // Constructs the profile string with user's details
            String profile = "\n👤 --- User Profile ---\n" +
                    "Name: " + loggedInUser.getName() + "\n" +
                    "Surname: " + loggedInUser.getSurname() + "\n" +
                    "Username: " + loggedInUser.getUsername() + "\n" +
                    "Cellphone: " + loggedInUser.getCellphoneNumber();
            JOptionPane.showMessageDialog(null, profile); // Displays the profile
        } else {
            JOptionPane.showMessageDialog(null, "⚠️ You are not logged in."); // Informs if no user is logged in
        }
    }


    // Methods to Manage User Data Storage

    /**
     * Saves the current map of users to a JSON file (`users.json`).
     * This method is protected, meaning it can be accessed within the package
     * or by subclasses.
     */
    protected void saveUsers() {
        try (Writer writer = new FileWriter(filePath)) { // Opens a FileWriter to the specified file path
            gson.toJson(users, writer); // Serializes the 'users' map to JSON and writes it to the file
        } catch (IOException e) {
            // Displays an error message if there's an issue saving the data
            JOptionPane.showMessageDialog(null, "Error saving user data: " + e.getMessage());
        }
    }

    /**
     * Loads user data from the JSON file (`users.json`) into the 'users' map.
     * If the file doesn't exist, it initializes an empty HashMap for users.
     * This method is protected.
     */
    protected void loadUsers() {
        try (Reader reader = new FileReader(filePath)) { // Opens a FileReader from the specified file path
            // Defines the generic type for Gson to correctly deserialize the Map
            Type type = new TypeToken<Map<String, User>>() {}.getType();
            users = gson.fromJson(reader, type); // Deserializes the JSON data into the 'users' map
            if (users == null) users = new HashMap<>(); // If the file was empty or contained null, initialize as empty map
        } catch (FileNotFoundException e) {
            // If the file does not exist, it's the first run, so initialize an empty map
            users = new HashMap<>();
        } catch (IOException e) {
            // Displays an error message if there's an issue loading the data (other than file not found)
            JOptionPane.showMessageDialog(null, "Error loading user data: " + e.getMessage());
        }
    }
}