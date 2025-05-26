import java.util.Map; // Imports the Map interface

public class Validation {

    // Stores the message about the last login attempt
    private String loginStatus = "";

    // ## Validation Methods

    /**
     * Checks if a username is valid.
     * Must be non-empty, up to 5 chars, contain '_', and use only alphanumeric or '_'.
     * @param username The username to check.
     * @return True if valid, false otherwise.
     */
    static boolean checkUserName(String username) {
        return username != null &&          // Not null
                !username.isEmpty() &&      // Not empty
                username.length() <= 5 &&   // Max 5 characters
                username.contains("_") &&   // Contains an underscore
                username.matches("^[a-zA-Z0-9_]+$"); // Alphanumeric or underscore only
    }

    /**
     * Checks if a password meets complexity rules.
     * Must be at least 8 characters, with an uppercase letter, a digit, and a special character.
     * @param password The password to check.
     * @return True if complex, false otherwise.
     */
    static boolean checkPasswordComplexity(String password) {
        return password != null &&
                password.length() >= 8 &&               // At least 8 characters
                password.matches(".*[A-Z].*") &&        // At least one uppercase letter
                password.matches(".*\\d.*") &&          // At least one digit
                password.matches(".*[!@#_$%^&*()].*");  // At least one special character
    }

    /**
     * Checks if a cell phone number is a valid South African number.
     * Must start with "+27" and be followed by 9 digits.
     * @param cellphoneNumber The phone number to check.
     * @return True if valid, false otherwise.
     */
    static boolean checkCellPhoneNumber(String cellphoneNumber) {
        String pattern = "^\\+27\\d{9}$"; // Regex for +27 and 9 digits
        return cellphoneNumber != null && cellphoneNumber.matches(pattern); // Not null and matches pattern
    }

    // ---
    // ## User Registration and Login
    // ---

    /**
     * Validates all inputs for user registration (username, password, phone number).
     * @param username The desired username.
     * @param password The desired password.
     * @param cellphoneNumber The user's cell phone number.
     * @return A string with all validation messages and overall success/failure.
     */
    public String registerUser(String username, String password, String cellphoneNumber) {
        StringBuilder messages = new StringBuilder(); // Builds validation messages
        boolean allValid = true;                      // Flag for overall validity

        // Check username and add message
        String usernameMessage = checkUserName(username) ? "Username is correctly formatted." :
                "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        if (!usernameMessage.equals("Username is correctly formatted.")) {
            allValid = false; // Mark as invalid if username is bad
        }
        messages.append(usernameMessage).append("\n"); // Add username message

        // Check password and add message
        String passwordMessage = checkPasswordComplexity(password) ? "Password is correctly formatted." :
                "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        if (!passwordMessage.equals("Password is correctly formatted.")) {
            allValid = false; // Mark as invalid if password is bad
        }
        messages.append(passwordMessage).append("\n"); // Add password message

        // Check cell phone number and add message
        String phoneMessage = checkCellPhoneNumber(cellphoneNumber) ? "Cell number successfully captured." :
                "Cell number is incorrectly formatted or does not contain an international code, please correct the number and try again";
        if (!phoneMessage.equals("Cell number successfully captured.")) {
            allValid = false; // Mark as invalid if phone number is bad
        }
        messages.append(phoneMessage).append("\n"); // Add phone number message

        // Add overall success or error message
        if (allValid) {
            messages.append("User registered successfully.");
        } else {
            messages.append("\nFix the errors above and try again.");
        }

        return messages.toString().trim(); // Return all messages
    }

    /**
     * Tries to log in a user. Checks username existence and password correctness.
     * Updates `loginStatus`.
     * @param username The login username.
     * @param password The login password.
     * @param users A map of existing users.
     * @return True if login is successful, false otherwise.
     */
    public boolean loginUser(String username, String password, Map<String, User> users) {
        if (!users.containsKey(username)) { // Check if username exists
            loginStatus = "Username does not exist."; // Set status
            return false; // Login failed
        }
        User user = users.get(username); // Get the user
        if (!user.getPassword().equals(password)) { // Check if password matches
            loginStatus = "Incorrect password."; // Set status
            return false; // Login failed
        }
        loginStatus = "Login successful. Welcome, " + user.getName() + "!"; // Set success status
        return true; // Login successful
    }

    /**
     * Returns the status message from the last login attempt.
     * @return The login status message.
     */
    public String returnLoginStatus() {
        return loginStatus; // Return login status
    }
}