package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import model.output.*;



public class Multiplex {
	
	private static Multiplex instance;	
	
	private StringProperty dabMode, managementport, telnetPort;
	private BooleanProperty syslog, writescca, tist;
	private IntegerProperty nbframes, totalCU;
	
	private Ensemble ensemble;
	
	private ObservableList<Service> serviceList;
	private ObservableList<Component> componentList;
	private ObservableList<Subchannel> subchannelList;	
	private ObservableList<Output> outputList;
	
	
	
	private Multiplex() {		
		
		dabMode = 			new SimpleStringProperty("1");	
		nbframes = 			new SimpleIntegerProperty(0);
		managementport = 	new SimpleStringProperty("0"); 		// TODO Statistic Port -> Operation GUI
		telnetPort = 		new SimpleStringProperty("0");		// TODO RemoteControl Port -> Op. GUI
		syslog = 			new SimpleBooleanProperty(false);
		writescca = 		new SimpleBooleanProperty(false);
		tist =				new SimpleBooleanProperty(false);
		totalCU = 			new SimpleIntegerProperty(0);
		
		ensemble = 			new Ensemble();
		
		serviceList = 		FXCollections.observableArrayList();
		componentList = 	FXCollections.observableArrayList();	
		subchannelList = 	FXCollections.observableArrayList();
		outputList =  		FXCollections.observableArrayList();
		
		subchannelList.addListener((ListChangeListener<Subchannel>) change -> updateCU());	
//		serviceList.addListener(new ListChangeListener<Service>() {
//			@Override
//			public void onChanged(Change<? extends Service> c) {
//				while (c.next()) {
//					if (c.wasAdded()) {
//						System.out.println("add");
//						for (Service addService: c.getAddedSubList()) {
//							serviceNameList.add(addService.getName());
//						}
//					}
//					else if (c.wasRemoved()) {
//						System.out.println("rem");
//						for (Service remService: c.getRemoved()) {
//							serviceNameList.remove(remService.getName());
//						}
//					}
//					else  {
//						System.out.println("update");
//						serviceNameList.clear();
//						for (Service service: serviceList) {
//							serviceNameList.add(service.getName());
//						}
//					}
//					
//				}
//			}
//		});
		
		
//		subchannelList.addListener(new ListChangeListener<Subchannel>() {
//			@Override
//			public void onChanged(Change<? extends Subchannel> c) {
//				while (c.next()) {
//					if (c.wasAdded()) {
//						for (Subchannel addSubCh: c.getAddedSubList()) {
//							cu.add(addSubCh.getInput().getCu().get());
//							System.out.println(addSubCh.getInput().getCu().get());
//						}
//					}
//					if (c.wasRemoved()) {
//						for (Subchannel remSubCh: c.getRemoved()) {
//							totalCU.add(remSubCh.getInput().getCu().negate());
//						}
//					}
//				}
//			}
//		});
		
		
		
	}
	
	public static synchronized Multiplex getInstance() {
	    if (Multiplex.instance == null) {
	      Multiplex.instance = new Multiplex();
	    }
	    return Multiplex.instance;
	}
	
	public ObservableList<String> getDabModeList() {
		return FXCollections.observableArrayList("1","2","3","4");
	}
	

	public StringProperty getDabMode() {
		return dabMode;
	}

	public IntegerProperty getNbframes() {
		return nbframes;
	}

	public StringProperty getManagementport() {
		return managementport;
	}

	public StringProperty getTelnetPort() {
		return telnetPort;
	}

	public BooleanProperty getSyslog() {
		return syslog;
	}

	public BooleanProperty getWritescca() {
		return writescca;
	}

	public BooleanProperty getTist() {
		return tist;
	}

	public IntegerProperty getTotalCU() {
		return totalCU;
	}

	public Ensemble getEnsemble() {
		return ensemble;
	}

	public ObservableList<Subchannel> getSubchannelList() {
		return subchannelList;
	}

	public ObservableList<Service> getServiceList() {
		return serviceList;
	}

	public ObservableList<Output> getOutputList() {
		return outputList;
	}

	public ObservableList<Component> getComponentList() {
		return componentList;
	}

	public void updateCU() {
		int cu = 0;
		for (Subchannel subCh: Multiplex.getInstance().getSubchannelList()) {
			cu += subCh.getInput().getCu().get();
		}
		totalCU.set(cu);
	}
	
	
}
