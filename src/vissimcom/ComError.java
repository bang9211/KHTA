/*
 * Copyright (C) 2011 NATSRL @ UMD (University Minnesota Duluth) and
 * Software and System Laboratory @ KNU (Kangwon National University, Korea) 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package vissimcom;

/**
 *
 * @author Soobin Jeon <j.soobin@gmail.com>
 */
public enum ComError {
    CORRECT(990,"success","success"),
    INCORRECTVERSION(991,"There is no VISSIM\nInstall the VISSIM or Select other VISSIM version","COMException: 80040154"),
    INCORRECTFILE(992,"Select correct file","select correct file"),
    Default(9999,"","default");
    
    private int etype;
    private String msg;
    private String error;
    ComError(int e, String m, String contain){
        etype = e;
        msg = m;
        error = contain;
    }
    public static ComError getError(String error){
        for(ComError ce : ComError.values()){
            if(error.contains(ce.getErrorValue()))
                return ce;
        }
        
        ComError ce = ComError.Default;
        ce.setErrorMsg(error);
        return ce;
    }
    
    public static ComError getErrorbyID(int id){
        for(ComError ce : ComError.values()){
            if(ce.getErrorType() == id)
                return ce;
        }
        
        return ComError.CORRECT;
    }
    
    public String getErrorValue(){
        return error;
    }
    public void setErrorMsg(String _msg){
        msg = _msg;
    }
    @Override
    public String toString(){
        return msg;
    }
    
    public int getErrorType(){
        return etype;
    }
    
    public boolean isCorrect(){return this==CORRECT;}
}
