package com.mythleds.ikigai.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mythleds.ikigai.R;
import com.mythleds.ikigai.Class.LocalDataManager;

public class Fragment3 extends Fragment {
    private TextView tv_testSeekBar1,tv_testSeekBar2,tv_testSeekBar3,tv_testSeekBar4,tv_testSeekBar5,tv_testSeekBar6,tv_testSeekBar7,tv_testSeekBar8;
    private SeekBar test_seekBar1,test_seekBar2,test_seekBar3,test_seekBar4,test_seekBar5,test_seekBar6,test_seekBar7,test_seekBar8;
    private String test_model = "test", model;
    private LocalDataManager localDataManager;
    private TextView tv_sb1title,tv_sb2title,tv_sb3title,tv_sb4title,tv_sb5title,tv_sb6title,tv_sb7title,tv_sb8title;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment3,container,false);
        init(view);

        localDataManager.setSharedPreference(getContext(),"test_model","test");

        model = localDataManager.getSharedPreference(getContext(),"model","manual");

        if (model.equals("manual")){

        }else if (model.equals("RGBW")){
            tv_sb1title.setVisibility(View.VISIBLE);
            tv_sb2title.setVisibility(View.VISIBLE);
            tv_sb3title.setVisibility(View.VISIBLE);
            tv_sb4title.setVisibility(View.VISIBLE);
            tv_sb5title.setVisibility(View.INVISIBLE);
            tv_sb6title.setVisibility(View.INVISIBLE);
            tv_sb7title.setVisibility(View.INVISIBLE);
            tv_sb8title.setVisibility(View.INVISIBLE);


            tv_sb1title.setText("RED");
            tv_sb2title.setText("GREEN");
            tv_sb3title.setText("BLUE");
            tv_sb4title.setText("WHITE");

            tv_testSeekBar1.setVisibility(View.VISIBLE);
            tv_testSeekBar2.setVisibility(View.VISIBLE);
            tv_testSeekBar3.setVisibility(View.VISIBLE);
            tv_testSeekBar4.setVisibility(View.VISIBLE);
            tv_testSeekBar5.setVisibility(View.INVISIBLE);
            tv_testSeekBar6.setVisibility(View.INVISIBLE);
            tv_testSeekBar7.setVisibility(View.INVISIBLE);
            tv_testSeekBar8.setVisibility(View.INVISIBLE);

            test_seekBar1.setVisibility(View.VISIBLE);
            test_seekBar2.setVisibility(View.VISIBLE);
            test_seekBar3.setVisibility(View.VISIBLE);
            test_seekBar4.setVisibility(View.VISIBLE);
            test_seekBar5.setVisibility(View.INVISIBLE);
            test_seekBar6.setVisibility(View.INVISIBLE);
            test_seekBar7.setVisibility(View.INVISIBLE);
            test_seekBar8.setVisibility(View.INVISIBLE);

        }
        else if (model.equals("SPECTRUM+")){
            tv_sb1title.setVisibility(View.VISIBLE);
            tv_sb2title.setVisibility(View.VISIBLE);
            tv_sb3title.setVisibility(View.VISIBLE);
            tv_sb4title.setVisibility(View.VISIBLE);
            tv_sb5title.setVisibility(View.INVISIBLE);
            tv_sb6title.setVisibility(View.INVISIBLE);
            tv_sb7title.setVisibility(View.INVISIBLE);
            tv_sb8title.setVisibility(View.INVISIBLE);

            tv_sb1title.setText("5000K");
            tv_sb2title.setText("6500K");
            tv_sb3title.setText("9000K");
            tv_sb4title.setText("MAGENTA");


            tv_testSeekBar1.setVisibility(View.VISIBLE);
            tv_testSeekBar2.setVisibility(View.VISIBLE);
            tv_testSeekBar3.setVisibility(View.VISIBLE);
            tv_testSeekBar4.setVisibility(View.VISIBLE);
            tv_testSeekBar5.setVisibility(View.INVISIBLE);
            tv_testSeekBar6.setVisibility(View.INVISIBLE);
            tv_testSeekBar7.setVisibility(View.INVISIBLE);
            tv_testSeekBar8.setVisibility(View.INVISIBLE);

            test_seekBar1.setVisibility(View.VISIBLE);
            test_seekBar2.setVisibility(View.VISIBLE);
            test_seekBar3.setVisibility(View.VISIBLE);
            test_seekBar4.setVisibility(View.VISIBLE);
            test_seekBar5.setVisibility(View.INVISIBLE);
            test_seekBar6.setVisibility(View.INVISIBLE);
            test_seekBar7.setVisibility(View.INVISIBLE);
            test_seekBar8.setVisibility(View.INVISIBLE);
        }
        else if (model.equals("WIDE SPECT")){
            tv_sb1title.setVisibility(View.VISIBLE);
            tv_sb2title.setVisibility(View.VISIBLE);
            tv_sb3title.setVisibility(View.VISIBLE);
            tv_sb4title.setVisibility(View.VISIBLE);
            tv_sb5title.setVisibility(View.INVISIBLE);
            tv_sb6title.setVisibility(View.INVISIBLE);
            tv_sb7title.setVisibility(View.INVISIBLE);
            tv_sb8title.setVisibility(View.INVISIBLE);

            tv_sb1title.setText("REDDISH WHITE");
            tv_sb2title.setText("GREENISH WHITE");
            tv_sb3title.setText("BLUEISH WHITE");
            tv_sb4title.setText("SUNLIKE WHITE");

            tv_testSeekBar1.setVisibility(View.VISIBLE);
            tv_testSeekBar2.setVisibility(View.VISIBLE);
            tv_testSeekBar3.setVisibility(View.VISIBLE);
            tv_testSeekBar4.setVisibility(View.VISIBLE);
            tv_testSeekBar5.setVisibility(View.INVISIBLE);
            tv_testSeekBar6.setVisibility(View.INVISIBLE);
            tv_testSeekBar7.setVisibility(View.INVISIBLE);
            tv_testSeekBar8.setVisibility(View.INVISIBLE);

            test_seekBar1.setVisibility(View.VISIBLE);
            test_seekBar2.setVisibility(View.VISIBLE);
            test_seekBar3.setVisibility(View.VISIBLE);
            test_seekBar4.setVisibility(View.VISIBLE);
            test_seekBar5.setVisibility(View.INVISIBLE);
            test_seekBar6.setVisibility(View.INVISIBLE);
            test_seekBar7.setVisibility(View.INVISIBLE);
            test_seekBar8.setVisibility(View.INVISIBLE);
        }
        else if (model.equals("UV+")){
            tv_sb1title.setVisibility(View.VISIBLE);
            tv_sb2title.setVisibility(View.VISIBLE);
            tv_sb3title.setVisibility(View.VISIBLE);
            tv_sb4title.setVisibility(View.VISIBLE);
            tv_sb5title.setVisibility(View.INVISIBLE);
            tv_sb6title.setVisibility(View.INVISIBLE);
            tv_sb7title.setVisibility(View.INVISIBLE);
            tv_sb8title.setVisibility(View.INVISIBLE);

            tv_sb1title.setText("5000K");
            tv_sb2title.setText("6500K");
            tv_sb3title.setText("9000K");
            tv_sb4title.setText("UV PLUS");


            tv_testSeekBar1.setVisibility(View.VISIBLE);
            tv_testSeekBar2.setVisibility(View.VISIBLE);
            tv_testSeekBar3.setVisibility(View.VISIBLE);
            tv_testSeekBar4.setVisibility(View.VISIBLE);
            tv_testSeekBar5.setVisibility(View.INVISIBLE);
            tv_testSeekBar6.setVisibility(View.INVISIBLE);
            tv_testSeekBar7.setVisibility(View.INVISIBLE);
            tv_testSeekBar8.setVisibility(View.INVISIBLE);

            test_seekBar1.setVisibility(View.VISIBLE);
            test_seekBar2.setVisibility(View.VISIBLE);
            test_seekBar3.setVisibility(View.VISIBLE);
            test_seekBar4.setVisibility(View.VISIBLE);
            test_seekBar5.setVisibility(View.INVISIBLE);
            test_seekBar6.setVisibility(View.INVISIBLE);
            test_seekBar7.setVisibility(View.INVISIBLE);
            test_seekBar8.setVisibility(View.INVISIBLE);
        }

        test_seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar1.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),test_model+"f1",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        test_seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar2.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),test_model+"f2",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        test_seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar3.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),test_model+"f3",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        test_seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar4.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),test_model+"f4",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        test_seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar5.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),test_model+"f5",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        test_seekBar6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar6.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),test_model+"f6",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        test_seekBar7.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar7.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),test_model+"f7",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        test_seekBar8.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_testSeekBar8.setText("% "+progress);
                localDataManager.setSharedPreference(getContext(),test_model+"f8",""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }
    public void init(View view){
        tv_sb1title = view.findViewById(R.id.tv_testsb1title);
        tv_sb2title = view.findViewById(R.id.tv_testSb2title);
        tv_sb3title = view.findViewById(R.id.tv_testSb3title);
        tv_sb4title = view.findViewById(R.id.tv_testSb4title);
        tv_sb5title = view.findViewById(R.id.tv_testSb5title);
        tv_sb6title = view.findViewById(R.id.tv_testSb6title);
        tv_sb7title = view.findViewById(R.id.tv_testSb7title);
        tv_sb8title = view.findViewById(R.id.tv_testSb8title);

        tv_testSeekBar1 = view.findViewById(R.id.tv_testSeekBar1);
        tv_testSeekBar2 = view.findViewById(R.id.tv_testSeekBar2);
        tv_testSeekBar3 = view.findViewById(R.id.tv_testSeekBar3);
        tv_testSeekBar4 = view.findViewById(R.id.tv_testSeekBar4);
        tv_testSeekBar5 = view.findViewById(R.id.tv_testSeekBar5);
        tv_testSeekBar6 = view.findViewById(R.id.tv_testSeekBar6);
        tv_testSeekBar7 = view.findViewById(R.id.tv_testSeekBar7);
        tv_testSeekBar8 = view.findViewById(R.id.tv_testSeekBar8);

        test_seekBar1 = view.findViewById(R.id.test_seekBar1);
        test_seekBar2 = view.findViewById(R.id.test_seekBar2);
        test_seekBar3 = view.findViewById(R.id.test_seekBar3);
        test_seekBar4 = view.findViewById(R.id.test_seekBar4);
        test_seekBar5 = view.findViewById(R.id.test_seekBar5);
        test_seekBar6 = view.findViewById(R.id.test_seekBar6);
        test_seekBar7 = view.findViewById(R.id.test_seekBar7);
        test_seekBar8 = view.findViewById(R.id.test_seekBar8);


        localDataManager = new LocalDataManager();

        String prg1 = localDataManager.getSharedPreference(getContext(),"testf1","0");
        String prg2 = localDataManager.getSharedPreference(getContext(),"testf2","0");
        String prg3 = localDataManager.getSharedPreference(getContext(),"testf3","0");
        String prg4 = localDataManager.getSharedPreference(getContext(),"testf4","0");
        String prg5 = localDataManager.getSharedPreference(getContext(),"testf5","0");
        String prg6 = localDataManager.getSharedPreference(getContext(),"testf6","0");
        String prg7 = localDataManager.getSharedPreference(getContext(),"testf7","0");
        String prg8 = localDataManager.getSharedPreference(getContext(),"testf8","0");

        tv_testSeekBar1.setText("% "+prg1);
        tv_testSeekBar2.setText("% "+prg2);
        tv_testSeekBar3.setText("% "+prg3);
        tv_testSeekBar4.setText("% "+prg4);
        tv_testSeekBar5.setText("% "+prg5);
        tv_testSeekBar6.setText("% "+prg6);
        tv_testSeekBar7.setText("% "+prg7);
        tv_testSeekBar8.setText("% "+prg8);


        test_seekBar1.setProgress(Integer.parseInt(prg1));
        test_seekBar2.setProgress(Integer.parseInt(prg2));
        test_seekBar3.setProgress(Integer.parseInt(prg3));
        test_seekBar4.setProgress(Integer.parseInt(prg4));
        test_seekBar5.setProgress(Integer.parseInt(prg5));
        test_seekBar6.setProgress(Integer.parseInt(prg6));
        test_seekBar7.setProgress(Integer.parseInt(prg7));
        test_seekBar8.setProgress(Integer.parseInt(prg8));
    }
}
