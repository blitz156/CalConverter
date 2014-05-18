package ru.dmdevelopment.Calconvertor.gui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ru.dmdevelopment.Calconvertor.R;
import ru.dmdevelopment.Calconvertor.core.Converter.ConverterFactory;

/**
 * Created by blitz on 18.05.14.
 */
public class SettingsActivity extends Activity implements View.OnClickListener {

    private String PREFS;
    private String PREFS_MAX_HISTORY_BTN;
    private String HISTORY_OPERATION_FILE;
    private String PREFER_OPERATION_FILE;
    private int maxHistoryBtn;
    private Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        PREFS = getIntent().getExtras().getString("PREFS");
        PREFS_MAX_HISTORY_BTN = getIntent().getExtras().getString("PREFS_MAX_HISTORY_BTN");
        HISTORY_OPERATION_FILE = getIntent().getExtras().getString("HISTORY_OPERATION_FILE");
        PREFER_OPERATION_FILE = getIntent().getExtras().getString("PREFER_OPERATION_FILE");

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        final String[] array = {"4","5","6","7","8","9","10", "11", "12"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, array);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                maxHistoryBtn = Integer.parseInt(array[i]);

                SharedPreferences prefs = context.getSharedPreferences(
                        PREFS, Context.MODE_PRIVATE);
                prefs.edit().putInt(PREFS_MAX_HISTORY_BTN, maxHistoryBtn).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SharedPreferences prefs = this.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        if (prefs.contains(PREFS_MAX_HISTORY_BTN)) {
            maxHistoryBtn = prefs.getInt(PREFS_MAX_HISTORY_BTN, 4);
        }

        int spinnerPosition = adapter.getPosition(String.valueOf(maxHistoryBtn));
        spinner.setSelection(spinnerPosition);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

    }
}
