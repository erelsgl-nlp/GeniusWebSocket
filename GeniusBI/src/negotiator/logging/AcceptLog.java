package negotiator.logging;

import negotiator.actions.*;

import javax.xml.bind.annotation.*;

import negotiator.analysis.*;

@XmlRootElement
public class AcceptLog extends ActionLog {
	
	
	private BidPoint mBid;
	
	public AcceptLog(){
		super();
	}
	
	public AcceptLog(AcceptOrReject tAccept){
		super(tAccept);
	}
	
	public AcceptLog(AcceptOrReject tAccept, BidPoint tBid){
		super(tAccept);
		mBid = tBid;
	}
	@XmlElement(name="Final_Bid")
	public BidPoint getMBid() {
		return mBid;
	}

	public void setMBid(BidPoint bid) {
		mBid = bid;
	}
	
}
