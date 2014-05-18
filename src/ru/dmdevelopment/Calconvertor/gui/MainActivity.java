package ru.dmdevelopment.Calconvertor.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import ru.dmdevelopment.Calconvertor.R;
import ru.dmdevelopment.Calconvertor.core.Calculator.ExpressionEstimator;
import ru.dmdevelopment.Calconvertor.core.Converter.ConverterFactory;
import ru.dmdevelopment.Calconvertor.core.Converter.CrossValueRates;
import ru.dmdevelopment.Calconvertor.core.Converter.Rates;
import ru.dmdevelopment.Calconvertor.core.helper.App;
import ru.dmdevelopment.Calconvertor.core.helper.Helper;

import java.io.*;
import java.util.*;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener, ISideNavigationCallback {
    private TextView textView;

    private SideNavigationView sideMenu;
    private StringBuffer calculatedStr = new StringBuffer("");
    private String saveCalculatedStr = "saveCalculatedStr";
    private float fromPosition;

    private List<ToggleButton> buttonMenuList[] = new ArrayList[14]; // in real use 1, 3, 5, ... , create for easy
    private int idFirstMenuBtnPressed = -1;
    private int idSecondMenuBtnPressed = -1;
    private int currentOpenExtraDialog = -1;

    private Map<Operation, Integer> sortedHistoryButtonMap = new HashMap<Operation, Integer>();
    private Map<Operation, Integer> sortedPreferButtonMap = new HashMap<Operation, Integer>();
    private int maxHistoryBtn = 4;

    private String PREFS = "prefs";
    private String PREFS_MAX_HISTORY_BTN = "maxHistoryBtnCount";
    private String HISTORY_OPERATION_FILE = "history_operations.txt";
    private String PREFER_OPERATION_FILE = "prefer_operations.txt";
    private boolean numInMemoryFlag;
    private String numInMemory;
    private boolean reverseFunctionFlag;
    private View selectedItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedPreferences prefs = this.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        if (prefs.contains(PREFS_MAX_HISTORY_BTN)) {
            maxHistoryBtn = prefs.getInt(PREFS_MAX_HISTORY_BTN, 4);
        }

        App.setContext(this);
        App.setActivity(this);

        setBtnListener();

        textView = (TextView) findViewById(R.id.textViewNumber);
        if (savedInstanceState != null) {
            textView.setText(savedInstanceState.getString(saveCalculatedStr));
        }

        sideMenu = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideMenu.setMenuItems(R.menu.side_navigation_menu);
        sideMenu.setMenuClickCallback(this);
        sideMenu.setMode(SideNavigationView.Mode.LEFT);
        sideMenu.setHideItemContent(buttonMenuList);

        findViewById(R.id.main_layout).setOnTouchListener(this);

        parseOperations();
        setOperationBtn();
    }

    @Override
    protected void onDestroy() {
        saveOperation();

        SharedPreferences prefs = this.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        prefs.edit().putInt(PREFS_MAX_HISTORY_BTN, maxHistoryBtn).commit();

        super.onDestroy();
    }

    @Override
    protected  void onResume() {
        super.onResume();
        SharedPreferences prefs = this.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
        if (prefs.contains(PREFS_MAX_HISTORY_BTN)) {
            maxHistoryBtn = prefs.getInt(PREFS_MAX_HISTORY_BTN, 4);
        }

        setOperationBtn();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.save:
                sortedPreferButtonMap.put((Operation) selectedItem.getTag(), 1);
                sortedHistoryButtonMap.remove(selectedItem.getTag());
                break;
            case R.id.delete:
                sortedPreferButtonMap.remove(selectedItem.getTag());
                sortedHistoryButtonMap.remove(selectedItem.getTag());
                break;
        }
        setOperationBtn();
        return true;
    }

    private void saveOperation() {
        String fileName = HISTORY_OPERATION_FILE;

        FileOutputStream outputStream = null;
        PrintWriter writer = null;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(
                    outputStream));

            for (Map.Entry<Operation, Integer> entry : sortedHistoryButtonMap.entrySet()) {
                Operation operation = entry.getKey();
                writer.write(operation.getPosition() + " " + operation.getFrom() + " " +
                        operation.getTo() + " " + entry.getValue() + "\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        fileName = PREFER_OPERATION_FILE;

        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(
                    outputStream));

            for (Map.Entry<Operation, Integer> entry : sortedPreferButtonMap.entrySet()) {
                Operation operation = entry.getKey();
                writer.write(operation.getPosition() + " " + operation.getFrom() + " " +
                        operation.getTo() + " " + entry.getValue() + "\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setBtnListener() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.layout1);
        for (int i = 0; i < vg.getChildCount(); i++) {
            vg.getChildAt(i).setOnClickListener(this);
        }

        vg = (ViewGroup) findViewById(R.id.layout2);
        for (int i = 0; i < vg.getChildCount(); i++) {
            vg.getChildAt(i).setOnClickListener(this);
        }

        vg = (ViewGroup) findViewById(R.id.layout3);
        for (int i = 0; i < vg.getChildCount(); i++) {
            vg.getChildAt(i).setOnClickListener(this);
        }

        vg = (ViewGroup) findViewById(R.id.layout4);
        for (int i = 0; i < vg.getChildCount(); i++) {
            vg.getChildAt(i).setOnClickListener(this);
        }

        vg = (ViewGroup) findViewById(R.id.layout5);
        for (int i = 0; i < vg.getChildCount(); i++) {
            vg.getChildAt(i).setOnClickListener(this);
        }
    }

    private void setOperationBtn() {
        LinearLayout horizontalLL = (LinearLayout) findViewById(R.id.layout_history_scroll_view);
        horizontalLL.removeAllViews();

        LinkedHashMap<Operation, Integer> tempMap = new LinkedHashMap<Operation, Integer>(sortedPreferButtonMap);
        tempMap.putAll(sortedHistoryButtonMap);
        int nowHistoryBtn = 1;
        for (Map.Entry<Operation, Integer> entry : tempMap.entrySet()) {
            final Operation operation = entry.getKey();
            final Rates converter = ConverterFactory.getConverter(operation.position);

            Button btn = new Button(this);
            btn.setText(operation.from + " -> " + operation.to);
            btn.setTag(operation);
            registerForContextMenu(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (calculatedStr.length() == 0) {
                        return;
                    }

                    setStrToTextView(String.valueOf(converter.convert(operation.from,
                            Double.parseDouble(calculatedStr.toString()),
                            operation.to
                    )));

                    updateHistoryOperation(operation);
                }
            });
            btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    selectedItem = v;
                    openContextMenu(v);
                    return true;
                }
            });
            horizontalLL.addView(btn);
            if (nowHistoryBtn == maxHistoryBtn) {
                break;
            }
            nowHistoryBtn++;
        }
    }

    private void updateHistoryOperation(Operation operation) {
        if (sortedHistoryButtonMap.containsKey(operation)) {
            int nowCount = sortedHistoryButtonMap.get(operation);
            sortedHistoryButtonMap.put(operation, ++nowCount);
        }
        else if (!sortedPreferButtonMap.containsKey(operation)) {
            sortedHistoryButtonMap.put(operation, 1);
        }
        sortedHistoryButtonMap = Helper.sortByValue(sortedHistoryButtonMap);

        setOperationBtn();
    }

    private void parseOperations() {
        String s = Helper.getStringFromDisk(HISTORY_OPERATION_FILE);
        if (s != "") {

            for (int i = 0; i < s.split("\\n").length; i++) {
                int position = Integer.parseInt(s.split("\\n")[i].split(" ")[0]);
                final String from = s.split("\\n")[i].split(" ")[1];
                final String to = s.split("\\n")[i].split(" ")[2];
                int count = Integer.parseInt(s.split("\\n")[i].split(" ")[3]);

                sortedHistoryButtonMap.put(new Operation(position, from, to), count);
            }
            sortedHistoryButtonMap = Helper.sortByValue(sortedHistoryButtonMap);
        }

        s = Helper.getStringFromDisk(PREFER_OPERATION_FILE);

        if (s == "") return;

        for (int i = 0; i < s.split("\\n").length; i++) {
            int position = Integer.parseInt(s.split("\\n")[i].split(" ")[0]);
            final String from = s.split("\\n")[i].split(" ")[1];
            final String to = s.split("\\n")[i].split(" ")[2];
            int count = Integer.parseInt(s.split("\\n")[i].split(" ")[3]);

            sortedPreferButtonMap.put(new Operation(position, from, to), count);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(saveCalculatedStr, (String) textView.getText());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.buttonLeftBkt) addSymbolToTextView('(');
        else if (id == R.id.buttonRightBkt) addSymbolToTextView(')');
        else if (id == R.id.buttonMC)  {
            if (numInMemoryFlag) {
                numInMemoryFlag = false;
            }
        }
        else if (id == R.id.buttonMPlus) {
            if (numInMemoryFlag) {
                insertTextToTextView(numInMemory + "+");
            }
            else if (calculatedStr.length() > 0) {
                numInMemoryFlag = true;
                numInMemory = calculatedStr.toString();
            }
        }
        else if (id == R.id.buttonMSub) {
            if (numInMemoryFlag) {
                insertTextToTextView(numInMemory + "-");
            }
        }
        else if (id == R.id.buttonMR) {
            if (numInMemoryFlag) {
                setStrToTextView(numInMemory);
            }
        }
        else if (id == R.id.button2ND) reverseFunction();
        else if (id == R.id.buttonX2) addTextToTextView("^2");
        else if (id == R.id.buttonX3) addTextToTextView("^3");
        else if (id == R.id.buttonXY) addSymbolToTextView('^');
        else if (id == R.id.buttonEX) wrapTextViewInText("e^(", ")");
        else if (id == R.id.button2X) wrapTextViewInText("2^(", ")");
        else if (id == R.id.button10X) wrapTextViewInText("10^(", ")");

        else if (id == R.id.button1_X) insertTextToTextView("1/");
        else if (id == R.id.button2sqrtX) addTextToTextView("^(1/2)");
        else if (id == R.id.button3sqrtX) addTextToTextView("^(1/3)");
        else if (id == R.id.buttonXsqrtY) addTextToTextView("^(1/");
        else if (id == R.id.buttonLn) wrapTextViewInText("ln(", ")");
        else if (id == R.id.buttonLb) wrapTextViewInText("lb(", ")");
        else if (id == R.id.buttonLog10) wrapTextViewInText("lg(", ")");

        else if (id == R.id.buttonFactX) wrapTextViewInText("fact(", ")");
        else if (id == R.id.buttonSin) wrapTextViewInText("sin(", ")");
        else if (id == R.id.buttonASin) wrapTextViewInText("asin(", ")");
        else if (id == R.id.buttonCos) wrapTextViewInText("cos(", ")");
        else if (id == R.id.buttonACos) wrapTextViewInText("acos(", ")");
        else if (id == R.id.buttonTan) wrapTextViewInText("tan(", ")");
        else if (id == R.id.buttonATan) wrapTextViewInText("atan(", ")");
        else if (id == R.id.buttonE) addTextToTextView(String.valueOf(Math.E));
        else if (id == R.id.buttonEE) addTextToTextView("*10^");

        else if (id == R.id.buttonRad) {
            ExpressionEstimator.setTrigonometricOptions(ExpressionEstimator.TRIGONOMETRIC_OPTIONS.RAD);
            findViewById(R.id.buttonRad).setVisibility(View.GONE);
            findViewById(R.id.buttonDeg).setVisibility(View.VISIBLE);
        }
        else if (id == R.id.buttonDeg) {
            ExpressionEstimator.setTrigonometricOptions(ExpressionEstimator.TRIGONOMETRIC_OPTIONS.DEG);
            findViewById(R.id.buttonDeg).setVisibility(View.GONE);
            findViewById(R.id.buttonRad).setVisibility(View.VISIBLE);
        }
        else if (id == R.id.buttonSinH) wrapTextViewInText("sinh(", ")");
        else if (id == R.id.buttonASinH) wrapTextViewInText("asinh(", ")");
        else if (id == R.id.buttonCosH) wrapTextViewInText("cosh(", ")");
        else if (id == R.id.buttonACosH) wrapTextViewInText("acosh(", ")");
        else if (id == R.id.buttonTanH) wrapTextViewInText("tanh(", ")");
        else if (id == R.id.buttonATanH) wrapTextViewInText("atanh(", ")");
        else if (id == R.id.buttonPi) addTextToTextView(String.valueOf(Math.PI));
        else if (id == R.id.buttonRand) addTextToTextView("random()");

        else if (id == R.id.button0) addSymbolToTextView('0');
        else if (id == R.id.button1) addSymbolToTextView('1');
        else if (id == R.id.button2) addSymbolToTextView('2');
        else if (id == R.id.button3) addSymbolToTextView('3');
        else if (id == R.id.button4) addSymbolToTextView('4');
        else if (id == R.id.button5) addSymbolToTextView('5');
        else if (id == R.id.button6) addSymbolToTextView('6');
        else if (id == R.id.button7) addSymbolToTextView('7');
        else if (id == R.id.button8) addSymbolToTextView('8');
        else if (id == R.id.button9) addSymbolToTextView('9');

        else if (id == R.id.buttonNotInteger) addSymbolToTextView('.');
        else if (id == R.id.buttonDel) removeLastSymbolFromTextView();
        else if (id == R.id.buttonPercent) addSymbolToTextView('%');
        else if (id == R.id.buttonDiv) addSymbolToTextView('/');
        else if (id == R.id.buttonMul) addSymbolToTextView('*');
        else if (id == R.id.buttonSub) addSymbolToTextView('-');
        else if (id == R.id.buttonAdd) addSymbolToTextView('+');

        else if (id == R.id.buttonClear) clearTextView();

        else if (id == R.id.buttonCalc) {
            try {
                double result = ExpressionEstimator.calculate(calculatedStr.toString());
                calculatedStr.delete(0, calculatedStr.length());
                calculatedStr.append(String.valueOf(result));
            } catch (Exception e) {
                calculatedStr.delete(0, calculatedStr.length());
                calculatedStr.append(e.toString());
            }

            textView.setText(calculatedStr);
        }
    }

    private void reverseFunction() {
        int gone = View.GONE;
        int visible = View.VISIBLE;
        if (reverseFunctionFlag) {
            gone = View.VISIBLE;
            visible = View.GONE;
            reverseFunctionFlag = false;
        }
        else {
            reverseFunctionFlag = true;
        }
        findViewById(R.id.buttonEX).setVisibility(gone);
        findViewById(R.id.button2X).setVisibility(visible);

        findViewById(R.id.buttonLn).setVisibility(gone);
        findViewById(R.id.buttonLb).setVisibility(visible);

        findViewById(R.id.buttonSin).setVisibility(gone);
        findViewById(R.id.buttonASin).setVisibility(visible);

        findViewById(R.id.buttonCos).setVisibility(gone);
        findViewById(R.id.buttonACos).setVisibility(visible);

        findViewById(R.id.buttonTan).setVisibility(gone);
        findViewById(R.id.buttonATan).setVisibility(visible);

        findViewById(R.id.buttonSinH).setVisibility(gone);
        findViewById(R.id.buttonASinH).setVisibility(visible);

        findViewById(R.id.buttonCosH).setVisibility(gone);
        findViewById(R.id.buttonACosH).setVisibility(visible);

        findViewById(R.id.buttonTanH).setVisibility(gone);
        findViewById(R.id.buttonATanH).setVisibility(visible);
    }

    private void addSymbolToTextView(char c) {
        calculatedStr.append(c);
        textView.setText(calculatedStr);
    }

    private void insertTextToTextView(String left) {
        calculatedStr.insert(0, left);
        textView.setText(calculatedStr);
    }

    private void addTextToTextView(String str) {
        calculatedStr.append(str);
        textView.setText(calculatedStr);
    }

    private void wrapTextViewInText(String left, String right) {
        calculatedStr.insert(0, left);
        calculatedStr.append(right);
        textView.setText(calculatedStr);
    }

    private void setStrToTextView(String str) {
        clearTextView();

        calculatedStr.append(str);
        textView.setText(str);
    }

    private void clearTextView() {
        calculatedStr.delete(0, calculatedStr.length());
        textView.setText(calculatedStr);
    }

    private  void  removeLastSymbolFromTextView() {
        if (calculatedStr.length() == 0) return;
        calculatedStr.delete(calculatedStr.length()-1, calculatedStr.length());
        textView.setText(calculatedStr);
    }

    // click on calculator screen (for show / hide fly-in app menu)
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float toPosition = event.getX();
                if (fromPosition < toPosition) {
                    sideMenu.showMenu();
                } else if (fromPosition > toPosition) {
                    sideMenu.hideMenu();
                }
            default:
                break;
        }
        return true;
    }

    // click on fly-in app menu item
    @Override
    public void onSideNavigationItemClick(int itemId) {
        switch (itemId) {
            case R.id.values_item:
                toggedExtraDialog(1);
                break;
            case R.id.mass_item:
                toggedExtraDialog(3);
                break;
            case R.id.length_item:
                toggedExtraDialog(5);
                break;
            case R.id.volume_item:
                toggedExtraDialog(7);
                break;
            case R.id.temperature_item:
                toggedExtraDialog(9);
                break;
            case R.id.sq_item:
                toggedExtraDialog(11);
                break;
            case R.id.speed_item:
                toggedExtraDialog(13);
                break;
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("PREFS", PREFS);
                intent.putExtra("PREFS_MAX_HISTORY_BTN", PREFS_MAX_HISTORY_BTN);
                intent.putExtra("HISTORY_OPERATION_FILE", HISTORY_OPERATION_FILE);
                intent.putExtra("PREFER_OPERATION_FILE", PREFER_OPERATION_FILE);
                startActivity(intent);
                break;
        }
    }

    // ------------------------------------------ DIALOGS ------------------------------------------ //

    // ------------------------------------------ TOGGED ------------------------------------------- //

    private void toggedExtraDialog(int position) {
        if (currentOpenExtraDialog != -1) {
            if (currentOpenExtraDialog == position) { // only close current
                hideExtraDialog(currentOpenExtraDialog);

            } else { // close current and open new
                hideExtraDialog(currentOpenExtraDialog);
                showExtraDialog(position);
            }
        } else { // open new
            showExtraDialog(position);
        }
    }

    // ------------------------------------------ TOGGED ------------------------------------------- //

    // ------------------------------------------- SHOW -------------------------------------------- //

    private void showExtraDialog(final int position) {
        if (buttonMenuList[position] == null || buttonMenuList[position].size() == 0)
            initHideMenuByPosition(position);

        currentOpenExtraDialog = position;
        sideMenu.setCurrentOpenExtraDialog(currentOpenExtraDialog);
    }

    private void initHideMenuByPosition(final int position) {
        buttonMenuList[position] = new ArrayList<ToggleButton>();

        final Rates converter = ConverterFactory.getConverter(position);

        int countValue = converter.getRatesCount();
        int nowCountValue = 0;

        while (nowCountValue < countValue) {
            for (int columnIndex = 0; columnIndex < 5; columnIndex++) {
                if (nowCountValue == countValue) break;

                ToggleButton btn = new ToggleButton(this);
                btn.setId(nowCountValue);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (idFirstMenuBtnPressed == -1) {
                            idFirstMenuBtnPressed = view.getId();
                        } else if (idFirstMenuBtnPressed == view.getId()) {
                            idSecondMenuBtnPressed = -1;
                        } else {
                            idSecondMenuBtnPressed = view.getId();
                            if (calculatedStr.length() > 0) {

                                String from = buttonMenuList[position].get(idFirstMenuBtnPressed).getText().toString();
                                String to = buttonMenuList[position].get(idSecondMenuBtnPressed).getText().toString();

                                setStrToTextView(String.valueOf(converter.convert(
                                        from,
                                        Double.parseDouble(calculatedStr.toString()),
                                        to
                                )));

                                updateHistoryOperation(new Operation(position, from, to));

                            }
                            toggedExtraDialog(position);
                            sideMenu.toggleMenu();
                        }
                    }
                });
                btn.setText(converter.getRatesCharacter(nowCountValue));
                buttonMenuList[position].add(btn);

                nowCountValue++;
            }
        }
    }

    // ------------------------------------------- SHOW -------------------------------------------- //

    // ------------------------------------------- HIDE -------------------------------------------- //

    private void hideExtraDialog(int position) {
        currentOpenExtraDialog = -1;
        sideMenu.setCurrentOpenExtraDialog(currentOpenExtraDialog);

        if (idFirstMenuBtnPressed != -1)
            buttonMenuList[position].get(idFirstMenuBtnPressed).setChecked(false);
        if (idSecondMenuBtnPressed != -1)
            buttonMenuList[position].get(idSecondMenuBtnPressed).setChecked(false);

        idFirstMenuBtnPressed = -1;
        idSecondMenuBtnPressed = -1;
    }

    // ------------------------------------------- HIDE -------------------------------------------- //

    class Operation {
        private int position;
        private String from;
        private String to;

        Operation(int position, String from, String to) {
            this.position = position;
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Operation that = (Operation) o;

            if (position != that.position) return false;
            if (!from.equals(that.from)) return false;
            if (!to.equals(that.to)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = 17;

            result = 37 * result + position;
            result = 37 * result + (from == null ? 0 : from.hashCode());
            result = 37 * result + (to == null ? 0 : to.hashCode());

            return result;
        }

        public int getPosition() {
            return position;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }
}
