
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
 * Dispatch: IDataCollectionEvaluation
 * Description: Interface to access and configure data collection evaluations.
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IDataCollectionEvaluation extends DispatchPtr {
	public static final GUID DIID = new GUID("{408c85c2-33b3-4869-8ECA-FBE39C2CB9DD}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IDataCollectionEvaluation.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IDataCollectionEvaluation (it is required by Jawin for some internal working though).
	 */
	public IDataCollectionEvaluation() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IDataCollectionEvaluation interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IDataCollectionEvaluation(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IDataCollectionEvaluation interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IDataCollectionEvaluation(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IDataCollectionEvaluation interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IDataCollectionEvaluation interface on.
	 */
	public IDataCollectionEvaluation(COMPtr comObject) throws COMException {
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
    
    * @param ConfigurationPath
    * @return void
    **/
    public void LoadConfiguration(String ConfigurationPath) throws COMException
    {
      
		invokeN("LoadConfiguration", new Object[] {ConfigurationPath});
        
    }
    /**
    *
    
    * @param ConfigurationPath
    * @return void
    **/
    public void SaveConfiguration(String ConfigurationPath) throws COMException
    {
      
		invokeN("SaveConfiguration", new Object[] {ConfigurationPath});
        
    }
    /**
    *
    
    * @param Parameter
    * @param Function
    * @param VehicleClass
    * @return void
    **/
    public void AddParameter(String Parameter,String Function,int VehicleClass) throws COMException
    {
      
		invokeN("AddParameter", new Object[] {Parameter, Function, new Integer(VehicleClass)});
        
    }
    /**
    *
    
    * @param Parameter
    * @param Function
    * @param VehicleClass
    * @return void
    **/
    public void RemoveParameter(String Parameter,String Function,int VehicleClass) throws COMException
    {
      
		invokeN("RemoveParameter", new Object[] {Parameter, Function, new Integer(VehicleClass)});
        
    }
}
