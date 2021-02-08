
import java.net.*;
import java.util.Scanner;

public class UdpClient {

    public static void main(String[] args) {
        UdpClient client = new UdpClient(7878, "192.168.0.101");
        client.start();
    }

    private int port;
    private InetAddress server;
    private DatagramSocket socket;
    private String request, response;
    private Scanner scanner;

    public UdpClient(int port, String ipAddress) {
        System.out.println("UDP client");
        this.port = port;
        try {
            this.server = InetAddress.getByName(ipAddress);
            System.out.println(server.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1); // Turn of the process if it's not possible to start the client
        }
    }

    public void start() {
        while (true) {
            try {
                System.out.println("UDP client");
                this.socket = new DatagramSocket(7898);
                System.out.println("(input): ");
                request = scan();
                sendToServer(request);
                response = receiveFromServer();
                System.out.println("Server: " + response);
            } catch (Exception ex) {
                System.out.println("Socket error: " + ex.getMessage());
            } finally {
                this.socket.close();
            }
        }
    }

    private void sendToServer(String msg) {
        DatagramPacket packet;
        packet = new DatagramPacket(msg.getBytes(), msg.length(), this.server, this.port);
        try {
            socket.send(packet);
        } catch (Exception ex) {
            System.out.println("Client: packet could not be sent");
        }

    }

    private String receiveFromServer() {
        byte[] buffer = new byte[200];
        DatagramPacket getPacket = new DatagramPacket(buffer, buffer.length, this.server, this.port);
        try {
            socket.receive(getPacket);
        } catch (Exception ex) {
            System.out.println("Client: packet could not be reciveved");
        }
        return packetToString(getPacket);
    }

    private String packetToString(DatagramPacket datagramPacket) {
        return new String(datagramPacket.getData(), 0, datagramPacket.getLength());
    }

    private String scan() {
        scanner = new Scanner(System.in);
        String info = scanner.nextLine();
        return info;
    }
    
}
