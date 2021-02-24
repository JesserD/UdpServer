import java.net.*;
import java.util.ArrayList;

public class MainServer {
    private ArrayList<UdpServer> servers;
    private int port;
    private DatagramSocket socket;

    public MainServer(int port) {
        this.servers = new ArrayList<>();
        this.port = port;
    }

    public void service() {
        try {
            this.socket = new DatagramSocket(this.port);
            System.out.println("Main UDP server is running at port " + this.port);
            while (true) {
                DatagramPacket receivedPacket = receive();
                String receivedData = packetToString(receivedPacket);
                System.out.println("Client " + receivedPacket.getSocketAddress() + ": " + receivedData);

                if (receivedData.contains(Commands.HOST_GAME_REQUEST.toString())
                        && !isGameRoundIdUsed(receivedData.split(" ")[1])) {
                    int subServerPortNumber = this.port + this.servers.size() + 1;
                    this.servers.add(new UdpServer(subServerPortNumber, receivedData.split(" ")[1]));
                    this.servers.get(servers.size() - 1).start();;
                    String response = Commands.HOST_GAME_RESPONSE.toString() + String.valueOf(subServerPortNumber);
                    send(response, receivedPacket);
                } else if (receivedData.contains(Commands.JOIN_GAME_REQUEST.toString()) && isGameRoundIdUsed(receivedData.split(" ")[1])) {
                    
                    for (int i = 0; i < this.servers.size(); i++) {
                        if (this.servers.get(i).getGameRound().getGameRoundId().equals(receivedData.split(" ")[1])) {
                            String response = Commands.JOIN_GAME_RESPONSE.toString() + String.valueOf(this.servers.get(i).getPort());
                            System.out.println(response);
                            send(response, receivedPacket);
                            break;
                        }
                    }
                } else {
                    send(Commands.HOST_GAME_FAIL.toString(), receivedPacket);
                }
            }

        } catch (Exception ex) {
            System.out.println("Socket error: " + ex.getCause().toString());
            System.exit(1); // close if the server can't start
        } finally {
            this.socket.close();
        }
    }

    private boolean isGameRoundIdUsed(String id) {
        for (UdpServer udpServer : this.servers) {
            System.out.println(udpServer.getGameRound().getGameRoundId());
            if (udpServer.getGameRound().getGameRoundId().equals(id))
                return true;
        }
        return false;
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

}
