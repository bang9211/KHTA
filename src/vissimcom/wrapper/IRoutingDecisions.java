
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
 * Dispatch: IRoutingDecisions
 * Description: IRoutingDecisions interface.
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IRoutingDecisions extends DispatchPtr {
	public static final GUID DIID = new GUID("{5406c2cc-f415-4b66-9DAE-C9C12131BC9D}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IRoutingDecisions.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IRoutingDecisions (it is required by Jawin for some internal working though).
	 */
	public IRoutingDecisions() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IRoutingDecisions interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IRoutingDecisions(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IRoutingDecisions interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IRoutingDecisions(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IRoutingDecisions interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IRoutingDecisions interface on.
	 */
	public IRoutingDecisions(COMPtr comObject) throws COMException {
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
    public IRoutingDecision getItem(Variant index) throws COMException
    {
        IRoutingDecision res = new IRoutingDecision();
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
    public IRoutingDecision GetRoutingDecisionByNumber(int Number) throws COMException
    {
      IRoutingDecision res = new IRoutingDecision();
          DispatchPtr dispPtr = (DispatchPtr)invokeN("GetRoutingDecisionByNumber", new Object[] {new Integer(Number)});
          res.stealUnknown(dispPtr);
          return res;
        
    }
    /**
    *
    
    * @param Link
    * @param XCoord
    * @return int
    **/
    public int AddStaticRoutingDecision(int Link,double XCoord) throws COMException
    {
      
		return ((Integer)invokeN("AddStaticRoutingDecision", new Object[] {new Integer(Link), new Double(XCoord)})).intValue();
        
    }
    /**
    *
    
    * @param Number
    * @return void
    **/
    public void RemoveRoutingDecision(int Number) throws COMException
    {
      
		invokeN("RemoveRoutingDecision", new Object[] {new Integer(Number)});
        
    }
}
