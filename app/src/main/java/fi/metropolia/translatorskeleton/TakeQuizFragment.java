package fi.metropolia.translatorskeleton;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TakeQuizFragment extends Fragment {


    public TakeQuizFragment() {
        // Required empty public constructor
    }
    public void onStart() {

        super.onStart();
        System.out.println("FROM ONSTART FRAGMENT");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_take_quiz, container, false);
    }

}
