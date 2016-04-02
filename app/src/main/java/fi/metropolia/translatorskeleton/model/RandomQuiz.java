package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author peterh
 */
public class RandomQuiz extends Quiz {

    public RandomQuiz(int quizLength, Dictionary dictionary) {
        super(Math.min(quizLength, dictionary.getKeys().size()), dictionary);

        this.items = new ArrayList<>();
        for(String word: pickRandom(dictionary, this.quizLength)) {
            items.add(new QuizItem(word));
        }
    }

    private Iterable<String> pickRandom(Dictionary dictionary, int quizLength) {
        ArrayList<String> words = new ArrayList<>(dictionary.getKeys());

        //Collections.shuffle(words);
        return words.subList(0, quizLength);
    }

}

