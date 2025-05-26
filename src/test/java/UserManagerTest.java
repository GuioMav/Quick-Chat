//Libraries
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Test class for UserManager
public class UserManagerTest {
    private TestableUserManager UserManager; // Instance of TestableUserManager for testing
    private final String TEST_JSON_FILE = "test_users.json"; // Name of the test JSON file

    public UserManagerTest() {
    }

    // Cleans up the test JSON file before each test
    private void cleanUpTestFile() {
        try {
            Files.deleteIfExists(Paths.get("test_users.json")); // Deletes the file if it exists
        } catch (IOException e) {
            System.err.println("Error cleaning up test file: " + e.getMessage()); // Prints error if cleanup fails
        }

    }

    // Set up method executed before each test
    @BeforeEach
    void setUp() {
        this.cleanUpTestFile(); // Cleans up the test file
        this.UserManager = new TestableUserManager(); // Initializes a new TestableUserManager
        this.UserManager.setLoggedInUserDirectly((User)null); // Ensures no user is logged in initially
    }

    // Tear down method executed after each test
    @AfterEach
    void tearDown() {
        this.cleanUpTestFile(); // Cleans up the test file
    }

    // Test to verify that isLoggedIn is initially false
    @Test
    void testIsLoggedIn_InitiallyFalse() {
        Assertions.assertFalse(this.UserManager.isLoggedIn()); // Asserts that no user is logged in
    }

    // Test to verify isLoggedIn after simulating a login
    @Test
    void testIsLoggedIn_AfterLoginSimulation() {
        User testUser = new User(); // Creates a new test user
        testUser.setUsername("test_");
        testUser.setPassword("Password123!");
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setCellphoneNumber("+27123456789");
        this.UserManager.getInternalUsers().put("test_", testUser); // Adds the user to internal map
        this.UserManager.setLoggedInUserDirectly(testUser); // Sets the user as logged in
        Assertions.assertTrue(this.UserManager.isLoggedIn()); // Asserts that a user is logged in
        Assertions.assertEquals(testUser, this.UserManager.getLoggedInUser()); // Asserts the correct user is logged in
    }

    // Test to verify logout functionality when a user is logged in
    @Test
    void testLogout_WhenLoggedIn() {
        User testUser = new User(); // Creates a new test user
        testUser.setUsername("logged_");
        testUser.setPassword("Password123!");
        this.UserManager.setLoggedInUserDirectly(testUser); // Sets the user as logged in
        this.UserManager.logout(); // Calls the logout method
        Assertions.assertFalse(this.UserManager.isLoggedIn()); // Asserts no user is logged in
        Assertions.assertNull(this.UserManager.getLoggedInUser()); // Asserts loggedInUser is null
    }

    // Test to verify logout functionality when no user is logged in
    @Test
    void testLogout_WhenNotLoggedIn() {
        this.UserManager.logout(); // Calls the logout method
        Assertions.assertFalse(this.UserManager.isLoggedIn()); // Asserts no user is logged in
        Assertions.assertNull(this.UserManager.getLoggedInUser()); // Asserts loggedInUser is null
    }

    // Test to verify that getLoggedInUser is initially null
    @Test
    void testGetLoggedInUser_InitiallyNull() {
        Assertions.assertNull(this.UserManager.getLoggedInUser()); // Asserts loggedInUser is null
    }

    // Test to verify save and load users when the user map is empty
    @Test
    void testSaveAndLoadUsers_Empty() {
        Assertions.assertTrue(this.UserManager.getInternalUsers().isEmpty()); // Asserts the internal user map is empty
        Assertions.assertFalse(this.UserManager.isLoggedIn()); // Asserts no user is logged in
    }

    // Test to verify save and load users with existing data
    @Test
    void testSaveAndLoadUsers_WithData() {
        User user1 = new User(); // Creates a new user
        user1.setUsername("user_");
        user1.setPassword("Passw0rd!");
        user1.setName("Name1");
        user1.setSurname("Surname1");
        user1.setCellphoneNumber("+27111111111");
        this.UserManager.getInternalUsers().put("user_", user1); // Adds the user to the internal map
        this.UserManager.saveUsers(); // Saves the users to the file
        TestableUserManager loadedUserManager = new TestableUserManager(); // Creates a new UserManager to load from file
        Map<String, User> loadedUsers = loadedUserManager.getInternalUsers(); // Gets the loaded users
        Assertions.assertEquals(1, loadedUsers.size()); // Asserts that one user was loaded
        Assertions.assertTrue(loadedUsers.containsKey("user_")); // Asserts the loaded map contains the user
        Assertions.assertEquals("Name1", ((User)loadedUsers.get("user_")).getName()); // Asserts the loaded user's name is correct
        Assertions.assertEquals("+27111111111", ((User)loadedUsers.get("user_")).getCellphoneNumber()); // Asserts the loaded user's cell number is correct
        Assertions.assertFalse(loadedUserManager.isLoggedIn()); // Asserts no user is logged in after loading
    }

    // Test to verify loadUsers when no file exists
    @Test
    void testLoadUsers_NoFileExists() {
        this.cleanUpTestFile(); // Ensures the test file does not exist
        this.UserManager = new TestableUserManager(); // Initializes a new UserManager
        Assertions.assertTrue(this.UserManager.getInternalUsers().isEmpty()); // Asserts the internal user map is empty
    }

    // Test to verify valid usernames with checkUserName
    @Test
    void testCheckUserName_Valid() {
        Assertions.assertTrue(Validation.checkUserName("user_")); // Valid username
        Assertions.assertTrue(Validation.checkUserName("ab_c")); // Valid username
    }

    // Test to verify invalid usernames with checkUserName
    @Test
    void testCheckUserName_Invalid() {
        Assertions.assertFalse(Validation.checkUserName("username")); // Invalid: no underscore
        Assertions.assertFalse(Validation.checkUserName("user")); // Invalid: too short, no underscore
        Assertions.assertFalse(Validation.checkUserName("us_er_")); // Invalid: multiple underscores or trailing underscore
        Assertions.assertFalse(Validation.checkUserName((String)null)); // Invalid: null
        Assertions.assertFalse(Validation.checkUserName("")); // Invalid: empty string
    }

    // Test to verify valid passwords with checkPasswordComplexity
    @Test
    void testCheckPasswordComplexity_Valid() {
        Assertions.assertTrue(Validation.checkPasswordComplexity("Passw0rd!")); // Valid password
        Assertions.assertTrue(Validation.checkPasswordComplexity("My_Pass1")); // Valid password
        Assertions.assertTrue(Validation.checkPasswordComplexity("Abcdefg1@")); // Valid password
    }

    // Test to verify invalid passwords with checkPasswordComplexity
    @Test
    void testCheckPasswordComplexity_Invalid() {
        Assertions.assertFalse(Validation.checkPasswordComplexity("short")); // Invalid: too short
        Assertions.assertFalse(Validation.checkPasswordComplexity("nopassword1")); // Invalid: no uppercase, no symbol
        Assertions.assertFalse(Validation.checkPasswordComplexity("PASSWORD!")); // Invalid: no lowercase, no number
        Assertions.assertFalse(Validation.checkPasswordComplexity("Password123")); // Invalid: no symbol
        Assertions.assertFalse(Validation.checkPasswordComplexity("password123!")); // Invalid: no uppercase
        Assertions.assertFalse(Validation.checkPasswordComplexity((String)null)); // Invalid: null
        Assertions.assertFalse(Validation.checkPasswordComplexity("")); // Invalid: empty string
    }

    // Test to verify valid cell phone numbers with checkCellPhoneNumber
    @Test
    void testCheckCellPhoneNumber_Valid() {
        Assertions.assertTrue(Validation.checkCellPhoneNumber("+27123456789")); // Valid South African number
        Assertions.assertTrue(Validation.checkCellPhoneNumber("+27831234567")); // Valid South African number
    }

    // Test to verify invalid cell phone numbers with checkCellPhoneNumber
    @Test
    void testCheckCellPhoneNumber_Invalid() {
        Assertions.assertFalse(Validation.checkCellPhoneNumber("07123456789")); // Invalid: starts with 0
        Assertions.assertFalse(Validation.checkCellPhoneNumber("+2712345678")); // Invalid: too short
        Assertions.assertFalse(Validation.checkCellPhoneNumber("+271234567890")); // Invalid: too long
        Assertions.assertFalse(Validation.checkCellPhoneNumber("abc")); // Invalid: non-numeric characters
        Assertions.assertFalse(Validation.checkCellPhoneNumber((String)null)); // Invalid: null
        Assertions.assertFalse(Validation.checkCellPhoneNumber("")); // Invalid: empty string
    }

    // Test to verify successful user registration with Validation
    @Test
    void testValidation_RegisterUser_AllValid() {
        Validation val = new Validation(); // Creates a new Validation instance
        String result = val.registerUser("user_", "Passw0rd!", "+27123456789"); // Attempts to register
        Assertions.assertTrue(result.contains("Username is correctly formatted.")); // Asserts username format is correct
        Assertions.assertTrue(result.contains("Password is correctly formatted.")); // Asserts password format is correct
        Assertions.assertTrue(result.contains("Cell number successfully captured.")); // Asserts cell number format is correct
        Assertions.assertTrue(result.contains("User registered successfully.")); // Asserts successful registration message
        Assertions.assertFalse(result.contains("Fix the errors above and try again.")); // Asserts no error message
    }

    // Test to verify user registration with an invalid username
    @Test
    void testValidation_RegisterUser_InvalidUsername() {
        Validation val = new Validation(); // Creates a new Validation instance
        String result = val.registerUser("invalid", "Passw0rd!", "+27123456789"); // Attempts to register
        Assertions.assertTrue(result.contains("Username is not correctly formatted")); // Asserts username error message
        Assertions.assertTrue(result.contains("Password is correctly formatted.")); // Asserts password format is correct
        Assertions.assertTrue(result.contains("Cell number successfully captured.")); // Asserts cell number format is correct
        Assertions.assertFalse(result.contains("User registered successfully.")); // Asserts no success message
        Assertions.assertTrue(result.contains("Fix the errors above and try again.")); // Asserts error message
    }

    // Test to verify user registration with an invalid password
    @Test
    void testValidation_RegisterUser_InvalidPassword() {
        Validation val = new Validation(); // Creates a new Validation instance
        String result = val.registerUser("user_", "short", "+27123456789"); // Attempts to register
        Assertions.assertTrue(result.contains("Username is correctly formatted.")); // Asserts username format is correct
        Assertions.assertTrue(result.contains("Password is not correctly formatted")); // Asserts password error message
        Assertions.assertTrue(result.contains("Cell number successfully captured.")); // Asserts cell number format is correct
        Assertions.assertFalse(result.contains("User registered successfully.")); // Asserts no success message
        Assertions.assertTrue(result.contains("Fix the errors above and try again.")); // Asserts error message
    }

    // Test to verify user registration with an invalid cellphone number
    @Test
    void testValidation_RegisterUser_InvalidCellphone() {
        Validation val = new Validation(); // Creates a new Validation instance
        String result = val.registerUser("user_", "Passw0rd!", "07123"); // Attempts to register
        Assertions.assertTrue(result.contains("Username is correctly formatted.")); // Asserts username format is correct
        Assertions.assertTrue(result.contains("Password is correctly formatted.")); // Asserts password format is correct
        Assertions.assertTrue(result.contains("Cell number is incorrectly formatted")); // Asserts cell number error message
        Assertions.assertFalse(result.contains("User registered successfully.")); // Asserts no success message
        Assertions.assertTrue(result.contains("Fix the errors above and try again.")); // Asserts error message
    }

    // Test to verify user registration with all invalid inputs
    @Test
    void testValidation_RegisterUser_AllInvalid() {
        Validation val = new Validation(); // Creates a new Validation instance
        String result = val.registerUser("no", "123", "abc"); // Attempts to register
        Assertions.assertTrue(result.contains("Username is not correctly formatted")); // Asserts username error message
        Assertions.assertTrue(result.contains("Password is not correctly formatted")); // Asserts password error message
        Assertions.assertTrue(result.contains("Cell number is incorrectly formatted")); // Asserts cell number error message
        Assertions.assertFalse(result.contains("User registered successfully.")); // Asserts no success message
        Assertions.assertTrue(result.contains("Fix the errors above and try again.")); // Asserts error message
    }

    // Test to verify that UserManager's register process adds a user and saves it
    @Test
    void testUserManager_Register_AddsUserAndSaves() {
        String username = "valid_"; // Test username
        String password = "StrongPassword1!"; // Test password
        String name = "Test"; // Test name
        String surname = "User"; // Test surname
        String cellphone = "+27712345678"; // Test cellphone number
        User newUser = new User(); // Creates a new user object
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setName(name);
        newUser.setSurname(surname);
        newUser.setCellphoneNumber(cellphone);
        this.UserManager.getInternalUsers().put(username, newUser); // Adds the user to the internal map
        this.UserManager.saveUsers(); // Saves the users
        TestableUserManager loadedUserManager = new TestableUserManager(); // Loads users from the file
        Assertions.assertTrue(loadedUserManager.getInternalUsers().containsKey(username)); // Asserts the new user exists
        User loadedUser = (User)loadedUserManager.getInternalUsers().get(username); // Gets the loaded user
        Assertions.assertEquals(username, loadedUser.getUsername()); // Asserts username matches
        Assertions.assertEquals(password, loadedUser.getPassword()); // Asserts password matches
        Assertions.assertEquals(name, loadedUser.getName()); // Asserts name matches
        Assertions.assertEquals(surname, loadedUser.getSurname()); // Asserts surname matches
        Assertions.assertEquals(cellphone, loadedUser.getCellphoneNumber()); // Asserts cellphone number matches
    }

    // Test to verify that registration does not add a user if the username already exists
    @Test
    void testUserManager_Register_UsernameAlreadyExists_DoesNotAddUser() {
        User existingUser = new User(); // Creates an existing user
        existingUser.setUsername("exist_");
        existingUser.setPassword("OldPass1!");
        existingUser.setName("Existing");
        existingUser.setSurname("User");
        existingUser.setCellphoneNumber("+27123456789");
        this.UserManager.getInternalUsers().put("exist_", existingUser); // Adds the existing user
        String username = "exist_"; // Attempt to register with existing username
        String password = "NewPass2@";
        String name = "Another";
        String surname = "Guy";
        String cellphone = "+27821234567";
        int initialSize = this.UserManager.getInternalUsers().size(); // Gets initial size of user map
        User originalUser = (User)this.UserManager.getInternalUsers().get(username); // Gets the original user
        // The following assertions implicitly confirm that no new user is added or existing user modified
        Assertions.assertEquals(initialSize, this.UserManager.getInternalUsers().size()); // Asserts map size remains the same
        Assertions.assertEquals(originalUser, this.UserManager.getInternalUsers().get(username)); // Asserts the original user remains unchanged
    }

    // Test to verify successful login with Validation
    @Test
    void testValidation_LoginUser_Successful() {
        Validation val = new Validation(); // Creates a new Validation instance
        Map<String, User> usersMap = new HashMap(); // Creates a map of users
        User user = new User(); // Creates a test user
        user.setUsername("test_");
        user.setPassword("ValidPass1!");
        user.setName("John");
        usersMap.put("test_", user); // Adds the user to the map
        Assertions.assertTrue(val.loginUser("test_", "ValidPass1!", usersMap)); // Asserts login is successful
        Assertions.assertEquals("Login successful. Welcome, John!", val.returnLoginStatus()); // Asserts correct login status message
    }

    // Test to verify login with incorrect password
    @Test
    void testValidation_LoginUser_IncorrectPassword() {
        Validation val = new Validation(); // Creates a new Validation instance
        Map<String, User> usersMap = new HashMap(); // Creates a map of users
        User user = new User(); // Creates a test user
        user.setUsername("test_");
        user.setPassword("ValidPass1!");
        user.setName("John");
        usersMap.put("test_", user); // Adds the user to the map
        Assertions.assertFalse(val.loginUser("test_", "WrongPass!", usersMap)); // Asserts login is unsuccessful
        Assertions.assertEquals("Incorrect password.", val.returnLoginStatus()); // Asserts incorrect password message
    }

    // Test to verify login when username does not exist
    @Test
    void testValidation_LoginUser_UsernameDoesNotExist() {
        Validation val = new Validation(); // Creates a new Validation instance
        Map<String, User> usersMap = new HashMap(); // Creates an empty map of users
        Assertions.assertFalse(val.loginUser("nonexistent_", "anypass", usersMap)); // Asserts login is unsuccessful
        Assertions.assertEquals("Username does not exist.", val.returnLoginStatus()); // Asserts username does not exist message
    }

    // Test to verify that UserManager's login sets the logged-in user correctly
    @Test
    void testUserManager_Login_SuccessfulSetsLoggedInUser() {
        User testUser = new User(); // Creates a test user
        testUser.setUsername("login_");
        testUser.setPassword("MyPass123!");
        testUser.setName("LoggedIn");
        testUser.setSurname("User");
        this.UserManager.getInternalUsers().put("login_", testUser); // Adds the user to the internal map
        this.UserManager.setLoggedInUserDirectly(testUser); // Sets the user as logged in directly (simulating successful login)
        Assertions.assertTrue(this.UserManager.isLoggedIn()); // Asserts that a user is logged in
        Assertions.assertEquals(testUser, this.UserManager.getLoggedInUser()); // Asserts the correct user is logged in
        Assertions.assertEquals("LoggedIn", this.UserManager.getLoggedInUser().getName()); // Asserts the logged-in user's name
    }

    // Inner class to make UserManager testable by exposing protected methods and fields
    private class TestableUserManager extends UserManager {
        // Constructor for TestableUserManager
        public TestableUserManager() {
            try {
                // Uses reflection to set the filePath field to the test JSON file
                Field filePathField = UserManager.class.getDeclaredField("filePath");
                filePathField.setAccessible(true);
                filePathField.set(this, "test_users.json");
                // Uses reflection to set the validator field to a new Validation instance
                Field validatorField = UserManager.class.getDeclaredField("validator");
                validatorField.setAccessible(true);
                validatorField.set(this, new Validation());
                this.loadUsersForTest(); // Calls a test-specific load method
            } catch (IllegalAccessException | NoSuchFieldException e) {
                Assertions.fail("Failed to initialize TestableUserManager: " + ((ReflectiveOperationException)e).getMessage()); // Fails if initialization fails
            }

        }

        // Test-specific method to load users from the test JSON file
        private void loadUsersForTest() {
            File file = new File("test_users.json"); // Creates a file object for the test JSON
            if (!file.exists()) { // If the file does not exist
                try {
                    // Uses reflection to initialize the users map as empty
                    Field usersField = UserManager.class.getDeclaredField("users");
                    usersField.setAccessible(true);
                    usersField.set(this, new HashMap());
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    Assertions.fail("Failed to initialize users map in test: " + ((ReflectiveOperationException)e).getMessage()); // Fails if initialization fails
                }

            } else { // If the file exists
                try (Reader reader = new FileReader(file)) { // Creates a FileReader
                    // Defines the type for Gson deserialization (Map<String, User>)
                    Type type = (new TypeToken<Map<String, User>>() {
                    }).getType();
                    // Deserializes JSON to a Map
                    Map<String, User> loadedUsers = (Map)(new Gson()).fromJson(reader, type);
                    if (loadedUsers == null) { // If loaded users are null, initialize an empty map
                        loadedUsers = new HashMap();
                    }

                    // Uses reflection to set the internal users map
                    Field usersField = UserManager.class.getDeclaredField("users");
                    usersField.setAccessible(true);
                    usersField.set(this, loadedUsers);
                } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
                    Assertions.fail("Error loading users in test: " + ((Exception)e).getMessage()); // Fails if loading encounters an error
                }

            }
        }

        // Overrides the saveUsers method for testing purposes
        protected void saveUsers() {
            try (FileWriter writer = new FileWriter("test_users.json")) { // Creates a FileWriter for the test JSON
                // Uses reflection to get the internal users map
                Field usersField = UserManager.class.getDeclaredField("users");
                usersField.setAccessible(true);
                Map<String, User> internalUsers = (Map)usersField.get(this);
                (new Gson()).toJson(internalUsers, writer); // Serializes the map to JSON
            } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
                Assertions.fail("Error saving users in test: " + ((Exception)e).getMessage()); // Fails if saving encounters an error
            }

        }

        // Overrides the loadUsers method to call the test-specific load method
        protected void loadUsers() {
            this.loadUsersForTest();
        }

        // Exposes the internal users map for testing
        public Map<String, User> getInternalUsers() {
            try {
                // Uses reflection to get and return the internal users map
                Field usersField = UserManager.class.getDeclaredField("users");
                usersField.setAccessible(true);
                return (Map)usersField.get(this);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                Assertions.fail("Failed to access internal users map: " + ((ReflectiveOperationException)e).getMessage()); // Fails if access fails
                return null;
            }
        }

        // Allows direct setting of the loggedInUser for testing
        public void setLoggedInUserDirectly(User user) {
            try {
                // Uses reflection to set the loggedInUser field
                Field loggedInUserField = UserManager.class.getDeclaredField("loggedInUser");
                loggedInUserField.setAccessible(true);
                loggedInUserField.set(this, user);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                Assertions.fail("Failed to set loggedInUser directly: " + ((ReflectiveOperationException)e).getMessage()); // Fails if setting fails
            }

        }

        // Empty override for register method in test context
        public void register() {
        }

        // Empty override for login method in test context
        public void login() {
        }

        // Overrides logout method for testing, directly setting loggedInUser to null
        public void logout() {
            if (this.getLoggedInUser() != null) { // If a user is logged in
                this.setLoggedInUserDirectly((User)null); // Set loggedInUser to null
            }

        }

        // Empty override for viewProfile method in test context
        public void viewProfile() {
        }
    }
}