//Libraries
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessagesTest {
    private Messages messages;
    private final String TEST_JSON_FILE = "temp_messages_test.json";

    public MessagesTest() {
    }

    private void cleanUpTestFile() {
        try {
            Files.deleteIfExists(Paths.get("temp_messages_test.json"));
        } catch (IOException e) {
            System.err.println("Error cleaning up test file: " + e.getMessage());
        }

    }

    @BeforeEach
    void setUp() {
        this.cleanUpTestFile();
        this.messages = new TestableMessages();
    }

    @AfterEach
    void tearDown() {
        this.cleanUpTestFile();
    }

    @Test
    void testGenerateMessageID() {
        String id = this.messages.generateMessageID();
        Assertions.assertNotNull(id);
        Assertions.assertEquals(10, id.length());
        Assertions.assertTrue(id.matches("\\d{10}"));
    }

    @Test
    void testCheckMessageID_Valid() {
        Assertions.assertFalse(this.messages.checkMessageID("1234567890"));
    }

    @Test
    void testCheckMessageID_InvalidTooShort() {
        Assertions.assertTrue(this.messages.checkMessageID("12345"));
    }

    @Test
    void testCheckRecipient_Valid() {
        Assertions.assertTrue(this.messages.checkRecipient("+27123456789"));
    }

    @Test
    void testCheckRecipient_InvalidFormat() {
        Assertions.assertFalse(this.messages.checkRecipient("07123456789"));
        Assertions.assertFalse(this.messages.checkRecipient("+2712345678"));
        Assertions.assertFalse(this.messages.checkRecipient("+271234567890"));
        Assertions.assertFalse(this.messages.checkRecipient("abc"));
        Assertions.assertFalse(this.messages.checkRecipient((String)null));
    }

    @Test
    void testCreateMessageHash_NormalMessage() {
        String hash = this.messages.createMessageHash("AB12345678", 1, "Hello world this is a test");
        Assertions.assertEquals("AB:1:HELLOTEST", hash);
    }

    @Test
    void testCreateMessageHash_SingleWordMessage() {
        String hash = this.messages.createMessageHash("CD98765432", 2, "Greeting");
        Assertions.assertEquals("CD:2:GREETINGGREETING", hash);
    }

    @Test
    void testCreateMessageHash_EmptyMessage() {
        String hash = this.messages.createMessageHash("EF00000000", 3, "");
        Assertions.assertEquals("EF:3:", hash);
    }

    @Test
    void testCreateMessageHash_WhitespaceMessage() {
        String hash = this.messages.createMessageHash("GH11111111", 4, "   ");
        Assertions.assertEquals("GH:4:", hash);
    }

    @Test
    void testReturnTotalMessages_Initial() {
        Assertions.assertEquals(0, this.messages.returnTotalMessages());
    }

    @Test
    void testReturnTotalMessages_AfterStoring() {
        this.messages.storeMessage("id1", "hash1", "+27123456789", "text1");
        Assertions.assertEquals(1, this.messages.returnTotalMessages());
        this.messages.storeMessage("id2", "hash2", "+27987654321", "text2");
        Assertions.assertEquals(2, this.messages.returnTotalMessages());
    }

    @Test
    void testStoreMessage_IncreasesTotalMessages() {
        Assertions.assertEquals(0, this.messages.returnTotalMessages());
        this.messages.storeMessage("id1", "hash1", "+27123456789", "text1");
        Assertions.assertEquals(1, this.messages.returnTotalMessages());
    }

    @Test
    void testStoreMessage_WritesToFileAndCanBeLoaded() {
        String id1 = "msgId1";
        String hash1 = "hash1";
        String recipient1 = "+27123456789";
        String text1 = "First test message.";
        String id2 = "msgId2";
        String hash2 = "hash2";
        String recipient2 = "+27987654321";
        String text2 = "Second test message.";
        this.messages.storeMessage(id1, hash1, recipient1, text1);
        this.messages.storeMessage(id2, hash2, recipient2, text2);
        Messages loadedMessagesInstance = new TestableMessages();
        Assertions.assertEquals(2, loadedMessagesInstance.returnTotalMessages());

        try {
            Field messagesField = Messages.class.getDeclaredField("messages");
            messagesField.setAccessible(true);
            List<MessageEntry> loadedList = (List)messagesField.get(loadedMessagesInstance);
            Assertions.assertEquals(id1, ((MessageEntry)loadedList.get(0)).MessageID);
            Assertions.assertEquals(hash1, ((MessageEntry)loadedList.get(0)).MessageHash);
            Assertions.assertEquals(recipient1, ((MessageEntry)loadedList.get(0)).Recipient);
            Assertions.assertEquals(text1, ((MessageEntry)loadedList.get(0)).Message);
            Assertions.assertEquals(id2, ((MessageEntry)loadedList.get(1)).MessageID);
            Assertions.assertEquals(hash2, ((MessageEntry)loadedList.get(1)).MessageHash);
            Assertions.assertEquals(recipient2, ((MessageEntry)loadedList.get(1)).Recipient);
            Assertions.assertEquals(text2, ((MessageEntry)loadedList.get(1)).Message);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Assertions.fail("Failed to access private messages list for verification: " + ((ReflectiveOperationException)e).getMessage());
        }

    }

    @Test
    void testLoadMessagesFromJson_NoFileExists() {
        this.cleanUpTestFile();
        Messages newMessages = new TestableMessages();
        Assertions.assertEquals(0, newMessages.returnTotalMessages());

        try {
            Field messagesField = Messages.class.getDeclaredField("messages");
            messagesField.setAccessible(true);
            List<MessageEntry> internalList = (List)messagesField.get(newMessages);
            Assertions.assertTrue(internalList.isEmpty());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Assertions.fail("Failed to access private messages list for verification: " + ((ReflectiveOperationException)e).getMessage());
        }

    }

    private class TestableMessages extends Messages {
        private TestableMessages() {
        }

        public void writeMessagesToJson() {
            try (FileWriter writer = new FileWriter("temp_messages_test.json")) {
                try {
                    Field messagesField = Messages.class.getDeclaredField("messages");
                    messagesField.setAccessible(true);
                    List<MessageEntry> internalMessages = (List)messagesField.get(this);
                    (new GsonBuilder()).setPrettyPrinting().create().toJson(internalMessages, writer);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    Assertions.fail("Failed to access private messages list for writing: " + ((ReflectiveOperationException)e).getMessage());
                }
            } catch (IOException e) {
                Assertions.fail("Error writing to JSON file in test: " + e.getMessage());
            }

        }

        public void loadMessagesFromJson() {
            File file = new File("temp_messages_test.json");
            if (file.exists()) {
                try (Scanner scanner = new Scanner(file)) {
                    StringBuilder json = new StringBuilder();

                    while(scanner.hasNextLine()) {
                        json.append(scanner.nextLine());
                    }

                    MessageEntry[] loadedMessages = (MessageEntry[])(new Gson()).fromJson(json.toString(), MessageEntry[].class);
                    if (loadedMessages != null) {
                        try {
                            Field messagesField = Messages.class.getDeclaredField("messages");
                            messagesField.setAccessible(true);
                            List<MessageEntry> internalMessages = (List)messagesField.get(this);
                            internalMessages.clear();

                            for(MessageEntry msg : loadedMessages) {
                                internalMessages.add(msg);
                            }

                            Field totalMessagesField = Messages.class.getDeclaredField("totalMessages");
                            totalMessagesField.setAccessible(true);
                            totalMessagesField.set(this, internalMessages.size());
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            Assertions.fail("Failed to access private messages list for loading: " + ((ReflectiveOperationException)e).getMessage());
                        }
                    }
                } catch (IOException e) {
                    Assertions.fail("Error loading from JSON file in test: " + e.getMessage());
                }

            }
        }
    }
}
