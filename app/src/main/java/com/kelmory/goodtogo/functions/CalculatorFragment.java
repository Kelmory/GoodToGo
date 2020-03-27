package com.kelmory.goodtogo.functions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import com.kelmory.goodtogo.R;

import java.util.Locale;


public class CalculatorFragment extends Fragment {

    private EditText editTextDist;
    private TimePicker timePicker;

    private TextView textViewSpeed;
    private TextView textViewPace;

    public CalculatorFragment() {
        // Required empty public constructor
    }

    static CalculatorFragment newInstance() {
        return new CalculatorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_calculator, container, false);

        // Set Views.
        editTextDist = view.findViewById(R.id.edittext_calc_dist);
        timePicker = view.findViewById(R.id.timepicker_calc);

        // Set time picker as 00:00;
        timePicker.setIs24HourView(true);
        timePicker.setHour(0);
        timePicker.setMinute(0);

        textViewSpeed = view.findViewById(R.id.textview_calc_speed);
        textViewPace = view.findViewById(R.id.textview_calc_pace);

        Button buttonCalc = view.findViewById(R.id.button_calc_calculate);
        buttonCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Time in min, and distance in km for calculation.
                double distInKm = 1.0;

                // Get time directly from time picker.
                int timeInMin = timePicker.getHour() * 60 + timePicker.getMinute();

                // Get distance from edit text
                try {
                    String distStr = editTextDist.getText().toString();
                    distInKm = Double.parseDouble(distStr);
                } catch (NumberFormatException e){
                    editTextDist.setText("1");
                }

                double speed = (distInKm * 1000) / (timeInMin * 60);
                double pace = timeInMin / distInKm;

                // Update text view upon finished.
                textViewSpeed.setText(String.format(Locale.ENGLISH, "%.2f", speed));
                textViewPace.setText(String.format(Locale.ENGLISH, "%.2f", pace));
            }
        });

        return view;
    }

}
