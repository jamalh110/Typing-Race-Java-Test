# Typing-Race-Java-Test

Demo Video: https://youtu.be/MvyhU7ZtN7w

This is the Java client I wrote to test the login and game servers. I am in the process of writing the iPhone app to replace this. 

The application consists of two classes - Main and Interface. 

Main is a container for the interface object and the methods I use to read and write data to the game and login servers. 
Interface is a JFrame that displays all the information and handles user interaction. 

All server queries are run in separate threads to avoid locking the UI

This is what the program does:

Send username and password to login server to log in (will be done over TLS before publishing on the App Store)
Receive login key for login server so it doesnt keep having to send the username and password
Send matchmaking request to Login server
(Login server then forwards that request to the Game Server)
Ping the Game Server until it returns a match ID
Connect to the Match by sending the match ID to the game server
Request the opponents username
Keep querying for the countdown timer
When the game starts, keep querying for the opponent's percentage through and if the game is over or not
When a word is typed, send it to the game server. 

