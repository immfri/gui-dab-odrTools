# gui-dab-odrTools

Creating a graphical user interface (GUI) written by JavaFX that controls the configuration of the DAB [ODR-mmbTools](https://github.com/Opendigitalradio).


## 1. New/Open Configuration:
At the beginning created a new configuration. Alternative open a configuration-file. 

Note:     Loading externally created configuration doesn't work to 100%
Reason:   The complexity of configuration settings!
Solution: Save the complete configuration by GUI and open it.

## 2. Configure:
* The Configuration in first three Tabs from GUI (Multiplex, Components, Subchannels) based on **ODR-DabMux**. 
* The Configuration in "Subchannels"-Tab based on **ODR-AudioEnc** and **ODR-PadEnc**. In the future a Data-Encoder could be added. 
* The Configuration in "Outputs"-Tab based on **ODR-DabMux** and **ODR-DabMod**. Between the ODR-Tools is always used ZeroMQ, with generated ports.

### 2.1 Specially:
Subchannel supported Input from Audio and Data.

*Audio-Inputs:*
 
  * Webstream (over libvlc)
  * ALSA  
  * JACK

*Outputs (combine DabMux and DabMod):*

* Device (FarSync Card, Ettus USRP)
* File (ETI-NI File, I/Q-Samples File)
* Network (EDI, ETI: UDP, TCP; ZeroMQ: ETI, I/Q-Samples;) 

## 3. Save Configuration:
If the configuration is to be stored, the user selects a folder or create a new one.

Files (don't rename it) inside project folder:
* dab.mux: Multiplex Configuration File 
* xxxx.mod: Modulator Configuration File for DabMod (filename equals output-name in dab.mux)
* dab.sh: Bash-File to start all processes with screen (GUI didn't execute this skript, only the processes without screen)

## 4. Execution:
If the complete configuration is without errors, it can be executed.

## 5. Monitoring
The GUI managed all Processes from ODR-Tools. If a process crashes or terminates, the user would be informed over a Alert-Window and additionally with E-Mail.

## 6. Addition
* Remote Control: TeamViewer, VNC, ...
* Check ETI-Stream: dablin_gtk (https://github.com/Opendigitalradio/dablin) -> need to create a ETI-File-Output (FIFO) for dablin input.
* Audio VU-meter: e.g. pavumeter (sudo  apt-get install pavumeter)
* E-Mail-Log: need configure 'postfix' with exist Mail-Account (https://help.ubuntu.com/community/Postfix) and enter receiver Mail-Address in the GUI

## Required:
* ODR-DabMux	>= v1.1.0
* ODR-DabMod 	>= v0.5.4
* ODR-AudioEnc 	>= v2.0.0
* ODR-PadEnc 	>= v2.0.0
* JAVA JRE Version >= 8

## Pre-Installation
* Check the required Softwares
* Under UNIX => Install Open-JavaFX ("sudo apt-get install openjfx")

## Installation:
* Set the executable Bit under Settings and run with double click
* open Console and start GUI with command "java -jar /path/of/the/jar/file.jar" 

## Status: v0.5 - finally 

* Open a new Configuration 	**==> work**
* Open a Configuration 		**==> work**
* Configure 				**==> work**
* Save 						**==> work**
* Execution					**==> work**
* Monitoring				**==> work**


<< Immanuel F. >>