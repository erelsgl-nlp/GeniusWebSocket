package negotiator.gui.chart;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.axis.*;
import org.jfree.data.xy.*;

import javax.swing.*;

public class UtilityPlot extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private DefaultXYDataset dataset = new DefaultXYDataset();
	private String xAxisLabel = "round";
	private String yAxisLabel = "my utility of bid";
	private ChartPanel panel;
	JFreeChart chart;
	
	//the constructor for the utilities per round graph:
	public UtilityPlot(double [][] myBidSeries, double [][] oppBidSeries){
		//add the series to the dataset:
		if (myBidSeries[0].length>0)
			dataset.addSeries("my bids",myBidSeries);
		if (oppBidSeries[0].length>0)
			dataset.addSeries("opponent's bids",oppBidSeries);
		init();
	}

	//the constructor for the utilities per round graph with mediator
	public UtilityPlot(double [][] myBidSeries, double [][] oppBidSeries, double [][] medBidSeries){
		//add the series to the dataset:
		if (myBidSeries[0].length>0)
			dataset.addSeries("my bids",myBidSeries);
		if (oppBidSeries[0].length>0)
			dataset.addSeries("opponent's bids",oppBidSeries);
		if (medBidSeries[0].length>0)
			dataset.addSeries("mediator's bids",medBidSeries);
		init();
	}
	private void init(){
		final XYItemRenderer renderer = new XYLineAndShapeRenderer();
		NumberAxis domainAxis = new NumberAxis(xAxisLabel);
        ValueAxis rangeAxis = new NumberAxis(yAxisLabel);
		XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
		chart = new JFreeChart("Utilities per round", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		
		/* just a simple line chart:
		 * chart = ChartFactory.createXYLineChart(
				headline,
				xAxisLabel,//xAxisLabel
				yAxisLabel,//yAxisLabel
				dataset, // the data to be displayed
				PlotOrientation.VERTICAL, //if set to horizontal x- and y-axis are switched
				true, // legend? yes we want a legend
				false, // tooltips?
				false // URLs?
		);*/
	
	    // This is how you change the values on the x-axis to only integer values
	    // the x-axis is called domainAxis, the y-axis rangeAxis
	    domainAxis = (NumberAxis) plot.getDomainAxis();
	    domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	    
	    rangeAxis = (NumberAxis) plot.getRangeAxis();
	    //if it is important that the axis' values start with 0:
	    //rangeAxis.setAutoRangeIncludesZero(true);
	    
	    //like this you can set the range of the axis manually 
	    //(since we know that the utilities are always between 0 and 1 it makes sense.)
	    //I set the range to 1.1 because it gives a little more space at the top, 
	    //since we could have utilities that are 1.0 it will look a bit better
	    //rangeAxis.setRange(0,1.1);
	    
	}
	
	public ChartPanel getChartPanel(){
		return panel;
	}
	
	public JFreeChart getChart(){
		return chart;
	}
}


