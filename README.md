# aipam

# Introduction #
A Java and JavaFX based program to visualise and cluster sound clips which can then be annotated and export by manual annotation. 
<p>
In passive acoustic monitoring it is often easy to make a <b>detector</b>, i.e. some algorithm which picks out all or a subset of interesting sounds. However making a <b>classifier</b> which accurately picks out a very particular subset of sound types is often difficult, due to varietation within species, differnet noise conditions etc. Machine learning is helping in this area however
humans remain the best pattern recognition machines, we have inititative and can deal with unexpected consitancies in datasets. 
<p>
AIPAM (working name) is designed to be used in a situation where there are lots of possible detections and provides an efficient data visualisation system for a human to then explore and manually annotate a dataset. The clips can be clusterred using the Barnes-Hut implementation of the t-SNE algorithm whihc organises clips and allows more rapid exploration and annotation of the datatset. 



# Installation #

AIPAM is based on JavaFX and requires the Java vitual machine to run.

The program allows the viewing of thousands / gigabytes of clips, however this requires several virtual machine arguments to ensure 
the program Java and JavaFX can be allocated sufficient memory.

```
  -Xmx4098m
  -Dprism.maxvram=2000m
  
```

A good graphics card is recommended. 

# Libraries #

There are multiple libraries used in the program
