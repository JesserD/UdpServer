

import java.util.Objects;

public class GameRound {

    private String gameRoundId;
    private String Player1;
    private String player2;

    public GameRound() {
    }

    public GameRound(String gameRoundId, String Player1, String player2) {
        this.gameRoundId = gameRoundId;
        this.Player1 = Player1;
        this.player2 = player2;
    }

    public String getGameRoundId() {
        return this.gameRoundId;
    }

    public void setGameRoundId(String gameRoundId) {
        this.gameRoundId = gameRoundId;
    }

    public String getPlayer1() {
        return this.Player1;
    }

    public void setPlayer1(String Player1) {
        this.Player1 = Player1;
    }

    public String getPlayer2() {
        return this.player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public GameRound gameRoundId(String gameRoundId) {
        setGameRoundId(gameRoundId);
        return this;
    }

    public GameRound Player1(String Player1) {
        setPlayer1(Player1);
        return this;
    }

    public GameRound player2(String player2) {
        setPlayer2(player2);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GameRound)) {
            return false;
        }
        GameRound gameRound = (GameRound) o;
        return Objects.equals(gameRoundId, gameRound.gameRoundId) && Objects.equals(Player1, gameRound.Player1) && Objects.equals(player2, gameRound.player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameRoundId, Player1, player2);
    }

    @Override
    public String toString() {
        return "{" +
            " gameRoundId='" + getGameRoundId() + "'" +
            ", Player1='" + getPlayer1() + "'" +
            ", player2='" + getPlayer2() + "'" +
            "}";
    }

}
