
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
 * Dispatch: IGraphics
 * Description: IGraphics interface
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IGraphics extends DispatchPtr {
	public static final GUID DIID = new GUID("{8da2801c-ebc5-4a5d-A348-097D64884306}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IGraphics.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IGraphics (it is required by Jawin for some internal working though).
	 */
	public IGraphics() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IGraphics interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IGraphics(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IGraphics interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IGraphics(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IGraphics interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IGraphics interface on.
	 */
	public IGraphics(COMPtr comObject) throws COMException {
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
    public void setAttValue(String Attribute,Object newValue2) throws COMException
    {
        put("AttValue", Attribute, newValue2);
    }
        
    /**
    *
    
    * @return void
    **/
    public void Redraw() throws COMException
    {
      
		invokeN("Redraw", new Object[] {});
        
    }
    /**
    *
    
    * @param WinName
    * @param ID
    * @return void
    **/
    public void GetWindow(String WinName,Variant[] Top,Variant[] Left,Variant[] Bottom,Variant[] Right,int ID) throws COMException
    {
      
		invokeN("GetWindow", new Object[] {WinName, Top, Left, Bottom, Right, new Integer(ID)});
        
    }
    /**
    *
    
    * @param WinName
    * @param Top
    * @param Left
    * @param Bottom
    * @param Right
    * @param ID
    * @return void
    **/
    public void SetWindow(String WinName,Variant Top,Variant Left,Variant Bottom,Variant Right,int ID) throws COMException
    {
      
		invokeN("SetWindow", new Object[] {WinName, Top, Left, Bottom, Right, new Integer(ID)});
        
    }
    /**
    *
    
    * @param Filename
    * @param scaleFactor
    * @return void
    **/
    public void Screenshot(String Filename,double scaleFactor) throws COMException
    {
      
		invokeN("Screenshot", new Object[] {Filename, new Double(scaleFactor)});
        
    }
    /**
    *
    
    * @param X1
    * @param Y1
    * @param X2
    * @param Y2
    * @return void
    **/
    public void ZoomTo(double X1,double Y1,double X2,double Y2) throws COMException
    {
      
		invokeN("ZoomTo", new Object[] {new Double(X1), new Double(Y1), new Double(X2), new Double(Y2)});
        
    }
    /**
    *
    
    * @param StartTime
    * @return void
    **/
    public void SetActiveKeyFrame(double StartTime) throws COMException
    {
      
		invokeN("SetActiveKeyFrame", new Object[] {new Double(StartTime)});
        
    }
}
