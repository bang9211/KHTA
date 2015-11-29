/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

/**
 *
 * @author soobisooba
 */
public class DBException extends Exception{
    public static enum DBEX{
        ConnectionError,
        DisconnectionError,
        LoadError;
    }
    
    private DBEX ex;
    
    public DBException(DBException.DBEX dbex){
        super(dbex.toString());
    }
    
    public DBEX getException(){
        return ex;
    }
}
