import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class ValidationTest {

    private Validation validator;

    // Set up a new Validation object before each test
    @BeforeEach
    void setUp() {
        validator = new Validation();
    }

    // --- Tests for static method: checkUserName ---
    // Test valid usernames
    @Test
    void testCheckUserName_Valid() {
        assertTrue(Validation.checkUserName("user_"));
        assertTrue(Validation.checkUserName("a_b"));
        assertTrue(Validation.checkUserName("1_2"));
        assertTrue(Validation.checkUserName("abc_")); // Length 5
    }

    // Test usernames that are too long
    @Test
    void testCheckUserName_Invalid_TooLong() {
        assertFalse(Validation.checkUserName("user_name")); // Length > 5
        assertFalse(Validation.checkUserName("long_user"));
    }

    // Test usernames without an underscore
    @Test
    void testCheckUserName_Invalid_NoUnderscore() {
        assertFalse(Validation.checkUserName("user"));
        assertFalse(Validation.checkUserName("abcde"));
    }

    // Test null username
    @Test
    void testCheckUserName_Invalid_Null() {
        assertFalse(Validation.checkUserName(null));
    }

    // Test empty username
    @Test
    void testCheckUserName_Invalid_Empty() {
        assertFalse(Validation.checkUserName(""));
    }

    // Test usernames with whitespace
    @Test
    void testCheckUserName_Invalid_Whitespace() {
        assertFalse(Validation.checkUserName("   _")); // Length check
        assertFalse(Validation.checkUserName(" _ ")); // Length check
    }

    // --- Tests for static method: checkPasswordComplexity ---
    // Test valid passwords
    @Test
    void testCheckPasswordComplexity_Valid() {
        assertTrue(Validation.checkPasswordComplexity("Passw0rd!"));
        assertTrue(Validation.checkPasswordComplexity("My_Pass1"));
        assertTrue(Validation.checkPasswordComplexity("Abcdefg1@"));
        assertTrue(Validation.checkPasswordComplexity("A1!aaaaa")); // Exactly 8 chars
    }

    // Test passwords that are too short
    @Test
    void testCheckPasswordComplexity_Invalid_TooShort() {
        assertFalse(Validation.checkPasswordComplexity("Short1!")); // 7 chars
        assertFalse(Validation.checkPasswordComplexity("abc"));
    }

    // Test passwords without a capital letter
    @Test
    void testCheckPasswordComplexity_Invalid_NoCapital() {
        assertFalse(Validation.checkPasswordComplexity("password123!"));
        assertFalse(Validation.checkPasswordComplexity("my_pass1"));
    }

    // Test passwords without a digit
    @Test
    void testCheckPasswordComplexity_Invalid_NoDigit() {
        assertFalse(Validation.checkPasswordComplexity("Password_!"));
        assertFalse(Validation.checkPasswordComplexity("MY_PASS!"));
    }

    // Test passwords without a special character
    @Test
    void testCheckPasswordComplexity_Invalid_NoSpecialChar() {
        assertFalse(Validation.checkPasswordComplexity("Password123"));
        assertFalse(Validation.checkPasswordComplexity("MyPass123"));
    }

    // Test null password
    @Test
    void testCheckPasswordComplexity_Invalid_Null() {
        assertFalse(Validation.checkPasswordComplexity(null));
    }

    // Test empty password
    @Test
    void testCheckPasswordComplexity_Invalid_Empty() {
        assertFalse(Validation.checkPasswordComplexity(""));
    }

    // --- Tests for static method: checkCellPhoneNumber ---
    // Test valid cellphone numbers
    @Test
    void testCheckCellPhoneNumber_Valid() {
        assertTrue(Validation.checkCellPhoneNumber("+27123456789"));
        assertTrue(Validation.checkCellPhoneNumber("+27831234567"));
        assertTrue(Validation.checkCellPhoneNumber("+27609876543"));
    }

    // Test cellphone numbers missing "+27"
    @Test
    void testCheckCellPhoneNumber_Invalid_MissingPlus27() {
        assertFalse(Validation.checkCellPhoneNumber("07123456789"));
        assertFalse(Validation.checkCellPhoneNumber("27123456789"));
    }

    // Test cellphone numbers that are too short
    @Test
    void testCheckCellPhoneNumber_Invalid_TooShort() {
        assertFalse(Validation.checkCellPhoneNumber("+2712345678")); // 9 digits
    }

    // Test cellphone numbers that are too long
    @Test
    void testCheckCellPhoneNumber_Invalid_TooLong() {
        assertFalse(Validation.checkCellPhoneNumber("+271234567890")); // 11 digits
    }

    // Test cellphone numbers with non-digits
    @Test
    void testCheckCellPhoneNumber_Invalid_NonDigits() {
        assertFalse(Validation.checkCellPhoneNumber("+27abcde123"));
        assertFalse(Validation.checkCellPhoneNumber("+2712345678A"));
    }

    // Test null cellphone number
    @Test
    void testCheckCellPhoneNumber_Invalid_Null() {
        assertFalse(Validation.checkCellPhoneNumber(null));
    }

    // Test empty cellphone number
    @Test
    void testCheckCellPhoneNumber_Invalid_Empty() {
        assertFalse(Validation.checkCellPhoneNumber(""));
    }

    // --- Tests for instance method: registerUser ---
    // Test registration with all valid inputs
    @Test
    void testRegisterUser_AllValid() {
        String result = validator.registerUser("user_", "Passw0rd!", "+27123456789");
        assertTrue(result.contains("Username is correctly formatted."));
        assertTrue(result.contains("Password is correctly formatted."));
        assertTrue(result.contains("Cell number successfully captured."));
        assertTrue(result.contains("User registered successfully."));
        assertFalse(result.contains("Fix the errors above and try again."));
    }

    // Test registration with an invalid username
    @Test
    void testRegisterUser_InvalidUsername() {
        String result = validator.registerUser("invalid", "Passw0rd!", "+27123456789");
        assertTrue(result.contains("Username is not correctly formatted"));
        assertTrue(result.contains("Password is correctly formatted."));
        assertTrue(result.contains("Cell number successfully captured."));
        assertFalse(result.contains("User registered successfully."));
        assertTrue(result.contains("Fix the errors above and try again."));
    }

    // Test registration with an invalid password
    @Test
    void testRegisterUser_InvalidPassword() {
        String result = validator.registerUser("user_", "short", "+27123456789");
        assertTrue(result.contains("Username is correctly formatted."));
        assertTrue(result.contains("Password is not correctly formatted"));
        assertTrue(result.contains("Cell number successfully captured."));
        assertFalse(result.contains("User registered successfully."));
        assertTrue(result.contains("Fix the errors above and try again."));
    }

    // Test registration with an invalid cellphone number
    @Test
    void testRegisterUser_InvalidCellphone() {
        String result = validator.registerUser("user_", "Passw0rd!", "07123");
        assertTrue(result.contains("Username is correctly formatted."));
        assertTrue(result.contains("Password is correctly formatted."));
        assertTrue(result.contains("Cell number is incorrectly formatted"));
        assertFalse(result.contains("User registered successfully."));
        assertTrue(result.contains("Fix the errors above and try again."));
    }

    // Test registration with all invalid inputs
    @Test
    void testRegisterUser_AllInvalid() {
        String result = validator.registerUser("no", "123", "abc");
        assertTrue(result.contains("Username is not correctly formatted"));
        assertTrue(result.contains("Password is not correctly formatted"));
        assertTrue(result.contains("Cell number is incorrectly formatted"));
        assertFalse(result.contains("User registered successfully."));
        assertTrue(result.contains("Fix the errors above and try again."));
    }

    // Test registration with null inputs
    @Test
    void testRegisterUser_NullInputs() {
        String result = validator.registerUser(null, null, null);
        assertTrue(result.contains("Username is not correctly formatted"));
        assertTrue(result.contains("Password is not correctly formatted"));
        assertTrue(result.contains("Cell number is incorrectly formatted"));
        assertFalse(result.contains("User registered successfully."));
        assertTrue(result.contains("Fix the errors above and try again."));
    }

    // --- Tests for instance method: loginUser ---
    // Test successful login
    @Test
    void testLoginUser_Successful() {
        Map<String, User> usersMap = new HashMap<>();
        User user = new User();
        user.setUsername("test_");
        user.setPassword("ValidPass1!");
        user.setName("John"); // Name is used in loginStatus
        usersMap.put("test_", user);

        assertTrue(validator.loginUser("test_", "ValidPass1!", usersMap));
        assertEquals("Login successful. Welcome, John!", validator.returnLoginStatus());
    }

    // Test login with incorrect password
    @Test
    void testLoginUser_IncorrectPassword() {
        Map<String, User> usersMap = new HashMap<>();
        User user = new User();
        user.setUsername("test_");
        user.setPassword("ValidPass1!");
        user.setName("John");
        usersMap.put("test_", user);

        assertFalse(validator.loginUser("test_", "WrongPass!", usersMap));
        assertEquals("Incorrect password.", validator.returnLoginStatus());
    }

    // Test login when username does not exist
    @Test
    void testLoginUser_UsernameDoesNotExist() {
        Map<String, User> usersMap = new HashMap<>(); // Empty map

        assertFalse(validator.loginUser("nonexistent_", "anypass", usersMap));
        assertEquals("Username does not exist.", validator.returnLoginStatus());
    }

    // This test case for NullUsername is incomplete and does not perform any assertions.
    // It should be expanded to test how `loginUser` handles a null username input.
    @Test
    void testLoginUser_NullUsername() {
        Map<String, User> usersMap = new HashMap<>();
        User user = new User();
        user.setUsername("test_");
        user.setPassword("ValidPass1!");
        user.setName("John");
        usersMap.put("test_", user);

        // Add assertions here to test the behavior with a null username.
        // For example:
        assertFalse(validator.loginUser(null, "ValidPass1!", usersMap));
        assertEquals("Username does not exist.", validator.returnLoginStatus());
    }

    // --- Tests for instance method: returnLoginStatus ---
    // Test initial login status (should be empty)
    @Test
    void testReturnLoginStatus_Initial() {
        assertEquals("", validator.returnLoginStatus());
    }

    // Test login status after a successful login
    @Test
    void testReturnLoginStatus_AfterSuccessfulLogin() {
        Map<String, User> usersMap = new HashMap<>();
        User user = new User();
        user.setUsername("test_");
        user.setPassword("ValidPass1!");
        user.setName("Alice");
        usersMap.put("test_", user);

        validator.loginUser("test_", "ValidPass1!", usersMap);
        assertEquals("Login successful. Welcome, Alice!", validator.returnLoginStatus());
    }

    // Test login status after a failed login (incorrect password)
    @Test
    void testReturnLoginStatus_AfterFailedLogin() {
        Map<String, User> usersMap = new HashMap<>();
        User user = new User();
        user.setUsername("test_");
        user.setPassword("ValidPass1!");
        usersMap.put("test_", user);

        validator.loginUser("test_", "WrongPass!", usersMap);
        assertEquals("Incorrect password.", validator.returnLoginStatus());
    }

    // Test login status after a failed login (username does not exist)
    @Test
    void testReturnLoginStatus_AfterUserDoesNotExist() {
        Map<String, User> usersMap = new HashMap<>(); // Empty map

        validator.loginUser("nonexistent_", "anypass", usersMap);
        assertEquals("Username does not exist.", validator.returnLoginStatus());
    }
}