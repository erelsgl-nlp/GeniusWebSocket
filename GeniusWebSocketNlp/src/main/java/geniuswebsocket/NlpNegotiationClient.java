package geniuswebsocket;
import java.util.Arrays;
import java.util.logging.Level;

import eu.excitementproject.eop.common.utilities.SimpleLogInitializer;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

import ac.biu.nlp.entailment.impl.biutee.GlobalBiuteeSystemInitialization;
import ac.biu.nlp.translate.*;
import ac.biu.nlp.translate.impl.entailment.BiuteeGrammarTranslator;

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
	protected Translator<ScoredTranslation> translator;
	
	/**
	 * @param domainFile full path to the Genius XML file with the domain data. 
	 * @param serverUrl full URL (http://host:port) of the socket.io game-server that handles the negotiation.
	 * @param gameType name of the game-class to join - from the games available on the game-server.
	 * @param translator - translates from natural language to semantic representation.
	 */
	public NlpNegotiationClient(Domain domain, String serverUrl, String gameType, Translator<ScoredTranslation> translator) {
		super(domain, serverUrl, gameType);
		this.translator=translator;
	}

	/**
	 * This function is called whenever the partner sends a message in natural language.
	 */
	@Override public void onNaturalLanguageMessage(String message) {
		ScoredTranslation translations = translator.translate(message, /*forward=*/true);
		
		if (translations.size()==0) {
			socket.emit("message", "I didn't understsand your message '"+message+"'. Please say it in other words.");
			return;
		}
		String semantics = StringUtil.join(Arrays.asList(translations.results()), " AND ");
		socket.emit("message", "I think you meant '"+semantics+"'.");
		return;
	}
	
	/*
	 * Main program:
	 */
	
	private static String thisClassName = Thread.currentThread().getStackTrace()[1].getClassName();
	
	public static void main(String[] args) throws Exception {
		if (args.length<3) {
			System.err.println("SYNTAX: "+thisClassName+" <path-to-domain-file> <url-of-negotiation-server> <game-type>");
			System.exit(1);
		}
		java.util.logging.Logger.getLogger("io.socket").setLevel(Level.WARNING);
		
		TranslatorFactory.INSTANCE.registerClass("BiuteeGrammarTranslator", BiuteeGrammarTranslator.class);
		SimpleLogInitializer.init();
		
		ConfigurationFile configurationFile = new ConfigurationFile("excitement_translate.xml");
		configurationFile.setExpandingEnvironmentVariables(true);
	
		ConfigurationParams globalBiuteeParams = configurationFile.getModuleConfiguration("biutee");
		ConfigurationParams grammarTranslatorParams = configurationFile.getModuleConfiguration("biutee grammar translator");
		GlobalBiuteeSystemInitialization.init(globalBiuteeParams);

		Translator<ScoredTranslation> translator = new BiuteeGrammarTranslator(grammarTranslatorParams);
		new NlpNegotiationClient(new Domain(args[0]), args[1], args[2], translator).start();  // Start the first client. It will launch new clients as the need arises.
	}
}
