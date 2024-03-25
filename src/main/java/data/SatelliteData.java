package data;

public class SatelliteData {

    private String id;
    private String[] tle;
    private long nominalDlFreqHz;
    private long nominalUlFreqHz;


    public SatelliteData(String id, String[] tle, long nominalDlFreqHz, long nominalUlFreqHz) {
        this.id = id;
        this.tle = tle;
        this.nominalDlFreqHz = nominalDlFreqHz;
        this.nominalUlFreqHz = nominalUlFreqHz;
    }

    public String getId() {
        return id;
    }

    public String[] getTle() {
        return tle;
    }

    public long getNominalDlFreqHz() {
        return nominalDlFreqHz;
    }

    public long getNominalUlFreqHz() {
        return nominalUlFreqHz;
    }

    @Override
    public String toString() {
        return "Satellite " + this.id;
    }
}
