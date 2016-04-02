package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */
import java.util.ArrayList;

/**
 *
 * @author petrive
 */
public class TimeOutQuestion implements Runnable, TimeOutSubject{

    /* TimeOutQuestion will notify registered observers once
    the timeout has expired. The thread in which this runnable is run
    should be interrupted once user has answered the question.
     */

    ArrayList<TimeOutObserver> timeOutObservers;
    int timeout;

    public TimeOutQuestion (int timeout) {
        timeOutObservers = new ArrayList<>();
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(timeout);
            notifyTimeOutObservers();
        } catch (InterruptedException ie) {
            //System.out.println (ie.toString());
            System.out.println ("");
        }
    }

    @Override
    public void registerTimeOutObserver(TimeOutObserver o) {
        timeOutObservers.add(o);
    }

    @Override
    public void removeTimeOutObserver(TimeOutObserver o) {
        timeOutObservers.remove(o);
    }

    @Override
    public void notifyTimeOutObservers() {
        for (TimeOutObserver to:timeOutObservers) {
            to.timeout();
        }
    }
}

