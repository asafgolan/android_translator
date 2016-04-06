package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author peterh
 */
public class Dictionary implements Serializable {
    private final String lang1;
    private final String lang2;
    private final TreeMap<String, HashSet<String>> lang1map;

    public Dictionary(String from, String to) {
        this.lang1 = from;
        this.lang2 = to;
        this.lang1map = new TreeMap<>();
    }

    public String getLang1() {
        return lang1;
    }

    public String getLang2() {
        return lang2;
    }

    public void addPair(String fromWord, String toWord) {
        HashSet<String> toWords = lang1map.get(fromWord);
        if(toWords == null) {
            toWords = new HashSet<>();
        }
        toWords.add(toWord);
        lang1map.put(fromWord, toWords);
    }

    public void deletePair(String word1, String word2) {
        HashSet<String> words2 = lang1map.get(word1);
        if (words2 != null) {
            words2.remove(word2);
            if (words2.isEmpty()) {
                lang1map.remove(word1);
            } else {
                lang1map.put(word1, words2);
            }
        }
    }

    public Set<String> getKeys() {
        return lang1map.keySet();
    }

    public JSONObject JsonObj(){
        JSONObject data = new JSONObject();

        for(String fromWord: lang1map.keySet()) {
            String s = "";
            //s += fromWord + ":";
            for(String toWord: lang1map.get(fromWord)) {
                //System.out.println("here");
                s += " " + toWord;
            }
            try {
                data.put(fromWord,s );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //s += "\n" + "}";
        }

        return data;
    }

    @Override
    public String toString() {
        String s = lang1 + " - " + lang2 + "\n";

        for(String fromWord: lang1map.keySet()) {
            s += fromWord + ":";
            for(String toWord: lang1map.get(fromWord)) {
                s += " " + toWord;
            }
            s += "\n";
        }
        return s;
    }

    public boolean keyHas(String question, String answer) {
        return lang1map.get(question).contains(answer);
    }

    public int getWordCount() {
        return lang1map.size();
    }

}

