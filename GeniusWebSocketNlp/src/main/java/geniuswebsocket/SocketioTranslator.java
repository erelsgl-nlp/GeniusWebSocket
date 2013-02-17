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
 * An asynchronous translator that uses a SocketIO connection.
 *
 * @author Erel Segal Halevi
 * @since 2013-02-17
 */
public abstract class SocketioTranslator<TransformationType extends Translation> implements IOCallback {
	
	/**
	 * socket for connecting to the SocketIO translation server:
	 */
	protected SocketIO translationSocket;

	public SocketioTranslator(String serverUrl) throws MalformedURLException {
		translationSocket = new SocketIO();
		translationSocket.connect(serverUrl, this);
	}

	public void sendToTranslationServer(String input, boolean forward) {
		try {
			translationSocket.emit("translate", new JSONObject()
				.put("text", input)
				.put("forward", forward)
				.put("targetsFileName", "")
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
		System.out.println("SocketioTranslator receives event '" + event + "' arg0="+args[0]);
		if ("translation".equals(event)) {
			JSONArray results = (JSONArray)args[0];
			try {
				onTranslation(jsonArrayToJavaList(results));
			} catch (JSONException e) {
				throw new RuntimeException("Cannot handle translation event",e);
			}
		}
	}

	public abstract void onTranslation(List<String> results);



	/**
	 * demo program
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
