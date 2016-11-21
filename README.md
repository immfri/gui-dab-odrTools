# gui-dab-odrTools

Creating a graphical user interface (GUI) written by JavaFX that controls the configuration and the monitoring of the DAB [ODR-mmbTools] (https://github.com/Opendigitalradio). The following tasks should be realized:

## 1. Configuration:
* At the beginning created a new configuration. Alternative open a configuration-file. 
* The Configuration in first three Tabs from GUI (Multiplex, Components, Subchannels) based on **ODR-DabMux**. 
* The Configuration in "Inputs"-Tab based on **ODR-AudioEnc** and **ODR-PadEnc**. In the future a Data-Encoder could be added. 
* The Configuration in "Outputs"-Tab based on **ODR-DabMux** and **ODR-DabMod**.
* All about Logging and Remote as well as useful Parameters (e.g. EMail-Address) can be found under Settings. 
* Between the ODR-Tools is always used ZeroMQ.
* If the multiplex configuration is successfully, could be started.

## 2. Execution:
* All necessary tools must be available before the execution. If doesn't, the execution wouldn't start.
* The software controls the complete run of all required tools (ODR-Tools, JACK, ...).
* If the execution isn’t possible, the user would be always informed. There can continue without missing optionally tools or stop the transmission.

## 3. Monitoring:
* The operating status of the multiplex (maybe with [dablin] (https://github.com/Opendigitalradio/dablin)) and odrTools should be visible.
* If any component has an error, an alert appears about the failure. Additional will be sent an email.
* The GUI could be controls and checked via remote-software (e.g. Team Viewer, VNC).

## 4. Software-Status:
JAR-file without functionality only gui-elements (v0.2) -> **coming soon**
    
    << I would be grateful for more suggestions and ideas >>
 
Cheers 

Immanuel F.
