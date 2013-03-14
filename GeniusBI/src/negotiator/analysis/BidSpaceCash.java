package negotiator.analysis;

import java.util.HashMap;

import negotiator.utility.UtilitySpace;

public class BidSpaceCash {
	private static HashMap<UtilitySpace,HashMap<UtilitySpace, BidSpace>> bidSpaceCash = new HashMap<UtilitySpace, HashMap<UtilitySpace,BidSpace>>();
	
	public static void addBidSpaceToCash(UtilitySpace spaceA, UtilitySpace spaceB, BidSpace bidSpace) {
		if(bidSpaceCash.get(spaceA)!=null) {
			if(bidSpaceCash.get(spaceA).get(spaceB)==null) {
				bidSpaceCash.get(spaceA).put(spaceB, bidSpace);
			}
		} else {
			HashMap<UtilitySpace, BidSpace> cashA = new HashMap<UtilitySpace, BidSpace>();
			cashA.put(spaceB, bidSpace);
			bidSpaceCash.put(spaceA, cashA);		
		}		
	}
	public static BidSpace getBidSpace(UtilitySpace spaceA, UtilitySpace spaceB) {		
		if(bidSpaceCash.get(spaceA)!=null)			
			return bidSpaceCash.get(spaceA).get(spaceB);
		else 
			return null;
	}
	

}
