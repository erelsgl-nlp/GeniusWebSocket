package negotiator.events;

public class LogMessageEvent extends NegotiationEvent {

	private String source;
	private String message;
	
	public LogMessageEvent(Object source, String pSource, String pMessage) {
		super(source);
		source = pSource;
		message = pMessage;
	}

	public String getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	}
}
