package fr.raksrinana.rsndiscord.utils.irc;

import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class IRCClient implements Closeable{
	public static final int DEFAULT_TIMEOUT = 600000;
	private final String host;
	private final int port;
	@Getter
	private final Set<String> joinedChannels;
	@Getter
	private final Collection<IRCListener> listeners;
	private String pass;
	private Socket socket;
	private PrintWriter socketWriter;
	@Getter
	private boolean connected;
	private IRCReaderThread ircReader;
	
	public IRCClient(@NonNull final String host, final int port){
		this.host = host;
		this.port = port;
		this.pass = null;
		this.connected = false;
		this.joinedChannels = new HashSet<>();
		this.listeners = new HashSet<>();
	}
	
	public void connect() throws IOException{
		this.socket = new Socket(this.host, this.port);
		this.socket.setSoTimeout(DEFAULT_TIMEOUT);
		this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
		this.ircReader = new IRCReaderThread(this, this.socket.getInputStream());
		this.ircReader.start();
		if(Objects.nonNull(this.pass)){
			this.sendMessage(String.format("PASS %s", this.pass));
			Log.getLogger(null).info("Using pass to connect to {}:{}", this.host, this.port);
		}
		this.connected = true;
		Log.getLogger(null).info("IRC connection initialized with {}:{}", this.host, this.port);
	}
	
	public void sendMessage(@NonNull final String message){
		this.socketWriter.println(message);
	}
	
	public void joinChannel(@NonNull final String channel){
		if(this.isConnected()){
			this.sendMessage(String.format("JOIN %s", channel));
			this.joinedChannels.add(channel);
			Log.getLogger(null).info("Joined channel {} on {}:{}", channel, this.host, this.port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	@Override
	public void close() throws IOException{
		if(this.isConnected()){
			this.sendMessage("QUIT");
			this.socketWriter.close();
			this.ircReader.close();
			this.socket.close();
			Log.getLogger(null).info("Connection IRC closed with {}:{}", this.host, this.port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	public void timedOut(){
		final var ls = new ArrayList<>(this.listeners);
		ls.forEach(l -> TwitchIRC.disconnect(l.getGuild(), l.getUser()));
		ls.forEach(l -> {
			try{
				TwitchIRC.connect(l.getGuild(), l.getUser());
			}
			catch(final IOException e){
				Log.getLogger(null).warn("Failed to reconnect {} to user {}", l.getGuild(), l.getUser());
			}
		});
	}
	
	public void addEventListener(@NonNull final IRCListener ircListener){
		this.listeners.add(ircListener);
	}
	
	public void leaveChannel(@NonNull final String channel){
		if(this.isConnected()){
			this.sendMessage(String.format("PART %s", channel));
			this.joinedChannels.remove(channel);
			Log.getLogger(null).info("Left channel {} on {}:{}", channel, this.host, this.port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	public void setNick(@NonNull final String nickname){
		if(this.isConnected()){
			this.sendMessage(String.format("NICK %s", nickname));
			Log.getLogger(null).info("Set nick to {} on {}:{}", nickname, this.host, this.port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	public void setSecureKeyPassword(@NonNull final String pass){
		this.pass = pass;
	}
}
