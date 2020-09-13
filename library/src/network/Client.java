package network;

import io.MenuHelper;
import network.packet.Packet;
import network.packet.PacketType;
import sale.menu.Menu;
import util.RuntimeHelper;

import java.io.*;
import java.net.*;
import java.time.LocalTime;

/**
 * Simple Client utility class.
 * @author Jordan Gray
 * @since 0.1.0
 * @version 1.1
 */
public final class Client {

    /**
     * Attempts to open a connection with the server, send a packet, and get a response.
     * @return The server's response.
     * @throws IOException If the connections could not be esablished.
     */
    public static Packet sendToServer(Packet toSend) throws IOException {
        Socket socket = new Socket(Server.DEFAULT_IP, Server.PORT);                                                     // Create socket connected to the server's

        // Connected to server, create stream.
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


        out.writeObject(toSend);                                                                                        // Send packet

        Packet response = null;                                                                                         // Get response
        try {
            response = (Packet) in.readObject();                                                                        // Read response from server
            assert response != null;                                                                                    // Assert response recieved
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();                                                                                        // Response was not found, or was not a valid packet object.
        } // Response was not a packet object

        RuntimeHelper.log("[Client]", "[" + LocalTime.now().format(RuntimeHelper.formatter) + "] Sent " + toSend.type() + ", Recieved " + response.type() +       // Log entire communication.
                ((response.metaMessage == null) ? "" : "; " + response.metaMessage)                                     // If no message, append nothing. Else format and append.
                );

        // Teardown
        in.close();
        out.close();
        socket.close();
        return response;
    }

    /**
     * Debug entry only.
     * Sends an empty packet for every packet type available to the client.
     * @param args Commandline args - unused.
     * @throws IOException If an io error occoured whilst creating or using the connection.
     */
    public static void main(String[] args) throws IOException {
        // Test of getting menu from the server, over the network.
        MenuHelper.menu =                                                   // Set client menu to response from server
                (Menu)                                                      // Cast serialized response to a Menu class instance
                        sendToServer(                                       // Send request to server;
                                new Packet(PacketType.MENU_REQUEST,         // Request is a menu request
                                        "menu plz, bitch"))    // Optional message about request
                                .getPacketData();                           // Get serialized data from the server's response, to cast to Menu and store in client.



        // Test of repeatidly sending every available packet type.
        while (true)
            for (PacketType e : PacketType.values())
                sendToServer(new Packet(e));
    }

    /**
     * Tests connection between between this client and a network server.
     *
     * Broadcasts a 'PING', expecting to connect and receive an 'ACKNOWLEDGE' in response.
     *
     * @throws IOException If this client fails to connnect, send a packet, and recieve the expected response in a compatible form.
     * @apiNote serialization versions for the class 'network.packet.Packet' between the client and server implementations
     * must match for serialization to be valid.
     */
    public static boolean assertConnection() {
        RuntimeHelper.log(Client.class, "[CONFIG] Validating server connection..");
        try{
            if (Client.sendToServer(new Packet(PacketType.PING)).type() != PacketType.ACKNOWLEDGE) throw new IOException("Did not receive a valid ping response.");
            else {
                RuntimeHelper.log(Client.class, "[CONFIG] Valid!");
                return true;
            }
        } catch (Exception e) {
            RuntimeHelper.log(Client.class, "[CONFIG] Connection not valid!");
            RuntimeHelper.alertFailiure("[CONFIG] Failed to connect to the server, ",e);
            return false;
        }
    }
}























