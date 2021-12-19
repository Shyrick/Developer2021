package lection2.Cli;

import lection2.Storage.JDBCStorage;

public class CommandHandler {

    private JDBCStorage storage;

    public CommandHandler(JDBCStorage storage){
        this.storage = storage;
    }
    public void handleCommand(String [] args){

    }


}
