
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
 * Dispatch: IPreviewer
 * Description: Interface to access the VISSIM Previewer.
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IPreviewer extends DispatchPtr {
	public static final GUID DIID = new GUID("{92c7100f-9554-47fd-9206-177C8454C07A}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IPreviewer.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IPreviewer (it is required by Jawin for some internal working though).
	 */
	public IPreviewer() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IPreviewer interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IPreviewer(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IPreviewer interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IPreviewer(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IPreviewer interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IPreviewer interface on.
	 */
	public IPreviewer(COMPtr comObject) throws COMException {
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
    
    * @return void
    **/
    public void New() throws COMException
    {
      
		invokeN("New", new Object[] {});
        
    }
    /**
    *
    
    * @param NetPath
    * @return void
    **/
    public void LoadNet(String NetPath) throws COMException
    {
      
		invokeN("LoadNet", new Object[] {NetPath});
        
    }
    /**
    *
    
    * @param LayoutPath
    * @return void
    **/
    public void LoadLayout(String LayoutPath) throws COMException
    {
      
		invokeN("LoadLayout", new Object[] {LayoutPath});
        
    }
    /**
    *
    
    * @param LayoutPath
    * @return void
    **/
    public void SaveLayout(String LayoutPath) throws COMException
    {
      
		invokeN("SaveLayout", new Object[] {LayoutPath});
        
    }
    /**
    *
    
    * @param NetPath
    * @return void
    **/
    public void ImportANM(String NetPath) throws COMException
    {
      
		invokeN("ImportANM", new Object[] {NetPath});
        
    }
    /**
    *
    
    * @param Number
    * @return void
    **/
    public void ZoomToNode(int Number) throws COMException
    {
      
		invokeN("ZoomToNode", new Object[] {new Integer(Number)});
        
    }
    /**
    *
    
    * @return void
    **/
    public void Exit() throws COMException
    {
      
		invokeN("Exit", new Object[] {});
        
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
    public void ShowMaximized() throws COMException
    {
      
		invokeN("ShowMaximized", new Object[] {});
        
    }
    /**
    *
    
    * @return void
    **/
    public void ShowMinimized() throws COMException
    {
      
		invokeN("ShowMinimized", new Object[] {});
        
    }
    /**
    *
    
    * @return void
    **/
    public void ShowNormal() throws COMException
    {
      
		invokeN("ShowNormal", new Object[] {});
        
    }
    /**
    *
    
    * @return void
    **/
    public void GetWindow(Variant[] Top,Variant[] Left,Variant[] Bottom,Variant[] Right) throws COMException
    {
      
		invokeN("GetWindow", new Object[] {Top, Left, Bottom, Right});
        
    }
    /**
    *
    
    * @param Top
    * @param Left
    * @param Bottom
    * @param Right
    * @return void
    **/
    public void SetWindow(Variant Top,Variant Left,Variant Bottom,Variant Right) throws COMException
    {
      
		invokeN("SetWindow", new Object[] {Top, Left, Bottom, Right});
        
    }
    /**
    *
    
    * @return void
    **/
    public void Focus() throws COMException
    {
      
		invokeN("Focus", new Object[] {});
        
    }
    /**
    *
    * @return void
    **/
    public ISimulation getSimulation() throws COMException
    {
        ISimulation res = new ISimulation();
          DispatchPtr dispPtr = (DispatchPtr)get("Simulation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
}
