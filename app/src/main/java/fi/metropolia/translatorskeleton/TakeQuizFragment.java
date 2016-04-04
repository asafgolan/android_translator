package fi.metropolia.translatorskeleton;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fi.metropolia.translatorskeleton.model.MyModelRoot;
import fi.metropolia.translatorskeleton.model.Quiz;
import fi.metropolia.translatorskeleton.model.RandomQuiz;
import fi.metropolia.translatorskeleton.model.User;
import fi.metropolia.translatorskeleton.model.UserData;


public class TakeQuizFragment extends Fragment {
    UserData u = MyModelRoot.getInstance().getUserData();

    public TakeQuizFragment() {
        // Required empty public constructor
    }
    public void onStart() {

        super.onStart();
        System.out.println("FROM ONSTART FRAGMENT");
        ((MainActivity)getActivity()).setFragmentisReady(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_take_quiz, container, false);
    }

}
