package com.jamdev.maven.aipam.clips;

import java.io.File;

import com.jamdev.maven.aipam.annotation.Annotation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 * A single clip for display on the clip pane. 
 * <p>
 * The clip stores a spectrogram image but no raw .wav data. 
 * Can be dealing with Gigabytes of clips so cannot store these in memory
 * 
 * @author Jamie Macaulay 
 *
 */
public class PAMClip {

	private static int DEFAULT_FFT_LEN =1024; 

	private static int DEFUALT_FFT_HOP = 512; 

	/**
	 * Data for colours for spectrogram is stored as a short.
	 */
	private double[][] spectrogramClip; 

	/**
	 * The filename of the clip
	 */
	public StringProperty fileName;

	/**
	 * The audio play is stored so the clip can be played. 
	 */
	private AudioPlay audioPlay;

	/**
	 * The file ID
	 */
	private StringProperty iD;

	/**
	 * The position defined by the cluster algorithm
	 */
	private double[] clusterPoint;

	/**
	 * The grid ID. 
	 */
	private IntegerProperty gridID;

	/**
	 * Object property for the annotation. 
	 */
	private ObjectProperty<Annotation> annotation;

	/**
	 * The static grid ID. 
	 */
	private SimpleIntegerProperty staticGridID; 


	public PAMClip(ClipWave wave, int gridID){
		this(wave , DEFAULT_FFT_LEN, DEFUALT_FFT_HOP, gridID); 
	} 

	public PAMClip(ClipWave wave, int fftLength, int fftHop, int gridID){

		this.staticGridID = new SimpleIntegerProperty(gridID);  
		this.gridID=new SimpleIntegerProperty(gridID); 

		this.spectrogramClip=wave.getSpectrogram(fftLength, fftLength/fftHop).getAbsoluteSpectrogram();

		//spectrogramClip =  DownSampleImpl.largestTriangleThreeBuckets(spectrogramClip, 50);
		//		System.out.println("The spectrogram clip is: " +  spectrogramClip.length + " x " +  spectrogramClip[0].length);

		//		System.out.println("The spectorgram size is: " + 
		//		spectrogramClip.getAbsoluteSpectrogramData().length + "x" +spectrogramClip.getAbsoluteSpectrogramData()); 
		this.audioPlay=wave.getAudioPlay(); 

		this.fileName=new SimpleStringProperty(wave.getFileName()); 

		//use the file name as the ID because this allows easy exporting of data and alos allows for 
		//clustering algorithm to be reoloaded. Condition of program is therefore that no two file names 
		//can be equal. 
		this.iD=new SimpleStringProperty(new File(wave.getFileName()).getName()); 

		//create the annotation property
		this.annotation= new SimpleObjectProperty<Annotation>(); 

		//do not want the raw wave data in memory so wave is not saved

		wave = null; //garbage collector probably gets rid of this anyway but makes me feel better. 
	} 


	@SuppressWarnings("unused")
	private double[][] clipSpectrogram(double[][] clip, int binsFreq) {
		double[][] newSpec = new double[clip.length][]; 
		for (int i=0; i<clip.length; i++) {
			double[] newSpecLine= new double[binsFreq]; 
			for (int j=0; j<binsFreq; j++) {
				newSpecLine[j]=clip[i][j]; 
			}
			newSpec[i]=newSpecLine; 
		}
		return newSpec; 
	}


	/**
	 * Get the spectrogram data for the clip. 
	 * @return
	 */
	public double[][] getSpectrogram() {
		return spectrogramClip;
	}

	/**
	 * Get the audio play. This plays the audio files.
	 * @return the audio play
	 */
	public AudioPlay getAudioPlay() {

		return audioPlay;
	}

	/**
	 * 
	 * Get a unique ID number for the clip. 
	 * @return the ID
	 */
	public String getID() {
		return iD.get(); 
	}

	/**
	 * Get the cluster point for the clip after having been 
	 * @return the cluster point in x,y and maybe z or even more. Can be null if no clustering. 
	 */
	public double[] getClusterPoint() {
		return clusterPoint; 
	}

	/**
	 * Set the cluster point.
	 * @param clusterPoint - the cluster point. 
	 */
	public void setClusterPoint(double[] clusterPoints) {
		this.clusterPoint=clusterPoints; 
	}

	/**
	 * Set the grid ID. This is the position of the pamclips on the grid. 
	 * @param i - the grid ID. This should ideally be unique for each pamclip 
	 */
	public void setGridID(int i) {
		this.gridID.set(i);; 

	}

	/**
	 * Get the grid ID. This is the position in the grid (counting from (0,0) along rows)
	 * @return the gridID of the clip. 
	 */
	public int getGridID() {
		return this.gridID.get();
	}

	/**
	 * Get the annotation for the clip. Can be null/ 
	 * @return the annotation of rthe clip
	 */
	public Annotation getAnnotation() {
		return annotation.get();
	}

	/**
	 * Set the annotation. 
	 * @param annotation - the annotation to set. 
	 */
	public void setAnnotation(Annotation annotation) {
		this.annotation.set(annotation);
	}

	/**
	 * Get the annotation property for the clip
	 * @return the annotation property. 
	 */
	public ObjectProperty<Annotation>  annotationProperty(){
		return this.annotation;
	}

	/**
	 * Get the static grid ID. 
	 * @return the static grid id
	 */
	public int getStaticGridID() {
		return staticGridID.get();
	}

	/**
	 * Get the filename associated with the clip. 
	 * @return the filename associated with the clip. 
	 */
	public String getFileName() {
		return this.fileName.get();
	}


}
