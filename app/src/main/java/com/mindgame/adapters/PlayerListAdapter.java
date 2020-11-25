package com.mindgame.adapters;

import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mindgame.activities.GameScreenActivity;
import com.mindgame.activities.R;


public class PlayerListAdapter extends ArrayAdapter<ResultInfo>  {


    private GameScreenActivity activity;


    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtMacAddr;
        TextView txtSts;
    }

    public PlayerListAdapter( GameScreenActivity activity) {
        super(activity, R.layout.player_list_item_layout);
        this.activity=activity;

    }


    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ResultInfo dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.player_list_item_layout, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.playerName);
            viewHolder.txtMacAddr = (TextView) convertView.findViewById(R.id.playerMac);
            viewHolder.txtSts = (TextView) convertView.findViewById(R.id.resultInfo);
            result=convertView;
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;
        viewHolder.txtName.setText(dataModel.getPlayer().getName());
        viewHolder.txtMacAddr.setText(dataModel.getPlayer().getMacAddress());
        String status=((dataModel.isResult())?"Won":"Lost");
        if(dataModel.isResult()) {
            status+=" [ "+dataModel.getElapsedTime()+" secs ]";
            viewHolder.txtSts.setTextColor(getContext().getResources().getColor(R.color.colorGreen));
        }
        else
        {
            viewHolder.txtSts.setTextColor(getContext().getResources().getColor(R.color.colorRed));

        }
        viewHolder.txtSts.setText(status);
        return convertView;
    }
}
