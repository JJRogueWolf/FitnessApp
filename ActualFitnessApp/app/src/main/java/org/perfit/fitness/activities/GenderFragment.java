package org.perfit.fitness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.VolleyCallback;

import java.util.HashMap;

public class GenderFragment extends Fragment {

    private SessionManager sessionManagerGender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gender, container, false);
        Button male = view.findViewById(R.id.male);
        Button female = view.findViewById(R.id.female);
        sessionManagerGender = new SessionManager(getContext());

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManagerGender.addgender("Male");
                Age age = new Age();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, age)
                        .commit();
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sessionManagerGender.addgender("Female");
                Age age = new Age();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, age)
                        .commit();
            }
        });

        return view;
    }
}
