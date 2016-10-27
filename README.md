# gui-dab-odrTools

Task description:
Creating a graphical user interface (GUI) written by JavaFX that controls the configuration and the monitoring of the ODR-mmbTools. In the first step the implementations without client-server-system. The following tasks should be realized in the GUI:

1. Configuration:
•	At the beginning created a new multiplex configuration. Alternative open a Multiplex-Configuration file.

•	The configuration starts with input section. Following are possible to select: 
   o	Audio-/Playlist-File
   o	Soundcard

•	 Added are the individual services and configured there (audio data rate, protection level, labels ...). Supported data services such as DLS and MOT SLS can be used.

•	If the configured multiplex is ready, must be set up the output: 
   o	Streams: EDI, ETI, ZeroMQ
   o	Files: I/Q-Samples, ETI 
   o	Supported Devices: Ettus USRP, GatesAir Maxiva

•	If the USRRP is selected, then in the modulator configuration, frequency and other settings they don’t be missing here.

•	The multiplex in successful configuration can be started.

2. Execution:
•	The GUI software controls the complete run of the tools.

•	If the execution isn’t possible, the user would be always informed. There can continue without missing optionally tools or stop the transmission.

•	A un-/installation of the OS software components aren’t possible, only for uses.

3. Monitoring:
•	The operating status of the multiplex or the mmbTools should always be visible.

•	If the component has an error, an alert appears about the failure. Additional will be sent an email.

•	Audio programs visualize the gain level and listen there selected streams

•	Optional the configuration of GatesAir VHF-Transmitter could be done with the GUI, without especially browser.

•	The complete monitoring could be checked via remote-software (e.g. Team Viewer).

    << I would be grateful for more suggestions and ideas >>
 
Cheers 
Immanuel F.
