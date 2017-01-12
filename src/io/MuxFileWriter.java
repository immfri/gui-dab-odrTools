package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import model.Element;
import model.output.Output;


public class MuxFileWriter extends FileWriter {
	
	
	public MuxFileWriter(File file, String sectionName, Object object, int valuesFrom, int valuesTo) throws IOException {
		super(file);
		
		writeSection(sectionName, object, valuesFrom, valuesTo);
	}
	
	
	public void writeSection(String sectionName, Object object, int valuesFrom, int valuesTo) throws IOException {
		
		// start of section
		this.write(sectionName +" {\n");
		
		// Attributes
		Field[] fields = object.getClass().getDeclaredFields();
		
		for (int index = valuesFrom; index < valuesTo; index++){
			fields[index].setAccessible(true);

			Object value = null;
			try {
				value = ((Property<?>) fields[index].get(object)).getValue();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			// No value
			if (value != null) { 

				// Value is not empty
				if (!value.toString().isEmpty()) {
					String name = fields[index].getName();
					
					// change character "_" -> "-"
					if (name.contains("_")) {
						name = name.replace('_', '-');
					}
					
					// Writing
					if (name.contains("label")) {
						this.write("\t"+ name +" \""+ value +"\"\n");
					} 
					else {
						this.write("\t"+ name +" "+ value +"\n");	
					} 
				}
			}
		}
		// End of section
		this.write("}\n\n");	
	}


	public void writeSectionList(String sectionName, ObservableList<?> list, int valuesFrom, int valuesTo) throws IOException {
		
		// start of section
		this.write(sectionName +" {\n");
		
		// List
		for (Object object: list) {
			
			// get All Attributes
			ArrayList<Field> fields = new ArrayList<>(Arrays.asList(object.getClass().getDeclaredFields()));
			fields.addAll(0, Arrays.asList(object.getClass().getSuperclass().getDeclaredFields()));
			
			// writeAttributes 
			for (int index = valuesFrom; index < valuesTo; index++){
				fields.get(index).setAccessible(true);
				
				Object value = null;
				try {
					// Value is a Service or Subchannel - Object
					if (sectionName.contains("components") && (index==1 || index==2)) {
						if ((Element) fields.get(index).get(object) != null) {
							value = ((Element) fields.get(index).get(object)).getName().getValue();
						} 	
					}
					else {	// Value is a Property<?>
						value = ((Property<?>) fields.get(index).get(object)).getValue();
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();			
				}
				
				// value exist
				if (value != null) { 	

					// Value is not empty
					if (!value.toString().isEmpty()) {
						
						// Write Section Name
						if (index == 0) {
							this.write("\t"+ value +" {\n");
						} 
						else {
							String name = fields.get(index).getName();
						
							// change character "_" -> "-"
							if (name.contains("_")) {
								name = name.replace('_', '-');
							}
							
							// Writing
							try {
								
								if (name.contentEquals("inputfile") || name.contains("label")) {
									this.write("\t\t"+ name +" \""+ value +"\"\n");
								} 
								
								else if (name.contentEquals("protection")) {
									this.write("\t\t"+ name +" "+ value.toString().substring(0,1) +"\n");
								}
								
								else if (name.contentEquals("encryption") || name.contentEquals("datagroup")) {
									if ((boolean)value == true) this.write("\t\t"+ name +" 1\n");
								}
								
								else if (name.contentEquals("datagroup")) {
									if ((boolean)value == true) this.write("\t\t"+ name +" " + value + "\n");
								}
						
								else {
									this.write("\t\t"+ name +" "+ value +"\n");
								}
							
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			// End of sub-section
			this.write("\t}\n");
		}
		// End of section
		this.write("}\n\n");			
	}


	public void writeOutputList(ObservableList<Output> list) throws IOException {
		
		boolean needSimul = false;
		
		// start of section
		this.write("outputs {\n");
		
		// check if edi-output exist
		for (Output output: list) {
			
			if (output.getFormat().getValue().contains("edi")) {				
				needSimul = true;
			
				// print first edi from List
				this.write("\tedi {\n");
				break;
			}
		}	
		
		if (needSimul) {
			
			for (Output output: list) {

				// edi
				if (output.getFormat().getValue().contains("edi")) {
					needSimul = true;

					// get All Attributes from edi
					ArrayList<Field> fields = new ArrayList<>(Arrays.asList(output.getClass().getDeclaredFields()));	//edi
					fields.addAll(0, Arrays.asList(output.getClass().getSuperclass().getDeclaredFields()));				//network
					fields.addAll(0, Arrays.asList(output.getClass().getSuperclass().getSuperclass().getDeclaredFields()));	//output
					fields.addAll(0, Arrays.asList(output.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredFields()));	// element

					// write Attributes 
					for (int index = 0; index < 8; index++) {
						fields.get(index).setAccessible(true);

						Object value = null;
						try {
							value = ((Property<?>) fields.get(index).get(output)).getValue();
						} catch (IllegalAccessException e) {
							e.printStackTrace();	
						}

						// value exist
						if (value != null) { 	

							// Value is not empty
							if (!value.toString().isEmpty()) {

								// Write Section Name
								if (index == 0) {
									this.write("\t\t"+ value +" {\n");
								} 
								else if (index > 1) {
									String name = fields.get(index).getName();

									// Writing
									if (name.contains("destination") || name.contentEquals("source")) {
										this.write("\t\t\t"+ name +" \""+ value +"\"\n");
									} 
									else if (!name.contains("multicast") && !name.contentEquals("port")) {
										this.write("\t\t\t"+ name +" "+ value +"\n");
									}
								}
							}
						}
					}
					this.write("\t\t}\n");
				} 
			}	

			// write advanced EDI Attributes
			for (Output output: list) {
				if (output.getFormat().getValue().contains("edi")) {

					ArrayList<Field> fields = new ArrayList<>(Arrays.asList(output.getClass().getDeclaredFields()));	//edi
					fields.addAll(0, Arrays.asList(output.getClass().getSuperclass().getDeclaredFields()));				//network
					fields.addAll(0, Arrays.asList(output.getClass().getSuperclass().getSuperclass().getDeclaredFields()));	//output

					for (int index = 2; index < 14; index++) {
						fields.get(index).setAccessible(true);

						Object value = null;
						try {
							value = ((Property<?>) fields.get(index).get(output)).getValue();
						} catch (IllegalAccessException e) {
							e.printStackTrace();	
						}

						// value exist
						if (value != null) { 	

							// Value is not empty
							if (!value.toString().isEmpty()) {

								String name = fields.get(index).getName();
								this.write("\t\t"+ name +" "+ value +"\n");
							}
						}
						if (index == 2) index = 7;
					}
					this.write("\t}\n");
					break;
				}
			}
		}
		
		// another outputs (not edi)
		for (Output output: list) {
			
			if (!output.getFormat().getValue().contains("edi")) {
				ArrayList<Field> fields = new ArrayList<>(Arrays.asList(output.getClass().getDeclaredFields()));	
				fields.addAll(0, Arrays.asList(output.getClass().getSuperclass().getDeclaredFields()));	
				fields.addAll(0, Arrays.asList(output.getClass().getSuperclass().getSuperclass().getDeclaredFields()));	
				
				
				for (int i=0; i < fields.size(); i++) {
					fields.get(i).setAccessible(true);
					
					// Output Format
					String format = output.getFormat().getValue();

					Object value = null;
					try {
						if (format.contains("fifo") && i == 4) {
							value = null;
						}
						else if (format.contains("zmq") && i == 3) {
							value = null;
						}
						else {
							value = ((Property<?>) fields.get(i).get(output)).getValue();
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();	
					}

					// value exist
					if (value != null) { 	

						// Value is not empty
						if (!value.toString().isEmpty()) {
							
							switch (i) {
							case 0:
								this.write("\t"+ value);
								break;
								
							case 1: 
								this.write(" \"" + value + "://");
								break;
								
							case 2:
								// UDP/TCP Outputs
								if (format.contentEquals("udp") || format.contentEquals("tcp")) {
									this.write(value + ":");
									break;
								}
								// File Output
								if (format.contains("fifo")) {
									this.write(value + "?type=");
									break;	
								}
								
								if (format.contains("zmq")) {
									this.write("*:" + value);
									break;	
								}
							
							case 3:
								this.write("" + value);
								break;
							
							case 4:
								this.write("?src=" + value);
								break;
								
							case 5:
								this.write(",ttl=" + value);
							}							
						}
					}
				}
				this.write("\"\n");
			}
			
			// set up for real-time
			if (output.getFormat().getValue().contains("zmq")) {
				needSimul = true;
			}
		}
		
		// check if need simul://
		if (needSimul) {
			this.write("\n\tthrottle \"simul://\"\n");
		}	
		
		// End of section
		this.write("}");	
	}
}
