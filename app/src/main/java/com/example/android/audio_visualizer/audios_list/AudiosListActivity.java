package com.example.android.audio_visualizer.audios_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.android.audio_visualizer.audio_playing.AudioPlayingActivity;
import com.example.android.audio_visualizer.audios_list.adapter.AudiosListAdapter;
import com.example.android.audio_visualizer.audios_list.adapter.AudiosListClickListeners;
import com.example.android.audio_visualizer.models.Audio;
import com.example.android.audio_visualizer.R;

import java.util.ArrayList;

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

        registerForContextMenu(mAudiosList);
    }

    @Override
    public void fillRecordsList(ArrayList<Audio> audios) {
        AudiosListAdapter adapter = new AudiosListAdapter(this , audios ,this);
        mAudiosList.setAdapter(adapter);
    }

    @Override
    public void listItemClicked(final Audio audio) {
        Intent intent = new Intent(getBaseContext() , AudioPlayingActivity.class);
        intent.putExtra(getString(R.string.AudioPathExtra) , audio.getmPath() );
        startActivity(intent);
    }

    @Override
    public void optionsMenuClicked(final Audio audio , View parentLayout) {
        PopupMenu popupMenu = new PopupMenu(this ,parentLayout);
        popupMenu.inflate(R.menu.audio_options_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.HoldClickMenu_Rename:
                        showRenameDialog(audio.getmPath() , audio.getmName());
                        return true;
                    case R.id.HoldClickMenu_Delete:
                        mPresenter.deleteRecord(audio.getmPath());
                        return true;
                        default: break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showRenameDialog(final String audioPath , String oldName) {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                this);
        alert.setTitle("Rename");

        final EditText input = new EditText(this);
        input.setText(oldName);
        alert.setView(input);


        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newName = input.getEditableText().toString();
                //updating database
                mPresenter.updateRecordName(audioPath , newName);

            }
        });

        alert.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
}
