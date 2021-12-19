package lection2.Cli;

import java.util.Scanner;

public class JdbcCli {

    private Scanner scanner;
    boolean end;

    public JdbcCli(){
        scanner = new Scanner(System.in);

        while(!end){
            readcommand();
            handlecommand();
        }
    }
    private void readcommand(){

    }

    private void handlecommand(){

    }

}
