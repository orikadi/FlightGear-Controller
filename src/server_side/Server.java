package server_side;

public interface Server {
	public void start(ClientHandler ch);
	public void stop();
}
