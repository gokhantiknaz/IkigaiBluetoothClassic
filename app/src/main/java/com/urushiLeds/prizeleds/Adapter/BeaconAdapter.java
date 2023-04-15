package com.urushiLeds.prizeleds.Adapter;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.urushi.prizeleds.R;
import com.urushiLeds.prizeleds.Beacon;

import java.util.List;

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.DeviceViewHolder> {
    private Context context;
    private List<Beacon> beacons;



    private ScanCallback callBackDevice;

    public BeaconAdapter(Context context, List<Beacon> beacons) {
        this.context = context;
        this.beacons = beacons;

    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bluetoothdevices, parent, false);
        return new DeviceViewHolder(view, context, beacons);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {

        holder.tv_bleadress.setText(beacons.get(position).getAddress());
        holder.tv_bleinfo.setText(beacons.get(position).getName());

        Log.i("address",beacons.get(position).getAddress() );
        Log.i("serial",beacons.get(position).getSerialNumber() );
        Log.i("name",beacons.get(position).getName() );
//        holder.rssiView.setText(beacons.get(position).getRssi());

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLineLight));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLineMedium));
        }


    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }


    public void setCallback(ScanCallback l){
        callBackDevice = l;
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_bleinfo,tv_bleadress;
        public CardView cv_rv;
        public ImageView iv_add;
        List<Beacon> beacons;
        Context context;
        public DeviceViewHolder(View view, Context context, List<Beacon> beacons) {
            super(view);
            this.beacons = beacons;
            this.context = context;

            view.setOnClickListener(this);
            tv_bleinfo = itemView.findViewById(R.id.tv_bleName);
            tv_bleadress = itemView.findViewById(R.id.tv_bleId);
            cv_rv = itemView.findViewById(R.id.cv_rv);
            iv_add = itemView.findViewById(R.id.iv_add);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Beacon beacon = beacons.get(position);

        }
    }
}

