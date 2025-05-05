# QuizGenAi

For this program to function correctly, you need to do the following:

1 - Download the project and unzip it.

2- Copy the schema from the "Database Schema" file and paste it into the MySQL WorkBench terminal.
  2.1 - Copy the code in the "Sample Data" file and paste it into the database to populate it with sample data.

3 - Open the project in your preferred IDE.

4 - Go to the "Database" file in the "org.Program" directory(this is the same directory that also includes the "Main" file).
  4.1 - Here, you will find 3 variables: databaseURL, user, and password.
  4.2 - Set the "password" variable to the password you use in your MySQL workbench. This will allow the program to connect to the database.
  4.3 - Also check that the "databaseURL" and "user" are the same as in MySQL workbench(Although you probably won't need to change them).

5 - Run the program. This might take a while since this project uses external libraries for database connectivity and LLM access, so your IDE will need to download them the first time.

6 - If you get the String "Connection established successfully" in the terminal after you run the program, that means you can use most of the program's functionality with no problem.

Note: I disabled the part of the program that communicates with the LLM because it uses my API key. It would be a waste to make that many calls to the LLM when testing other features of the program. 
Currently, the program uses a sample reply from the LLM to generate quizzes. We will discuss this when we meet over Discord.
