package fr.raksrinana.rsndiscord.api.irc;

import fr.raksrinana.rsndiscord.log.Log;
import lombok.Getter;
import lombok.NonNull;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static java.util.Objects.nonNull;

public class IRCClient implements Closeable{
	public static final int DEFAULT_TIMEOUT = 600000;
	private final String host;
	private final int port;
	@Getter
	private final Set<String> joinedChannels;
	@Getter
	private final Collection<IIRCListener> listeners;
	private final IIRCMessageBuilder ircMessageBuilder;
	private String pass;
	private Socket socket;
	private PrintWriter socketWriter;
	@Getter
	private boolean connected;
	private IRCReaderThread ircReader;
	
	public IRCClient(@NonNull final String host, final int port, @NonNull final IIRCMessageBuilder ircMessageBuilder){
		this.host = host;
		this.port = port;
		this.pass = null;
		this.connected = false;
		this.joinedChannels = new HashSet<>();
		this.listeners = new HashSet<>();
		this.ircMessageBuilder = ircMessageBuilder;
	}
	
	public void connect() throws IOException{
		this.socket = new Socket(this.host, this.port);
		this.socket.setSoTimeout(DEFAULT_TIMEOUT);
		this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
		this.ircReader = new IRCReaderThread(this, this.ircMessageBuilder, this.socket.getInputStream());
		this.ircReader.start();
		if(nonNull(this.pass)){
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
	}
	
	public void timedOut(){
		final var ls = new ArrayList<>(this.listeners);
		ls.forEach(IIRCListener::timedOut);
	}
	
	public void addEventListener(@NonNull final IIRCListener ircListener){
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
