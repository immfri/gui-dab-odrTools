# gui-dab-odrTools

Creating a graphical user interface (GUI) written by JavaFX that controls the configuration and the monitoring of the DAB [ODR-mmbTools] (https://github.com/Opendigitalradio). The following tasks should be realized:

## 1. Configuration:

* At the beginning created a new multiplex configuration. Alternative open a configuration-file of Multiplex.

* The configuration starts with **Audio** section. Following are possible to select: 
 * Audio/Playlist (File or Stream)
 * Soundcard (with ALSA/JACK)
 
* The next tab are the **PAD**. Supported data services such as DLS (incl. DL+) and MOT SLS can be used.
	
* Following added Audio/Data are in **Services** and configured the **Ensemble** of DAB. 

* At the end must be set up the **Output** from multiplex:
 * Streams: ETI/EDI/ZeroMQ
 * Files: I/Q-Samples/ETI-NI 
 * Supported Devices: Ettus USRP, GatesAir Maxiva (The configuration could be done without especially web-browser)

* If the multiplex configuration is successful, could be started.


## 2. Execution:

* The software controls the complete run of all required tools (odrTools, MPlayer, JACK, ...).

* If the execution isn’t possible, the user would be always informed. There can continue without missing optionally tools or stop the transmission.

* A un-/installation of the all tools aren’t possible, only for uses.


## 3. Monitoring:

* The operating status of the multiplex and odrTools should be visible.
 * mutiplex check maybe with [dablin] (https://github.com/Opendigitalradio/dablin)
 * odrTools check with a seperate window
 
* If any component has an error, an alert appears about the failure. Additional will be sent an email.

* The GUI could be checked via remote-software (e.g. Team Viewer, VNC).

## 4. Settings:

comming soon
    
    << I would be grateful for more suggestions and ideas >>
 
Cheers 

Immanuel F.
