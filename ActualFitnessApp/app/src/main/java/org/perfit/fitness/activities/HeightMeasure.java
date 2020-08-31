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

public class HeightMeasure extends Fragment {

    private TextView height_value;
    private SessionManager sessionManager;
    private String height_value_store = "170";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_height_measure, container, false);
        RulerValuePicker height_picker = view.findViewById(R.id.height_adjust);
        height_value = view.findViewById(R.id.heightValue);
        Button heightContinue = view.findViewById(R.id.height_continue);
        height_picker.selectValue(170);
        sessionManager = new SessionManager(getContext());

        height_picker.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
                height_value_store = String.format("%d",selectedValue);
                height_value.setText(selectedValue + " cm");
            }

            @Override
            public void onIntermediateValueChange(int selectedValue) {
                height_value_store = String.format("%d",selectedValue);
                height_value.setText(selectedValue + " cm");
            }
        });

        heightContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.addHeight(height_value_store);
                WeightMeasure weightMeasure = new WeightMeasure();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,weightMeasure)
                        .commit();
            }
        });
        return view;

    }
}
