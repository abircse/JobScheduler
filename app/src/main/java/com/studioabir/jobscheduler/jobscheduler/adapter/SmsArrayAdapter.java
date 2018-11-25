package com.studioabir.jobscheduler.jobscheduler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.studioabir.jobscheduler.jobscheduler.R;
import com.studioabir.jobscheduler.jobscheduler.dbhelper.SmsDatabaseHelper;
import com.studioabir.jobscheduler.jobscheduler.model.Sms;

import java.util.List;

public class SmsArrayAdapter extends BaseAdapter {

    private Context context;
    private List<Sms> smsArrayList;
    SmsDatabaseHelper databaseHelper;
    private LayoutInflater layoutInflater;

    public SmsArrayAdapter(Context context, List<Sms> smsArrayList) {

        this.context = context;
        this.smsArrayList = smsArrayList;

        databaseHelper = new SmsDatabaseHelper(context);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return smsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return smsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHOLDER holder;

        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.layout_row_sms,null);
            holder = new ViewHOLDER();

            holder.textViewNumber = convertView.findViewById(R.id.textViewNumber);
            holder.textViewTime = convertView.findViewById(R.id.textViewTime);
            holder.textViewMessage = convertView.findViewById(R.id.textViewMessage);
            holder.textViewDate = convertView.findViewById(R.id.textViewDate);
        }
        else
        {
            holder = (ViewHOLDER) convertView.getTag();
        }

        holder.textViewNumber.setText(smsArrayList.get(position).getNumber());
        holder.textViewMessage.setText(smsArrayList.get(position).getMessage());
        holder.textViewTime.setText(smsArrayList.get(position).getTime());
        holder.textViewDate.setText(smsArrayList.get(position).getDate());

        return convertView;
    }

    class ViewHOLDER
    {
        TextView textViewNumber;
        TextView textViewTime;
        TextView textViewMessage;
        TextView textViewDate;
    }
}
