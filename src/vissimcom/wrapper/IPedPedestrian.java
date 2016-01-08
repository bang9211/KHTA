
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
 * Dispatch: IPedPedestrian
 * Description: Interface to access and modify pedestrians.
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IPedPedestrian extends DispatchPtr {
	public static final GUID DIID = new GUID("{66bbaaeb-5b96-4584-9BC0-66C7104C3F69}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IPedPedestrian.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IPedPedestrian (it is required by Jawin for some internal working though).
	 */
	public IPedPedestrian() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IPedPedestrian interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IPedPedestrian(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IPedPedestrian interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IPedPedestrian(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IPedPedestrian interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IPedPedestrian interface on.
	 */
	public IPedPedestrian(COMPtr comObject) throws COMException {
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
    * @return Variant
    **/
    public Object getAttValue1(String Attribute,Variant Parameter) throws COMException
    {
         return getN("AttValue1", new Object[] {Attribute,Parameter});
    }
        
    /**
    *
    * @param AttValue1
    **/
    public void setAttValue1(String Attribute,Variant Parameter,Variant newValue3) throws COMException
    {
        putN("AttValue1", new Object[] {Attribute, Parameter} , newValue3);
    }
        
    /**
    *
    * @return Variant
    **/
    public Object getAttValue2(String Attribute,Variant Parameter1,Variant Parameter2) throws COMException
    {
         return getN("AttValue2", new Object[] {Attribute,Parameter1,Parameter2});
    }
        
    /**
    *
    * @param AttValue2
    **/
    public void setAttValue2(String Attribute,Variant Parameter1,Variant Parameter2,Variant newValue4) throws COMException
    {
        putN("AttValue2", new Object[] {Attribute, Parameter1, Parameter2} , newValue4);
    }
        
    /**
    *
    
    * @param XCoord
    * @param yCoord
    * @return void
    **/
    public void SetBeelineAim(double XCoord,double yCoord) throws COMException
    {
      
		invokeN("SetBeelineAim", new Object[] {new Double(XCoord), new Double(yCoord)});
        
    }
}
