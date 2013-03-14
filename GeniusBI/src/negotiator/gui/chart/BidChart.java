package negotiator.gui.chart;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.axis.*;
import org.jfree.data.xy.*;


public class BidChart {
	
	private double [][] possibleBids;
	private double [][]pareto;
	private double [][] bidSeriesA;
	private double [][] bidSeriesB;
	private String agentAName = "Agent A";
	private String agentBName = "Agent B";
	private JFreeChart chart;
	private XYPlot plot;
	private DefaultXYDataset possibleBidData = new DefaultXYDataset();
	private DefaultXYDataset paretoData = new DefaultXYDataset();
	private DefaultXYDataset bidderAData = new DefaultXYDataset();
	private DefaultXYDataset bidderBData = new DefaultXYDataset();
	private DefaultXYDataset nashData = new DefaultXYDataset();
	private DefaultXYDataset kalaiData = new DefaultXYDataset();
	private DefaultXYDataset agreementData = new DefaultXYDataset();
	final XYDotRenderer dotRenderer = new XYDotRenderer();
	final XYDotRenderer nashRenderer = new XYDotRenderer();
	final XYDotRenderer kalaiRenderer = new XYDotRenderer();
	final XYDotRenderer agreementRenderer = new XYDotRenderer();
	//final XYItemRenderer agreementRenderer = new XYLineAndShapeRenderer(false, true);
	final XYItemRenderer paretoRenderer = new XYLineAndShapeRenderer(true,false);
	final XYItemRenderer lineARenderer = new XYLineAndShapeRenderer();
	final XYItemRenderer lineBRenderer = new XYLineAndShapeRenderer();
	private NumberAxis domainAxis ;
    private ValueAxis rangeAxis;

	//empty constructor; but: don't you always know the possible bids and the pareto before the 1st bid? 
	public BidChart(){

		BidChart1();
		
	}
	public BidChart(String agentAname, String agentBname, double [][] possibleBids,double[][] pareto){		
		this.agentAName = agentAname;
		this.agentBName = agentBname;
		this.pareto = pareto;
		this.possibleBids = possibleBids;
		BidChart1();
	}
	public void BidChart1(){
		chart = createOverlaidChart();  
//		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//		rangeAxis.setRange(0,1.1);
//		NumberAxis domainAxis = (NumberAxis)plot.getDomainAxis(); 
//		domainAxis.setRange(0,1.1);
	}
	//returning the chart 
	public JFreeChart getChart(){
		return chart;
	}
	
	//set-Methods
	public void setPareto(double [][] pareto){
		this.pareto = pareto;
		paretoData.addSeries("Pareto efficient frontier",pareto);
		chart.fireChartChanged();
	}
	
	public void setPossibleBids(double [][] possibleBids){
		this.possibleBids = possibleBids;
		possibleBidData.addSeries("all possible bids",possibleBids);
		chart.fireChartChanged();
	}
	
	public void setBidSeriesA(double [][] bidSeriesA){
		this.bidSeriesA = bidSeriesA;
		bidderAData.addSeries("Agent A's bids",bidSeriesA);   
		chart.fireChartChanged();
	}
        
	public void setBidSeriesB(double [][] bidSeriesB){
		this.bidSeriesB = bidSeriesB;
		bidderBData.addSeries("Agent B's bids",bidSeriesB);
		chart.fireChartChanged();
	}
	
	public void setNash(double[][]nash){
		nashData.addSeries("Nash Point",nash);
		chart.fireChartChanged();
	}
	
	public void setKalai(double[][]kalai){
		nashData.addSeries("Kalai Point",kalai);
		chart.fireChartChanged();
	}
	
	public void setAgreementPoint(double[][]agreement){
		agreementData.addSeries("Agreement",agreement);
		chart.fireChartChanged();
	}
	
	public void removeAllPlots(){
		if(bidderAData.getSeriesCount()!=0)
			bidderAData.removeSeries("Bids of "+ agentAName);
		if(bidderBData.getSeriesCount()!=0)
			bidderBData.removeSeries("Bids of " + agentBName);
		if(agreementData.getSeriesCount()!=0)
			agreementData.removeSeries("Agreement");
		chart.fireChartChanged();		
	}
			
	/**
     * Creates an overlaid chart.
     *
     * @return The chart.
     */
    private JFreeChart createOverlaidChart() {
    	domainAxis = new NumberAxis(agentAName);
        rangeAxis = new NumberAxis(agentBName);
        dotRenderer.setDotHeight(2);
        dotRenderer.setDotWidth(2);
        nashRenderer.setDotHeight(5);
        nashRenderer.setDotWidth(5);
        nashRenderer.setSeriesPaint(0,Color.black);
        kalaiRenderer.setDotHeight(5);
        kalaiRenderer.setDotWidth(5);
        kalaiRenderer.setSeriesPaint(0,Color.pink);
        paretoRenderer.setSeriesPaint(0, Color.RED);
        lineARenderer.setSeriesPaint(0, Color.GREEN);
        lineBRenderer.setSeriesPaint(0, Color.BLUE);
        agreementRenderer.setDotHeight(10);
        agreementRenderer.setDotWidth(10);
        //agreementRenderer.setSeriesShape(0, new Ellipse2D.Float(10.0f, 10.0f, 10.0f, 10.0f));
        agreementRenderer.setSeriesPaint(0, Color.RED);
       
		//create default plot, quick hack so that the graph panel is not empty
    	if(possibleBids!=null){
    		possibleBidData.addSeries("all possible bids",possibleBids);
    	}
    	if (pareto!=null){
        	setPareto(pareto);   
        }
        // create plot ...
    	plot = new XYPlot(possibleBidData, domainAxis, rangeAxis, dotRenderer);
    	plot.setDataset(2, paretoData);
        plot.setRenderer(2, paretoRenderer);
    	plot.setDataset(3, bidderAData);
	    plot.setRenderer(3, lineARenderer);
	    plot.setDataset(4, bidderBData);
	    plot.setRenderer(4, lineBRenderer);
	   
	    plot.setDataset(5, nashData);
	    plot.setRenderer(5, nashRenderer);
	    plot.setDataset(6, kalaiData);
	    plot.setRenderer(6, kalaiRenderer);
	    plot.setDataset(7, agreementData);
	    plot.setRenderer(7, agreementRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        // return a new chart containing the overlaid plot...
        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.setBackgroundPaint(new Color(255,255,255));
        return chart;
    }
    public void setAgentAName (String value) {
    	agentAName = value;
    	domainAxis.setLabel(agentAName);
		chart.fireChartChanged();
    }
    public void setAgentBName (String value) {
    	agentBName = value;
    	rangeAxis.setLabel(agentBName);
		chart.fireChartChanged();
    }
}
