package com.example.android.audio_visualizer.AudiosList.Adapter;

import android.view.View;

import com.example.android.audio_visualizer.Models.Audio;

public interface AudiosListClickListeners {
    public View.OnClickListener listItemClicked(final Audio audio);
}
