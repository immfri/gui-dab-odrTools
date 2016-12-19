# gui-dab-odrTools

Creating a graphical user interface (GUI) written by JavaFX that controls the configuration of the DAB [ODR-mmbTools](https://github.com/Opendigitalradio).


## 1. New/Open Configuration:
At the beginning created a new configuration. Alternative open a configuration-file. 

## 2. Configure:
* The Configuration in first three Tabs from GUI (Multiplex, Components, Subchannels) based on **ODR-DabMux**. 
* The Configuration in "Subchannels"-Tab based on **ODR-AudioEnc** and **ODR-PadEnc**. In the future a Data-Encoder could be added. 
* The Configuration in "Outputs"-Tab based on **ODR-DabMux** and **ODR-DabMod**. Between the ODR-Tools is always used ZeroMQ, with generated ports.

### 2.1 Specially:
Subchannel supported Input from Audio and Data.

*Audio-Inputs:*

  * File (play Audiofile with MPlayer in loop over JACK input)  
  * Stream (read webstream over libvlc)
  * ALSA  
  * JACK

*Outputs (combine DabMux and DabMod):*

* Device (FarSync Card, Ettus USRP)
* File (ETI-NI File, I/Q-Samples File)
* Network (EDI, ETI: UDP, TCP; ZeroMQ: ETI, I/Q-Samples;) 

## 3. Save Configuration:
If the configuration is to be stored, the user selects a folder or create a new one.

## 4. Execution:
If the complete configuration is without errors, it can be executed.


## Required:
* ODR-DabMux	>= v1.0
* ODR-DabMod 	>= v0.6
* ODR-AudioEnc 	>= v2.0
* ODR-PadEnc 	>= v2.0
* JAVA JRE Version >= 8

    
    << I would be grateful for more suggestions and ideas >>
 
Cheers 

Immanuel F.