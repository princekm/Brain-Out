package com.mindgame.adapters;

import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mindgame.activities.PlayerListScreenActivity;
import com.mindgame.activities.R;
import com.mindgame.connection.MultiPlayerHandler;
import com.mindgame.connection.packets.Player;


public class DeviceListAdapter extends ArrayAdapter<Player> implements ListView.OnItemClickListener {

    private PlayerListScreenActivity activity;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Player dataModel=getItem(position);
        activity.joinRoom(dataModel);

    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtMacAddr;
        TextView txtSts;
    }

    public DeviceListAdapter( PlayerListScreenActivity activity) {
        super(activity, R.layout.device_list_item_layout);
        this.activity=activity;

    }


    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Player dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.device_list_item_layout, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.deviceName);
            viewHolder.txtMacAddr = (TextView) convertView.findViewById(R.id.deviceMac);
            viewHolder.txtSts = (TextView) convertView.findViewById(R.id.deviceStatus);
            result=convertView;
            convertView.setTag(viewHolder);
        }
        else
            {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;
        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtMacAddr.setText(dataModel.getMacAddress());
        String status="";
        MultiPlayerHandler multiPlayerHandler = MultiPlayerHandler.getInstance(parent.getContext());
        if(!multiPlayerHandler.hasJoinedGroup())
        {
            if(dataModel.equals(multiPlayerHandler.getPlayer()))
                status="";
            else
                status="Join Group";
        }
        else
        {
            status="Joined";
        }
        viewHolder.txtSts.setText(status);
        return convertView;
    }
}
