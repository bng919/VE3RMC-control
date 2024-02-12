package data;

public class Satellite {

    private String id;
    private String[] tle;
    private int currAz;
    private int currAl;
    private boolean visible;
    private long nominalDlFreqHz;
    private long nominalUlFreqHz;


    public Satellite(String id, String[] tle, int currAz, int currAl, boolean visible, long nominalDlFreqHz, long nominalUlFreqHz) {
        this.id = id;
        this.tle = tle;
        this.currAz = currAz;
        this.currAl = currAl;
        this.visible = visible;
        this.nominalDlFreqHz = nominalDlFreqHz;
        this.nominalUlFreqHz = nominalUlFreqHz;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getTle() {
        return tle;
    }

    public void setTle(String[] tle) {
        this.tle = tle;
    }

    public int getCurrAz() {
        return currAz;
    }

    public void setCurrAz(int currAz) {
        this.currAz = currAz;
    }

    public int getCurrAl() {
        return currAl;
    }

    public void setCurrAl(int currAl) {
        this.currAl = currAl;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public long getNominalDlFreqHz() {
        return nominalDlFreqHz;
    }

    public void setNominalDlFreqHz(long nominalDlFreqHz) {
        this.nominalDlFreqHz = nominalDlFreqHz;
    }

    public long getNominalUlFreqHz() {
        return nominalUlFreqHz;
    }

    public void setNominalUlFreqHz(long nominalUlFreqHz) {
        this.nominalUlFreqHz = nominalUlFreqHz;
    }
}
