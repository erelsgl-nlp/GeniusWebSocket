package geniuswebsocket;
/*
 * socket.io-java-client Test.java
 *
 * Copyright (c) 2012, Enno Boland
 * socket.io-java-client is a implementation of the socket.io protocol in Java.
 * 
 * See LICENSE file for more information
 */
import java.util.Date;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple echo client for testing.
 * 
 * Based on Gottox's socket.io client.
 *
 * @author Erel Segal Halevi
 * @since 2013-02
 */
public class EchoGottox implements IOCallback {
	private SocketIO socket;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new EchoGottox();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EchoGottox() throws Exception {
		socket = new SocketIO();
		socket.connect("http://localhost:4000/", this);
		//socket.connect("http://negochat.azurewebsites.net:80/", this);

		socket.emit("start_session", new JSONObject()
			.put("userid", "Java "+new Date().toString())
			.put("gametype", "menus_humanvshuman")
			.put("role", "Candidate")
			);

		socket.send("Hello! I will echo everything you say.");
	}

	@Override public void onMessage(JSONObject json, IOAcknowledge ack) {
		try {
			System.out.println("Server said:" + json.toString(2));
			socket.send("You just said '"+json.toString(2)+"'");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override public void onMessage(String data, IOAcknowledge ack) {
		System.out.println("Server said: " + data);
		socket.send("You just said '"+data+"'");
	}

	@Override public void onError(SocketIOException socketIOException) {
		System.out.println("an Error occured");
		socketIOException.printStackTrace();
	}

	@Override public void onDisconnect() {
		System.out.println("Connection terminated.");
	}

	@Override public void onConnect() {
		System.out.println("Connection established");
	}

	@Override public void on(String event, IOAcknowledge ack, Object... args) {
		System.out.println("Server triggered event '" + event + "'");
		if (event.equals("message")) {
			JSONObject arg0 = (JSONObject)args[0];
			try {
				if (arg0.get("id").equals("Employer"))
					socket.send("You just said '"+arg0.get("msg")+"'");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
