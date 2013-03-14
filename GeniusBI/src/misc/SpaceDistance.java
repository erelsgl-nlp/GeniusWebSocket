package misc;

import java.util.Random;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;

import negotiator.issue.Issue;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleElement;

/**
 * 
 * This class calculate three types of distance measure between two utility spaces:
 * 1. Euclidean distance
 * 2. Ranking distance
 * 3. Pearson correlation coefficient 
 * 
 * @author Dmytro Tykhonov
 *
 */
public class SpaceDistance {
	private UtilitySpace utilitySpaceA;
	private UtilitySpace utilitySpaceB;
	public SpaceDistance(UtilitySpace utilitySpaceA, UtilitySpace utilitySpaceB) {
		this.utilitySpaceA = utilitySpaceA;
		this.utilitySpaceB = utilitySpaceB;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

			String domainFileName = "D:\\_Work\\eclipse\\NegotiatorGUI\\etc\\templates\\SON\\son_domain.xml";
			String dir = "D:\\_Work\\eclipse\\NegotiatorGUI\\etc\\templates\\SON\\";
			calculateDistances(domainFileName, dir+"son_center_12.xml", dir+"son_seller_2.xml");
			calculateDistances(domainFileName, dir+"son_center_12.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_1.xml", dir+"son_seller_2.xml");
			calculateDistances(domainFileName, dir+"son_center_1.xml", dir+"son_seller_8.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_9.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_11.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_9.xml");

			calculateDistances(domainFileName, dir+"son_center_8.xml", dir+"son_seller_11.xml");
			calculateDistances(domainFileName, dir+"son_center_8.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_3.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_6.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_4.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_11.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_8.xml");
			calculateDistances(domainFileName, dir+"son_center_11.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_11.xml", dir+"son_seller_11.xml");
			calculateDistances(domainFileName, dir+"son_center_6.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_6.xml", dir+"son_seller_3.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_8.xml", dir+"son_seller_6.xml");
			calculateDistances(domainFileName, dir+"son_center_8.xml", dir+"son_seller_3.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_9.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_1.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_4.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_2.xml");
			calculateDistances(domainFileName, dir+"son_center_1.xml", dir+"son_seller_3.xml");
			calculateDistances(domainFileName, dir+"son_center_1.xml", dir+"son_seller_6.xml");
			calculateDistances(domainFileName, dir+"son_center_8.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_8.xml", dir+"son_seller_6.xml");
			calculateDistances(domainFileName, dir+"son_center_4.xml", dir+"son_seller_12.xml");
			calculateDistances(domainFileName, dir+"son_center_4.xml", dir+"son_seller_3.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_2.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_11.xml");
			calculateDistances(domainFileName, dir+"son_center_6.xml", dir+"son_seller_2.xml");
			calculateDistances(domainFileName, dir+"son_center_6.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_2.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_6.xml");
			calculateDistances(domainFileName, dir+"son_center_12.xml", dir+"son_seller_8.xml");
			calculateDistances(domainFileName, dir+"son_center_12.xml", dir+"son_seller_12.xml");
			calculateDistances(domainFileName, dir+"son_center_9.xml", dir+"son_seller_6.xml");
			calculateDistances(domainFileName, dir+"son_center_9.xml", dir+"son_seller_2.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_11.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_1.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_8.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_11.xml", dir+"son_seller_8.xml");
			calculateDistances(domainFileName, dir+"son_center_11.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_4.xml");
			calculateDistances(domainFileName, dir+"son_center_8.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_8.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_12.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_12.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_11.xml");
			calculateDistances(domainFileName, dir+"son_center_12.xml", dir+"son_seller_8.xml");
			calculateDistances(domainFileName, dir+"son_center_12.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_9.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_4.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_12.xml");
			calculateDistances(domainFileName, dir+"son_center_1.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_1.xml", dir+"son_seller_3.xml");
			calculateDistances(domainFileName, dir+"son_center_11.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_11.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_1.xml", dir+"son_seller_8.xml");
			calculateDistances(domainFileName, dir+"son_center_1.xml", dir+"son_seller_12.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_4.xml");
			calculateDistances(domainFileName, dir+"son_center_10.xml", dir+"son_seller_6.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_4.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_6.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_3.xml");
			calculateDistances(domainFileName, dir+"son_center_2.xml", dir+"son_seller_6.xml");
			calculateDistances(domainFileName, dir+"son_center_5.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_5.xml", dir+"son_seller_11.xml");
			calculateDistances(domainFileName, dir+"son_center_12.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_12.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_6.xml", dir+"son_seller_9.xml");
			calculateDistances(domainFileName, dir+"son_center_6.xml", dir+"son_seller_11.xml");
			calculateDistances(domainFileName, dir+"son_center_4.xml", dir+"son_seller_10.xml");
			calculateDistances(domainFileName, dir+"son_center_4.xml", dir+"son_seller_7.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_5.xml");
			calculateDistances(domainFileName, dir+"son_center_3.xml", dir+"son_seller_2.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_2.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_4.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_4.xml");
			calculateDistances(domainFileName, dir+"son_center_7.xml", dir+"son_seller_2.xml");




	}
	private static void calculateDistances(String domainFileName, String fileNameA, String fileNameB) {
		try {
			Domain domain = new Domain(domainFileName);
			UtilitySpace utilitySpaceA, utilitySpaceB;
			utilitySpaceA =  new UtilitySpace(domain, fileNameA);
			utilitySpaceB =  new UtilitySpace(domain, fileNameB);
			SpaceDistance dist = new SpaceDistance(utilitySpaceA, utilitySpaceB);
			
			System.out.println(fileNameA);
			System.out.println(fileNameB);
			dist.dumpDistancesToLog(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private double sq(double x) { return x*x; }

	private double calculateEuclideanDistanceUtilitySpace(double[] pLearnedUtil, double[] pOpponentUtil) {		
		double lDistance = 0;
		try {
			for(int i=0;i<pLearnedUtil.length;i++)
				lDistance = lDistance + sq( pOpponentUtil[i]-pLearnedUtil[i]);
		} catch (Exception e) {				
			e.printStackTrace();
		}
		lDistance = lDistance / utilitySpaceA.getDomain().getNumberOfPossibleBids();
		return lDistance;
	}	
	private double calculateEuclideanDistanceWeghts(double[] pExpectedWeight) {
		double lDistance = 0;
		int i=0;
		try {
			for(Issue lIssue : utilitySpaceA.getDomain().getIssues()) {
				lDistance = lDistance +sq(utilitySpaceB.getWeight(lIssue.getNumber()) -pExpectedWeight[i]);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lDistance/(double)i;
	}

	private double calculatePearsonDistanceUtilitySpace(double[] pLearnedUtility, double[] pOpponentUtil) {
		double lDistance = 0;
		double lAverageLearnedUtil=0;
		double lAverageOriginalUtil=0;
		//calculate average values
		for(int i=0;i<pLearnedUtility.length;i++) {
			lAverageLearnedUtil = lAverageLearnedUtil + pLearnedUtility[i];
			lAverageOriginalUtil = lAverageOriginalUtil + pOpponentUtil[i];
		}
		lAverageLearnedUtil = lAverageLearnedUtil/(double)(utilitySpaceA.getDomain().getNumberOfPossibleBids());
		lAverageOriginalUtil = lAverageOriginalUtil/ (double)(utilitySpaceA.getDomain().getNumberOfPossibleBids());
		//calculate the distance itself
		double lSumX=0;
		double lSumY=0;
		for(int i=0;i<pLearnedUtility.length;i++) { 
			lDistance = lDistance + (pLearnedUtility[i]-lAverageLearnedUtil)*
			(pOpponentUtil[i]-lAverageOriginalUtil);
			lSumX = lSumX + sq(pLearnedUtility[i]-lAverageLearnedUtil);
			lSumY = lSumY + sq(pOpponentUtil[i]-lAverageOriginalUtil);

		}


		return  lDistance/(Math.sqrt(lSumX*lSumY));
	}

	private double calculatePearsonDistanceWeghts(double[] pExpectedWeight) {
		double lDistance = 0;
		double lAverageLearnedWeight=0;
		double lAverageOriginalWeight=0;
		int i=0;
		try {
			for(Issue lIssue : utilitySpaceA.getDomain().getIssues()) {
				lAverageLearnedWeight = lAverageLearnedWeight +pExpectedWeight[i];
				lAverageOriginalWeight = lAverageOriginalWeight + utilitySpaceB.getWeight(lIssue.getNumber());
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		lAverageLearnedWeight = lAverageLearnedWeight/(double)(i);
		lAverageOriginalWeight= lAverageOriginalWeight/ (double)(i);

		//calculate the distance itself
		i=0;
		double lSumX=0;
		double lSumY=0;
		try {
			for(Issue lIssue : utilitySpaceA.getDomain().getIssues()) {
				lDistance = lDistance +(utilitySpaceB.getWeight(lIssue.getNumber())- lAverageOriginalWeight)*(pExpectedWeight[i]-lAverageLearnedWeight);
				lSumX = lSumX + sq(utilitySpaceB.getWeight(lIssue.getNumber())- lAverageOriginalWeight);
				lSumY = lSumY + sq(pExpectedWeight[i]-lAverageLearnedWeight);
				i++;
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lDistance/(Math.sqrt(lSumX*lSumY));
	}
	private double calculateRankingDistanceUtilitySpaceMonteCarlo(double[] pLearnedUtil, double[] pOpponentUtil) {
		double lDistance = 0;
		int lNumberOfPossibleBids = (int)(utilitySpaceA.getDomain().getNumberOfPossibleBids());
		int lNumberOfComparisons = 10000000;
		for(int k=0;k<lNumberOfComparisons ;k++) {
			int i = (new Random()).nextInt(lNumberOfPossibleBids-1);
			int j = (new Random()).nextInt(lNumberOfPossibleBids-1);
			if(((pLearnedUtil[i]>pLearnedUtil[j])&&(pOpponentUtil[i]>pOpponentUtil[j]))||
					((pLearnedUtil[i]<pLearnedUtil[j])&&(pOpponentUtil[i]<pOpponentUtil[j]))||
					((pLearnedUtil[i]==pLearnedUtil[j])&&(pOpponentUtil[i]==pOpponentUtil[j]))) {

			} else
				lDistance++;

		}
		return ((double)lDistance)/((double)lNumberOfComparisons);
	}
	private double calculateRankingDistanceUtilitySpace(double[] pLearnedUtil, double[] pOpponentUtil) {

		double lDistance = 0;
		int lNumberOfPossibleBids = (int)(utilitySpaceA.getDomain().getNumberOfPossibleBids()); 

		try {		
			for(int i=0;i<lNumberOfPossibleBids-1;i++) {
				for(int j=i+1;j<lNumberOfPossibleBids;j++) {
					//if(i==j) continue;
					if (Math.signum(pLearnedUtil[i]-pLearnedUtil[j])!=Math.signum(pOpponentUtil[i]-pOpponentUtil[j]))
						lDistance++;
					/*
					if(((pLearnedUtil[i]>pLearnedUtil[j])&&(pOpponentUtil[i]>pOpponentUtil[j]))||
					   ((pLearnedUtil[i]<pLearnedUtil[j])&&(pOpponentUtil[i]<pOpponentUtil[j]))||
					   ((pLearnedUtil[i]==pLearnedUtil[j])&&(pOpponentUtil[i]==pOpponentUtil[j]))) {

					} else
						lDistance++;
					j++;
					 */
				} //for
			} //for
		} catch (Exception e) {				
			e.printStackTrace();
		}

		lDistance = 2 * lDistance / (utilitySpaceA.getDomain().getNumberOfPossibleBids()*(utilitySpaceA.getDomain().getNumberOfPossibleBids()));
		return lDistance;
	}
	private double calculateRankingDistanceWeghts(double pExpectedWeights[]) {
		double lDistance = 0;
		double[] lOriginalWeights = new double[utilitySpaceA.getDomain().getIssues().size()];
		int k=0;
		try {
			for(Issue lIssue : utilitySpaceA.getDomain().getIssues()) {
				lOriginalWeights[k] = utilitySpaceB.getWeight(lIssue.getNumber());
				k++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		k=0;
		int nrOfIssues = utilitySpaceA.getDomain().getIssues().size();
		for(int i=0; i<nrOfIssues-1;i++) {			
			for(int j=i+1;j<nrOfIssues;j++) {
				k++;
				double tmpWeightLearned = pExpectedWeights[i];
				double tmpWeightOriginal = lOriginalWeights[i];
				double tmpWeight2Learned = pExpectedWeights[j];
				double tmpWeight2Original = lOriginalWeights[j];
				if(((tmpWeightLearned>tmpWeight2Learned)&&(tmpWeightOriginal>tmpWeight2Original))||
						((tmpWeightLearned<tmpWeight2Learned)&&(tmpWeightOriginal<tmpWeight2Original))||
						((tmpWeightLearned==tmpWeight2Learned)&&(tmpWeightOriginal==tmpWeight2Original)))
				{

				} else
					lDistance++;

			}			
		}		
		return ((double)lDistance)/((double)k);
	}

	public SimpleElement calculateDistances() {
		double lExpectedWeights[] = new double[utilitySpaceA.getDomain().getIssues().size()];
		int i=0;
		for(Issue lIssue : utilitySpaceA.getDomain().getIssues()) {
			lExpectedWeights[i]=utilitySpaceA.getWeight(lIssue.getNumber());
			i++;
		}	


		double pLearnedUtil[] = new double[(int)(utilitySpaceA.getDomain().getNumberOfPossibleBids())];
//		HashMap<Bid, Double> pLearnedSpace = new HashMap<Bid, Double>();
		BidIterator lIter = new BidIterator( utilitySpaceA.getDomain());
		i=0;
		while(lIter.hasNext()) {
			Bid lBid = lIter.next();
			try {
				pLearnedUtil[i] =utilitySpaceA.getUtility(lBid);
//				pLearnedSpace.put(lBid, new Double(pLearnedUtil[i]));

			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
		double pOpponentUtil[] = new double[(int)(utilitySpaceA.getDomain().getNumberOfPossibleBids())];
//		HashMap<Bid, Double> pOpponentSpace = new HashMap<Bid, Double>();
		lIter = new BidIterator( utilitySpaceA.getDomain());
		i=0;
		while(lIter.hasNext()) {
			Bid lBid = lIter.next();
			try {
				pOpponentUtil[i] = utilitySpaceB.getUtility( lBid);
//				pOpponentSpace.put(lBid, new Double(pOpponentUtil[i]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}

		double lEuclideanDistUtil 		= calculateEuclideanDistanceUtilitySpace(pLearnedUtil,pOpponentUtil);
		double lEuclideanDistWeights 	= calculateEuclideanDistanceWeghts(lExpectedWeights);
		double lRankingDistUtil 		= 0;
		if((int)(utilitySpaceA.getDomain().getNumberOfPossibleBids())>100000) 
			lRankingDistUtil = calculateRankingDistanceUtilitySpaceMonteCarlo(pLearnedUtil, pOpponentUtil);
		else 
			lRankingDistUtil = calculateRankingDistanceUtilitySpace(pLearnedUtil, pOpponentUtil);
		double lRankingDistWeights 		= calculateRankingDistanceWeghts(lExpectedWeights);
		double lPearsonDistUtil			= calculatePearsonDistanceUtilitySpace(pLearnedUtil,pOpponentUtil);
		double lPearsonDistWeights		= calculatePearsonDistanceWeghts(lExpectedWeights);
		SimpleElement lLearningPerformance = new SimpleElement("learning_performance");
		
		lLearningPerformance.setAttribute("euclidean_distance_utility_space", String.valueOf(lEuclideanDistUtil));
		lLearningPerformance.setAttribute("euclidean_distance_weights", String.valueOf(lEuclideanDistWeights));
		lLearningPerformance.setAttribute("ranking_distance_utility_space", String.valueOf(lRankingDistUtil));
		lLearningPerformance.setAttribute("ranking_distance_weights", String.valueOf(lRankingDistWeights));
		lLearningPerformance.setAttribute("pearson_distance_utility_space", String.valueOf(lPearsonDistUtil));
		lLearningPerformance.setAttribute("pearson_distance_weights", String.valueOf(lPearsonDistWeights));
		return lLearningPerformance;
	}
	
	protected void dumpDistancesToLog(int pRound) {

		//System.out.print(getName() + ": calculating distance between the learned space and the original one ...");
		SimpleElement lLearningPerformance = calculateDistances();
		lLearningPerformance.setAttribute("round", String.valueOf(pRound));
		System.out.println("Done!");
		System.out.println(lLearningPerformance.toString());

	}

}
