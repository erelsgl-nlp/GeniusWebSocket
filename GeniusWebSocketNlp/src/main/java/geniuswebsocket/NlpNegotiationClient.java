package geniuswebsocket;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;

import org.json.JSONException;

import eu.excitementproject.eop.common.utilities.StringUtil;
import ac.biu.nlp.translate.*;
import geniuswebsocket.NegotiationClient;
import negotiator.Domain;
import negotiator.exceptions.NegotiatorException;

/**
 * A socket.io client that negotiates with humans. 
 *
 * @author Erel Segal Halevi
 * @since 2013-02
 */
public class NlpNegotiationClient extends NegotiationClient {
	
	/**
	 * translates from natural language to semantic representation.
	 */
	protected SocketioTranslator<ScoredTranslation> translator;
	
	/**
	 * full URL (http://host:port) of the socket.io server that handles the translations to semantics. 
	 */
	protected String translationServerUrl;
	
	protected boolean debug;
	
	/**
	 * @param domainFile full path to the Genius XML file with the domain data. 
	 * @param serverUrl full URL (http://host:port) of the socket.io game-server that handles the negotiation.
	 * @param gameType name of the game-class to join - from the games available on the game-server.
	 * @param translationServerUrl full URL (http://host:port) of the socket.io server that handles the translations to semantics. 
	 * @throws MalformedURLException 
	 */
	public NlpNegotiationClient(Domain domain, String negotiationServerUrl, String gameType, String translationServerUrl) throws MalformedURLException {
		super(domain, negotiationServerUrl, gameType);
		this.translationServerUrl = translationServerUrl;
		this.translator = newTranslator(translationServerUrl);
	}

	
	/**
	 * This function is called whenever the partner sends a message in natural language.
	 */
	@Override public void onNaturalLanguageMessage(String message) {
		if (message.startsWith("debug=")) {
			if ("debug=1".equals(message)) {
				debug = true;
			} else if ("debug=0".equals(message)) {
				debug = false;
			}
			return;
		}

		if (debug) sayToNegotiationServer("Translating '"+message+"'...");
		translator.sendToTranslationServer(message, /*forward=*/true);
	}
	

	/**
	 * This function is called whenever a new partner joins and starts negotiating with the current client.
	 */
	@Override public NegotiationClient clone() {
		try {
			return new NlpNegotiationClient(domain, serverUrl, gameType, translationServerUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Cannot clone",e);
		}
	}
	
	protected SocketioTranslator<ScoredTranslation> newTranslator(String translationServerUrl) throws MalformedURLException {
		return new SocketioTranslator<ScoredTranslation>(translationServerUrl) {
			
			/**
			 * This function is called whenever the translator returns a semantic representation.
			 */
			@Override public void onTranslation(List<String> results) {
				String message="";
				System.out.println("NlpNegotiationClient received translations: "+StringUtil.join(results, " AND "));
				if (debug) sayToNegotiationServer("I got "+results.size()+" translations.");
				if (results.size()==0) {
					sayToNegotiationServer("I didn't understsand your message '"+message+"'. Please say it in other words.");
					return;
				}

				String semantics = StringUtil.join(results, " AND ");
				sayToNegotiationServer("I think you meant '"+semantics+"'.");
			}
		};
	}

	
	/*
	 * Main program:
	 */
	
	private static String thisClassName = Thread.currentThread().getStackTrace()[1].getClassName();
	
	public static void main(String[] args) throws Exception {
		if (args.length<4) {
			System.err.println("SYNTAX: "+thisClassName+" <path-to-domain-file> <url-of-negotiation-server> <game-type> <url-of-translation-server>");
			System.exit(1);
		}
		java.util.logging.Logger.getLogger("io.socket").setLevel(Level.WARNING);
		new NlpNegotiationClient(new Domain(args[0]), args[1], args[2], args[3]).start();  // Start the first client. It will launch new clients as the need arises.
	}
}
