package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author peterh
 */
public class Quiz {
    protected int quizLength;
    protected Dictionary dictionary;
    protected ArrayList<QuizItem> items;
    protected long completed;

    public Quiz(int quizLength, Dictionary dictionary) {
        this.quizLength = quizLength;
        this.dictionary = dictionary;
        this.items = new ArrayList<>();
    }

    public int getQuizLength() {
        return this.quizLength;
    }

    public QuizItem getItem(int index) {
        return items.get(index);
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public boolean checkAnswer(int index, String answer) {
        boolean gotIt = dictionary.keyHas(items.get(index).getQuestion(), answer);

        items.get(index).setSolved(gotIt);
        return gotIt;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public List<QuizItem> getItems() {
        return this.items;
    }
}

