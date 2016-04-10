package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */

import android.content.Context;

/**
 *
 * @author petrive
 */
public interface TimeOutSubject {
    public void registerTimeOutObserver(TimeOutObserver o);
    public void removeTimeOutObserver(TimeOutObserver o);
    public void notifyTimeOutObservers();
}
