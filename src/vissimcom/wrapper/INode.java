
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
 * Dispatch: INode
 * Description: INode interface
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class INode extends DispatchPtr {
	public static final GUID DIID = new GUID("{5f04524c-d854-4700-AD04-1D6099CAD7D3}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, INode.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * INode (it is required by Jawin for some internal working though).
	 */
	public INode() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the INode interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public INode(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the INode interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public INode(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the INode interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the INode interface on.
	 */
	public INode(COMPtr comObject) throws COMException {
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
    
    * @param Time
    * @param Parameter
    * @param Function
    * @param VehicleClass
    * @return Variant
    **/
    public Object GetResult(double Time,String Parameter,String Function,int VehicleClass) throws COMException
    {
      
		return invokeN("GetResult", new Object[] {new Double(Time), Parameter, Function, new Integer(VehicleClass)});
        
    }
    /**
    *
    
    * @param Time
    * @param FromLink
    * @param ToLink
    * @param Parameter
    * @param Function
    * @param VehicleClass
    * @return Variant
    **/
    public Object GetMovementResult(double Time,int FromLink,int ToLink,String Parameter,String Function,int VehicleClass) throws COMException
    {
      
		return invokeN("GetMovementResult", new Object[] {new Double(Time), new Integer(FromLink), new Integer(ToLink), Parameter, Function, new Integer(VehicleClass)});
        
    }
}
