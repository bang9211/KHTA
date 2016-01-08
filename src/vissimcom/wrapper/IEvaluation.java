
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
 * Dispatch: IEvaluation
 * Description: Interface to switch on/off evaluations and acces to the configuration interfaces.
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IEvaluation extends DispatchPtr {
	public static final GUID DIID = new GUID("{19480615-dc0e-40ef-BF8F-8D5976BC86AA}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IEvaluation.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IEvaluation (it is required by Jawin for some internal working though).
	 */
	public IEvaluation() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IEvaluation interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IEvaluation(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IEvaluation interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IEvaluation(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IEvaluation interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IEvaluation interface on.
	 */
	public IEvaluation(COMPtr comObject) throws COMException {
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
    public ILinkEvaluation getLinkEvaluation() throws COMException
    {
        ILinkEvaluation res = new ILinkEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("LinkEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IDataCollectionEvaluation getDataCollectionEvaluation() throws COMException
    {
        IDataCollectionEvaluation res = new IDataCollectionEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("DataCollectionEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IQueueCounterEvaluation getQueueCounterEvaluation() throws COMException
    {
        IQueueCounterEvaluation res = new IQueueCounterEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("QueueCounterEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ITravelTimeEvaluation getTravelTimeEvaluation() throws COMException
    {
        ITravelTimeEvaluation res = new ITravelTimeEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("TravelTimeEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IDelayEvaluation getDelayEvaluation() throws COMException
    {
        IDelayEvaluation res = new IDelayEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("DelayEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public INodeEvaluation getNodeEvaluation() throws COMException
    {
        INodeEvaluation res = new INodeEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("NodeEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IAnalyzerEvaluation getAnalyzerEvaluation() throws COMException
    {
        IAnalyzerEvaluation res = new IAnalyzerEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("AnalyzerEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedDataCollectionEvaluation getPedDataCollectionEvaluation() throws COMException
    {
        IPedDataCollectionEvaluation res = new IPedDataCollectionEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("PedDataCollectionEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedTravelTimeEvaluation getPedTravelTimeEvaluation() throws COMException
    {
        IPedTravelTimeEvaluation res = new IPedTravelTimeEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("PedTravelTimeEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPedProtocolEvaluation getPedProtocolEvaluation() throws COMException
    {
        IPedProtocolEvaluation res = new IPedProtocolEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("PedProtocolEvaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
}
