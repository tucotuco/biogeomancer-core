// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package edu.tulane.geolocate;


public class Georef {
    protected edu.tulane.geolocate.LocalityDescription localityDescription;
    protected boolean hwyX;
    protected boolean findWaterbody;
    
    public Georef() {
    }
    
    public Georef(edu.tulane.geolocate.LocalityDescription localityDescription, boolean hwyX, boolean findWaterbody) {
        this.localityDescription = localityDescription;
        this.hwyX = hwyX;
        this.findWaterbody = findWaterbody;
    }
    
    public edu.tulane.geolocate.LocalityDescription getLocalityDescription() {
        return localityDescription;
    }
    
    public void setLocalityDescription(edu.tulane.geolocate.LocalityDescription localityDescription) {
        this.localityDescription = localityDescription;
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
