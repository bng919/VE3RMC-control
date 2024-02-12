package data;

import java.util.Date;

public class Pass {

    private Satellite sat;
    private Date aos;
    private Date los;
    private int durationS;
    private int profileStepS;
    private int[] azProfile;
    private int[] elProfile;
    private int[] dlFreqHzAdjProfile;


    public Pass(Satellite sat, Date aos, Date los, int durationS, int profileStepS, int[] azProfile, int[] elProfile, int[] dlFreqHzAdjProfile) {
        this.sat = sat;
        this.aos = aos;
        this.los = los;
        this.durationS = durationS;
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

    public int getDurationS() {
        return durationS;
    }

    public void setDurationS(int durationS) {
        this.durationS = durationS;
    }

    public int[] getAzProfile() {
        return azProfile;
    }

    public void setAzProfile(int[] azProfile) {
        this.azProfile = azProfile;
    }

    public int[] getElProfile() {
        return elProfile;
    }

    public void setElProfile(int[] elProfile) {
        this.elProfile = elProfile;
    }

    public int getProfileStepS() {
        return profileStepS;
    }

    public void setProfileStepS(int profileStepS) {
        this.profileStepS = profileStepS;
    }

    public int[] getDlFreqHzAdjProfile() {
        return dlFreqHzAdjProfile;
    }

    public void setDlFreqHzAdjProfile(int[] dlFreqHzAdjProfile) {
        this.dlFreqHzAdjProfile = dlFreqHzAdjProfile;
    }
}
