package com.example.android.audio_visualizer.AudiosList.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.audio_visualizer.Models.Audio;
import com.example.android.audio_visualizer.R;

import java.util.List;

public class AudiosListAdapter extends ArrayAdapter<Audio> {
    private Context mContext;
    private AudiosListClickListeners mClickListeners;

    public AudiosListAdapter(@NonNull Context context, List<Audio> audios
            , AudiosListClickListeners clickListeners) {
        super(context, 0,audios);

        mContext = context;
        mClickListeners = clickListeners;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Audio Item = getItem(position);
        View ListItemView = convertView;
        if(ListItemView == null)
        {
            ListItemView = LayoutInflater.from(getContext()).inflate(R.layout.audio_list_item,parent,false);
        }

        //getting views
        RelativeLayout parentLayout = (RelativeLayout)ListItemView.findViewById(R.id.AudioListItem_Parent_RelativeLayout);
        TextView audioName =  (TextView) ListItemView.findViewById(R.id.AudioListItem_AudioName_TextView);
        TextView audioDuration = (TextView) ListItemView.findViewById(R.id.AudioListItem_Duration_TextView);
        TextView audioDate = (TextView) ListItemView.findViewById(R.id.AudioListItem_Date_TextView);
        TextView audioPath = (TextView) ListItemView.findViewById(R.id.AudioListItem_Path_TextView);
        //setting data
        audioName.setText(Item.getmName());
        audioDuration.setText(String.valueOf(Item.getmDuration()));
        audioDate.setText(Item.getmDate());
        //Todo is it good to add non visible view just to carry important info which i will use later if the view is clicked?
        audioPath.setText(Item.getmPath());

        //click listeners
        parentLayout.setOnClickListener(mClickListeners.listItemClicked(Item));

        return ListItemView;
    }
}
