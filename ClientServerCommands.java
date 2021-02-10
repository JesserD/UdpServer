public enum ClientServerCommands {
    HOST_GAME_REQUEST("host "),
    JOIN_GAME_REQUEST("join "),
    HOST_GAME_FAIL("fail"),
    JOIN_GAME_FAIL("fail"),
    TANK_PLACED ("tank placed"),
    START_ROUND("start round"),
    TANK_POSITION("tank position "),
    SERVER_PORT("7878");

    private String msg;
    ClientServerCommands(String msg)
    {
        this.msg = msg;
    }

    @Override
    public String toString()
    {
        return msg;
    }
}