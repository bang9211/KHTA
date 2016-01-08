
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
 * Dispatch: ISimulation
 * Description: Interface to run and control simulation and to access and modify its options.
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class ISimulation extends DispatchPtr {
	public static final GUID DIID = new GUID("{7d04a0fe-4bfa-4134-873A-8A3A518A3F29}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, ISimulation.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * ISimulation (it is required by Jawin for some internal working though).
	 */
	public ISimulation() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the ISimulation interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public ISimulation(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the ISimulation interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public ISimulation(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the ISimulation interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the ISimulation interface on.
	 */
	public ISimulation(COMPtr comObject) throws COMException {
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
    * @return String
    **/
    public String getComment() throws COMException
    {
         return (String)get("Comment");
    }
        
    /**
    *
    * @param Comment
    **/
    public void setComment(String Comment) throws COMException
    {
        put("Comment", Comment);
    }
        
    /**
    *
    * @return double
    **/
    public double getPeriod() throws COMException
    {
         return ((Double)get("Period")).doubleValue();
    }
        
    /**
    *
    * @param Period
    **/
    public void setPeriod(double Period) throws COMException
    {
        put("Period", Period);
    }
        
    /**
    *
    * @return String
    **/
    public String getStartTime() throws COMException
    {
         return (String)get("StartTime");
    }
        
    /**
    *
    * @param StartTime
    **/
    public void setStartTime(String StartTime) throws COMException
    {
        put("StartTime", StartTime);
    }
        
    /**
    *
    * @return double
    **/
    public double getSpeed() throws COMException
    {
         return ((Double)get("Speed")).doubleValue();
    }
        
    /**
    *
    * @param Speed
    **/
    public void setSpeed(double Speed) throws COMException
    {
        put("Speed", Speed);
    }
        
    /**
    *
    * @return int
    **/
    public int getResolution() throws COMException
    {
        return ((Integer)get("Resolution")).intValue();
    }
        
    /**
    *
    * @param Resolution
    **/
    public void setResolution(int Resolution) throws COMException
    {
        put("Resolution", Resolution);
    }
        
    /**
    *
    * @return int
    **/
    public int getRandomSeed() throws COMException
    {
        return ((Integer)get("RandomSeed")).intValue();
    }
        
    /**
    *
    * @param RandomSeed
    **/
    public void setRandomSeed(int RandomSeed) throws COMException
    {
        put("RandomSeed", RandomSeed);
    }
        
    /**
    *
    * @return double
    **/
    public double getBreakAt() throws COMException
    {
         return ((Double)get("BreakAt")).doubleValue();
    }
        
    /**
    *
    * @param BreakAt
    **/
    public void setBreakAt(double BreakAt) throws COMException
    {
        put("BreakAt", BreakAt);
    }
        
    /**
    *
    * @return int
    **/
    public int getLeftSideTraffic() throws COMException
    {
        return ((Integer)get("LeftSideTraffic")).intValue();
    }
        
    /**
    *
    * @param LeftSideTraffic
    **/
    public void setLeftSideTraffic(int LeftSideTraffic) throws COMException
    {
        put("LeftSideTraffic", LeftSideTraffic);
    }
        
    /**
    *
    * @return int
    **/
    public int getRunIndex() throws COMException
    {
        return ((Integer)get("RunIndex")).intValue();
    }
        
    /**
    *
    * @param RunIndex
    **/
    public void setRunIndex(int RunIndex) throws COMException
    {
        put("RunIndex", RunIndex);
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
    
    * @param NumRuns
    * @return void
    **/
    public void RunMulti(int NumRuns) throws COMException
    {
      
		invokeN("RunMulti", new Object[] {new Integer(NumRuns)});
        
    }
    /**
    *
    
    * @return void
    **/
    public void Stop() throws COMException
    {
      
		invokeN("Stop", new Object[] {});
        
    }
    /**
    *
    
    * @param SnapshotPath
    * @return void
    **/
    public void LoadSnapshot(String SnapshotPath) throws COMException
    {
      
		invokeN("LoadSnapshot", new Object[] {SnapshotPath});
        
    }
    /**
    *
    
    * @param SnapshotPath
    * @return void
    **/
    public void SaveSnapshot(String SnapshotPath) throws COMException
    {
      
		invokeN("SaveSnapshot", new Object[] {SnapshotPath});
        
    }
}
