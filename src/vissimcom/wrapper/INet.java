
package vissimcom.wrapper;

import org.jawin.*;
import org.jawin.constants.*;
import org.jawin.marshal.*;
import org.jawin.io.*;
import java.io.*;
import java.util.Date;

/**
 * Jawin generated code please do not edit
 *
 * Dispatch: INet
 * Description: Net interface
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class INet extends DispatchPtr {
	public static final GUID DIID = new GUID("{8d92fe46-02c5-4582-881E-B12182061F7F}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, INet.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * INet (it is required by Jawin for some internal working though).
	 */
	public INet() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the INet interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public INet(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the INet interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public INet(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the INet interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the INet interface on.
	 */
	public INet(COMPtr comObject) throws COMException {
		super(comObject);
	}

	public int getIIDToken() {
		return IID_TOKEN;
	}
	
	
    /**
    *
    
    * @return void
    **/
    /*public void QueryInterface(Object[] riid,void[] 
        [] ppvObj) throws COMException
    {
      
		invokeN("QueryInterface", new Object[] {riid, ppvObj});
        
    }*/
    /**
    *
    
    * @return int
    **/
    /*public int AddRef() throws COMException
    {
      
		return ((Integer)invokeN("AddRef", new Object[] {})).intValue();
        
    }*/
    /**
    *
    
    * @return int
    **/
    /*public int Release() throws COMException
    {
      
		return ((Integer)invokeN("Release", new Object[] {})).intValue();
        
    }*/
    /**
    *
    
    * @return void
    **/
    /*public void GetTypeInfoCount(int[] pctinfo) throws COMException
    {
      
		invokeN("GetTypeInfoCount", new Object[] {pctinfo});
        
    }*/
    /**
    *
    
    * @param itinfo
    * @param lcid
    * @return void
    **/
    /*public void GetTypeInfo(int itinfo,int lcid,void[] 
        [] pptinfo) throws COMException
    {
      
		invokeN("GetTypeInfo", new Object[] {new Integer(itinfo), new Integer(lcid), pptinfo});
        
    }*/
    /**
    *
    
    * @param cNames
    * @param lcid
    * @return void
    **/
    /*public void GetIDsOfNames(Object[] riid,int[] 
        [] rgszNames,int cNames,int lcid,int[] rgdispid) throws COMException
    {
      
		invokeN("GetIDsOfNames", new Object[] {riid, rgszNames, new Integer(cNames), new Integer(lcid), rgdispid});
        
    }*/
    /**
    *
    
    * @param dispidMember
    * @param lcid
    * @param wFlags
    * @return void
    **/
    /*public void Invoke(int dispidMember,Object[] riid,int lcid,short wFlags,Object[] pdispparams,Variant[] pvarResult,Object[] pexcepinfo,int[] puArgErr) throws COMException
    {
      
		invokeN("Invoke", new Object[] {new Integer(dispidMember), riid, new Integer(lcid), new Short(wFlags), pdispparams, pvarResult, pexcepinfo, puArgErr});
        
    }*/
    /**
    *
    * @return int
    **/
    public int getID() throws COMException
    {
        return ((Integer)get("ID")).intValue();
    }
        
    /**
    *
    * @param ID
    **/
    public void setID(int ID) throws COMException
    {
        put("ID", ID);
    }
        
    /**
    *
    * @return String
    **/
    public String getName() throws COMException
    {
         return (String)get("Name");
    }
        
    /**
    *
    * @param Name
    **/
    public void setName(String Name) throws COMException
    {
        put("Name", Name);
    }
        
    /**
    *
    * @return Variant
    **/
    public Object getAttValue(String Attribute) throws COMException
    {
         return get("AttValue", Attribute);
    }
        
    /**
    *
    * @param AttValue
    **/
    public void setAttValue(String Attribute,Variant newValue2) throws COMException
    {
        put("AttValue", Attribute, newValue2);
    }
        
    /**
    *
    * @return void
    **/
    public ILinks getLinks() throws COMException
    {
        ILinks res = new ILinks();
          DispatchPtr dispPtr = (DispatchPtr)get("Links");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public INodes getNodes() throws COMException
    {
        INodes res = new INodes();
          DispatchPtr dispPtr = (DispatchPtr)get("Nodes");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPaths getpaths() throws COMException
    {
        IPaths res = new IPaths();
          DispatchPtr dispPtr = (DispatchPtr)get("paths");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IVehicles getVehicles() throws COMException
    {
        IVehicles res = new IVehicles();
          DispatchPtr dispPtr = (DispatchPtr)get("Vehicles");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISignalControllers getSignalControllers() throws COMException
    {
        ISignalControllers res = new ISignalControllers();
          DispatchPtr dispPtr = (DispatchPtr)get("SignalControllers");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IDataCollections getDataCollections() throws COMException
    {
        IDataCollections res = new IDataCollections();
          DispatchPtr dispPtr = (DispatchPtr)get("DataCollections");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IQueueCounters getQueueCounters() throws COMException
    {
        IQueueCounters res = new IQueueCounters();
          DispatchPtr dispPtr = (DispatchPtr)get("QueueCounters");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IVehicleInputs getVehicleInputs() throws COMException
    {
        IVehicleInputs res = new IVehicleInputs();
          DispatchPtr dispPtr = (DispatchPtr)get("VehicleInputs");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IRoutingDecisions getRoutingDecisions() throws COMException
    {
        IRoutingDecisions res = new IRoutingDecisions();
          DispatchPtr dispPtr = (DispatchPtr)get("RoutingDecisions");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ITravelTimes getTravelTimes() throws COMException
    {
        ITravelTimes res = new ITravelTimes();
          DispatchPtr dispPtr = (DispatchPtr)get("TravelTimes");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IDelays getDelays() throws COMException
    {
        IDelays res = new IDelays();
          DispatchPtr dispPtr = (DispatchPtr)get("Delays");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ITrafficCompositions getTrafficCompositions() throws COMException
    {
        ITrafficCompositions res = new ITrafficCompositions();
          DispatchPtr dispPtr = (DispatchPtr)get("TrafficCompositions");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IDrivingBehaviorParSets getDrivingBehaviorParSets() throws COMException
    {
        IDrivingBehaviorParSets res = new IDrivingBehaviorParSets();
          DispatchPtr dispPtr = (DispatchPtr)get("DrivingBehaviorParSets");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IParkingLots getParkingLots() throws COMException
    {
        IParkingLots res = new IParkingLots();
          DispatchPtr dispPtr = (DispatchPtr)get("ParkingLots");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPTCallingPoints getPTCallingPoints() throws COMException
    {
        IPTCallingPoints res = new IPTCallingPoints();
          DispatchPtr dispPtr = (DispatchPtr)get("PTCallingPoints");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IStopSigns getStopSigns() throws COMException
    {
        IStopSigns res = new IStopSigns();
          DispatchPtr dispPtr = (DispatchPtr)get("StopSigns");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IStaticObjects getStaticObjects() throws COMException
    {
        IStaticObjects res = new IStaticObjects();
          DispatchPtr dispPtr = (DispatchPtr)get("StaticObjects");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IDesiredSpeedDecisions getDesiredSpeedDecisions() throws COMException
    {
        IDesiredSpeedDecisions res = new IDesiredSpeedDecisions();
          DispatchPtr dispPtr = (DispatchPtr)get("DesiredSpeedDecisions");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IReducedSpeedAreas getReducedSpeedAreas() throws COMException
    {
        IReducedSpeedAreas res = new IReducedSpeedAreas();
          DispatchPtr dispPtr = (DispatchPtr)get("ReducedSpeedAreas");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ITransitLines getTransitLines() throws COMException
    {
        ITransitLines res = new ITransitLines();
          DispatchPtr dispPtr = (DispatchPtr)get("TransitLines");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ITransitStops getTransitStops() throws COMException
    {
        ITransitStops res = new ITransitStops();
          DispatchPtr dispPtr = (DispatchPtr)get("TransitStops");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedTypes getPedTypes() throws COMException
    {
        IPedTypes res = new IPedTypes();
          DispatchPtr dispPtr = (DispatchPtr)get("PedTypes");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedClasses getPedClasses() throws COMException
    {
        IPedClasses res = new IPedClasses();
          DispatchPtr dispPtr = (DispatchPtr)get("PedClasses");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedPedestrians getPedPedestrians() throws COMException
    {
        IPedPedestrians res = new IPedPedestrians();
          DispatchPtr dispPtr = (DispatchPtr)get("PedPedestrians");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedAreas getPedAreas() throws COMException
    {
        IPedAreas res = new IPedAreas();
          DispatchPtr dispPtr = (DispatchPtr)get("PedAreas");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedInputs getPedInputs() throws COMException
    {
        IPedInputs res = new IPedInputs();
          DispatchPtr dispPtr = (DispatchPtr)get("PedInputs");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedRoutingDecisions getPedRoutingDecisions() throws COMException
    {
        IPedRoutingDecisions res = new IPedRoutingDecisions();
          DispatchPtr dispPtr = (DispatchPtr)get("PedRoutingDecisions");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedTravelTimes getPedTravelTimes() throws COMException
    {
        IPedTravelTimes res = new IPedTravelTimes();
          DispatchPtr dispPtr = (DispatchPtr)get("PedTravelTimes");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    
    * @param Angle
    * @return void
    **/
    public void Rotate(double Angle) throws COMException
    {
      
		invokeN("Rotate", new Object[] {new Double(Angle)});
        
    }
    /**
    *
    
    * @param X
    * @param Y
    * @param Z
    * @return void
    **/
    public void Translate(double X,double Y,double Z) throws COMException
    {
      
		invokeN("Translate", new Object[] {new Double(X), new Double(Y), new Double(Z)});
        
    }
    /**
    *
    * @return void
    **/
    public IPedWalkingBehaviorParSets getPedWalkingBehaviorParSets() throws COMException
    {
        IPedWalkingBehaviorParSets res = new IPedWalkingBehaviorParSets();
          DispatchPtr dispPtr = (DispatchPtr)get("PedWalkingBehaviorParSets");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedAreaBehaviorTypes getPedAreaBehaviorTypes() throws COMException
    {
        IPedAreaBehaviorTypes res = new IPedAreaBehaviorTypes();
          DispatchPtr dispPtr = (DispatchPtr)get("PedAreaBehaviorTypes");
          res.stealUnknown(dispPtr);
          return res;
    }
        
}
