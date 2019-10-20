package com.kelmory.goodtogo.functions;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kelmory.goodtogo.R;

import org.w3c.dom.Text;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_calculator, container, false);

        editTextDist = view.findViewById(R.id.edittext_calc_dist);
        timePicker = view.findViewById(R.id.timepicker_calc);
        timePicker.setIs24HourView(true);
        timePicker.setHour(0);
        timePicker.setMinute(0);

        textViewSpeed = view.findViewById(R.id.textview_calc_speed);
        textViewPace = view.findViewById(R.id.textview_calc_pace);

        Button buttonCalc = view.findViewById(R.id.button_calc_calculate);
        buttonCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double distInKm = 1.0;
                int timeInMin = timePicker.getHour() * 60 + timePicker.getMinute();

                try {
                    String distStr = editTextDist.getText().toString();
                    distInKm = Double.parseDouble(distStr);
                } catch (NumberFormatException e){
                    editTextDist.setText("1");
                }

                double speed = (distInKm * 1000) / (timeInMin * 60);
                double pace = timeInMin / distInKm;

                textViewSpeed.setText(String.format(Locale.ENGLISH, "%.2f", speed));
                textViewPace.setText(String.format(Locale.ENGLISH, "%.2f", pace));
            }
        });

        return view;
    }

}
