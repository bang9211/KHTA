
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
 * Dispatch: IVehicles
 * Description: IVehicles interface
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IVehicles extends DispatchPtr {
	public static final GUID DIID = new GUID("{6725f5de-fb66-4930-B222-E1051E0D3251}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IVehicles.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IVehicles (it is required by Jawin for some internal working though).
	 */
	public IVehicles() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IVehicles interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IVehicles(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IVehicles interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IVehicles(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IVehicles interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IVehicles interface on.
	 */
	public IVehicles(COMPtr comObject) throws COMException {
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
    * @return IUnknown
    **/
    public IUnknown get_NewEnum() throws COMException
    {
         return (IUnknown)get("_NewEnum");
    }
        
    /**
    *
    * @return void
    **/
    public IVehicle getItem(Variant index) throws COMException
    {
        IVehicle res = new IVehicle();
          DispatchPtr dispPtr = (DispatchPtr)get("Item", index);
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return int
    **/
    public int getCount() throws COMException
    {
        return ((Integer)get("Count")).intValue();
    }
        
    /**
    *
    * @return Variant
    **/
    public Object getIDs(String Attribute,Variant Value) throws COMException
    {
         return getN("IDs", new Object[] {Attribute,Value});
    }
        
    /**
    *
    
    * @param Number
    * @return void
    **/
    public IVehicle GetVehicleByNumber(int Number) throws COMException
    {
      IVehicle res = new IVehicle();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("GetVehicleByNumber", new Object[] {new Integer(Number)});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @param IDs
    * @param Attribute
    * @return void
    **/
    public void GetMultiAttValues(Variant IDs,String Attribute,Variant[] pValues) throws COMException
    {
      
		invokeN("GetMultiAttValues", new Object[] {IDs, Attribute, pValues});
        
    }
    /**
    *
    
    * @param IDs
    * @param Attribute
    * @param Values
    * @return void
    **/
    public void SetMultiAttValues(Variant IDs,String Attribute,Variant Values) throws COMException
    {
      
		invokeN("SetMultiAttValues", new Object[] {IDs, Attribute, Values});
        
    }
    /**
    *
    
    * @param Type
    * @param ZoneNr
    * @return void
    **/
    public IVehicle AddVehicleInZone(int Type,int ZoneNr) throws COMException
    {
      IVehicle res = new IVehicle();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("AddVehicleInZone", new Object[] {new Integer(Type), new Integer(ZoneNr)});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @param Type
    * @param ParkingID
    * @return void
    **/
    public IVehicle AddVehicleInParkingLot(int Type,int ParkingID) throws COMException
    {
      IVehicle res = new IVehicle();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("AddVehicleInParkingLot", new Object[] {new Integer(Type), new Integer(ParkingID)});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @param Type
    * @param DesiredSpeed
    * @param Link
    * @param Lane
    * @param XCoord
    * @param Interaction
    * @return void
    **/
    public IVehicle AddVehicleAtLinkCoordinate(int Type,double DesiredSpeed,int Link,int Lane,double XCoord,int Interaction) throws COMException
    {
      IVehicle res = new IVehicle();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("AddVehicleAtLinkCoordinate", new Object[] {new Integer(Type), new Double(DesiredSpeed), new Integer(Link), new Integer(Lane), new Double(XCoord), new Integer(Interaction)});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @param Type
    * @param FromNode
    * @param ToNode
    * @return void
    **/
    public IVehicle AddVehicleBetweenNodes(int Type,int FromNode,int ToNode) throws COMException
    {
      IVehicle res = new IVehicle();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("AddVehicleBetweenNodes", new Object[] {new Integer(Type), new Integer(FromNode), new Integer(ToNode)});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @param Type
    * @param TransitLineNr
    * @return void
    **/
    public IVehicle AddVehicleToTransitLine(int Type,int TransitLineNr) throws COMException
    {
      IVehicle res = new IVehicle();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("AddVehicleToTransitLine", new Object[] {new Integer(Type), new Integer(TransitLineNr)});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @param Number
    * @return void
    **/
    public void RemoveVehicle(int Number) throws COMException
    {
      
		invokeN("RemoveVehicle", new Object[] {new Integer(Number)});
        
    }
    /**
    *
    
    * @return void
    **/
    public IVehicles GetQueued() throws COMException
    {
      IVehicles res = new IVehicles();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("GetQueued", new Object[] {});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @return void
    **/
    public IVehicles GetArrived() throws COMException
    {
      IVehicles res = new IVehicles();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("GetArrived", new Object[] {});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @return void
    **/
    public IVehicles GetParked() throws COMException
    {
      IVehicles res = new IVehicles();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("GetParked", new Object[] {});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @return void
    **/
    public IVehicles GetDeparted() throws COMException
    {
      IVehicles res = new IVehicles();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("GetDeparted", new Object[] {});
          res.stealUnknown(dispPtr);
          return res;
        
    }
}
