
/**
 * GeoServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */

    package edu.mit.pt.geows;

    /**
     *  GeoServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class GeoServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public GeoServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public GeoServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getDistance method
            * override this method for handling normal response from getDistance operation
            */
           public void receiveResultgetDistance(
                    edu.mit.pt.geows.GeoServiceStub.GetDistanceResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getDistance operation
           */
            public void receiveErrorgetDistance(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getStateList method
            * override this method for handling normal response from getStateList operation
            */
           public void receiveResultgetStateList(
                    edu.mit.pt.geows.GeoServiceStub.GetStateListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getStateList operation
           */
            public void receiveErrorgetStateList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getLocationBySearch method
            * override this method for handling normal response from getLocationBySearch operation
            */
           public void receiveResultgetLocationBySearch(
                    edu.mit.pt.geows.GeoServiceStub.GetLocationBySearchResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getLocationBySearch operation
           */
            public void receiveErrorgetLocationBySearch(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getLocationByPostalCode method
            * override this method for handling normal response from getLocationByPostalCode operation
            */
           public void receiveResultgetLocationByPostalCode(
                    edu.mit.pt.geows.GeoServiceStub.GetLocationByPostalCodeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getLocationByPostalCode operation
           */
            public void receiveErrorgetLocationByPostalCode(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for findNearestLocation method
            * override this method for handling normal response from findNearestLocation operation
            */
           public void receiveResultfindNearestLocation(
                    edu.mit.pt.geows.GeoServiceStub.FindNearestLocationResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from findNearestLocation operation
           */
            public void receiveErrorfindNearestLocation(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getTransactionHistory method
            * override this method for handling normal response from getTransactionHistory operation
            */
           public void receiveResultgetTransactionHistory(
                    edu.mit.pt.geows.GeoServiceStub.GetTransactionHistoryResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getTransactionHistory operation
           */
            public void receiveErrorgetTransactionHistory(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCountryList method
            * override this method for handling normal response from getCountryList operation
            */
           public void receiveResultgetCountryList(
                    edu.mit.pt.geows.GeoServiceStub.GetCountryListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCountryList operation
           */
            public void receiveErrorgetCountryList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getHistory method
            * override this method for handling normal response from getHistory operation
            */
           public void receiveResultgetHistory(
                    edu.mit.pt.geows.GeoServiceStub.GetHistoryResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getHistory operation
           */
            public void receiveErrorgetHistory(java.lang.Exception e) {
            }
                


    }
    