package com.example.android.audio_visualizer.audios_list.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.audio_visualizer.models.Audio;
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
        final Audio item = getItem(position);
        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.audio_list_item,parent,false);
        }

        //getting views
        final RelativeLayout parentLayout = (RelativeLayout)listItemView.findViewById(R.id.AudioListItem_Parent_RelativeLayout);
        TextView audioName =   listItemView.findViewById(R.id.AudioListItem_AudioName_TextView);
        TextView audioDuration =  listItemView.findViewById(R.id.AudioListItem_Duration_TextView);
        TextView audioDate =  listItemView.findViewById(R.id.AudioListItem_Date_TextView);
        TextView audioPath =  listItemView.findViewById(R.id.AudioListItem_Path_TextView);
        //setting data
        audioName.setText(item.getmName());
        audioDuration.setText(String.valueOf(item.getmDuration()));
        audioDate.setText(item.getmDate());


        //click listeners
        /////////////////////////////////////////////////////////////////////////
        parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListeners.optionsMenuClicked(item , parentLayout);
                return true;
            }
        });

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListeners.listItemClicked(item);
            }
        });
        ///////////////////////////////////////////////////////////////////////////

        return listItemView;
    }
}
