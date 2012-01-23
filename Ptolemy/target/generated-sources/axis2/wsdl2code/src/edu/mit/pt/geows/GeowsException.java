
/**
 * GeowsException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */

package edu.mit.pt.geows;

public class GeowsException extends java.lang.Exception{
    
    private edu.mit.pt.geows.GeoServiceStub.Fault faultMessage;
    
    public GeowsException() {
        super("GeowsException");
    }
           
    public GeowsException(java.lang.String s) {
       super(s);
    }
    
    public GeowsException(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(edu.mit.pt.geows.GeoServiceStub.Fault msg){
       faultMessage = msg;
    }
    
    public edu.mit.pt.geows.GeoServiceStub.Fault getFaultMessage(){
       return faultMessage;
    }
}
    