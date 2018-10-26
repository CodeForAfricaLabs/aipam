package com.jamdev.maven.aipam.layout.clustering;

import java.util.ArrayList;

import org.controlsfx.control.PopOver;

import com.jamdev.maven.aipam.layout.AIPamView;
import com.jamdev.maven.aipam.layout.clips.SpectrogramImage;
import com.jamdev.maven.clips.PAMClip;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;


/**
 * Pane which shows the position of clips on a 2D dimensional scatter plot.
 * Eventually this could be 3D which  would be pretty cool. 
 * 
 * @author Jamie Macaulay. 
 *
 */
public class ClusterGraphPane extends BorderPane {
	
	/**
	 * Reference to the view. 
	 */
	private AIPamView aiPamView;
	
	/**
	 * The main pane. 
	 */
	private Pane mainPane;

	/**
	 * The line chart
	 */
	private ScatterChart<Number, Number> scatterChart;


	public ClusterGraphPane(AIPamView aiPamView) {
		this.aiPamView=aiPamView;
		mainPane = createPane();
		this.setCenter(mainPane);
	}

	
	/**
	 * Create the pane. 
	 * @return create the pane
	 */
	private Pane createPane() {
		
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        //creating the chart
        scatterChart = 
                new ScatterChart<Number,Number>(xAxis,yAxis);
                
        scatterChart.setTitle("Clustering");
        
        
        BorderPane mainPane = new BorderPane(); 
        mainPane.setCenter(scatterChart);
        
        
		return mainPane;
	}

	/**
	 * Update the cluster pane. 
	 * @param pamClips - the cluster pane. 
	 */
	public void update(ArrayList<PAMClip> pamClips) {
		
		scatterChart.getData().clear();
		
        XYChart.Series series = new XYChart.Series();
		for (int i=0; i<pamClips.size(); i++) {
			
			XYChart.Data dataPoint = new XYChart.Data(pamClips.get(i).getClusterPoint()[0],
					 pamClips.get(i).getClusterPoint()[1]);
						
			series.getData().add(dataPoint); 
			
//			dataPoint.getNode().setOnMouseClicked((event)->{
//				PopOver popOver = new PopOver(); 
//			});
			 
		}
        scatterChart.getData().add(series);
        
        Tooltip toolTip; 
        SpectrogramImage image;
        for (XYChart.Series<Number, Number> s : scatterChart.getData()) {
            int n=0;
            for (XYChart.Data<Number, Number> d : s.getData()) {
     
            	toolTip = new Tooltip(
                        String.format("%2.1f = %2.1f", 
                                d.getXValue().doubleValue(), 
                                d.getYValue().doubleValue()));
            	image = new SpectrogramImage(pamClips.get(n).getSpectrogram(), 
            			aiPamView.getCurrentColourArray(), aiPamView.getAIParams().colourLims); 
            	toolTip.setGraphic(new ImageView(image.getSpecImage(100, 100)));
            	
            	n++;
                Tooltip.install(d.getNode(), toolTip);
            }
        }

	}
	
	
 

}