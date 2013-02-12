package geniuswebsocket;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.clwillingham.socket.io.*;

/**
 * A simple echo client for testing.
 * 
 * Based on cliwillingham's socket.io client.
 *
 * @author Erel Segal Halevi
 * @since 2013-02
 */
public class EchoClwillingham  {
	public static void main(String[] args) throws IOException {
		final IOSocket socket = new IOSocket("http://localhost:4000",null);
		socket.setCallback(new MessageCallback() {
			@Override public void onConnect() {
				System.out.println("Connection established");
				try {
					socket.emit("start_session", new JSONObject()
					.put("userid", "Java123")
					.put("gametype", "menus_humanvshuman")
					.put("role", "Candidate")
					);
					
					socket.send("Hello! I will echo everything you say.");
				} catch (IOException | JSONException e) {
					e.printStackTrace();
				}
			}

			@Override public void on(String event, JSONObject... args) {
				System.out.println("Server triggered event '" + event + "'");
				if (event.equals("message")) {
					JSONObject arg0 = (JSONObject)args[0];
					try {
						if (arg0.get("id").equals("Employer"))
							socket.send("You just said '"+arg0.get("msg")+"'");
					} catch (JSONException | IOException e) {
						e.printStackTrace();
					}
				}/**
				 * A simple echo client for testing.
				 * 
				 * Based on Gottox's socket.io client.
				 *
				 * @author Erel Segal Halevi
				 * @since 2013-02
				 */

			}

			@Override public void onMessage(String message) {
				System.out.println("Server said: " + message);
				try {
					socket.send("You just said '"+message+"'");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override public void onMessage(JSONObject json) {
				try {
					System.out.println("Server said:" + json.toString(2));
					socket.send("You just said '"+json.toString(2)+"'");
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}
			}

			@Override public void onDisconnect() {
				System.out.println("Connection terminated.");
			}
		});
		
		socket.connect();
	}
}
