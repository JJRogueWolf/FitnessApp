package org.perfit.fitness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import org.perfit.fitness.R;
import org.perfit.fitness.exercise.PhoneOrientation;

public class Training extends Fragment {
    private CheckBox skipAllVideo;
    private SessionManager trainingManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training, container, false);
        skipAllVideo = view.findViewById(R.id.playVideoController);
        trainingManager = new SessionManager(getContext());
        ImageView startButton = view.findViewById(R.id.training_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainingManager.addSkipAllVideoBoolean(skipAllVideo.isChecked());
                Intent intent = new Intent(getActivity().getApplicationContext(), PhoneOrientation.class);
//                Intent intent = new Intent(getActivity().getApplicationContext(), IntroVideo.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}
