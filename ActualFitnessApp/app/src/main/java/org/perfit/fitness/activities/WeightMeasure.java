package org.perfit.fitness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.kevalpatel2106.rulerpicker.RulerValuePicker;
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.perfit.fitness.R;
import org.perfit.fitness.utilities.APIManager;
import org.perfit.fitness.utilities.VolleyCallback;

import java.util.HashMap;

public class WeightMeasure extends Fragment {

    private TextView weight_value;
    private String weight_value_store = "75";
    private String height, gender;
    private int age, userId;
    private SessionManager sessionManagerGender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight_measure, container, false);
        RulerValuePicker weight_picker = view.findViewById(R.id.weight_adjust);
        weight_value = view.findViewById(R.id.weightValue);
        Button weightContinue = view.findViewById(R.id.weight_continue);
        weight_picker.selectValue(75);
        sessionManagerGender = new SessionManager(getContext());

        HashMap<String,Integer> getuserId = sessionManagerGender.getUserId();
        userId = getuserId.get(SessionManager.KEY_USERID);

        HashMap<String,String> updateData = sessionManagerGender.getUserDetails();
        height = updateData.get(SessionManager.KEY_HEIGHT);
        gender = updateData.get(SessionManager.KEY_GENDER);

        HashMap<String,Integer> ageRetri = sessionManagerGender.getTimeAge();
        age = ageRetri.get(SessionManager.KEY_AGE);

        weight_picker.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
                weight_value_store = String.format("%d",selectedValue);
                weight_value.setText(selectedValue + " Kg");
            }

            @Override
            public void onIntermediateValueChange(int selectedValue) {
                weight_value_store = String.format("%d",selectedValue);
                weight_value.setText(selectedValue + " Kg");
            }
        });

        weightContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdateAPICall();
            }
        });
        return view;
    }

    private void pdateAPICall(){

        JSONObject updateparameter = new JSONObject();
        try {
            updateparameter.put("userId",userId);
            updateparameter.put("height",height);
            updateparameter.put("weight", weight_value_store);
            updateparameter.put("age",age);
            updateparameter.put("gender",gender);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APIManager.CallAPI(APIManager.UPDATE_USER, updateparameter, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                Intent nextActivity = new Intent(getActivity(), BaseDashBoard.class);
                sessionManagerGender.addWeight(weight_value_store);
                nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(nextActivity);
                getActivity().finish();
            }

            @Override
            public void onFailureResponse(String result) {
                Toast.makeText(getContext(), "Sorry... We hit a road block. Lets try again", Toast.LENGTH_SHORT).show();
                sessionManagerGender.logoutUser();
                Intent failedUpdateIntent = new Intent(getActivity(),MainLogin.class);
                startActivity(failedUpdateIntent);
                getActivity().finish();
            }
        });
    }

}
