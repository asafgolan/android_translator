package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */
import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.translatorskeleton.MainActivity;

/**
 *
 * @author petrive
 */
public class TimeOutQuestion implements Runnable, TimeOutSubject{

    /* TimeOutQuestion will notify registered observers once
    the timeout has expired. The thread in which this runnable is run
    should be interrupted once user has answered the question.
     */
    public boolean ran = false;

    ArrayList<TimeOutObserver> timeOutObservers;
    int timeout;

    public TimeOutQuestion (int timeout) {
        timeOutObservers = new ArrayList<>();
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            System.out.println("run() method Sleeping");
            Thread.sleep(timeout);
            notifyTimeOutObservers();

        } catch (InterruptedException ie) {
            //System.out.println (ie.toString());
            System.out.println ("");
        }
    }

    @Override
    public void registerTimeOutObserver(TimeOutObserver o) {
        System.out.println("registerTimeOutObserver");
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

