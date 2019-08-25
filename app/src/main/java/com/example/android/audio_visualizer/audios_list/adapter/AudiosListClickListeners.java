package com.example.android.audio_visualizer.audios_list.adapter;

import android.view.View;

import com.example.android.audio_visualizer.models.Audio;

public interface AudiosListClickListeners {
    void listItemClicked(final Audio audio);
    void optionsMenuClicked(final Audio audio, View parentLayout);
}
