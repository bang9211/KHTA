
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
 * Dispatch: ISignalController
 * Description: ISignalController interface
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class ISignalController extends DispatchPtr {
	public static final GUID DIID = new GUID("{0a2472b5-49dd-4ee1-9047-E982D175C83D}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, ISignalController.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * ISignalController (it is required by Jawin for some internal working though).
	 */
	public ISignalController() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the ISignalController interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public ISignalController(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the ISignalController interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public ISignalController(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the ISignalController interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the ISignalController interface on.
	 */
	public ISignalController(COMPtr comObject) throws COMException {
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
    public IDetectors getDetectors() throws COMException
    {
        IDetectors res = new IDetectors();
          DispatchPtr dispPtr = (DispatchPtr)get("Detectors");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISignalGroups getSignalGroups() throws COMException
    {
        ISignalGroups res = new ISignalGroups();
          DispatchPtr dispPtr = (DispatchPtr)get("SignalGroups");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    
    * @param channelId
    * @return int
    **/
    public int ReadChannel(int channelId) throws COMException
    {
      
		return ((Integer)invokeN("ReadChannel", new Object[] {new Integer(channelId)})).intValue();
        
    }
    /**
    *
    
    * @param channelId
    * @param Value
    * @return void
    **/
    public void WriteChannel(int channelId,int Value) throws COMException
    {
      
		invokeN("WriteChannel", new Object[] {new Integer(channelId), new Integer(Value)});
        
    }
    /**
    *
    * @return void
    **/
    public ISCDetRecord getSCDetRecord() throws COMException
    {
        ISCDetRecord res = new ISCDetRecord();
          DispatchPtr dispPtr = (DispatchPtr)get("SCDetRecord");
          res.stealUnknown(dispPtr);
          return res;
    }
        
}
