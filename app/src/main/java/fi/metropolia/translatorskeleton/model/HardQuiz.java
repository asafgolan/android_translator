package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */
import java.util.ArrayList;
import java.util.Collections;


/**
 *
 * @author peterh
 */
public class HardQuiz extends Quiz {

    public HardQuiz(int quizLength, Dictionary d, TrackRecord t) {
        super(quizLength, d);

        // select at most quizLength most negative from track record
        ArrayList<String> negatives = t.getNegatives(d);
        Collections.shuffle(negatives);

        this.quizLength = Math.min(quizLength, negatives.size());

        for(int i = 0; i < this.quizLength; i++) {
            this.items.add(new QuizItem(negatives.get(i)));
        }
    }
}