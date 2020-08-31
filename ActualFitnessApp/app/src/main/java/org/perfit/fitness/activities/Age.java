package org.perfit.fitness.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kevalpatel2106.rulerpicker.RulerValuePicker;
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;

import org.perfit.fitness.R;

public class Age extends Fragment {

    private TextView age_value;
    private SessionManager sessionManager;
    private int age_value_store = 25;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_age, container, false);
        RulerValuePicker age_picker = view.findViewById(R.id.height_adjust);
        age_value = view.findViewById(R.id.heightValue);
        Button ageContinue = view.findViewById(R.id.height_continue);
        age_picker.selectValue(25);
        sessionManager = new SessionManager(getContext());

        age_picker.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
                age_value_store = selectedValue;
                age_value.setText(String.valueOf(selectedValue));
            }

            @Override
            public void onIntermediateValueChange(int selectedValue) {
                age_value_store = selectedValue;
                age_value.setText(String.valueOf(selectedValue));
            }
        });

        ageContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.addAge(age_value_store);
                HeightMeasure heightFragment = new HeightMeasure();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,heightFragment)
                        .commit();
            }
        });
        return view;

    }
}
