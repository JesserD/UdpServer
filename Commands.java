public enum Commands {
    HOST_GAME_REQUEST("host "), // + gameRoundId , Client has to send host to main server then join to sub server
    HOST_GAME_RESPONSE("hostRes "), // + Sub server port number
    JOIN_GAME_REQUEST("join "), // + gameRoundId  Client has to send join to both main server and sub server
    JOIN_GAME_RESPONSE("joinRes "), // + Sub server port number
    HOST_GAME_FAIL("fail"),
    JOIN_GAME_FAIL("fail"),
    TANK_PLACED("tank placed"),
    START_ROUND("start round"),
    TANK_POSITION("tank_position"),
    PROJECTILE_START_POSITION("projectile_start_position"), // + directionAngle + x + y + z + projId
    PROJECTILE_HIT_OPPONENT("hit"), // + projectile id
    HP_UPDATE("hp_update"), // hp + tankToBeUpdated + opponent/client
    MAIN_SERVER_PORT("7878");

    private String msg;

    Commands(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}