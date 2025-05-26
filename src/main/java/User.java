public class User {
    //Attributes
    private String name;
    private String surname;
    private String username;
    private String password;
    private String cellphoneNumber;


    //Constructor
    //The constructor doesn't have arguments because they will be initialized later
    public User() {}

    //Special Methods, Getters and setters
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getCellphoneNumber() { return cellphoneNumber; }

    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setCellphoneNumber(String cellphoneNumber) { this.cellphoneNumber = cellphoneNumber; }


}
