package negotiator.gui.chart;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.axis.*;
import org.jfree.data.xy.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class ScatterPlot extends JPanel{
	
	private DefaultXYDataset dataset = new DefaultXYDataset();
	private String headline = "";
	private String xAxisLabel = "round";
	private String yAxisLabel = "my utility of bid";
	private ChartPanel panel;
	JFreeChart chart;
	
	//the constructor for the utilities per round graph:
	public ScatterPlot(double [][] myBidSeries, double [][] oppBidSeries){
		//add the series to the dataset:
		dataset.addSeries("my bids",myBidSeries);
		dataset.addSeries("opponent's bids",oppBidSeries);
		init();
	}
	
	//generic constructor:
	public ScatterPlot(String legendA,double [][] seriesA,String legendB,double [][] seriesB, String headline, String xLabel, String yLabel){
		//add the series to the dataset:
		dataset.addSeries(legendA,seriesA);
		dataset.addSeries(legendB,seriesB);
		this.headline = headline;
		xAxisLabel = xLabel;
		yAxisLabel = yLabel;
		init();
	}
	
	private void init(){
		
		chart = ChartFactory.createScatterPlot(
				headline,
				xAxisLabel,//xAxisLabel
				yAxisLabel,//yAxisLabel
				dataset, // the data to be displayed
				PlotOrientation.VERTICAL, //if set to horizontal x- and y-axis are switched
				true, // legend? yes we want a legend
				false, // tooltips?
				false // URLs?
		);
	
	    // This is how you change the values on the x-axis to only integer values
	    // the x-axis is called domainAxis, the y-axis rangeAxis
	    XYPlot plot = chart.getXYPlot();
	    NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
	    domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	    
	    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	    //if it is important that the axis' values start with 0:
	    //rangeAxis.setAutoRangeIncludesZero(true);
	    
	    //like this you can set the range of the axis manually 
	    //(since we know that the utilities are always between 0 and 1 it makes sense.)
	    //I set the range to 1.1 because it gives a little more space at the top, 
	    //since we could have utilities that are 1.0 it will look a bit better
	    rangeAxis.setRange(0,1.1);
	    
	}
	
	public ChartPanel getChartPanel(){
		return panel;
	}
	
	public JFreeChart getChart(){
		return chart;
	}
}