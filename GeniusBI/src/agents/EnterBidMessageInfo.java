/*
 * EnterBidDialog.java
 *
 * Created on November 16, 2006, 10:18 AM
 */

package agents;

import negotiator.actions.Action;


/**
 * holds information of one past action in the negotiation
 * @author Yinon Oshrat 
 */
class EnterBidMessageInfo{
	Action action;
	private int turn;

	public EnterBidMessageInfo (Action action, int turn){
		this.action = action;
		this.turn = turn;
	}	

	public int getTurn (){
		return turn;
	}

	public Action getAction (){
		return action;
	}

}

