
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
 * Dispatch: ITravelTimes
 * Description: Interface to access travel time collections.
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class ITravelTimes extends DispatchPtr {
	public static final GUID DIID = new GUID("{ba5b7e74-2590-4965-B03B-9C2E945D558E}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, ITravelTimes.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * ITravelTimes (it is required by Jawin for some internal working though).
	 */
	public ITravelTimes() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the ITravelTimes interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public ITravelTimes(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the ITravelTimes interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public ITravelTimes(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the ITravelTimes interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the ITravelTimes interface on.
	 */
	public ITravelTimes(COMPtr comObject) throws COMException {
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
    public ITravelTime getItem(int index) throws COMException
    {
        ITravelTime res = new ITravelTime();
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
    
    * @param Number
    * @return void
    **/
    public ITravelTime GetTravelTimeByNumber(int Number) throws COMException
    {
      ITravelTime res = new ITravelTime();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("GetTravelTimeByNumber", new Object[] {new Integer(Number)});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @param Attribute
    * @param Value
    * @param Compare
    * @return void
    **/
    public void GetIDs(Variant[] pIDs,String Attribute,Variant Value,short Compare) throws COMException
    {
      
		invokeN("GetIDs", new Object[] {pIDs, Attribute, Value, new Short(Compare)});
        
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
}
