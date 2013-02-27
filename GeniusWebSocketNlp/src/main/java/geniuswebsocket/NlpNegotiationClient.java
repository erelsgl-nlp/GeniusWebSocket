package geniuswebsocket;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;

import eu.excitementproject.eop.common.utilities.StringUtil;
import ac.biu.nlp.translate.*;
import geniuswebsocket.NegotiationClient;
import negotiator.Domain;

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
	
	/**
	 * Name of the grammar file (relative to the maps folder on the translation server)  
	 */
	protected String targetsFileName;
	
	protected boolean debug;
	
	/**
	 * @param domainFile full path to the Genius XML file with the domain data. 
	 * @param serverUrl full URL (http://host:port) of the socket.io game-server that handles the negotiation.
	 * @param gameType name of the game-class to join - from the games available on the game-server.
	 * @param translationServerUrl full URL (http://host:port) of the socket.io server that handles the translations to semantics. 
	 * @throws MalformedURLException 
	 */
	public NlpNegotiationClient(Domain domain, String negotiationServerUrl, String gameType, String translationServerUrl, String targetsFileName) throws MalformedURLException {
		super(domain, negotiationServerUrl, gameType);
		this.translationServerUrl = translationServerUrl;
		this.translator = newTranslator(translationServerUrl);
		this.targetsFileName = targetsFileName;
	}

	/**
	 * This function is called whenever the partner sends a message in natural language.
	 */
	@Override public void onNaturalLanguageMessage(String message) {
		if (message.startsWith("debug=")) {
			debug = (message.replace("debug=","").equals("1"));
			sayToNegotiationServer("Setting debug to '"+debug+"'");
		} else if (message.startsWith("grammar=")) {
			targetsFileName = message.replace("grammar=","");
			sayToNegotiationServer("Setting grammar to '"+targetsFileName+"'");
		} else {
			if (debug) sayToNegotiationServer("Translating '"+message+"'...");
			try {
				translator.sendToTranslationServer(message, /*forward=*/true, targetsFileName);
			} catch (MalformedURLException e) {
				sayToNegotiationServer("Cannot translate because: "+e);
			}
		}
	}

	/**
	 * This function is called whenever a new partner joins and starts negotiating with the current client.
	 */
	@Override public NlpNegotiationClient clone() {
		try {
			return new NlpNegotiationClient(domain, serverUrl, gameType, translationServerUrl, targetsFileName);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Cannot clone",e);
		}
	}
	
	protected SocketioTranslator<ScoredTranslation> newTranslator(String translationServerUrl) throws MalformedURLException {
		return new SocketioTranslator<ScoredTranslation>(translationServerUrl) {
			
			/**
			 * This function is called whenever the translator returns a semantic representation.
			 */
			@Override public void onTranslation(String text, List<String> translations) {
				if (debug) sayToNegotiationServer("I got "+translations.size()+" translations.");
				if (translations.size()==0) {
					sayToNegotiationServer("What did you mean when you said '"+text+"'? Please say it in other words.");
					return;
				}
				
				String semantics = StringUtil.join(translations, " AND ");
				System.out.println("NlpNegotiationClient received translations: "+semantics);
				try {
					JSONObject action = JsonUtils.deepMerge(translations);
					if (debug) sayToNegotiationServer("I think you mean '"+action+"'.");
					onPartnerOffer(action);
				} catch (JSONException e) {
					sayToNegotiationServer("I think you mean '"+semantics+"' but I can't understand further.");
				}
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
		
		for (String gameType: new String[] {"negochat", "negomenus"}) 
			new NlpNegotiationClient(new Domain(args[0]), args[1], gameType, args[3], "NegotiationGrammarJson.txt").start();  // Start the first client. It will launch new clients as the need arises.
	}
}
