package com.example.android.audio_visualizer.audio_playing.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.audio_visualizer.models.Audio_Picture;
import com.example.android.audio_visualizer.R;

import java.io.File;
import java.util.ArrayList;

public class PicturesListAdapter extends RecyclerView.Adapter<PicturesListAdapter.ViewHolder> {

    public ArrayList<Audio_Picture> audioPics ;
    public Context mContext;
    public PicuresListClickListeners mClickListeners;

    public PicturesListAdapter(ArrayList<Audio_Picture> audioPics, Context mContext , PicuresListClickListeners clickListeners) {
        this.audioPics = audioPics;
        this.mContext = mContext;
        mClickListeners = clickListeners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.picture_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        //showing snap time
        viewHolder.mSnapTime.setText(audioPics.get(i).getmSnapTime());

        //showing image
        File imgFile = new  File(audioPics.get(i).getmPicturePath());

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            viewHolder.mPic.setImageBitmap(myBitmap);

            //setting click listener
            viewHolder.mPic.setOnClickListener(mClickListeners.pictureClicked(myBitmap));
        }
    }

    @Override
    public int getItemCount() {
        return audioPics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPic;
        private TextView mSnapTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.PictureListItem_Picture_ImageView);
            mSnapTime = itemView.findViewById(R.id.PictureListItem_SnapTime_TextView);
        }
    }
}