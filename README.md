# 💬 QuickChat v0.2.2-beta.1

**QuickChat** is a simple, menu-driven chat application built in Java as part of an IIE assignment. It allows users to register, log in, send and view messages, and manage user profiles. All user data and messages are stored locally in a JSON file using the Gson library.

---

## 📌 Version Highlights

**v0.2.2-beta.2** introduces:
- Ability to send Messages
- Persistent storage using JSON
- Returning users can log in using stored credentials
- Ability to view previously sent messages
- Profile viewing functionality

---

## 🚀 Features

- ✅ User Registration and Login
- ✅ Message sending and history
- ✅ Profile viewing
- ✅ Local data persistence (JSON)
- ✅ GUI-based (Graphical User Interface)

---

## 🛠️ Technologies Used

- **Java**
- **Gson** – for JSON parsing and storage
- **IntelliJ IDEA** – development environment
- **AI Assistance** – OpenAI ChatGPT and Google Gemini used for brainstorming and code guidance

---

## 📁 Project Structure

```
/QuickChat
 │
├── Main.java                                    # Entry point of the application
├── User.java                                    # User model
├── UserManager.java                     # Handles user registration, login, and profile
├── Messages.java                          # Manages message-related operations
├── MessageEntry.java                   # Message model
├── Validation.java                          # Input validation utilities
 │
├── UserManagerTest.java            # Unit tests for UserManager
├── MessagesTest.java                  # Unit tests for Messages
├── ValidationTest.java                  # Unit tests for Validation
 │
└── README.md                           # Project documentation
``` 

---

## 📷 Screenshots

### 🔐 Menu 1 – Registration & Login  
![Menu 1](https://github.com/user-attachments/assets/92699b48-85c5-490c-b4a8-e0c766495caf)

### 🏠 Menu 2 – Logged-in User Options  
![Menu 2](https://github.com/user-attachments/assets/5cee9602-e418-4b86-aad6-97ddbf049eb2)

### ✍️ Message Writing  
![Message Writing](https://github.com/user-attachments/assets/5d597e6f-8623-4090-acf0-e46e37ec4a7e)

### 🕓 Message History  
![Message History](https://github.com/user-attachments/assets/cb3b1eab-f6ed-40c0-bcc8-3ef3a05a2965)

### 👤 View Profile  
![Profile View](https://github.com/user-attachments/assets/5221d9a6-866a-409d-b579-ed50c0d401a4)

### ❌ Exit Screen  
![Exit Screen](https://github.com/user-attachments/assets/7b0db3a4-5f96-4568-a679-bda917236bbc)

---

## 🧪 How to Run

1. **Clone the repository**  
   ```bash
   git clone https://github.com/GuioMav/Quick-Chat
   ```

2. **Open in IntelliJ IDEA**  
   Open the project folder and make sure Gson is added as a library.

3. **Run the Application**  
   Run `Main.java` to start the app in your terminal.

---

## 📖 Notes

This app was developed for educational purposes to demonstrate:
- Java basics (OOP, file handling)
- Menu-based interaction
- JSON data storage using external libraries
- Simple application flow control

---

## 👨‍💻 Author

**Manuel Mavungo**  
Software Developer & Student – IIE  Rosebank College
Passionate about clean, simple code and real-world learning through projects.

---

## 📃 License

This project is part of an academic submission and is provided as-is for learning purposes.

---

🚧 Beta Release – Not Production Ready
