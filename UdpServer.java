
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
            System.out.println("Udp server is waiting for position");
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
        System.out.println("UDP server is waiting for players");
        while (this.player1 == null || this.player2 == null) {
            DatagramPacket receivedPacket = null;
            receivedPacket = receive();
            String receivedData = packetToString(receivedPacket);
            System.out.println("Client " + receivedPacket.getSocketAddress() + ": " + receivedData);

            if (receivedData.contains(ClientServerCommands.JOIN_GAME_REQUEST.toString()) && this.player1 == null) {

                this.player1 = receivedPacket;
                send("player1", receivedPacket);
                System.out.println("player1 is ready");

            } else if (receivedData.contains(ClientServerCommands.JOIN_GAME_REQUEST.toString()) && this.player2 == null
                    && !receivedPacket.getSocketAddress().equals(this.player1.getSocketAddress())) {

                this.player2 = receivedPacket;
                send("player2", receivedPacket);
                System.out.println("player2 is ready");

            } else {
                send("fail", receivedPacket);
                System.out.println("fail sent to " + receivedPacket.getSocketAddress());
            }
        }
        this.isBusy = true;
    }

    private void startNewGameRound() {
        System.out.println("UDP server is waiting for the tanks to be placed");
        while (this.tank1Placed == false || this.tank2Placed == false) {
            DatagramPacket receivedPacket = null;
            receivedPacket = receive();
            String receivedData = packetToString(receivedPacket);
            System.out.println("Client " + receivedPacket.getSocketAddress() + ": " + receivedData);

            if (receivedData.contains(ClientServerCommands.TANK_PLACED.toString())
                    && this.player1.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
                this.tank1Placed = true;
                System.out.println("tank1 placed");

            } else if (receivedData.contains(ClientServerCommands.TANK_PLACED.toString())
                    && this.player2.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
                this.tank2Placed = true;
                System.out.println("tank2 placed");
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

        boolean containsPosition = receivedData.contains(ClientServerCommands.TANK_POSITION.toString())
                || receivedData.contains(ClientServerCommands.PROJECTILE_START_POSITION.toString());

        if (containsPosition && this.player1.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            send(receivedData, this.player2);
            System.out.println("position sent");
        } else if (containsPosition && this.player2.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            send(receivedData, this.player1);
            System.out.println("position sent");
        } else {
            System.out.println("containsPosition " + containsPosition);
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
