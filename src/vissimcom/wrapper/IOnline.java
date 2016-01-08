
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
 * Dispatch: IOnline
 * Description: Online interface
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IOnline extends DispatchPtr {
	public static final GUID DIID = new GUID("{4dc406dc-6af4-4cb8-9909-4B53C630F0D2}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IOnline.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IOnline (it is required by Jawin for some internal working though).
	 */
	public IOnline() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IOnline interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IOnline(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IOnline interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IOnline(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IOnline interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IOnline interface on.
	 */
	public IOnline(COMPtr comObject) throws COMException {
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
    
    * @param flag
    * @return void
    **/
    public void PropagateRealVolumes(int flag) throws COMException
    {
      
		invokeN("PropagateRealVolumes", new Object[] {new Integer(flag)});
        
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
    
    * @param TurnRel
    * @return void
    **/
    public void SetTurnRelations(Object TurnRel) throws COMException
    {
      
		invokeN("SetTurnRelations", new Object[] {TurnRel});
        
    }
}
