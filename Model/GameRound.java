package Model;

import java.net.DatagramPacket;

public class GameRound {

    public String gameRoundId;
    public DatagramPacket player1;
    public DatagramPacket player2;
    public Tank tank1;
    public Tank tank2;

    public GameRound(String gameRoundId2) {
        this.gameRoundId = gameRoundId2;
        this.player1 = null;
        this.player2 = null;
        this.tank1 = new Tank (100);
        this.tank2 = new Tank (100);
	}

	public String getGameRoundId() {
        return this.gameRoundId;
    }

    public void setGameRoundId(String gameRoundId) {
        this.gameRoundId = gameRoundId;
    }

    public DatagramPacket getPlayer1() {
        return this.player1;
    }

    public void setPlayer1(DatagramPacket player1) {
        this.player1 = player1;
    }

    public DatagramPacket getPlayer2() {
        return this.player2;
    }

    public void setPlayer2(DatagramPacket player2) {
        this.player2 = player2;
    }

    public Tank getTank1() {
        return this.tank1;
    }

    public void setTank1(Tank tank1) {
        this.tank1 = tank1;
    }

    public Tank getTank2() {
        return this.tank2;
    }

    public void setTank2(Tank tank2) {
        this.tank2 = tank2;
    }

    @Override
    public String toString() {
        return "{" +
            " gameRoundId='" + getGameRoundId() + "'" +
            ", player1='" + getPlayer1() + "'" +
            ", player2='" + getPlayer2() + "'" +
            ", tank1='" + getTank1() + "'" +
            ", tank2='" + getTank2() + "'" +
            "}";
    }
    

}
