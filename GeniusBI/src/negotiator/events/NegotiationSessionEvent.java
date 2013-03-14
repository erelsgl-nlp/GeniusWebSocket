package negotiator.events;

import negotiator.protocol.Protocol;


public class NegotiationSessionEvent extends NegotiationEvent {
	private Protocol session;
	
	public NegotiationSessionEvent(Object source, Protocol session) {
		super(source);
		this.session = session;
	}
	public Protocol getSession() {
		return session;
	}

}
