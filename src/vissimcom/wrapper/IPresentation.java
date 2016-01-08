
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
 * Dispatch: IPresentation
 * Description: Interface to run and control presentations and to access and modify its options.
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IPresentation extends DispatchPtr {
	public static final GUID DIID = new GUID("{b2622c44-a395-4ce0-888F-BFA8022485F8}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IPresentation.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IPresentation (it is required by Jawin for some internal working though).
	 */
	public IPresentation() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IPresentation interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IPresentation(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IPresentation interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IPresentation(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IPresentation interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IPresentation interface on.
	 */
	public IPresentation(COMPtr comObject) throws COMException {
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
    
    * @return void
    **/
    public void RunContinuous() throws COMException
    {
      
		invokeN("RunContinuous", new Object[] {});
        
    }
    /**
    *
    
    * @return void
    **/
    public void RunSingleStep() throws COMException
    {
      
		invokeN("RunSingleStep", new Object[] {});
        
    }
    /**
    *
    
    * @return void
    **/
    public void Stop() throws COMException
    {
      
		invokeN("Stop", new Object[] {});
        
    }
}
