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
public class NlpNegotiationClient {
	
	/**
	 * translates from natural language to semantic representation.
	 */
	protected SocketioTranslator<ScoredTranslation> translator;
	
	/**
	 * Handles strategic negotiation.
	 */
	protected NegotiationClient negotiator;
	
	/**
	 * @param domainFile full path to the Genius XML file with the domain data. 
	 * @param serverUrl full URL (http://host:port) of the socket.io game-server that handles the negotiation.
	 * @param gameType name of the game-class to join - from the games available on the game-server.
	 * @param translator - translates from natural language to semantic representation.
	 * @throws MalformedURLException 
	 */
	public NlpNegotiationClient(Domain domain, String negotiationServerUrl, String gameType, String translationServerUrl) throws MalformedURLException {
		this.negotiator = new NegotiationClient(domain, negotiationServerUrl, gameType) {
			/**
			 * This function is called whenever the partner sends a message in natural language.
			 */
			@Override public void onNaturalLanguageMessage(String message) {
				negotiationSocket.emit("message", "Translating '"+message+"'...");
				translator.sendToTranslationServer(message, /*forward=*/true);
			}
		};
		
	
		this.translator=new SocketioTranslator<ScoredTranslation>(translationServerUrl) {
			/**
			 * This function is called whenever the translator returns a semantic representation.
			 */
			@Override public void onTranslation(List<String> results) {
				String message="";
				System.out.println("NlpNegotiationClient received translations: "+StringUtil.join(results, " AND "));
				negotiator.sayToNegotiationServer("I got "+results.size()+" translations.");
				if (results.size()==0) {
					negotiator.sayToNegotiationServer("I didn't understsand your message '"+message+"'. Please say it in other words.");
					return;
				}

				String semantics = StringUtil.join(results, " AND ");
				negotiator.sayToNegotiationServer("I think you meant '"+semantics+"'.");
			}
		};
		
		//translator.sendToTranslationServer("test", /*forward=*/true);
	}

	public void start() throws JSONException, IOException, NegotiatorException {
		negotiator.start();
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
