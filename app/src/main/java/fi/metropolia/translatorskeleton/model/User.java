package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author petrive
 */
public class User implements Serializable {
    private final String name;
    private transient final ArrayList<Quiz> history = new ArrayList<>();
    private final TrackRecord trackRecord;

    public User(String name) {
        this.name = name;
        this.trackRecord = new TrackRecord();
    }

    public void addQuiz(Quiz q) {
        q.setCompleted(System.currentTimeMillis());
        history.add(q);
        trackRecord.add(q);
    }

    public TrackRecord getTrackRecord() {
        return this.trackRecord;
    }
}
