package eit.cli;

import java.io.IOException;
import java.util.Arrays;

import edu.fra.uas.oop.Terminal;
import eit.host.Client;
import eit.host.Server;
import eit.host.exception.HostException;
import eit.linecode.CodeTable;
import eit.linecode.Decoder;
import eit.linecode.Encoder;
import eit.linecode.Utils;

/**
 * The Main Class
 * <p>
 * this Class is used to manage the input and output Parameters
 * of encoding and decoding using the Terminal Class.
 */
public class Main {
    /**
     * <p>
     * <p>
     * this is the main method of Program. the Program recognizes different Command.
     * The start command is used to initialise the client and server objects.
     * The objects have to be initialised before a connection can be established
     * between the client and the server.
     * The connect command is used to establish a connection between client and server.
     * Without this command, the client cannot send dataframes to the server.
     * The send  command in combination of the amount of bytes to sent  is used
     * to transfer random data with a certain length from the client to the server
     * The disconnect command is used to terminate the connection between client and server.
     * if the received command  is enter, the server displays the data it has
     * received from the server and clears its buffer.
     * The quit command is used to terminate the program
     *
     * @param args argument
     */
    public static void main(String[] args) {

        Server server = null;
        Client client = null;
        CodeTable codeTable = new CodeTable();
        Encoder encoder = new Encoder(codeTable);
        Decoder decoder = new Decoder(codeTable);
        label:
        while (true) {
            String[] input = Terminal.readLine().split(" ", 2);
            switch (input[0]) {
                case "start":
                    if (server == null) {
                        try {
                            Terminal.printLine("Client and Server started!");
                            server = new Server(encoder, decoder);
                            client = new Client(encoder, decoder);
                        } catch (IOException e) {
                            Terminal.printError(e.toString());
                        }
                    } else {
                        Terminal.printError("Already initialized");
                    }
                    break;
                case "connect":
                    try {
                        server.start();
                        if (client.connect()) {
                            Terminal.printLine("Connected!");
                        } else {
                            Terminal.printError("Connection failed");
                        }
                    } catch (IOException e) {
                        Terminal.printError(e.toString());
                    } catch (NullPointerException e) {
                        Terminal.printError("Not initialized");
                    }
                    break;
                case "send":
                    try {
                        if (client != null && server != null) {
                            int number = Integer.parseInt(input[1]);
                            byte[] bytes = Utils.getRandomBytes(number);
                            Terminal.printLine("send " + number + " bytes");
                            Terminal.printLine(Arrays.toString(bytes));
                            client.sendData(bytes);
                        } else {
                            Terminal.printError("Not initialized");
                        }
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        Terminal.printError("Please type in a number");
                    } catch (HostException e) {
                        Terminal.printError(e.toString());
                    }
                    break;
                case "received":
                    try {
                        byte[] bytes = server.read();
                        Terminal.printLine("received " + bytes.length + " bytes");
                        Terminal.printLine(Arrays.toString(bytes));
                    } catch (NullPointerException e) {
                        Terminal.printError("Not Initialzed");
                    }
                    break;
                case "disconnect":
                    if (server != null) {
                        try {
                            if (client.disconnect()) {
                                Terminal.printLine("Disconnected!");
                            } else {
                                Terminal.printLine("Could not disconnected check code");
                            }
                        } catch (IOException e) {
                            Terminal.printError(e.toString());
                        }
                    } else {
                        Terminal.printError("Not initialized");
                    }
                    break;
                case "quit":
                    System.exit(0);
                    break label;
                default:
                    Terminal.printError("unknown command");
                    break;
            }
        }
    }
}
