package vissimcom.wrapper;

import org.jawin.*;

/**
 * Jawin generated code please do not edit
 *
 * Dispatch: IVissim
 * Description: Interface to access and configure VISSIM.
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IVissim extends DispatchPtr {
	public static final GUID DIID = new GUID("{6cff911e-e5ed-4689-9CBA-3741423FD63B}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IVissim.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IVissim (it is required by Jawin for some internal working though).
	 */
	public IVissim() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IVissim interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IVissim(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IVissim interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IVissim(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IVissim interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IVissim interface on.
	 */
	public IVissim(COMPtr comObject) throws COMException {
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
    * @param Additive
    * @return void
    **/
    public void LoadNet(String NetPath,int Additive) throws COMException
    {
      
		invokeN("LoadNet", new Object[] {NetPath, new Integer(Additive)});
        
    }
    /**
    *
    
    * @return void
    **/
    public void SaveNet() throws COMException
    {
      
		invokeN("SaveNet", new Object[] {});
        
    }
    /**
    *
    
    * @param NetPath
    * @return void
    **/
    public void SaveNetAs(String NetPath) throws COMException
    {
      
		invokeN("SaveNetAs", new Object[] {NetPath});
        
    }
    /**
    *
    
    * @param NetPath
    * @param RoutesPath
    * @param InputPath
    * @param ImportType
    * @param importOptions
    * @param evaluationInterval
    * @return void
    **/
    public void ImportANM(String NetPath,String RoutesPath,String InputPath,int ImportType,int importOptions,int evaluationInterval) throws COMException
    {
      
		invokeN("ImportANM", new Object[] {NetPath, RoutesPath, InputPath, new Integer(ImportType), new Integer(importOptions), new Integer(evaluationInterval)});
        
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
    public INet getNet() throws COMException
    {
        INet res = new INet();
          DispatchPtr dispPtr = (DispatchPtr)get("Net");
          res.stealUnknown(dispPtr);
          return res;
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
        
    /**
    *
    * @return void
    **/
    public IDynamicAssignment getDynamicAssignment() throws COMException
    {
        IDynamicAssignment res = new IDynamicAssignment();
          DispatchPtr dispPtr = (DispatchPtr)get("DynamicAssignment");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IGraphics getGraphics() throws COMException
    {
        IGraphics res = new IGraphics();
          DispatchPtr dispPtr = (DispatchPtr)get("Graphics");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IEvaluation getEvaluation() throws COMException
    {
        IEvaluation res = new IEvaluation();
          DispatchPtr dispPtr = (DispatchPtr)get("Evaluation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IOnline getOnline() throws COMException
    {
        IOnline res = new IOnline();
          DispatchPtr dispPtr = (DispatchPtr)get("Online");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IAnimation getAnimation() throws COMException
    {
        IAnimation res = new IAnimation();
          DispatchPtr dispPtr = (DispatchPtr)get("Animation");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IPresentation getPresentation() throws COMException
    {
        IPresentation res = new IPresentation();
          DispatchPtr dispPtr = (DispatchPtr)get("Presentation");
          res.stealUnknown(dispPtr);
          return res;
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
    public void BringToFront() throws COMException
    {
      
		invokeN("BringToFront", new Object[] {});
        
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
    public IWorldPoint getNewWorldPoint(double X,double Y,double Z) throws COMException
    {
        IWorldPoint res = new IWorldPoint();
          DispatchPtr dispPtr = (DispatchPtr)getN("NewWorldPoint", new Object[] {new Double(X),new Double(Y),new Double(Z)});
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    
    * @return void
    **/
    public void DoEvents() throws COMException
    {
      
		invokeN("DoEvents", new Object[] {});
        
    }
    /**
    *
    * @return void
    **/
    public ITriggeredScripting getTriggeredScripting() throws COMException
    {
        ITriggeredScripting res = new ITriggeredScripting();
          DispatchPtr dispPtr = (DispatchPtr)get("TriggeredScripting");
          res.stealUnknown(dispPtr);
          return res;
    }
        
}
