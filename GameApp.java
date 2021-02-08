

import java.io.IOException;

public class GameApp {

	public static void main(String[] args) throws IOException {
		UdpServer server = new UdpServer();
		server.service();
	}

} 