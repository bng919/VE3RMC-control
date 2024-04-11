package data;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PassData {

    private SatelliteData sat;
    private ZonedDateTime aos;
    private ZonedDateTime los;
    private int durationS;
    private int profileStepS;
    private List<Double> azProfile;
    private List<Double> elProfile;
    private List<Long> dlFreqHzAdjProfile;


    public PassData(SatelliteData sat, ZonedDateTime aos, ZonedDateTime los, int profileStepS, List<Double> azProfile, List<Double> elProfile, List<Long> dlFreqHzAdjProfile) {
        this.sat = sat;
        this.aos = aos;
        this.los = los;
        this.durationS = Math.toIntExact(Duration.between(aos, los).getSeconds());
        this.profileStepS = profileStepS;
        this.azProfile = azProfile;
        this.elProfile = elProfile;
        this.dlFreqHzAdjProfile = dlFreqHzAdjProfile;
    }

    public SatelliteData getSat() {
        return sat;
    }

    public void setSat(SatelliteData sat) {
        this.sat = sat;
    }

    public ZonedDateTime getAos() {
        return aos;
    }

    public void setAos(ZonedDateTime aos) {
        this.aos = aos;
    }

    public ZonedDateTime getLos() {
        return los;
    }

    public void setLos(ZonedDateTime los) {
        this.los = los;
    }

    public int getDurationS() {
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
        return this.sat + "\nStart time " + this.aos
                + "\nEnd time   " + this.los
                + "\nStart Az: " + this.azProfile.getFirst() + ", End Az: " + this.azProfile.getLast() + "\nMax El: " + Collections.max(this.elProfile)
                + "\n================================================================";
    }
}
