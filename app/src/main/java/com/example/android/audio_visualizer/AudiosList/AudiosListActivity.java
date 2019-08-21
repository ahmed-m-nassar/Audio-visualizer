package com.example.android.audio_visualizer.AudiosList;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.android.audio_visualizer.AudioPlaying.AudioPlayingActivity;
import com.example.android.audio_visualizer.AudiosList.Adapter.AudiosListAdapter;
import com.example.android.audio_visualizer.AudiosList.Adapter.AudiosListClickListeners;
import com.example.android.audio_visualizer.Models.Audio;
import com.example.android.audio_visualizer.R;

import java.util.ArrayList;
import java.util.List;

public class AudiosListActivity extends AppCompatActivity implements AudiosListClickListeners , AudiosListContract.View {

    private AudiosListPresenter mPresenter;
    private ListView            mAudiosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audios_list);

        //initializing data
        ///////////////////////////////////////////////////////////////
        mPresenter = new AudiosListPresenter(this);
        mAudiosList = (ListView)findViewById(R.id.AudiosList_List);
        //////////////////////////////////////////////////////////////


        mPresenter.getRecords();

    }

    @Override
    public void fillRecordsList(ArrayList<Audio> audios) {
        AudiosListAdapter adapter = new AudiosListAdapter(this , audios ,this);
        mAudiosList.setAdapter(adapter);
    }

    @Override
    public android.view.View.OnClickListener listItemClicked(final Audio audio) {
        android.view.View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext() , AudioPlayingActivity.class);
                intent.putExtra(getString(R.string.AudioPathExtra) , audio.getmPath() );
                startActivity(intent);
            }
        };

        return clickListener;
    }
}
