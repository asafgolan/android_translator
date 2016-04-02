package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 29.3.16.
 */

import java.util.HashMap;
import java.util.Map;

public class UserData {

    private User user;
    private final Map<String, Dictionary> dictionaries;

    public UserData() {
        this.dictionaries = new HashMap<>();
    }

    public void addDictionary(String name, Dictionary d) {
        dictionaries.put(name, d);
    }

    public Dictionary getDictionary(String name) {
        return dictionaries.get(name);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
