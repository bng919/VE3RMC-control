package data;

import java.util.Date;
import java.util.List;

public class Pass {

    private Satellite sat;
    private Date aos;
    private Date los;
    private long durationS;
    private int profileStepS;
    private List<Double> azProfile;
    private List<Double> elProfile;
    private List<Long> dlFreqHzAdjProfile;


    public Pass(Satellite sat, Date aos, Date los, int profileStepS, List<Double> azProfile, List<Double> elProfile, List<Long> dlFreqHzAdjProfile) {
        this.sat = sat;
        this.aos = aos;
        this.los = los;
        this.durationS = (los.getTime()-aos.getTime())/1000;
        this.profileStepS = profileStepS;
        this.azProfile = azProfile;
        this.elProfile = elProfile;
        this.dlFreqHzAdjProfile = dlFreqHzAdjProfile;
    }

    public Satellite getSat() {
        return sat;
    }

    public void setSat(Satellite sat) {
        this.sat = sat;
    }

    public Date getAos() {
        return aos;
    }

    public void setAos(Date aos) {
        this.aos = aos;
    }

    public Date getLos() {
        return los;
    }

    public void setLos(Date los) {
        this.los = los;
    }

    public long getDurationS() {
        return durationS;
    }

    public void setDurationS(int durationS) {
        this.durationS = durationS;
    }

    public List<Double> getAzProfile() {
        return azProfile;
    }

    public void setAzProfile(List<Double> azProfile) {
        this.azProfile = azProfile;
    }

    public List<Double> getElProfile() {
        return elProfile;
    }

    public void setElProfile(List<Double> elProfile) {
        this.elProfile = elProfile;
    }

    public int getProfileStepS() {
        return profileStepS;
    }

    public void setProfileStepS(int profileStepS) {
        this.profileStepS = profileStepS;
    }

    public List<Long> getDlFreqHzAdjProfile() {
        return dlFreqHzAdjProfile;
    }

    public void setDlFreqHzAdjProfile(List<Long> dlFreqHzAdjProfile) {
        this.dlFreqHzAdjProfile = dlFreqHzAdjProfile;
    }

    @Override
    public String toString() {
        return this.sat + " pass beginning " + this.aos + ", ending " + this.los;
    }
}
