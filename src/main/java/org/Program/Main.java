package org.Program;
public class Main {
    public static void main(String[] args){
        Database.connect(Constants.databaseURL, Constants.user, Constants.password);
        new Window();
    }
}