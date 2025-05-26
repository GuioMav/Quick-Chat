class MessageEntry {
    //Attributes
    String MessageID;
    String MessageHash;
    String Recipient;
    String Message;

    //Constructor
    public MessageEntry(String id, String hash, String recipient, String message) {
        this.MessageID = id;
        this.MessageHash = hash;
        this.Recipient = recipient;
        this.Message = message;
    }
}
