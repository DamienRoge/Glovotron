package com.rossuyt.glovotron;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Luong-Thi-Bien BOSSUYT on 03/04/2017.
 */

class DevicesListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<BluetoothDevice> mData;
    private OnPairButtonClickListener mListener;

    DevicesListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    void setData(List<BluetoothDevice> data) {
        mData = data;
    }

    void setListener(OnPairButtonClickListener listener) {
        mListener = listener;
    }

    public int getCount() {
        return (mData == null) ? 0 : mData.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @SuppressLint("InflateParams")
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_devices, parent, false);

            holder = new ViewHolder();

            holder.nameTv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.addressTv = (TextView) convertView.findViewById(R.id.tv_address);
            holder.pairBtn = (Button) convertView.findViewById(R.id.btn_pair);

            convertView.setTag(holder);
            convertView.setSelected(true);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = mData.get(position);

        holder.nameTv.setText(device.getName());
        holder.addressTv.setText(device.getAddress());
        holder.pairBtn.setText((device.getBondState() == BluetoothDevice.BOND_BONDED) ? "Supprimer" : "Lier");

        holder.pairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPairButtonClick(position);
                }
            }
        });

        convertView.setSelected(true);
        return convertView;
    }

    private static class ViewHolder {
        TextView nameTv;
        TextView addressTv;
        TextView pairBtn;
    }

    interface OnPairButtonClickListener {
        public abstract void onPairButtonClick(int position);
    }
}
