package com.mythleds.ikigai.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.mythleds.ikigai.Class.DateTime;
import com.mythleds.ikigai.Class.LocalDataManager;
import com.urushi.prizeleds.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.mythleds.ikigai.Class.Template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;


public class Fragment1 extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "Fragment1";

    private TextView tv_status;
    private SeekBar seekBar1, seekBar2, seekBar3, seekBar4;
    private Spinner sp_channel;
    LineDataSet lDataSet1, lDataSet2, lDataSet3, lDataSet4, lDataSet5, lDataSet6, lDataSet7, lDataSet8;
    LineData chartData = new LineData();
    String selectedChannel;
    private TextView tv_seekBar1, tv_seekBar2, tv_seekBar3, tv_seekBar4, tv_sb1title, tv_sb2title, tv_sb3title, tv_sb4title;
    private Button btn_gd, btn_gb, btn_g, btn_a, savetmButton, loadtmpButton;

    final static int MODEL_DEFAULT = 0;
    final static int MODEL_FMAJOR = 1;
    final static int MODEL_SMAJOR = 2;
    final static int MODEL_FMAX = 3;
    final static int MODEL_SMAX = 4;
    int modelNo = MODEL_DEFAULT;
    TextView weekdayz;

    int seekbar1, seekbar2, seekbar3, seekbar4;
    int gdh = 8, gdm = 0, gh = 12, gm = 0, gbh = 16, gbm = 0, ah = 20, am = 0;

    private LineChart mChart;
    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<Entry> entries2 = new ArrayList<>();
    private ArrayList<Entry> entries3 = new ArrayList<>();
    private ArrayList<Entry> entries4 = new ArrayList<>();
    private ArrayList<Entry> entries5 = new ArrayList<>();
    private ArrayList<Entry> entries6 = new ArrayList<>();
    private ArrayList<Entry> entries7 = new ArrayList<>();
    private ArrayList<Entry> entries8 = new ArrayList<>();
    private ArrayList<Template> templates = new ArrayList<>();
    ArrayList<String> channels = new ArrayList<>();
    final String[] weekdays = {"00:00-08:00", "08:00-16:00", "16:00-23:59"};
    private String model;

    private LocalDataManager localDataManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);

        View inf = inflater.inflate(R.layout.activity_main, container, false);
        tv_status = (TextView) inf.findViewById(R.id.tv_status);
        init(view);

        localDataManager.setSharedPreference(getContext(), "model", "manual");

        chartInit();

        setBundle();

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar1 = progress;
                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "f1", "" + progress);
                setSeekBar(1, seekbar1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar2 = progress;
                setSeekBar(2, seekbar2);
                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "f2", "" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar3 = progress;
                setSeekBar(3, seekbar3);
                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "f3", "" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar4 = progress;
                setSeekBar(4, progress);
                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "f4", "" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_gd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("SUNRISE");
            }
        });

        btn_g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("SUN");
            }
        });

        btn_gb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("SUNSET");
            }
        });

        btn_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("NIGHT");
            }
        });


        savetmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showAddItemDialog(getContext());

            }
        });

        loadtmpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> templateArrray = LoadTemplateList();
                String[] stringArray = templateArrray.toArray(new String[0]);


                AlertDialog.Builder listAlert = new AlertDialog.Builder(getContext());

                listAlert.setTitle("Select template");
                listAlert.setItems(stringArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadState(stringArray[which]);
                    }
                }).show();
            }
        });

        return view;
    }

    private void showAddItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Save Template")
                .setMessage("Enter File Name")
                .setView(taskEditText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = String.valueOf(taskEditText.getText());
                        saveState(fileName);
                        if (tv_status != null) {
                            tv_status.setText("Saved... ");
                            tv_status.setTextColor(Color.GREEN);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    public void init(View view) {
        model = "manual";
        channels.add("ROYAL");
        channels.add("BLUE");
        channels.add("CYAN +");
        channels.add("ACTINIC +");
        channels.add("HE WHITE");
        channels.add("MAGENTA +");
        // channels.add("Channel 7");
        // channels.add("Channel 8");
        seekBar1 = view.findViewById(R.id.seekBar1);
        seekBar2 = view.findViewById(R.id.seekBar2);
        seekBar3 = view.findViewById(R.id.seekBar3);
        seekBar4 = view.findViewById(R.id.seekBar4);
        mChart = view.findViewById(R.id.linechart);
        // weekdayz = view.findViewById(R.id.weekdayz);
        sp_channel = view.findViewById(R.id.sp_channels);
        sp_channel.setOnItemSelectedListener(Fragment1.this);
        tv_seekBar1 = view.findViewById(R.id.tv_seekBar1);
        tv_seekBar2 = view.findViewById(R.id.tv_seekBar2);
        tv_seekBar3 = view.findViewById(R.id.tv_seekBar3);
        tv_seekBar4 = view.findViewById(R.id.tv_seekBar4);
        tv_sb1title = view.findViewById(R.id.tv_testsb1title);
        tv_sb2title = view.findViewById(R.id.tv_sb2title);
        tv_sb3title = view.findViewById(R.id.tv_sb3title);
        tv_sb4title = view.findViewById(R.id.tv_sb4title);
        btn_gd = view.findViewById(R.id.btn_gd);
        btn_g = view.findViewById(R.id.btn_g);
        btn_gb = view.findViewById(R.id.btn_gb);
        btn_a = view.findViewById(R.id.btn_a);

        savetmButton = view.findViewById(R.id.btnsaveTmp);
        loadtmpButton = view.findViewById(R.id.btnLoadTmp);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, channels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        sp_channel.setAdapter(adapter);
        localDataManager = new LocalDataManager();
    }

    public void chartInit() {

        model = localDataManager.getSharedPreference(getContext(), "model", "manual");
        /*
         * Seekbar ların set edilmesi
         * fixme Bu bölümü retrieveMemorizedData kısmında yapabilirsin
         */
        String val = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "f1", "");
        if (!val.isEmpty()) {
            seekBar1.setProgress(Integer.parseInt(val));
        }
        String val2 = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "f2", "");
        if (!val2.isEmpty()) {
            seekBar2.setProgress(Integer.parseInt(val2));
        }
        String val3 = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "f3", "");
        if (!val3.isEmpty()) {
            seekBar3.setProgress(Integer.parseInt(val3));
        }
        String val4 = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "f4", "");
        if (!val4.isEmpty()) {
            seekBar4.setProgress(Integer.parseInt(val4));
        }

        mChart.getXAxis().setDrawGridLines(true);
        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getAxisRight().setDrawGridLines(true);
        mChart.getLegend().setEnabled(false);
        mChart.getDescription().setEnabled(false);

        YAxis yAxisLeft = mChart.getAxisLeft();
        YAxis yAxisRight = mChart.getAxisRight();
        yAxisLeft.setAxisMaximum(100f); // the axis maximum is 100
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);

        //to hide right Y and top X border
        YAxis rightYAxis = mChart.getAxisRight();
        rightYAxis.setEnabled(true);
        YAxis leftYAxis = mChart.getAxisLeft();
        leftYAxis.setEnabled(true);
        XAxis topXAxis = mChart.getXAxis();
        topXAxis.setEnabled(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(false);
        xAxis.setEnabled(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        entries.add(new Entry(0, 0));
        entries.add(new Entry(0, 0));
        entries.add(new Entry(0, 0));
        entries.add(new Entry(0, 0));
        entries.add(new Entry(0, 0));
        entries.add(new Entry(0, 0));

        entries2.add(new Entry(1, 0));
        entries2.add(new Entry(1, 0));
        entries2.add(new Entry(1, 0));
        entries2.add(new Entry(1, 0));
        entries2.add(new Entry(1, 0));
        entries2.add(new Entry(1, 0));


        entries3.add(new Entry(2, 0));
        entries3.add(new Entry(2, 0));
        entries3.add(new Entry(2, 0));
        entries3.add(new Entry(2, 0));
        entries3.add(new Entry(2, 0));
        entries3.add(new Entry(2, 0));

        entries4.add(new Entry(3, 0));
        entries4.add(new Entry(3, 0));
        entries4.add(new Entry(3, 0));
        entries4.add(new Entry(3, 0));
        entries4.add(new Entry(3, 0));
        entries4.add(new Entry(3, 0));

        entries5.add(new Entry(4, 0));
        entries5.add(new Entry(4, 0));
        entries5.add(new Entry(4, 0));
        entries5.add(new Entry(4, 0));
        entries5.add(new Entry(4, 0));
        entries5.add(new Entry(4, 0));

        entries6.add(new Entry(5, 0));
        entries6.add(new Entry(5, 0));
        entries6.add(new Entry(5, 0));
        entries6.add(new Entry(5, 0));
        entries6.add(new Entry(5, 0));
        entries6.add(new Entry(5, 0));

        lDataSet1 = new LineDataSet(entries, "ROYAL");
        lDataSet1.setDrawFilled(false);
        lDataSet1.setDrawValues(false);
        lDataSet1.setDrawCircles(false);
        lDataSet1.setLineWidth(5);
        chartData.addDataSet(lDataSet1);

        lDataSet2 = new LineDataSet(entries2, "BLUE");
        lDataSet2.setDrawFilled(false);
        lDataSet2.setDrawValues(false);
        lDataSet2.setDrawCircles(false);
        lDataSet2.setLineWidth(5);
        chartData.addDataSet(lDataSet2);

        lDataSet3 = new LineDataSet(entries3, "CYAN +");
        lDataSet3.setDrawFilled(false);
        lDataSet3.setDrawValues(false);
        lDataSet3.setDrawCircles(false);
        lDataSet3.setLineWidth(5);
        chartData.addDataSet(lDataSet3);

        lDataSet4 = new LineDataSet(entries4, "ACTINIC +");
        lDataSet4.setDrawFilled(false);
        lDataSet4.setDrawValues(false);
        lDataSet4.setLineWidth(5);
        lDataSet4.setDrawCircles(false);
        chartData.addDataSet(lDataSet4);


        lDataSet5 = new LineDataSet(entries5, "HE WHITE");
        lDataSet5.setDrawFilled(false);
        lDataSet5.setDrawValues(false);
        lDataSet5.setLineWidth(5);
        lDataSet5.setDrawCircles(false);
        chartData.addDataSet(lDataSet5);

        lDataSet6 = new LineDataSet(entries6, "MAGENTA +");
        lDataSet6.setDrawFilled(false);
        lDataSet6.setDrawValues(false);
        lDataSet6.setLineWidth(5);
        lDataSet6.setDrawCircles(false);
        chartData.addDataSet(lDataSet6);

        /*
        lDataSet7 = new LineDataSet(entries7, "Kanal 7");
        lDataSet7.setDrawFilled(false);
        lDataSet7.setDrawValues(false);
        lDataSet7.setLineWidth(5);
        lDataSet7.setDrawCircles(false);
        chartData.addDataSet(lDataSet7);


        lDataSet8 = new LineDataSet(entries8, "Kanal 8");
        lDataSet8.setDrawFilled(false);
        lDataSet8.setDrawValues(false);
        lDataSet8.setLineWidth(5);
        lDataSet8.setDrawCircles(false);
        chartData.addDataSet(lDataSet8);
        */
        mChart.setMaxVisibleValueCount(0);


        // üst kanal secimine burası ekliyor.
        if (model.equals("manual")) {
            channels.clear();
            channels.add("ROYAL");
            channels.add("BLUE");
            channels.add("CYAN +");
            channels.add("ACTINIC +");
            channels.add("HE WHITE");
            channels.add("MAGENTA +");
            //  channels.add("Channel 7");
            //  channels.add("Channel 8");
        } else if (model.equals("RGBW")) {
            lDataSet1.setLabel("Cool White");
            lDataSet2.setLabel("Wide Spectrum");
            channels.clear();
            channels.add("Cool White");
            channels.add("Wide Spectrum");
            setSpinner();
        } else if (model.equals("SPECTRUM+")) {
            lDataSet1.setLabel("Deep Blue");
            lDataSet2.setLabel("Aqua Sun");
            channels.clear();
            channels.add("Deep Blue");
            channels.add("Aqua Sun");
            setSpinner();
        } else if (model.equals("WIDE SPECT")) {
            lDataSet1.setLabel("Cool White");
            lDataSet2.setLabel("Full Spectrum");
            lDataSet3.setLabel("Reddish White");
            lDataSet4.setLabel("Blueish White");
            channels.clear();
            channels.add("Cool White");
            channels.add("Full Spectrum");
            channels.add("Reddish White");
            channels.add("Blueish White");
            setSpinner();
        } else if (model.equals("UV+")) {
            lDataSet1.setLabel("Deep Blue");
            lDataSet2.setLabel("Aqua Sun");
            lDataSet3.setLabel("Magenta");
            lDataSet4.setLabel("Sky Blue");
            channels.clear();
            channels.add("Deep Blue");
            channels.add("Aqua Sun");
            channels.add("Magenta");
            channels.add("Sky Blue");
            setSpinner();
        }

        mChart.setData(chartData);
        mChart.animateY(5000);
    }

    /*                                    SPINNER SETTINGS START                                   */
    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectedChannel = sp_channel.getSelectedItem().toString();

        if (selectedChannel == "ROYAL")
            selectedChannel = "Channel 1";

        if (selectedChannel == "BLUE")
            selectedChannel = "Channel 2";

        if (selectedChannel == "CYAN +")
            selectedChannel = "Channel 3";

        if (selectedChannel == "ACTINIC +")
            selectedChannel = "Channel 4";

        if (selectedChannel == "HE WHITE")
            selectedChannel = "Channel 5";

        if (selectedChannel == "MAGENTA +")
            selectedChannel = "Channel 6";

        String gdh = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gdh", "07");
        String gdm = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gdm", "00");
        String gh = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gh", "12");
        String gm = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gm", "00");
        String gbh = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gbh", "17");
        String gbm = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gbm", "00");
        String ah = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "ah", "22");
        String am = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "am", "00");

        try {
            btn_gd.setText("SUNRISE " + String.format("%02d", Integer.parseInt(gdh)) + ":" + String.format("%02d", Integer.parseInt(gdm)));
            btn_g.setText("SUN " + String.format("%02d", Integer.parseInt(gh)) + ":" + String.format("%02d", Integer.parseInt(gm)));
            btn_gb.setText("SUNSET " + String.format("%02d", Integer.parseInt(gbh)) + ":" + String.format("%02d", Integer.parseInt(gbm)));
            btn_a.setText("NIGHT " + String.format("%02d", Integer.parseInt(ah)) + ":" + String.format("%02d", Integer.parseInt(am)));
        } catch (Exception e) {

        }

        String val = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "f1", "");
        String val2 = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "f2", "");
        String val3 = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "f3", "");
        String val4 = localDataManager.getSharedPreference(getContext(), model + selectedChannel + "f4", "");

        retrieveMemorizedDatas();

        if (model.equals("RGBW") || model.equals("SPECTRUM+")) {
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            seekBar1.setProgress(0);
            seekBar4.setProgress(0);
        } else if (!val.isEmpty()) {
            seekBar1.setProgress(Integer.parseInt(val));
        } else {
            seekBar1.setProgress(0);
        }
        if (!val2.isEmpty()) {
            seekBar2.setProgress(Integer.parseInt(val2));
        } else {
            seekBar2.setProgress(0);
        }
        if (!val3.isEmpty()) {
            seekBar3.setProgress(Integer.parseInt(val3));
        } else {
            seekBar3.setProgress(0);
        }
        if (!val4.isEmpty()) {
            seekBar4.setProgress(Integer.parseInt(val4));
        } else {
            seekBar4.setProgress(0);
        }

        if (model.equals("manual")) {
            lDataSet1.setLabel("ROYAL");
            lDataSet2.setLabel("BLUE");
            lDataSet3.setLabel("CYAN +");
            lDataSet4.setLabel("ACTINIC +");
            lDataSet5.setLabel("HE WHITE");
            lDataSet6.setLabel("MAGENTA +");

            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);

            if (selectedChannel.equals("Channel 1")) {
                setDatasetSettings(1, "ROYAL");
            } else if (selectedChannel.equals("Channel 2")) {
                setDatasetSettings(2, "BLUE");
            } else if (selectedChannel.equals("Channel 3")) {
                setDatasetSettings(3, "CYAN +");
            } else if (selectedChannel.equals("Channel 4")) {
                setDatasetSettings(4, "ACTINIC +");
            } else if (selectedChannel.equals("Channel 5")) {
                setDatasetSettings(5, "HE WHITE");
            } else if (selectedChannel.equals("Channel 6")) {
                setDatasetSettings(6, "MAGENTA +");
            }
        } else if (model.equals("RGBW")) {
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            lDataSet1.setLabel("RED");
            lDataSet2.setLabel("GREEN");
            lDataSet3.setLabel("BLUE");
            lDataSet4.setLabel("WHITE");
            if (selectedChannel.equals("RED")) {
                setDatasetSettings(1, "RED");
            } else if (selectedChannel.equals("GREEN")) {
                setDatasetSettings(2, "GREEN");
            } else if (selectedChannel.equals("BLUE")) {
                setDatasetSettings(3, "BLUE");
            } else if (selectedChannel.equals("WHITE")) {
                setDatasetSettings(4, "WHITE");
            }
        } else if (model.equals("SPECTRUM+")) {
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            lDataSet1.setLabel("5000K");
            lDataSet2.setLabel("6500K");
            lDataSet3.setLabel("9000K");
            lDataSet4.setLabel("MAGENTA");
            if (selectedChannel.equals("5000K")) {
                setDatasetSettings(1, "5000K");
            } else if (selectedChannel.equals("6500K")) {
                setDatasetSettings(2, "6500K");
            } else if (selectedChannel.equals("9000K")) {
                setDatasetSettings(3, "9000K");
            } else if (selectedChannel.equals("MAGENTA")) {
                setDatasetSettings(4, "MAGENTA");
            }
        } else if (model.equals("WIDE SPECT")) {
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            lDataSet1.setLabel("REDDISH WHITE");
            lDataSet2.setLabel("GREENISH WHITE");
            lDataSet3.setLabel("BLUEISH WHITE");
            lDataSet4.setLabel("SUNLIKE WHITE");
            if (selectedChannel.equals("REDDISH WHITE")) {
                setDatasetSettings(1, "REDDISH WHITE");
            } else if (selectedChannel.equals("GREENISH WHITE")) {
                setDatasetSettings(2, "GREENISH WHITE");
            } else if (selectedChannel.equals("BLUEISH WHITE")) {
                setDatasetSettings(3, "BLUEISH WHITE");
            } else if (selectedChannel.equals("SUNLIKE WHITE")) {
                setDatasetSettings(4, "SUNLIKE WHITE");
            }

        }
        /*else if (model.equals("UV+")){
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            lDataSet1.setLabel("Deep Blue");
            lDataSet2.setLabel("Aqua Sun");
            lDataSet3.setLabel("Magenta");
            lDataSet4.setLabel("Sky Blue");
            if (selectedChannel.equals("Deep Blue")){
                setDatasetSettings(1,"Deep Blue");
            }else if (selectedChannel.equals("Aqua Sun")){
                setDatasetSettings(2,"Aqua Sun");
            }else if (selectedChannel.equals("Magenta")){
                setDatasetSettings(3,"Magenta");
            }else if (selectedChannel.equals("Sky Blue")){
                setDatasetSettings(4,"Sky Blue");
            }
        }*/
        else if (model.equals("UV+")) {
            seekBar1.setEnabled(false);
            seekBar4.setEnabled(false);
            lDataSet1.setLabel("5000K");
            lDataSet2.setLabel("6500K");
            lDataSet3.setLabel("9000K");
            lDataSet4.setLabel("UV PLUS");
            if (selectedChannel.equals("5000K")) {
                setDatasetSettings(1, "5000K");
            } else if (selectedChannel.equals("6500K")) {
                setDatasetSettings(2, "6500K");
            } else if (selectedChannel.equals("9000K")) {
                setDatasetSettings(3, "9000K");
            } else if (selectedChannel.equals("UV PLUS")) {
                setDatasetSettings(4, "UV PLUS");
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showTimeDialog(String timename) {
        // Dialog nesnesi oluştur layout dosyasıne bağlan
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_settime);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_setTime = dialog.findViewById(R.id.btn_timeset);
        NumberPicker np1 = dialog.findViewById(R.id.np1);
        NumberPicker np2 = dialog.findViewById(R.id.npd2);
        TextView tv_timeTitle = dialog.findViewById(R.id.tv_timeTitle);


        float mgdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gdh", "7"));
        float mgdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gdm", "0"));
        float mgh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gh", "12"));
        float mgm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gm", "0"));
        float mgbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gbh", "17"));
        float mgbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + selectedChannel + "gbm", "0"));
        float mah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + selectedChannel + "ah", "22"));
        float mam = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + selectedChannel + "am", "0"));

        tv_timeTitle.setText(timename);

        np1.setMaxValue(DateTime.hournp1.length - 1);
        np1.setMinValue(0);
        np1.setDisplayedValues(DateTime.hournp1);
        np1.setWrapSelectorWheel(true);

        np2.setMaxValue(DateTime.minutenp2.length - 1);
        np2.setMinValue(0);
        np2.setDisplayedValues(DateTime.minutenp2);
        np2.setWrapSelectorWheel(true);

        btn_setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String alarm1_hour = DateTime.hournp1[np1.getValue()];
                String alarm1_minute = DateTime.minutenp2[np2.getValue()];

                if (timename.equals("SUNRISE")) {
                    gdh = Integer.parseInt(alarm1_hour);
                    gdm = Integer.parseInt(alarm1_minute);
                    if (gdh <= mgh && gdh <= mgbh && gdh <= mah) {
                        if (gdh == mgh) {
                            if (gdm < mgm) {
                                btn_gd.setText("SUNRISE " + alarm1_hour + ":" + alarm1_minute);

                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gdh", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gdm", alarm1_minute);
                            } else {
                                Toast.makeText(getContext(), "WRONG HOURS!!! SUNRISE HOURS MUST BE LESS THEN SUNSET HOURS", Toast.LENGTH_LONG).show();
                            }
                        } else if (gdh == mgbh) {
                            if (gdm < mgbm) {
                                btn_gd.setText("SUNRISE " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gdh", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gdm", alarm1_minute);

                                Log.d("Gün Doğumu2>>>", "OK");
                                Log.d("MODEL>>>", model);
                                Log.d("selectedChannel>>>", selectedChannel);

                            } else {
                                Toast.makeText(getContext(), "WRONG HOURS!!! SUNRISE HOURS MUST BE LESS THEN OTHER HOURS", Toast.LENGTH_LONG).show();

                            }
                        } else if (gdh == mah) {
                            if (gdm < mam) {
                                btn_gd.setText("SUNRISE " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gdh", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gdm", alarm1_minute);

                                Log.d("Gün Doğumu3>>>", "OK");
                                Log.d("MODEL>>>", model);
                                Log.d("selectedChannel>>>", selectedChannel);

                            } else {
                                Toast.makeText(getContext(), "WRONG HOURS!!! SUNRISE HOURS MUST BE LESS THEN OTHER HOURS", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            btn_gd.setText("SUNRISE " + alarm1_hour + ":" + alarm1_minute);
                            localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gdh", alarm1_hour);
                            localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gdm", alarm1_minute);

                            Log.d("Gün Doğumu4>>>", "OK");
                            Log.d("MODEL>>>", model);
                            Log.d("selectedChannel>>>", model + selectedChannel + "gdh");

                            Log.d(model + selectedChannel + "gdh>>>>", alarm1_hour);
                            Log.d(model + selectedChannel + "gdm>>>>", alarm1_minute);
                        }

                    } else {
                        Toast.makeText(getContext(), "WRONG HOURS!!! SUNRISE HOURS MUST BE LESS THEN OTHER HOURS", Toast.LENGTH_LONG).show();
                    }

                } else if (timename.equals("SUN")) {
                    gh = Integer.parseInt(alarm1_hour);
                    gm = Integer.parseInt(alarm1_minute);
                    Log.d(TAG, "güneş saat:" + gh + " güneş dakika : " + gm);


                    Log.d("Gün Doğumu>>>", "OK");
                    Log.d("MODEL>>>", model);
                    Log.d("selectedChannel>>>", selectedChannel);

                    if (gh >= mgdh && gh <= mgbh && gh <= mah) {
                        if (gh == mgdh) {
                            if (gm > mgdm) {
                                btn_g.setText("SUN " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gh", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gm", alarm1_minute);
                            } else {
                                Toast.makeText(getContext(), "WRONG HOURS!!! SUN HOURS MUST BIGGER THEN SUNRISE AND MUST BE LESS THEN OTHERS", Toast.LENGTH_LONG).show();
                            }
                        } else if (gh == mgbh) {
                            if (gm < gbm) {
                                btn_g.setText("SUN " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gh", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gm", alarm1_minute);
                            } else {

                                Toast.makeText(getContext(), "WRONG HOURS!!! SUN HOURS MUST BIGGER THEN SUNRISE AND MUST BE LESS THEN OTHERS", Toast.LENGTH_LONG).show();
                            }
                        } else if (gh == mah) {
                            if (gm < mam) {
                                btn_g.setText("SUN " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gh", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gm", alarm1_minute);
                            } else {
                                Toast.makeText(getContext(), "" +
                                        "WRONG HOURS!!! SUN HOURS MUST BIGGER THEN SUNRISE AND MUST BE LESS THEN OTHERS", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            btn_g.setText("SUN " + alarm1_hour + ":" + alarm1_minute);
                            localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gh", alarm1_hour);
                            localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gm", alarm1_minute);
                        }

                    } else {
                        Toast.makeText(getContext(), "WRONG HOURS!!! SUN HOURS MUST BIGGER THEN SUNRISE AND MUST BE LESS THEN OTHERS", Toast.LENGTH_LONG).show();
                    }

                } else if (timename.equals("SUNSET")) {
                    gbh = Integer.parseInt(alarm1_hour);
                    gbm = Integer.parseInt(alarm1_minute);
                    if (gbh >= mgdh && gbh >= mgh && gbh <= mah) {
                        if (gbh == mgdh) {
                            if (gbm > mgdm) {
                                btn_gb.setText("SUNSET " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gbh", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gbm", alarm1_minute);
                            } else {
                                Toast.makeText(getContext(), "WRONG VALUE, SUNSET HOURS MUST BE GREATER THAN SUNRISE AND SMALLER THEN SUN AND NIGHT HOURS!", Toast.LENGTH_LONG).show();
                            }
                        } else if (gbh == mgh) {
                            if (gbm > mgm) {
                                btn_gb.setText("SUNSET " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gbh", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gbm", alarm1_minute);
                            } else {

                                Toast.makeText(getContext(),
                                        "WRONG VALUE, SUNSET HOURS MUST BE GREATER THAN SUNRISE AND SUN AND SMALLER THEN NIGHT HOURS!", Toast.LENGTH_LONG).show();
                            }
                        } else if (gbh == mah) {
                            if (gbm < mam) {
                                btn_gb.setText("SUNSET " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gbh", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gbm", alarm1_minute);
                            } else {

                                Toast.makeText(getContext(), "WRONG VALUE, SUNSET HOURS MUST BE GREATER THAN SUNRISE AND SUN AND SMALLER THEN NIGHT HOURS!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            btn_gb.setText("SUNSET" + alarm1_hour + ":" + alarm1_minute);
                            localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gbh", alarm1_hour);
                            localDataManager.setSharedPreference(getContext(), model + selectedChannel + "gbm", alarm1_minute);
                        }
                    } else {
                        Toast.makeText(getContext(), "WRONG VALUE, SUNSET HOURS MUST BE GREATER THAN SUNRISE AND SUN AND SMALLER THEN NIGHT HOURS!", Toast.LENGTH_LONG).show();
                    }
                } else if (timename.equals("NIGHT")) {
                    ah = Integer.parseInt(alarm1_hour);
                    am = Integer.parseInt(alarm1_minute);
                    if (ah > mgdh && ah > mgh && ah > mgbh) {
                        if (ah == mgdh) {
                            if (am > mgdm) {
                                btn_a.setText("NIGHT " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "ah", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "am", alarm1_minute);
                            } else {

                                Toast.makeText(getContext(), "WRONG VALUE, NIGHT HOURS MUST BE GREATER THAN ALL OTHER HOURS!", Toast.LENGTH_LONG).show();
                            }
                        } else if (ah == mgh) {
                            if (am > mgm) {
                                btn_a.setText("NIGHT " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "ah", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "am", alarm1_minute);
                            } else {
                                Toast.makeText(getContext(), "WRONG VALUE, NIGHT HOURS MUST BE GREATER THAN ALL OTHER HOURS!", Toast.LENGTH_LONG).show();
                            }
                        } else if (ah == mgbh) {
                            if (am > mgbm) {
                                btn_a.setText("NIGHT " + alarm1_hour + ":" + alarm1_minute);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "ah", alarm1_hour);
                                localDataManager.setSharedPreference(getContext(), model + selectedChannel + "am", alarm1_minute);
                            } else {
                                Toast.makeText(getContext(), "WRONG VALUE, NIGHT HOURS MUST BE GREATER THAN ALL OTHER HOURS!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            btn_a.setText("NIGHT " + alarm1_hour + ":" + alarm1_minute);
                            localDataManager.setSharedPreference(getContext(), model + selectedChannel + "ah", alarm1_hour);
                            localDataManager.setSharedPreference(getContext(), model + selectedChannel + "am", alarm1_minute);
                        }
                    } else {
                        Toast.makeText(getContext(), "WRONG VALUE, NIGHT HOURS MUST BE GREATER THAN ALL OTHER HOURS!", Toast.LENGTH_LONG).show();
                    }
                }
                retrieveMemorizedDatas();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void setBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            model = getArguments().getString("model", "");
            modelNo = MODEL_DEFAULT;

            if (model.equals("RGBW")) {
                lDataSet1.setLabel("RED");
                lDataSet2.setLabel("GREEN");
                lDataSet3.setLabel("BLUE");
                lDataSet4.setLabel("WHITE");
                seekBar1.setProgress(0);
                seekBar4.setProgress(0);
                channels.clear();
                channels.add("RED");
                channels.add("GREEN");
                channels.add("BLUE");
                channels.add("WHITE");
                setSpinner();
                modelNo = MODEL_FMAJOR;
                tv_sb1title.setText("SUNRISE");
                tv_sb2title.setText("SUN");
                tv_sb3title.setText("SUNSET");
                tv_sb4title.setText("NIGHT");
                seekBar3.setVisibility(View.VISIBLE);
                seekBar4.setVisibility(View.VISIBLE);
                tv_seekBar3.setVisibility(View.VISIBLE);
                tv_seekBar4.setVisibility(View.VISIBLE);
                tv_sb3title.setVisibility(View.VISIBLE);
                tv_sb4title.setVisibility(View.VISIBLE);
            } else if (model.equals("SPECTRUM+")) {
                lDataSet1.setLabel("5000K");
                lDataSet2.setLabel("6500K");
                lDataSet3.setLabel("9000K");
                lDataSet4.setLabel("MAGENTA");
                channels.clear();
                channels.add("5000K");
                channels.add("6500K");
                channels.add("9000K");
                channels.add("MAGENTA");
                setSpinner();

                modelNo = MODEL_SMAJOR;
                tv_sb1title.setText("SUNRISE");
                tv_sb2title.setText("SUN");
                tv_sb3title.setText("SUNSET");
                tv_sb4title.setText("NIGHT");
                seekBar3.setVisibility(View.VISIBLE);
                seekBar4.setVisibility(View.VISIBLE);
                tv_seekBar3.setVisibility(View.VISIBLE);
                tv_seekBar4.setVisibility(View.VISIBLE);
                tv_sb3title.setVisibility(View.VISIBLE);
                tv_sb4title.setVisibility(View.VISIBLE);
            } else if (model.equals("WIDE SPECT")) {
                lDataSet1.setLabel("REDDISH WHITE");
                lDataSet2.setLabel("GREENISH WHITE");
                lDataSet3.setLabel("BLUEISH WHITE");
                lDataSet4.setLabel("SUNLIKE WHITE");
                channels.clear();
                channels.add("REDDISH WHITE");
                channels.add("GREENISH WHITE");
                channels.add("BLUEISH WHITE");
                channels.add("SUNLIKE WHITE");
                setSpinner();

                modelNo = MODEL_FMAX;
                tv_sb1title.setText("SUNRISE");
                tv_sb2title.setText("SUN");
                tv_sb3title.setText("SUNSET");
                tv_sb4title.setText("NIGHT");
                seekBar3.setVisibility(View.VISIBLE);
                seekBar4.setVisibility(View.VISIBLE);
                tv_seekBar3.setVisibility(View.VISIBLE);
                tv_seekBar4.setVisibility(View.VISIBLE);
                tv_sb3title.setVisibility(View.VISIBLE);
                tv_sb4title.setVisibility(View.VISIBLE);
            } else if (model.equals("UV+")) {
                lDataSet1.setLabel("5000K");
                lDataSet2.setLabel("6500K");
                lDataSet3.setLabel("9000K");
                lDataSet4.setLabel("UV PLUS");

                channels.clear();
                channels.add("5000K");
                channels.add("6500K");
                channels.add("9000K");
                channels.add("UV PLUS");
                setSpinner();

                modelNo = MODEL_SMAX;
                tv_sb1title.setText("SUNRISE");
                tv_sb2title.setText("SUN");
                tv_sb3title.setText("SUNSET");
                tv_sb4title.setText("NIGHT");
                seekBar3.setVisibility(View.VISIBLE);
                seekBar4.setVisibility(View.VISIBLE);
                tv_seekBar3.setVisibility(View.VISIBLE);
                tv_seekBar4.setVisibility(View.VISIBLE);
                tv_sb3title.setVisibility(View.VISIBLE);
                tv_sb4title.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setDataset(LineDataSet dataset1, int lineWidth, int color, String label) {
        dataset1.setColor(color);
        dataset1.setLineWidth(lineWidth);
        dataset1.setLabel(label);
    }

    public void setDatasetSettings(int dataset, String label) {
        switch (dataset) {
            case 1:
                lDataSet1.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(true);
                lDataSet2.setDrawFilled(false);
                lDataSet3.setDrawFilled(false);
                lDataSet4.setDrawFilled(false);
                lDataSet5.setDrawFilled(false);
                lDataSet6.setDrawFilled(false);
                //lDataSet7.setDrawFilled(false);
                //lDataSet8.setDrawFilled(false);

                setDataset(lDataSet1, 5, R.color.purple_500, label);
                setDataset(lDataSet2, 2, R.color.lighgray, label);
                setDataset(lDataSet3, 2, R.color.lighgray, label);
                setDataset(lDataSet4, 2, R.color.lighgray, label);
                setDataset(lDataSet5, 2, R.color.lighgray, label);
                setDataset(lDataSet6, 2, R.color.lighgray, label);
                //setDataset(lDataSet7,2,R.color.lighgray,label);
                //setDataset(lDataSet8,2,R.color.lighgray,label);
                break;
            case 2:
                lDataSet2.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(false);
                lDataSet2.setDrawFilled(true);
                lDataSet3.setDrawFilled(false);
                lDataSet4.setDrawFilled(false);
                lDataSet5.setDrawFilled(false);
                lDataSet6.setDrawFilled(false);
                //   lDataSet7.setDrawFilled(false);
                // lDataSet8.setDrawFilled(false);

                setDataset(lDataSet1, 2, R.color.lighgray, label);
                setDataset(lDataSet2, 5, R.color.purple_500, label);
                setDataset(lDataSet3, 2, R.color.lighgray, label);
                setDataset(lDataSet4, 2, R.color.lighgray, label);
                setDataset(lDataSet5, 5, R.color.lighgray, label);
                setDataset(lDataSet6, 2, R.color.lighgray, label);
                //   setDataset(lDataSet7,2,R.color.lighgray,label);
                //  setDataset(lDataSet8,2,R.color.lighgray,label);
                break;
            case 3:
                lDataSet3.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(false);
                lDataSet2.setDrawFilled(false);
                lDataSet3.setDrawFilled(true);
                lDataSet4.setDrawFilled(false);
                lDataSet5.setDrawFilled(false);
                lDataSet6.setDrawFilled(false);
                // lDataSet7.setDrawFilled(false);
                // lDataSet8.setDrawFilled(false);

                setDataset(lDataSet1, 2, R.color.lighgray, label);
                setDataset(lDataSet2, 2, R.color.lighgray, label);
                setDataset(lDataSet3, 5, R.color.purple_500, label);
                setDataset(lDataSet4, 2, R.color.lighgray, label);
                setDataset(lDataSet5, 5, R.color.lighgray, label);
                setDataset(lDataSet6, 2, R.color.lighgray, label);
                // setDataset(lDataSet7,2,R.color.lighgray,label);
                //  setDataset(lDataSet8,2,R.color.lighgray,label);

                break;
            case 4:
                lDataSet4.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(false);
                lDataSet2.setDrawFilled(false);
                lDataSet3.setDrawFilled(false);
                lDataSet4.setDrawFilled(true);
                lDataSet5.setDrawFilled(false);
                lDataSet6.setDrawFilled(false);
                // lDataSet7.setDrawFilled(false);
                // lDataSet8.setDrawFilled(false);

                setDataset(lDataSet1, 2, R.color.lighgray, label);
                setDataset(lDataSet2, 2, R.color.lighgray, label);
                setDataset(lDataSet3, 2, R.color.lighgray, label);
                setDataset(lDataSet4, 5, R.color.purple_500, label);
                setDataset(lDataSet5, 5, R.color.lighgray, label);
                setDataset(lDataSet6, 2, R.color.lighgray, label);
                // setDataset(lDataSet7,2,R.color.lighgray,label);
                // setDataset(lDataSet8,2,R.color.lighgray,label);
                break;
            case 5:
                lDataSet5.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(false);
                lDataSet2.setDrawFilled(false);
                lDataSet3.setDrawFilled(false);
                lDataSet4.setDrawFilled(false);
                lDataSet6.setDrawFilled(false);
                lDataSet5.setDrawFilled(true);
                //lDataSet7.setDrawFilled(false);,
                //lDataSet8.setDrawFilled(false);


                setDataset(lDataSet1, 2, R.color.lighgray, label);
                setDataset(lDataSet2, 2, R.color.lighgray, label);
                setDataset(lDataSet3, 2, R.color.lighgray, label);
                setDataset(lDataSet4, 2, R.color.lighgray, label);
                setDataset(lDataSet5, 5, R.color.purple_500, label);
                setDataset(lDataSet6, 2, R.color.lighgray, label);
                //setDataset(lDataSet7,2,R.color.lighgray,label);
                //  setDataset(lDataSet8,2,R.color.lighgray,label);
                break;
            case 6:
                lDataSet6.setFillColor(R.color.purple_500);
                lDataSet1.setDrawFilled(false);
                lDataSet2.setDrawFilled(false);
                lDataSet3.setDrawFilled(false);
                lDataSet4.setDrawFilled(false);
                lDataSet5.setDrawFilled(false);
                lDataSet6.setDrawFilled(true);

                // lDataSet7.setDrawFilled(false);
                // lDataSet8.setDrawFilled(false);

                setDataset(lDataSet1, 2, R.color.lighgray, label);
                setDataset(lDataSet2, 2, R.color.lighgray, label);
                setDataset(lDataSet3, 2, R.color.lighgray, label);
                setDataset(lDataSet4, 2, R.color.lighgray, label);
                setDataset(lDataSet6, 5, R.color.purple_500, label);
                setDataset(lDataSet5, 2, R.color.lighgray, label);
                // setDataset(lDataSet7,2,R.color.lighgray,label);
                // setDataset(lDataSet8,2,R.color.lighgray,label);
                break;

            default:
                break;
        }
        mChart.invalidate();
    }

    public void setSeekBar(int seekBarNo, int progress) {
        switch (seekBarNo) {
            case 1:
                tv_seekBar1.setText("%" + progress);
                break;
            case 2:
                tv_seekBar2.setText("%" + progress);
                break;
            case 3:
                tv_seekBar3.setText("%" + progress);
                break;
            case 4:
                tv_seekBar4.setText("%" + progress);

            default:
                break;
        }
        if (model.equals("manual")) {
            if (selectedChannel.equals("Channel 1")) {
                setSwitch(entries, seekBarNo, progress, lDataSet1, "ROYAL");
            } else if (selectedChannel.equals("Channel 2")) {
                setSwitch(entries2, seekBarNo, progress, lDataSet2, "BLUE");
            } else if (selectedChannel.equals("Channel 3")) {
                setSwitch(entries3, seekBarNo, progress, lDataSet3, "CYAN +");
            } else if (selectedChannel.equals("Channel 4")) {
                setSwitch(entries4, seekBarNo, progress, lDataSet4, "ACTINIC +");
            } else if (selectedChannel.equals("Channel 5")) {
                setSwitch(entries5, seekBarNo, progress, lDataSet5, "HE WHITE");
            } else if (selectedChannel.equals("Channel 6")) {
                setSwitch(entries6, seekBarNo, progress, lDataSet6, "MAGENTA +");
            }

        } else if (model.equals("RGBW")) {
            if (selectedChannel.equals("RED")) {
                setSwitch(entries, seekBarNo, progress, lDataSet1, "RED");
            } else if (selectedChannel.equals("GREEN")) {
                setSwitch(entries2, seekBarNo, progress, lDataSet2, "GREEN");
            } else if (selectedChannel.equals("BLUE")) {
                setSwitch(entries3, seekBarNo, progress, lDataSet3, "BLUE");
            } else if (selectedChannel.equals("WHITE")) {
                setSwitch(entries4, seekBarNo, progress, lDataSet4, "WHITE");
            }
        }
        else if (model.equals("SPECTRUM+")) {
            if (selectedChannel.equals("5000K")) {
                setSwitch(entries, seekBarNo, progress, lDataSet1, "5000K");
            } else if (selectedChannel.equals("6500K")) {
                setSwitch(entries2, seekBarNo, progress, lDataSet2, "6500K");
            } else if (selectedChannel.equals("9000K")) {
                setSwitch(entries3, seekBarNo, progress, lDataSet3, "9000K");
            } else if (selectedChannel.equals("MAGENTA")) {
                setSwitch(entries4, seekBarNo, progress, lDataSet4, "MAGENTA");
            }

        }
        else if (model.equals("WIDE SPECT")) {
            if (selectedChannel.equals("REDDISH WHITE")) {
                setSwitch(entries, seekBarNo, progress, lDataSet1, "REDDISH WHITE");
            } else if (selectedChannel.equals("GREENISH WHITE")) {
                setSwitch(entries2, seekBarNo, progress, lDataSet2, "GREENISH WHITE");
            } else if (selectedChannel.equals("BLUEISH WHITE")) {
                setSwitch(entries3, seekBarNo, progress, lDataSet3, "BLUEISH WHITE");
            } else if (selectedChannel.equals("SUNLIKE WHITE")) {
                setSwitch(entries4, seekBarNo, progress, lDataSet4, "SUNLIKE WHITE");
            }

        }
        else if (model.equals("UV+")) {
            if (selectedChannel.equals("5000K")) {
                setSwitch(entries, seekBarNo, progress, lDataSet1, "5000K");
            } else if (selectedChannel.equals("6500K")) {
                setSwitch(entries2, seekBarNo, progress, lDataSet2, "6500K");
            } else if (selectedChannel.equals("9000K")) {
                setSwitch(entries3, seekBarNo, progress, lDataSet3, "9000K");
            } else if (selectedChannel.equals("MAGENTA")) {
                setSwitch(entries4, seekBarNo, progress, lDataSet4, "MAGENTA");
            }

        }
        chartData.clearValues();
        chartData.addDataSet(lDataSet1);
        chartData.addDataSet(lDataSet2);
        chartData.addDataSet(lDataSet3);
        chartData.addDataSet(lDataSet4);
        chartData.addDataSet(lDataSet5);
        chartData.addDataSet(lDataSet6);
        //chartData.addDataSet(lDataSet7);
        //chartData.addDataSet(lDataSet8);

        mChart.setData(chartData);
        mChart.invalidate();

    }

    public void setSwitch(ArrayList entry, int seekBarNo, int progress, LineDataSet lDataSet, String label) {
        switch (modelNo) {
            case MODEL_DEFAULT:
            case MODEL_FMAX:
            case MODEL_SMAJOR:
            case MODEL_SMAX:
            case MODEL_FMAJOR:
                try {
                    //entry.set(seekBarNo-1,new Entry(seekBarNo-1,progress));
                    lDataSet.setLabel(label);
                    retrieveMemorizedDatas();
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                break;
            default:
                break;
        }
    }

    public void setSpinner() {
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, channels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_channel.setAdapter(adapter);
    }

    public void retrieveMemorizedDatas() {
        entries.clear();
        entries2.clear();
        entries3.clear();
        entries4.clear();
        entries5.clear();
        entries6.clear();
        entries7.clear();
        entries8.clear();

        String model = localDataManager.getSharedPreference(getContext(), "model", "manual");

        if (model.equals("manual")) {
            String mChannel = "Channel 1";
            String c1f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c1f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c1f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c1f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");

            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c1gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c1gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));

            entries.add(new Entry(c1gdh / 8f + c1gdm / 1000f, Integer.parseInt(c1f1)));
            entries.add(new Entry(c1gh / 8f + c1gm / 1000f, Integer.parseInt(c1f2)));
            entries.add(new Entry(c1gbh / 8f + c1gbm / 1000f, Integer.parseInt(c1f3)));
            entries.add(new Entry(c1ah / 8f + c1am / 1000f, Integer.parseInt(c1f4)));

            lDataSet1 = new LineDataSet(entries, "ROYAL");
            lDataSet1.setLineWidth(2);
            chartData.addDataSet(lDataSet1);
            lDataSet1.setColor(R.color.lighgray);

            mChannel = "Channel 2";
            String c2f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c2f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c2f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c2f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");

            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "07"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c2gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c2gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));

            entries2.add(new Entry(c2gdh / 8f + c2gdm / 1000f, Integer.parseInt(c2f1)));
            entries2.add(new Entry(c2gh / 8f + c2gm / 1000f, Integer.parseInt(c2f2)));
            entries2.add(new Entry(c2gbh / 8f + c2gbm / 1000f, Integer.parseInt(c2f3)));
            entries2.add(new Entry(c2ah / 8f + c2am / 1000f, Integer.parseInt(c2f4)));

            lDataSet2 = new LineDataSet(entries2, "BLUE");
            lDataSet2.setLineWidth(2);
            chartData.addDataSet(lDataSet2);
            lDataSet2.setColor(R.color.lighgray);

            mChannel = "Channel 3";
            String c3f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c3f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c3f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c3f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");

            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "07"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c3gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c3gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));


            entries3.add(new Entry(c3gdh / 8f + c3gdm / 1000f, Integer.parseInt(c3f1)));
            entries3.add(new Entry(c3gh / 8f + c3gm / 1000f, Integer.parseInt(c3f2)));
            entries3.add(new Entry(c3gbh / 8f + c3gbm / 1000f, Integer.parseInt(c3f3)));
            entries3.add(new Entry(c3ah / 8f + c3am / 1000f, Integer.parseInt(c3f4)));


            lDataSet3 = new LineDataSet(entries3, "CYAN +");
            lDataSet3.setLineWidth(2);
            chartData.addDataSet(lDataSet3);
            lDataSet3.setColor(R.color.lighgray);

            mChannel = "Channel 4";
            String c4f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c4f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c4f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c4f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");

            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "07"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c4gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c4gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));

            entries4.add(new Entry(c4gdh / 8f + c4gdm / 1000f, Integer.parseInt(c4f1)));
            entries4.add(new Entry(c4gh / 8f + c4gm / 1000f, Integer.parseInt(c4f2)));
            entries4.add(new Entry(c4gbh / 8f + c4gbm / 1000f, Integer.parseInt(c4f3)));
            entries4.add(new Entry(c4ah / 8f + c4am / 1000f, Integer.parseInt(c4f4)));

            lDataSet4 = new LineDataSet(entries4, "ACTINIC +");
            lDataSet4.setLineWidth(2);
            chartData.addDataSet(lDataSet4);
            lDataSet4.setColor(R.color.lighgray);

            mChannel = "Channel 5";
            String c5f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c5f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c5f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c5f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");

            float c5gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "07"));
            float c5gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c5gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c5gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
            float c5gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c5gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c5ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c5am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));


            entries5.add(new Entry(c5gdh / 8f + c5gdm / 1000f, Integer.parseInt(c5f1)));
            entries5.add(new Entry(c5gh / 8f + c5gm / 1000f, Integer.parseInt(c5f2)));
            entries5.add(new Entry(c5gbh / 8f + c5gbm / 1000f, Integer.parseInt(c5f3)));
            entries5.add(new Entry(c5ah / 8f + c5am / 1000f, Integer.parseInt(c5f4)));

            lDataSet5 = new LineDataSet(entries5, "HE WHITE");
            lDataSet5.setLineWidth(2);
            chartData.addDataSet(lDataSet5);
            lDataSet5.setColor(R.color.lighgray);


            mChannel = "Channel 6";
            String c6f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c6f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c6f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c6f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");

            float c6gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "07"));
            float c6gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c6gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c6gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "6gm", "00"));
            float c6gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c6gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c6ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c6am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));


            entries6.add(new Entry(c6gdh / 8f + c6gdm / 1000f, Integer.parseInt(c6f1)));
            entries6.add(new Entry(c6gh / 8f + c6gm / 1000f, Integer.parseInt(c6f2)));
            entries6.add(new Entry(c6gbh / 8f + c6gbm / 1000f, Integer.parseInt(c6f3)));
            entries6.add(new Entry(c6ah / 8f + c6am / 1000f, Integer.parseInt(c6f4)));


            lDataSet6 = new LineDataSet(entries6, "MAGENTA +");
            lDataSet6.setLineWidth(2);
            chartData.addDataSet(lDataSet6);
            lDataSet6.setColor(R.color.lighgray);

/*
            mChannel = "Channel 7";
            String c7f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c7f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c7f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c7f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");

            float c7gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c7gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float  c7gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float  c7gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c7gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c7gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float  c7ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float  c7am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","00"));

            entries7.add(new Entry(c7gdh/8f+c7gdm/1000f, Integer.parseInt(c7f1)));
            entries7.add(new Entry(c7gh/8f+c7gm/1000f, Integer.parseInt(c7f2)));
            entries7.add(new Entry(c7gbh/8f+c7gbm/1000f, Integer.parseInt(c7f3)));
            entries7.add(new Entry(c7ah/8f+c7am/1000f, Integer.parseInt(c7f4)));


            lDataSet7 = new LineDataSet(entries7, "Kanal 7");
            lDataSet7.setLineWidth(2);
            chartData.addDataSet(lDataSet7);
            lDataSet7.setColor(R.color.lighgray);


            mChannel = "Channel 8";
            String c8f1 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f1","0");
            String c8f2 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f2","0");
            String c8f3 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f3","0");
            String c8f4 = localDataManager.getSharedPreference(getContext(),model+mChannel+"f4","0");

            float c8gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdh","07"));
            float c8gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gdm","00"));
            float  c8gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gh","12"));
            float  c8gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gm","00"));
            float c8gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbh","17"));
            float c8gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"gbm","00"));
            float  c8ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"ah","22"));
            float  c8am = Float.parseFloat(localDataManager.getSharedPreference(getContext(),model+mChannel+"am","00"));

            entries8.add(new Entry(c8gdh/8f+c8gdm/1000f, Integer.parseInt(c8f1)));
            entries8.add(new Entry(c8gh/8f+c8gm/1000f, Integer.parseInt(c8f2)));
            entries8.add(new Entry(c8gbh/8f+c8gbm/1000f, Integer.parseInt(c8f3)));
            entries8.add(new Entry(c8ah/8f+c8am/1000f, Integer.parseInt(c8f4)));


            lDataSet8 = new LineDataSet(entries8, "Kanal 8");
            lDataSet8.setLineWidth(2);
            chartData.addDataSet(lDataSet8);
            lDataSet8.setColor(R.color.lighgray);


            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            /*
            refreshChart(entries,Integer.parseInt(c1f1),Integer.parseInt(c1f2),Integer.parseInt(c1f3),Integer.parseInt(c1f4), c1gdh/8f+c1gdm/1000f,c1gh/8f+c1gm/1000f,c1gbh/8f+c1gbm/1000f,c1ah/8f+c1am/1000f,lDataSet1,2,"Channel 1",R.color.lighgray);
            refreshChart(entries2,Integer.parseInt(c2f1),Integer.parseInt(c2f2),Integer.parseInt(c2f3),Integer.parseInt(c2f4),c2gdh/8f+c2gdm/1000f,c2gh/8f+c2gm/1000f,c2gbh/8f+c2gbm/1000f,c2ah/8f+c2am/1000f,lDataSet2,2,"Channel 2",R.color.lighgray);
            refreshChart(entries3,Integer.parseInt(c3f1),Integer.parseInt(c3f2),Integer.parseInt(c3f3),Integer.parseInt(c3f4),c3gdh/8f+c3gdm/1000f,c3gh/8f+c3gm/1000f,c3gbh/8f+c3gbm/1000f,c3ah/8f+c3am/1000f,lDataSet3,2,"Channel 3",R.color.lighgray);
            refreshChart(entries4,Integer.parseInt(c4f1),Integer.parseInt(c4f2),Integer.parseInt(c4f3),Integer.parseInt(c4f4),c4gdh/8f+c4gdm/1000f,c4gh/8f+c4gm/1000f,c4gbh/8f+c4gbm/1000f,c4ah/8f+c4am/1000f,lDataSet4,2,"Channel 4",R.color.lighgray);
            refreshChart(entries5,Integer.parseInt(c5f1),Integer.parseInt(c5f2),Integer.parseInt(c5f3),Integer.parseInt(c5f4),c5gdh/8f+c5gdm/1000f,c5gh/8f+c5gm/1000f,c5gbh/8f+c5gbm/1000f,c5ah/8f+c5am/1000f,lDataSet5,2,"Channel 5",R.color.lighgray);
            refreshChart(entries6,Integer.parseInt(c6f1),Integer.parseInt(c6f2),Integer.parseInt(c6f3),Integer.parseInt(c6f4),c6gdh/8f+c6gdm/1000f,c6gh/8f+c6gm/1000f,c6gbh/8f+c6gbm/1000f,c6ah/8f+c6am/1000f,lDataSet6,2,"Channel 6",R.color.lighgray);
 */

            mChart.setData(chartData);
            mChart.invalidate();
        }
        else if (model.equals("RGBW")) {
            String mChannel = "RED";
            String c1f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c1f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c1f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c1f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c1gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c1gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));
            mChannel = "GREEN";
            String c2f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c2f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c2f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c2f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c2gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c2gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));
            mChannel = "BLUE";
            String c3f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c3f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c3f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c3f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c3gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c3gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));
            mChannel = "WHITE";
            String c4f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c4f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c4f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c4f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c4gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c4gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));

            refreshChart(entries, Integer.parseInt(c1f1), Integer.parseInt(c1f2), Integer.parseInt(c1f3), Integer.parseInt(c1f4), c1gdh / 8f + c1gdm / 1000f, c1gh / 8f + c1gm / 1000f, c1gbh / 8f + c1gbm / 1000f, c1ah / 8f + c1am / 1000f, lDataSet1, 2, "RED", R.color.lighgray);
            refreshChart(entries2, Integer.parseInt(c2f1), Integer.parseInt(c2f2), Integer.parseInt(c2f3), Integer.parseInt(c2f4), c2gdh / 8f + c2gdm / 1000f, c2gh / 8f + c2gm / 1000f, c2gbh / 8f + c2gbm / 1000f, c2ah / 8f + c2am / 1000f, lDataSet2, 2, "GREEN", R.color.lighgray);
            refreshChart(entries3, Integer.parseInt(c3f1), Integer.parseInt(c3f2), Integer.parseInt(c3f3), Integer.parseInt(c3f4), c3gdh / 8f + c3gdm / 1000f, c3gh / 8f + c3gm / 1000f, c3gbh / 8f + c3gbm / 1000f, c3ah / 8f + c3am / 1000f, lDataSet3, 2, "BLUE", R.color.lighgray);
            refreshChart(entries4, Integer.parseInt(c4f1), Integer.parseInt(c4f2), Integer.parseInt(c4f3), Integer.parseInt(c4f4), c4gdh / 8f + c4gdm / 1000f, c4gh / 8f + c4gm / 1000f, c4gbh / 8f + c4gbm / 1000f, c4ah / 8f + c4am / 1000f, lDataSet4, 2, "WHITE", R.color.lighgray);

        }
        else if (model.equals("SPECTRUM+")) {
            String mChannel = "5000K";
            String c1f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c1f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c1f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c1f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c1gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c1gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));
            mChannel = "6500K";
            String c2f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c2f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c2f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c2f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c2gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c2gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));
            mChannel = "9000K";
            String c3f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c3f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c3f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c3f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c3gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c3gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));
            mChannel = "MAGENTA";
            String c4f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c4f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c4f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c4f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c4gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c4gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));

            refreshChart(entries, Integer.parseInt(c1f1), Integer.parseInt(c1f2), Integer.parseInt(c1f3), Integer.parseInt(c1f4), c1gdh / 8f + c1gdm / 1000f, c1gh / 8f + c1gm / 1000f, c1gbh / 8f + c1gbm / 1000f, c1ah / 8f + c1am / 1000f, lDataSet1, 2, "5000K", R.color.lighgray);
            refreshChart(entries2, Integer.parseInt(c2f1), Integer.parseInt(c2f2), Integer.parseInt(c2f3), Integer.parseInt(c2f4), c2gdh / 8f + c2gdm / 1000f, c2gh / 8f + c2gm / 1000f, c2gbh / 8f + c2gbm / 1000f, c2ah / 8f + c2am / 1000f, lDataSet2, 2, "6500K", R.color.lighgray);
            refreshChart(entries3, Integer.parseInt(c3f1), Integer.parseInt(c3f2), Integer.parseInt(c3f3), Integer.parseInt(c3f4), c3gdh / 8f + c3gdm / 1000f, c3gh / 8f + c3gm / 1000f, c3gbh / 8f + c3gbm / 1000f, c3ah / 8f + c3am / 1000f, lDataSet3, 2, "9000K", R.color.lighgray);
            refreshChart(entries4, Integer.parseInt(c4f1), Integer.parseInt(c4f2), Integer.parseInt(c4f3), Integer.parseInt(c4f4), c4gdh / 8f + c4gdm / 1000f, c4gh / 8f + c4gm / 1000f, c4gbh / 8f + c4gbm / 1000f, c4ah / 8f + c4am / 1000f, lDataSet4, 2, "MAGENTA", R.color.lighgray);
        }
        else if (model.equals("WIDE SPECT")) {
            String mChannel = "REDDISH WHITE";
            String c1f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c1f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c1f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c1f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c1gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c1gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));
            mChannel = "GREENISH WHITE";
            String c2f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c2f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c2f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c2f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c2gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c2gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));
            mChannel = "BLUEISH WHITE";
            String c3f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c3f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c3f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c3f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c3gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c3gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));
            mChannel = "SUNLIKE WHITE";
            String c4f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c4f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c4f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c4f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "0"));
            float c4gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c4gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "0"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "0"));
            float c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "0"));

            refreshChart(entries, Integer.parseInt(c1f1), Integer.parseInt(c1f2), Integer.parseInt(c1f3), Integer.parseInt(c1f4), c1gdh / 8f + c1gdm / 1000f, c1gh / 8f + c1gm / 1000f, c1gbh / 8f + c1gbm / 1000f, c1ah / 8f + c1am / 1000f, lDataSet1, 2, "REDDISH WHITE", R.color.lighgray);
            refreshChart(entries2, Integer.parseInt(c2f1), Integer.parseInt(c2f2), Integer.parseInt(c2f3), Integer.parseInt(c2f4), c2gdh / 8f + c2gdm / 1000f, c2gh / 8f + c2gm / 1000f, c2gbh / 8f + c2gbm / 1000f, c2ah / 8f + c2am / 1000f, lDataSet2, 2, "GREENISH WHITE", R.color.lighgray);
            refreshChart(entries3, Integer.parseInt(c3f1), Integer.parseInt(c3f2), Integer.parseInt(c3f3), Integer.parseInt(c3f4), c3gdh / 8f + c3gdm / 1000f, c3gh / 8f + c3gm / 1000f, c3gbh / 8f + c3gbm / 1000f, c3ah / 8f + c3am / 1000f, lDataSet3, 2, "BLUEISH WHITE", R.color.lighgray);
            refreshChart(entries4, Integer.parseInt(c4f1), Integer.parseInt(c4f2), Integer.parseInt(c4f3), Integer.parseInt(c4f4), c4gdh / 8f + c4gdm / 1000f, c4gh / 8f + c4gm / 1000f, c4gbh / 8f + c4gbm / 1000f, c4ah / 8f + c4am / 1000f, lDataSet4, 2, "SUNLIKE WHITE", R.color.lighgray);
        }
        else if (model.equals("UV+")) {
            String mChannel = "5000K";
            String c1f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c1f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c1f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c1f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c1gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "07"));
            float c1gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c1gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c1gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
            float c1gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c1gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c1ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c1am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));
            mChannel = "6500K";
            String c2f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c2f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c2f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c2f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c2gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "07"));
            float c2gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c2gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c2gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
            float c2gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c2gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c2ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c2am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));
            mChannel = "9000K";
            String c3f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c3f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c3f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c3f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c3gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "07"));
            float c3gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c3gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c3gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
            float c3gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c3gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c3ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c3am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));
            mChannel = "UV PLUS";
            String c4f1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
            String c4f2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
            String c4f3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
            String c4f4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");
            float c4gdh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "07"));
            float c4gdm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
            float c4gh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
            float c4gm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
            float c4gbh = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
            float c4gbm = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
            float c4ah = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22"));
            float c4am = Float.parseFloat(localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00"));

            refreshChart(entries, Integer.parseInt(c1f1), Integer.parseInt(c1f2), Integer.parseInt(c1f3), Integer.parseInt(c1f4), c1gdh / 8f + c1gdm / 1000f, c1gh / 8f + c1gm / 1000f, c1gbh / 8f + c1gbm / 1000f, c1ah / 8f + c1am / 1000f, lDataSet1, 2, "5000K", R.color.lighgray);
            refreshChart(entries2, Integer.parseInt(c2f1), Integer.parseInt(c2f2), Integer.parseInt(c2f3), Integer.parseInt(c2f4), c2gdh / 8f + c2gdm / 1000f, c2gh / 8f + c2gm / 1000f, c2gbh / 8f + c2gbm / 1000f, c2ah / 8f + c2am / 1000f, lDataSet2, 2, "6500K", R.color.lighgray);
            refreshChart(entries3, Integer.parseInt(c3f1), Integer.parseInt(c3f2), Integer.parseInt(c3f3), Integer.parseInt(c3f4), c3gdh / 8f + c3gdm / 1000f, c3gh / 8f + c3gm / 1000f, c3gbh / 8f + c3gbm / 1000f, c3ah / 8f + c3am / 1000f, lDataSet3, 2, "9000K", R.color.lighgray);
            refreshChart(entries4, Integer.parseInt(c4f1), Integer.parseInt(c4f2), Integer.parseInt(c4f3), Integer.parseInt(c4f4), c4gdh / 8f + c4gdm / 1000f, c4gh / 8f + c4gm / 1000f, c4gbh / 8f + c4gbm / 1000f, c4ah / 8f + c4am / 1000f, lDataSet4, 2, "UV PLUS", R.color.lighgray);
        }
    }

    private void saveState(String fileName) {
        FileOutputStream outStream = null;
        templates = new ArrayList<>();
        try {
            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/IkigaiTemplates";

            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File f = new File(fullPath, "/" + fileName + ".myth");
            if (f.exists())
                f.delete();

            outStream = new FileOutputStream(f);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);

            for (int i = 1; i < 7; i++) {
                String mChannel = "Channel " + Integer.toString(i);

                Template t = new Template();
                String cxf1 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f1", "0");
                String cxf2 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f2", "0");
                String cxf3 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f3", "0");
                String cxf4 = localDataManager.getSharedPreference(getContext(), model + mChannel + "f4", "0");

                String cxgdh = (localDataManager.getSharedPreference(getContext(), model + mChannel + "gdh", "7"));
                String cxgdm = (localDataManager.getSharedPreference(getContext(), model + mChannel + "gdm", "00"));
                String cxgh = (localDataManager.getSharedPreference(getContext(), model + mChannel + "gh", "12"));
                String cxgm = (localDataManager.getSharedPreference(getContext(), model + mChannel + "gm", "00"));
                String cxgbh = (localDataManager.getSharedPreference(getContext(), model + mChannel + "gbh", "17"));
                String cxgbm = (localDataManager.getSharedPreference(getContext(), model + mChannel + "gbm", "00"));
                String cxah = localDataManager.getSharedPreference(getContext(), model + mChannel + "ah", "22");
                String cxam = localDataManager.getSharedPreference(getContext(), model + mChannel + "am", "00");

                t.Channel = mChannel;
                t.sabahBrightness = cxf1;
                t.ogleBrightness = cxf2;
                t.aksamBrightness = cxf3;
                t.geceBrightness = cxf4;

                t.sabahHour = cxgdh;
                t.sabahMin = cxgdm;

                t.ogleHour = cxgh;
                t.ogleMin = cxgm;

                t.aksamHour = cxgbh;
                t.aksamMin = cxgbm;

                t.geceHour = cxah;
                t.geceMin = cxam;
                templates.add(t);
            }


            objectOutStream.writeObject(templates);
            objectOutStream.close();


        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void loadState(String fileName) {
        ArrayList<Template> loadedData = null;
        FileInputStream inStream = null;
        try {
            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/IkigaiTemplates";
            File f = new File(fullPath, "/" + fileName);
            inStream = new FileInputStream(f);
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);

            loadedData = ((ArrayList<Template>) objectInStream.readObject());
            objectInStream.close();

            int x=1;
            for (Template data : loadedData) {
                try {
                    String cxf1 = data.sabahBrightness;
                    String cxf2 = data.ogleBrightness;
                    String cxf3 = data.aksamBrightness;
                    String cxf4 = data.geceBrightness;

                    localDataManager.setSharedPreference(getContext(), model + data.Channel + "gdh", data.sabahHour);
                    localDataManager.setSharedPreference(getContext(), model + data.Channel + "gdm", data.sabahMin);

                    localDataManager.setSharedPreference(getContext(), model + data.Channel + "gh", data.ogleHour);
                    localDataManager.setSharedPreference(getContext(), model + data.Channel + "gm", data.ogleMin);

                    localDataManager.setSharedPreference(getContext(), model + data.Channel + "gbh", data.aksamHour);
                    localDataManager.setSharedPreference(getContext(), model + data.Channel + "gbm", data.aksamMin);

                    localDataManager.setSharedPreference(getContext(), model + data.Channel + "ah",data.geceHour);
                    localDataManager.setSharedPreference(getContext(), model + data.Channel + "am", data.geceMin);

                    localDataManager.setSharedPreference(getContext(), model + selectedChannel + "f1", "" + cxf1);
                    localDataManager.setSharedPreference(getContext(), model + selectedChannel + "f2", "" + cxf2);
                    localDataManager.setSharedPreference(getContext(), model + selectedChannel + "f3", "" + cxf3);
                    localDataManager.setSharedPreference(getContext(), model + selectedChannel + "f4", "" + cxf4);

                    seekbar1=Integer.parseInt(cxf1);
                    seekbar2=Integer.parseInt(cxf2);
                    seekbar3=Integer.parseInt(cxf3);
                    seekbar4=Integer.parseInt(cxf4);

                    selectedChannel=data.Channel;

                    setSeekBar(1, seekbar1);
                    setSeekBar(2, seekbar2);
                    setSeekBar(3, seekbar3);
                    setSeekBar(4, seekbar4);

                    sp_channel.setSelection(x-1);
                    onItemSelected(null,getView(),0,0);

//                    btn_gd.setText("SUNRISE " + String.format("%02d", Integer.parseInt(data.sabahHour)) + ":" + String.format("%02d", Integer.parseInt(data.sabahMin)));
//                    btn_g.setText("SUN " + String.format("%02d", Integer.parseInt(data.ogleHour)) + ":" + String.format("%02d", Integer.parseInt(data.ogleMin)));
//                    btn_gb.setText("SUNSET " + String.format("%02d", Integer.parseInt(data.aksamHour)) + ":" + String.format("%02d", Integer.parseInt(data.aksamMin)));
//                    btn_a.setText("NIGHT " + String.format("%02d", Integer.parseInt(data.geceHour)) + ":" + String.format("%02d", Integer.parseInt(data.geceMin)));

                    x++;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            retrieveMemorizedDatas();
            sp_channel.setSelection(0);
            onItemSelected(null,getView(),0,0);

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (OptionalDataException e1) {
            e1.printStackTrace();
        } catch (StreamCorruptedException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    private ArrayList<String> LoadTemplateList() {
        File directoryDefault = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "IkigaiTemplates");

        ArrayList<String> namesArray = new ArrayList<>();
        File[] arrayFiles = directoryDefault.listFiles();

        for (File file : arrayFiles) {
            namesArray.add(file.getName());
        }
        return namesArray;
    }

    public void refreshChart(ArrayList entry, int pivot1, int pivot2, int pivot3, int pivot4, float time1, float time2, float time3, float time4, LineDataSet lineDataSet, int width, String channel, int color) {

        entry.add(new Entry(time1, pivot1));
        entry.add(new Entry(time2, pivot2));
        entry.add(new Entry(time3, pivot3));
        entry.add(new Entry(time4, pivot4));


        //lineDataSet = (LineDataSet) chartData.getDataSetByLabel(channel,false);
        lineDataSet = new LineDataSet(entry, channel);
        lineDataSet.setLineWidth(width);
        chartData.addDataSet(lineDataSet);
        lineDataSet.setColor(color);

        mChart.setData(chartData);
        mChart.invalidate();

    }

    public void refreshChart8Channel(ArrayList entry, int pivot1, int pivot2, int pivot3, int pivot4, int pivot5, int pivot6, int pivot7, int pivot8, float time1, float time2, float time3, float time4, float time5, float time6, float time7, float time8, LineDataSet lineDataSet, int width, String channel, int color) {

        entry.add(new Entry(time1, pivot1));
        entry.add(new Entry(time2, pivot2));
        entry.add(new Entry(time3, pivot3));
        entry.add(new Entry(time4, pivot4));
        entry.add(new Entry(time5, pivot5));
        entry.add(new Entry(time6, pivot6));
        entry.add(new Entry(time7, pivot7));
        entry.add(new Entry(time8, pivot8));

        //lineDataSet = (LineDataSet) chartData.getDataSetByLabel(channel,false);
        lineDataSet = new LineDataSet(entry, channel);
        lineDataSet.setLineWidth(width);
        chartData.addDataSet(lineDataSet);
        lineDataSet.setColor(color);

        mChart.setData(chartData);
        mChart.invalidate();

    }

    /////////////////////////////////////// MP-ANDROID CHART IMPLEMENTATIONS ///////////////////////////////////////////////////
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
