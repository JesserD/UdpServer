
import Model.GameRound;
import Model.Projectile;
import java.net.*;
import java.util.List;
import java.util.UUID;

public class UdpServer extends Thread {
    private int port;
    private DatagramSocket socket;
    private final int timeout = 14 * 1000; // 7 seconds
    private boolean isBusy;
    private long lastResponseFromClient;
    private GameRound gameRound;

    public UdpServer(int port, String gameRoundId) {
        this.port = port;
        this.isBusy = false;
        this.lastResponseFromClient = 0;
        this.gameRound = new GameRound(gameRoundId);
    }

    public GameRound getGameRound () {
        return this.gameRound;
    }

    public int getPort () {
        return this.port;
    }

    @Override
    public void run(){
        try {
            this.socket = new DatagramSocket(this.port);
            System.out.println("Sub UDP server is running at port " + this.port);
            connectTwoPlayers();
            startNewGameRound();
            System.out.println("Sub Udp server is waiting for position");
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
        while (this.gameRound.getPlayer1() == null || this.gameRound.getPlayer2() == null) {
            DatagramPacket receivedPacket = null;
            receivedPacket = receive();
            String receivedData = packetToString(receivedPacket);
            System.out.println("(Sub server) Client " + receivedPacket.getSocketAddress() + ": " + receivedData);

            if (receivedData.contains(Commands.JOIN_GAME_REQUEST.toString())
                    && this.gameRound.getPlayer1() == null) {

                this.gameRound.setPlayer1(receivedPacket);
                send("player1", receivedPacket);
                System.out.println("player1 is ready");

            } else if (receivedData.contains(Commands.JOIN_GAME_REQUEST.toString())
                    && this.gameRound.getPlayer2() == null
                    && !receivedPacket.getSocketAddress().equals(this.gameRound.getPlayer1().getSocketAddress())) {

                this.gameRound.setPlayer2(receivedPacket);
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
        while (this.gameRound.getTank1().isPlaced() == false || this.gameRound.getTank2().isPlaced() == false) {
            DatagramPacket receivedPacket = null;
            receivedPacket = receive();
            String receivedData = packetToString(receivedPacket);
            System.out.println("Client " + receivedPacket.getSocketAddress() + ": " + receivedData);

            if (receivedData.contains(Commands.TANK_PLACED.toString())
                    && this.gameRound.getPlayer1().getSocketAddress().equals(receivedPacket.getSocketAddress())) {
                this.gameRound.getTank1().setPlaced(true);
                System.out.println("tank1 placed");

            } else if (receivedData.contains(Commands.TANK_PLACED.toString())
                    && this.gameRound.getPlayer2().getSocketAddress().equals(receivedPacket.getSocketAddress())) {
                this.gameRound.getTank2().setPlaced(true);
                System.out.println("tank2 placed");
            }
            if (this.gameRound.getTank1().isPlaced() && this.gameRound.getTank2().isPlaced()) {
                send(Commands.START_ROUND.toString(), this.gameRound.getPlayer1());
                send(Commands.START_ROUND.toString(), this.gameRound.getPlayer2());
            }
        }
    }

    private void gameLoop() {
        DatagramPacket receivedPacket = null;
        receivedPacket = receive();
        String receivedData = packetToString(receivedPacket);
        System.out.println("Client " + receivedPacket.getSocketAddress() + ": " + receivedData);

        boolean containsPosition = receivedData.contains(Commands.TANK_POSITION.toString())
                || receivedData.contains(Commands.PROJECTILE_START_POSITION.toString());

        if (containsPosition && this.gameRound.player1.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            send(receivedData, this.gameRound.player2);
            System.out.println("position sent");
        } else if (containsPosition
                && this.gameRound.player2.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            send(receivedData, this.gameRound.player1);
            System.out.println("position sent");
        } else {
        }

        boolean containsProjStartPos = receivedData.contains(Commands.PROJECTILE_START_POSITION.toString());

        if (containsProjStartPos
                && this.gameRound.player1.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            this.gameRound.tank1.getProjectiles().add(createNewProjectileObject(receivedData));
            send(receivedData, this.gameRound.player2);
            System.out.println("projectile start position sent to player2");
        } else if (containsProjStartPos
                && this.gameRound.player2.getSocketAddress().equals(receivedPacket.getSocketAddress())) {
            this.gameRound.tank2.getProjectiles().add(createNewProjectileObject(receivedData));
            send(receivedData, this.gameRound.player1);
            System.out.println("projectile start position sent to player1");
        } else {
        }

        boolean containsHit = receivedData.contains(Commands.PROJECTILE_HIT_OPPONENT.toString());

        if (containsHit && listContainsProjectile(this.gameRound.tank1.getProjectiles(), receivedData.split(" ")[1])) {
            this.gameRound.tank2.setHealth(this.gameRound.tank2.getHealth()-25);
            String responseToPlayer1 = Commands.HP_UPDATE.toString() + " " + this.gameRound.tank2.getHealth() + " opponent";
            String responseToPlayer2 = Commands.HP_UPDATE.toString() + " " + this.gameRound.tank2.getHealth() + " client";
            send(responseToPlayer1, this.gameRound.player1);
            send(responseToPlayer2, this.gameRound.player2);
        } else if (containsHit && listContainsProjectile(this.gameRound.tank2.getProjectiles(), receivedData.split(" ")[1])) {
            this.gameRound.tank1.setHealth(this.gameRound.tank1.getHealth()-25);
            String responseToPlayer2 = Commands.HP_UPDATE.toString() + " " + this.gameRound.tank1.getHealth() + " opponent";
            String responseToPlayer1 = Commands.HP_UPDATE.toString() + " " + this.gameRound.tank1.getHealth() + " client";
            send(responseToPlayer2, this.gameRound.player2);
            send(responseToPlayer1, this.gameRound.player1); 
        } else if (containsHit) {
            boolean firstCondition = listContainsProjectile(this.gameRound.tank2.getProjectiles(), receivedData.split(" ")[1]);
            boolean secondCondition = listContainsProjectile(this.gameRound.tank1.getProjectiles(), receivedData.split(" ")[1]);
            System.out.println("firstCondition " + firstCondition);
            System.out.println("secondCondition " + secondCondition);
        }
    }

    private boolean listContainsProjectile (List<Projectile> list, String Id) {
        for (Projectile projectile : list) {
            System.out.println(projectile.getId());
            if (projectile.getId().equals(Id) || projectile.getId().contains(Id)){
                return true;
            }
        }
        return false;
    }

    private Projectile createNewProjectileObject(String receivedData) {
        float directionAngle = Float.parseFloat(receivedData.split(" ")[1]);
        float x = Float.parseFloat(receivedData.split(" ")[2]);
        float y = Float.parseFloat(receivedData.split(" ")[3]);
        float z = Float.parseFloat(receivedData.split(" ")[4]);
        String ProjId = receivedData.split(" ")[5];
        return new Projectile(directionAngle, x, y, z, ProjId);
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
