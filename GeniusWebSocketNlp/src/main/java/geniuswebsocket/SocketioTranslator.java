package geniuswebsocket;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ac.biu.nlp.translate.*;

/**
 * A client to a translation server. To use, create a sub-class MyTranslator and override "onTranslation". Then:
 * 
 * <p>MyTranslator t = new MyTranslator("http://localhost:4000"); // connect to socket.io server
 * <p>t.sendToTranslationServer("hello", true, "targets.txt");    // send a translation-request to the server
 * 
 * When the server sends a translation, it will be sent to the method "onTranslation".
 *
 * @author Erel Segal Halevi
 * @since 2013-02-17
 */
public abstract class SocketioTranslator<TransformationType extends Translation> implements IOCallback {
	
	/**
	 * socket for connecting to the SocketIO translation server:
	 */
	protected SocketIO translationSocket;
	
	protected String serverUrl;

	public SocketioTranslator(String serverUrl)   {
		this.serverUrl = serverUrl;
		translationSocket = null;
	}

	public void sendToTranslationServer(String input, boolean forward, String targetsFileName)  throws MalformedURLException {
		try {
			if (translationSocket==null || !translationSocket.isConnected()) {
				translationSocket = new SocketIO();
				translationSocket.connect(serverUrl, this);
			}
			translationSocket.emit("translate", new JSONObject()
				.put("text", input)
				.put("forward", forward)
				.put("targetsFileName", targetsFileName)
				.put("numOfThreads", /*keep current number of threads*/ 0)
				);
		} catch (JSONException e) {
			throw new RuntimeException("Cannot translate", e);
		}
	}

	@Override public void onConnect() {
		System.out.println("SocketioTranslator Connection established.");
	}

	@Override public void onMessage(JSONObject arg0, IOAcknowledge ack) {
		try {
			System.out.println("SocketioTranslator receives an object: " + arg0.toString(2));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override public void onMessage(String data, IOAcknowledge ack) {
		System.out.println("SocketioTranslator receives a message: " + data);
	}

	@Override public void onError(SocketIOException socketIOException) {
		System.out.println("SocketioTranslator receives an error:");
		socketIOException.printStackTrace();
	}

	@Override public void onDisconnect() {
		System.out.println("SocketioTranslator Connection terminated.");
	}

	/**
	 * @see http://stackoverflow.com/a/3395811/827927
	 */
	public static List<String> jsonArrayToJavaList(JSONArray jsonArray) throws JSONException {
		List<String> list = new ArrayList<String>();     
		for (int i=0;i<jsonArray.length();i++) 
			list.add(jsonArray.get(i).toString());
		return list;
	}

	@Override public void on(String event, IOAcknowledge ack, Object... args) {
		if ("translation".equals(event)) {
			System.out.println("SocketioTranslator receives event '" + event + "' arg0="+args[0]);
			JSONObject result = (JSONObject)args[0];
			try {
				String text = result.getString("text");
				JSONArray translations = result.getJSONArray("translations");
				onTranslation(text, jsonArrayToJavaList(translations));
			} catch (JSONException e) {
				throw new RuntimeException("Cannot handle translation event",e);
			}
		}
	}

	public abstract void onTranslation(String text, List<String> translations);



	/**
	 * demo program
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
