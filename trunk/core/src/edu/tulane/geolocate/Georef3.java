// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package edu.tulane.geolocate;


public class Georef3 {
    protected java.lang.String vLocality;
    protected java.lang.String vGeography;
    protected boolean hwyX;
    protected boolean findWaterbody;
    
    public Georef3() {
    }
    
    public Georef3(java.lang.String vLocality, java.lang.String vGeography, boolean hwyX, boolean findWaterbody) {
        this.vLocality = vLocality;
        this.vGeography = vGeography;
        this.hwyX = hwyX;
        this.findWaterbody = findWaterbody;
    }
    
    public java.lang.String getVLocality() {
        return vLocality;
    }
    
    public void setVLocality(java.lang.String vLocality) {
        this.vLocality = vLocality;
    }
    
    public java.lang.String getVGeography() {
        return vGeography;
    }
    
    public void setVGeography(java.lang.String vGeography) {
        this.vGeography = vGeography;
    }
    
    public boolean isHwyX() {
        return hwyX;
    }
    
    public void setHwyX(boolean hwyX) {
        this.hwyX = hwyX;
    }
    
    public boolean isFindWaterbody() {
        return findWaterbody;
    }
    
    public void setFindWaterbody(boolean findWaterbody) {
        this.findWaterbody = findWaterbody;
    }
}