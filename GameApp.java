

import java.io.IOException;

public class GameApp {

	public static void main(String[] args) throws IOException {
		MainServer mainServer = new MainServer(Integer.parseInt(Commands.MAIN_SERVER_PORT.toString()));
		mainServer.service();
	}





} 