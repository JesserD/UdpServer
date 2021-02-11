
import java.net.*;

public class UdpServer {
    private int port;
    private DatagramSocket socket;
    private final int timeout = 14 * 1000; // 7 seconds
    private boolean isBusy;
    private long lastResponseFromClient;
    private DatagramPacket player1;
    private DatagramPacket player2;
    private boolean tank1Placed;
    private boolean tank2Placed;

    public UdpServer() {
        this.port = Integer.parseInt(ClientServerCommands.SERVER_PORT.toString());
        this.isBusy = false;
        this.lastResponseFromClient = 0;
        this.player1 = null;
        this.player2 = null;
        this.tank1Placed = false;
        this.tank2Placed = false;
    }

    public void service() {
        try {
            this.socket = new DatagramSocket(this.port);
            System.out.println("UDP server running at port " + this.port);
            connectTwoPlayers();
            startNewGameRound();
            while (true) {
                gameLoop();
            }

        } catch (Exception ex) {
            System.out.println("Socket error: " + ex.getCause().toString());
            System.exit(1); // close if the server can't start
        } finally {
            this.socket.close();
        }
    }

    private void connectTwoPlayers() {
        while (this.player1 == null || this.player2 == null) {
            System.out.println("UDP server is waiting for players");
            DatagramPacket receivedPacket = null;
            receivedPacket = receive();
            String receivedData = packetToString(receivedPacket);
            System.out.println("Client " + receivedPacket.getSocketAddress() + ": " + receivedData);

            if (receivedData.contains(ClientServerCommands.JOIN_GAME_REQUEST.toString()) && this.player1 == null) {

                this.player1 = receivedPacket;
                send("player1", receivedPacket);

            } else if (receivedData.equalsIgnoreCase(ClientServerCommands.JOIN_GAME_REQUEST.toString())
                    && this.player2 == null
                    && !receivedPacket.getSocketAddress().equals(this.player1.getSocketAddress())) {

                this.player2 = receivedPacket;
                send("player2", receivedPacket);

            } else {
                send("fail", receivedPacket);
            }
        }
        this.isBusy = true;
    }

    private void startNewGameRound() {
        while (this.tank1Placed || this.tank2Placed) {
            System.out.println("UDP server is waiting for the tanks to be placed");
            DatagramPacket receivedPacket = null;
            receivedPacket = receive();
            String receivedData = packetToString(receivedPacket);
            System.out.println("Client " + receivedPacket.getSocketAddress() + ": " + receivedData);

            if (receivedData.contains(ClientServerCommands.TANK_PLACED.toString())
                    && this.player1.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
                this.tank1Placed = true;

            } else if (receivedData.contains(ClientServerCommands.TANK_PLACED.toString())
                    && this.player2.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
                this.tank2Placed = true;
            }
            if (this.tank1Placed && this.tank2Placed) {
                send(ClientServerCommands.START_ROUND.toString(), this.player1);
                send(ClientServerCommands.START_ROUND.toString(), this.player2);
            }
        }
    }

    private void gameLoop() {
        DatagramPacket receivedPacket = null;
        receivedPacket = receive();
        String receivedData = packetToString(receivedPacket);
        System.out.println("Client " + receivedPacket.getSocketAddress() + ": " + receivedData);

        if (receivedData.contains(ClientServerCommands.TANK_POSITION.toString())
                && this.player1.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            send(receivedData, this.player2);
        } else if (receivedData.contains(ClientServerCommands.TANK_POSITION.toString())
                && this.player2.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            send(receivedData, this.player1);
        }

        else if (receivedData.contains(ClientServerCommands.PROJECTILE_START_POSITION.toString())
                && this.player1.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            send(receivedData, this.player2);
        } else if (receivedData.contains(ClientServerCommands.PROJECTILE_START_POSITION.toString())
                && this.player2.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            send(receivedData, this.player1);
        }
    }

    private DatagramPacket receive() {
        byte[] buffer = new byte[200];
        DatagramPacket tempPacket = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(tempPacket);
        } catch (Exception ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }

        return tempPacket;
    }

    private void send(String msg, DatagramPacket client) {
        DatagramPacket send = new DatagramPacket(msg.getBytes(), msg.length(), client.getAddress(), client.getPort());
        try {
            this.socket.send(send);
        } catch (Exception ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private String packetToString(DatagramPacket datagramPacket) {
        return new String(datagramPacket.getData(), 0, datagramPacket.getLength());
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
        } catch (NullPointerException ex) {
            return false;
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
