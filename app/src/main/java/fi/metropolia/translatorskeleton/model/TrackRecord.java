package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author peterh
 */
public class TrackRecord implements Serializable {
    private HashMap<Dictionary, HashMap<String, Integer>> totals;

    public TrackRecord() {
        this.totals = new HashMap<>();
    }

    public  HashMap<Dictionary, HashMap<String, Integer>>  getTotals(){
        return totals;
    }

    public void add(Quiz q) {
        for(QuizItem qi: q.getItems()) {
            HashMap<String, Integer> k = totals.get(q.getDictionary());
            if (k == null) {
                k = new HashMap<>();
            }
            Integer score = k.get(qi.getQuestion());
            if (score == null) {
                score = 0;
            }

            if (qi.isSolved()) {
                score =0;
                score++;
            } else {
                score = 0;
                score--;
            }
            k.put(qi.getQuestion(), score);
            totals.put(q.getDictionary(), k);
        }
    }

    @Override
    public String toString() {
        String s = "Track record:\n";

        for(Dictionary d: totals.keySet()) {
            s += "Dictionary " + d.getLang1() + " -> " + d.getLang2() + "\n";
            for(String word: totals.get(d).keySet()) {
                s += word + ": " + totals.get(d).get(word) + "\n";
            }
        }

        return s;
    }

    public int getWordCount() {
        int count = 0;
        for(Dictionary d: totals.keySet()) {
            count += totals.get(d).size();
        }
        return count;
    }

    public ArrayList<String> getNegatives(Dictionary d) {
        ArrayList<String> negatives = new ArrayList<>();

        System.out.println("Iterating over " + totals.get(d).keySet().toString());
        for(String word: totals.get(d).keySet()) {
            if (totals.get(d).get(word) < 0) {
                System.out.println("found negative");
                negatives.add(word);
            }
        }
        return negatives;
    }
}
