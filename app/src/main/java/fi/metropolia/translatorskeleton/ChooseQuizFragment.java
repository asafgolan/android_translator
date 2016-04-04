package fi.metropolia.translatorskeleton;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fi.metropolia.translatorskeleton.R;


public class ChooseQuizFragment extends Fragment {


    public ChooseQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_quize, container, false);
    }

}
