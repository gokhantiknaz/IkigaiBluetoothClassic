package com.mythleds.ikigai.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mythleds.ikigai.Class.Ble_devices;
import com.mythleds.ikigai.Interface.CallBackDevice;
import com.mythleds.ikigai.R;

import java.util.ArrayList;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder> {

    private ArrayList<Ble_devices> mBleList;
    private CallBackDevice callBackDevice;
    private boolean iv_clicked = false;
    public int pos = 0;
    public void setCallback(CallBackDevice l){
        callBackDevice = l;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_bleinfo,tv_bleadress;
        public CardView cv_rv;
        public ImageView iv_add;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_bleinfo = itemView.findViewById(R.id.tv_bleName);
            tv_bleadress = itemView.findViewById(R.id.tv_bleId);
            cv_rv = itemView.findViewById(R.id.cv_rv);
            iv_add = itemView.findViewById(R.id.iv_add);
        }

    }

    public DeviceAdapter(ArrayList<Ble_devices> ble_devices_list, CallBackDevice callBackDevice1){
        mBleList = ble_devices_list;
        this.callBackDevice = callBackDevice1;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bluetoothdevices,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Ble_devices ble_devices = mBleList.get(position);

        holder.tv_bleinfo.setText("Cihaz adı : " + ble_devices.getDevice_name());
        holder.tv_bleadress.setText(ble_devices.getDevice_id());

        holder.cv_rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cv_rv.getCardBackgroundColor().getDefaultColor() != v.getResources().getColor(R.color.purple_700)){
                    //holder.iv_add.setImageResource(R.drawable.ic_baseline_cancel_24);
                    holder.cv_rv.setCardBackgroundColor(v.getResources().getColor(R.color.design_default_color_secondary));
                    if (callBackDevice != null){
                        callBackDevice.listenerMethod(holder.tv_bleinfo.getText().toString(),holder.tv_bleadress.getText().toString(),true,holder.getPosition());
                    }

                }/*else if (holder.cv_rv.getCardBackgroundColor().getDefaultColor() == v.getResources().getColor(R.color.green)){
                    holder.iv_add.setImageResource(R.drawable.ic_baseline_add_circle_24);
                    holder.cv_rv.setCardBackgroundColor(WHITE);

                    if (callBackDevice != null){
                        callBackDevice.listenerMethod(holder.tv_bleinfo.getText().toString(),holder.tv_bleadress.getText().toString(),false,holder.getPosition());
                    }
                }*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBleList.size();
    }
}
