package com.jamdev.maven.aipam;

import java.io.File;
import java.util.ArrayList;

import com.jamdev.maven.aipam.annotation.AnnotationManager;
import com.jamdev.maven.aipam.clips.AudioInfo;
import com.jamdev.maven.aipam.clips.ClipExporter;
import com.jamdev.maven.aipam.clips.PAMClip;
import com.jamdev.maven.aipam.clips.PAMClipManager;
import com.jamdev.maven.aipam.clustering.ClipClusterManager;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLStructure;

import javafx.concurrent.Task;

/**
 * Main controller class for AIPAM.
 * 
 * @author Jamie Macaulay
 *
 */
public class AiPamController {


	public int verbose = 1; 

	/**
	 * Start loading audio data from a file
	 */
	public static final int START_FILE_LOAD = 1;

	/**
	 * End loading audio data from a file. 
	 */
	public static final int END_FILE_LOAD =2;

	/**
	 * Start creating spectrogrma images for all the loaded clips. 
	 */
	public static final int START_IMAGE_LOAD =3;

	/**
	 * Flag indicating end of image loading. 
	 */
	public static final int END_IMAGE_LOAD =4;

	/**
	 * Cancelled image load. 
	 */
	public static final int CANCELLED_IMAGE_LOAD =5;


	/**
	 * Message for cancelled data loaded.
	 */
	public static final int CANCELLED_FILE_LOAD=6; 

	/**
	 * Start the clustering algorithm 
	 */
	public static final int START_CLUSTERING_ALGORITHM =7;

	/**
	 * End the clustering algorithm 
	 */
	public static final int END_CLUSTERING_ALGORITHM =8;

	/**
	 * The clustering algorithm has been cancelled. 
	 */
	public static final int CANCEL_CLUSTERING_ALGORITHM = 9;

	/**
	 * There is no audio directory does not exist. 
	 */
	public static final int NO_AUDIO_DIRECTORY = 10;

	/**
	 * End the file header load. 
	 */
	public static final int END_FILE_HEADER_LOAD = 11;
	
	/**
	 * Start the clip export
	 */
	public static final int START_CLIP_EXPORT = 12; 
	
	/**
	 * Cancel the clip export
	 */
	public static final int CANCEL_CLIP_EXPORT = 13; 
	
	/**
	 * End the clip export
	 */
	public static final int END_CLIP_EXPORT = 14; 



	/**
	 * The cluster manager
	 */
	private ClipClusterManager pamClusterManager; 

	/**
	 * Manages importing audio clips. 
	 */
	private PAMClipManager pamClipManager;
	
	/**
	 * The annotation manager. 
	 */
	private AnnotationManager annotationManager; 

	/**
	 * The current parameters for the program. 
	 */
	private AIPamParams aiPamParams;

	/**
	 * Listeners for messages form the controller. 
	 */
	ArrayList<AIMessageListener> aiMessageListeners = new ArrayList<AIMessageListener>();

	/**
	 * The last AiParams use din importing and/or clustering. Primarily used by a GUI
	 * to check whether a new settings files need clips imported again and/or clustered again. 
	 */
	private AIPamParams lastAiParams;

	/**
	 * The settings import and exporter. 
	 */
	private SettingsImportExport settingsImportExport;

	private Task<Integer> clusterTask;

	/**
	 * Exports clips
	 */
	private ClipExporter clipExporter;

	/**
	 * Export clip task. 
	 */
	private Task<Integer> exportTask;

	/**
	 * The main controller. 
	 */
	public AiPamController() {
		this.aiPamParams = new AIPamParams();
		this.pamClipManager = new PAMClipManager(); 
		this.pamClusterManager= new ClipClusterManager(); 
		this.annotationManager= new AnnotationManager(this); 
		this.settingsImportExport = new SettingsImportExport(this);
		this.clipExporter = new ClipExporter(); 
	}


	/**
	 * Add a sensor message listener 
	 * @param the aiMessageListener to add. 
	 */
	public void addSensorMessageListener(AIMessageListener aiMessageListener){
		aiMessageListeners.add(aiMessageListener); 
	}

	/**
	 * Update the message listeners.
	 * @param flag - the flag to update. 
	 * @param data - the data object- can be null
	 */
	public void updateMessageListeners(int flag, Object data) {
		for (AIMessageListener listener: aiMessageListeners) {
			listener.newMessage(flag, data);
		}
	}


	/**
	 * Load the audio data. 
	 * @param selectedDirectory - the directory for the clips
	 */
	public void loadAudioData(File selectedDirectory) {
		loadAudioData( selectedDirectory, true); 
	}


	/**
	 * Load the audio data. 
	 * @param selectedDirectory - the directory for the clips
	 * @param loadClips - true to load the clips. False checks the files. 
	 */
	public void loadAudioData(File selectedDirectory, boolean loadClips) {

		this.lastAiParams = aiPamParams.clone(); 
		this.lastAiParams.clusterParams = null; //indicates no clustering has taken place since last audio import. 
		if (!loadClips) lastAiParams.decimatorSR=null; //so that the algorithm know stuff has not been loaded yet. 
				
				
		Task<Integer> task = pamClipManager.importClipsTask(selectedDirectory, this.aiPamParams, loadClips);
		updateMessageListeners(START_FILE_LOAD, task); 

		task.setOnCancelled((value)->{
			//send notification when 
			updateMessageListeners(CANCELLED_FILE_LOAD, task); 
		});
		task.setOnSucceeded((value)->{
			if (loadClips)
				updateMessageListeners(END_FILE_LOAD, task); 
			else 
				updateMessageListeners(END_FILE_HEADER_LOAD, task); 

		});

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start(); 
	}

	/**
	 * Start a thread clustering the data. 
	 */
	public void clusterData() {

	}

	/**
	 * Get the currently loaded clips. 
	 * @return the currently loaded clips. 
	 */
	public ArrayList<PAMClip> getPAMClips() {
		return pamClipManager.getCurrentClips();
	}

	/**
	 * Get the audio info. This gets information. 
	 * @return the audio info.
	 */
	public AudioInfo getAudioInfo() {
		return pamClipManager.getCurrentAudioInfo();
	}


	/**
	 * Get the parameters for AIPAM
	 * @return the main parameters class. 
	 */
	public AIPamParams getParams() {
		return this.aiPamParams;
	}

	/**
	 * Cluster the clips. 
	 */
	public void clusterClips() {

		//keep a copy of the last settings
		if (lastAiParams!=null) {
			this.lastAiParams.clusterParams = aiPamParams.clusterParams.clone();
		}
		
		//cancel the cluster task
		if (clusterTask!=null) {
			clusterTask.cancel(true);
		}
		
		clusterTask = pamClusterManager.clusterDataTask(pamClipManager.getCurrentClips(), this.aiPamParams); 
		updateMessageListeners(START_CLUSTERING_ALGORITHM, clusterTask); 

		clusterTask.setOnCancelled((value)->{
			//send notification when 
			updateMessageListeners(CANCEL_CLUSTERING_ALGORITHM, clusterTask); 
		});
		clusterTask.setOnSucceeded((value)->{
			//
			updateMessageListeners(END_CLUSTERING_ALGORITHM, clusterTask); 
		});

		Thread th = new Thread(clusterTask);
		th.setDaemon(true);
		th.start(); 
	}

	/**
	 * Get the last params during audio load or clustering. 
	 * @return the last AIPamParams used during audio load and/or clustering. 
	 */
	public AIPamParams getLastAiParams() {
		return lastAiParams;
	}


	/**
	 * Import clips. 
	 */
	public void importClips() {
		File file =new File(aiPamParams.audioFolder);
		if (file.exists()) {
			loadAudioData(new File(aiPamParams.audioFolder)); 
		}
		else {
			System.err.println("The file does not exist: " + aiPamParams.audioFolder);
			updateMessageListeners(NO_AUDIO_DIRECTORY, null); 
		}
	}

	/**
	 * The annotations manager. 
	 * @return the annotation manager
	 */
	public AnnotationManager getAnnotationManager() {
		return this.annotationManager;
	}


	/**
	 * Get the cluster manager
	 * @return - the cluster manager. 
	 */
	public ClipClusterManager getClusterManager() {
		return this.pamClusterManager;
	}


	/**
	 * Save settings to a file. 
	 * @param file - the file to save to (should be *.mat)
	 */
	public void saveSettings(File file) {
		MLStructure struct = settingsImportExport.settingsToStruct(this.aiPamParams); 
		ArrayList<MLArray> results = new ArrayList<MLArray>(); 
		results.add(struct); 
		settingsImportExport.saveMTFile(file, results);
	}


	public void loadSettings(File file) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Export clips to sub folders organised by annotation names. 
	 * @param file - the directory to export to. 
	 */
	public void exportAnnotations(File file) {
		
		ArrayList<String> annotatedClips = this.annotationManager.getAnnotaedClipFiles(); 
		ArrayList<String> exportedClips = this.annotationManager.getAnnotatedExportFiles(file); 
		
		//cancel the cluster task
		if (exportTask!=null) {
			exportTask.cancel(true);
		}
		
		exportTask = 	this.clipExporter.exportClipTask(annotatedClips, exportedClips, this.aiPamParams); 
		
		updateMessageListeners(START_CLIP_EXPORT, exportTask); 

		exportTask.setOnCancelled((value)->{
			//send notification when 
			updateMessageListeners(CANCEL_CLIP_EXPORT, exportTask); 
		});
		exportTask.setOnSucceeded((value)->{
			//
			updateMessageListeners(END_CLIP_EXPORT, exportTask); 
		});

		Thread th = new Thread(exportTask);
		th.setDaemon(true);
		th.start(); 
		
	}






}
