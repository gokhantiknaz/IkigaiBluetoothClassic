package com.mythleds.ikigai.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mythleds.ikigai.R;
import com.mythleds.ikigai.Class.LocalDataManager;

public class Fragment2 extends Fragment {

    private Button btn_custom,btn_fmajor,btn_smajor,btn_fmax,btn_smax;
    private ImageButton ib_back;
    LocalDataManager localDataManager;

    public static final String MODEL_FMAJOR = "RGBW";
    public static final String MODEL_SMAJOR = "SPECTRUM+";
    public static final String MODEL_FMAX = "WIDE SPECT";
    public static final String MODEL_SMAX = "UV+";
    public static final String MODEL_MANUAL = "manual";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2,container,false);

        init(view);

        localDataManager = new LocalDataManager();
        String model = localDataManager.getSharedPreference(getContext(),"model","manual");
        if (model.equals(MODEL_MANUAL)){
            btn_custom.setText("Selected");
            btn_custom.setBackgroundColor(getResources().getColor(R.color.accent));
        }else if (model.equals(MODEL_FMAJOR)){
            btn_fmajor.setText("Selected");
            btn_fmajor.setBackgroundColor(getResources().getColor(R.color.accent));
        }else if (model.equals(MODEL_SMAJOR)){
            btn_smajor.setText("Selected");
            btn_smajor.setBackgroundColor(getResources().getColor(R.color.accent));
        }else if (model.equals(MODEL_FMAX)){
            btn_fmax.setText("Selected");
            btn_fmax.setBackgroundColor(getResources().getColor(R.color.accent));
        }else if (model.equals(MODEL_SMAX)){
            btn_smax.setText("Selected");
            btn_smax.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        btn_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_custom.setText("Selected");
                btn_custom.setBackgroundColor(getResources().getColor(R.color.accent));

                btn_fmajor.setText("Select");
                btn_fmajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smajor.setText("Select");
                btn_smajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_fmax.setText("Select");
                btn_fmax.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smax.setText("Select");
                btn_smax.setBackgroundColor(getResources().getColor(R.color.purple_500));

                localDataManager.setSharedPreference(getContext(),"model",MODEL_MANUAL);
                localDataManager.setSharedPreference(getContext(),"test_model","false");
                sendDatatoFragment("model",MODEL_MANUAL);

            }
        });

        btn_smax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_custom.setText("Select");
                btn_custom.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_fmajor.setText("Select");
                btn_fmajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smajor.setText("Select");
                btn_smajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_fmax.setText("Select");
                btn_fmax.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smax.setText("Selected");
                btn_smax.setBackgroundColor(getResources().getColor(R.color.accent));

                localDataManager.setSharedPreference(getContext(),"model",MODEL_SMAX);
                localDataManager.setSharedPreference(getContext(),"test_model","false");
                sendDatatoFragment("model",MODEL_SMAX);

            }
        });

        btn_fmax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_custom.setText("Select");
                btn_custom.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_fmajor.setText("Select");
                btn_fmajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smajor.setText("Select");
                btn_smajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_fmax.setText("Selected");
                btn_fmax.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smax.setText("Select");
                btn_smax.setBackgroundColor(getResources().getColor(R.color.accent));

                localDataManager.setSharedPreference(getContext(),"model",MODEL_FMAX);
                localDataManager.setSharedPreference(getContext(),"test_model","false");
                sendDatatoFragment("model",MODEL_FMAX);
            }
        });

        btn_smajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_custom.setText("Select");
                btn_custom.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_fmajor.setText("Select");
                btn_fmajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smajor.setText("Selected");
                btn_smajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_fmax.setText("Select");
                btn_fmax.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smax.setText("Select");
                btn_smax.setBackgroundColor(getResources().getColor(R.color.accent));

                localDataManager.setSharedPreference(getContext(),"model",MODEL_SMAJOR);
                localDataManager.setSharedPreference(getContext(),"test_model","false");
                sendDatatoFragment("model",MODEL_SMAJOR);
            }
        });

        btn_fmajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_custom.setText("Select");
                btn_custom.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_fmajor.setText("Selected");
                btn_fmajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smajor.setText("Select");
                btn_smajor.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_fmax.setText("Select");
                btn_fmax.setBackgroundColor(getResources().getColor(R.color.purple_500));

                btn_smax.setText("Select");
                btn_smax.setBackgroundColor(getResources().getColor(R.color.accent));

                localDataManager.setSharedPreference(getContext(),"model",MODEL_FMAJOR);
                localDataManager.setSharedPreference(getContext(),"test_model","false");
                sendDatatoFragment("model",MODEL_FMAJOR);
            }
        });

        return view;
    }

    public void init(View view){

        btn_custom = view.findViewById(R.id.btn_modelCustom);
        btn_fmajor = view.findViewById(R.id.btn_modelFmajor);
        btn_smajor = view.findViewById(R.id.btn_modelSmajor);
        btn_fmax = view.findViewById(R.id.btn_modelfmax);
        btn_smax = view.findViewById(R.id.btn_modelSmax);

        ib_back = view.findViewById(R.id.ib_back);

        ib_click();
    }

    public void sendDatatoFragment(String key, String message){
        Bundle bundle = new Bundle();
        bundle.putString(key,message);
        Fragment1 fragment1 = new Fragment1();
        fragment1.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frame,fragment1).commit();
    }

    public void ib_click(){
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Fragment1 fragment1 = new Fragment1();
                fragment1.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.frame,fragment1).commit();
            }
        });
    }
}
