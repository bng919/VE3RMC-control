package audio;

public class AudioRecorderFactory {

    private AudioRecorderFactory() {}

    public static AudioRecord createAudioRecord(String audioRecord) {
        if (audioRecord == null || audioRecord.isEmpty()) {
            throw new RuntimeException("AudioRecorderFactory could not create instrument with null or empty string");
        } else if (audioRecord.equalsIgnoreCase("Windows")) {
            return new AudioRecordWin();
        } else {
            throw new RuntimeException("AudioRecorderFactory could not create instrument with ID " + audioRecord);
        }
    }

}
