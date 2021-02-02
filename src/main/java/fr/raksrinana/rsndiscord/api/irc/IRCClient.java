package fr.raksrinana.rsndiscord.api.irc;

import fr.raksrinana.rsndiscord.log.Log;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
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
	
	public IRCClient(@NotNull String host, int port, @NotNull IIRCMessageBuilder ircMessageBuilder){
		this.host = host;
		this.port = port;
		this.ircMessageBuilder = ircMessageBuilder;
		pass = null;
		connected = false;
		joinedChannels = new HashSet<>();
		listeners = new HashSet<>();
	}
	
	public void connect() throws IOException{
		socket = new Socket(host, port);
		socket.setSoTimeout(DEFAULT_TIMEOUT);
		socketWriter = new PrintWriter(socket.getOutputStream(), true);
		ircReader = new IRCReaderThread(this, ircMessageBuilder, socket.getInputStream());
		ircReader.start();
		if(nonNull(pass)){
			sendMessage("PASS " + pass);
			Log.getLogger(null).info("Using pass to connect to {}:{}", host, port);
		}
		connected = true;
		Log.getLogger(null).info("IRC connection initialized with {}:{}", host, port);
	}
	
	public void sendMessage(@NotNull String message){
		socketWriter.println(message);
	}
	
	public void joinChannel(@NotNull String channel){
		if(isConnected()){
			sendMessage("JOIN " + channel);
			joinedChannels.add(channel);
			Log.getLogger(null).info("Joined channel {} on {}:{}", channel, host, port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	@Override
	public void close() throws IOException{
		if(isConnected()){
			sendMessage("QUIT");
			socketWriter.close();
			ircReader.close();
			socket.close();
			Log.getLogger(null).info("Connection IRC closed with {}:{}", host, port);
		}
	}
	
	public void timedOut(){
		var ls = new ArrayList<>(listeners);
		ls.forEach(IIRCListener::timedOut);
	}
	
	public void addEventListener(@NotNull IIRCListener ircListener){
		listeners.add(ircListener);
	}
	
	public void leaveChannel(@NotNull String channel){
		if(isConnected()){
			sendMessage("PART " + channel);
			joinedChannels.remove(channel);
			Log.getLogger(null).info("Left channel {} on {}:{}", channel, host, port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	public void setNick(@NotNull String nickname){
		if(isConnected()){
			sendMessage("NICK " + nickname);
			Log.getLogger(null).info("Set nick to {} on {}:{}", nickname, host, port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	public void setSecureKeyPassword(@NotNull String pass){
		this.pass = pass;
	}
}
