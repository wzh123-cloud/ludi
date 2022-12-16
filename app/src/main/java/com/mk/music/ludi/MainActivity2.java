package com.mk.music.ludi;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity2 extends Activity implements View.OnClickListener {

    private ListView playlist;
    //当前正在播放歌曲，用于判断继续播放
    public static int now_playing = -1;
    private ObjectAnimator animator;
    private List<Song> songs;
    TextView now_playing_songname;
    ImageView now_playing_songfront;
    Button now_playing_pause;
    ImageView now_playing_next;
    LinearLayout now_playing_bar;
    SongChangeReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        check();
    }





    @Override
    protected void onRestart() {
        if (now_playing != -1) {
            now_playing_songname.setText(songs.get(now_playing).getName());
            now_playing_songfront.setImageBitmap(songs.get(now_playing).getFront());
            super.onRestart();
        }
    }

    //动态获取权限
    public void check() {
        Log.i("MainActivity2","check ---> Dexter");
        Dexter.withContext(MainActivity2.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    //设置播放列表
    public void displaySongs(){
        SongTools service = new SongTools();
        songs = service.findSongs(MainActivity2.this);
        playlist = findViewById(R.id.playlist);
        now_playing_songname = findViewById(R.id.now_playing_songname_tv);
        now_playing_songfront = findViewById(R.id.now_playing_songfront);
        now_playing_bar = findViewById(R.id.now_playing_bar);
        now_playing_pause = findViewById(R.id.now_playing_pause_btn);
        now_playing_next = findViewById(R.id.now_playing_next_btn);

        now_playing_bar.setOnClickListener(this);
        now_playing_pause.setOnClickListener(this);
        now_playing_next.setOnClickListener(this);

        receiver = new SongChangeReceiver();
        registerReceiver(receiver, new IntentFilter(SongChangeReceiver.ACTION));
        MyAdapter adapter = new MyAdapter(songs,MainActivity2.this);
        playlist.setAdapter(adapter);
        playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MainActivity2.this, player.class);
                bundle.putInt("position", i);
                bundle.putInt("now_playing", now_playing);
                intent.putExtras(bundle);
                //返回值为当前正在播放的歌曲位置
                startActivityForResult(intent, 0x11);
            }
        });
        if (now_playing != -1){
            now_playing_songname.setText(songs.get(now_playing).getName());
            now_playing_songfront.setImageBitmap(songs.get(now_playing).getFront());
            now_playing_pause.setBackgroundResource(R.drawable.pause_blue);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("MainActivity2", resultCode+"");
        if (requestCode == 0x11){
            if (resultCode == RESULT_OK){
                this.now_playing = data.getIntExtra("now_playing", -1);
                if (player.mediaPlayer != null){
                    now_playing_songname.setText(songs.get(now_playing).getName());
                    now_playing_songfront.setImageBitmap(songs.get(now_playing).getFront());
                    if (player.mediaPlayer.isPlaying())
                        now_playing_pause.setBackgroundResource(R.drawable.pause_blue);
                    else
                        now_playing_pause.setBackgroundResource(R.drawable.play_blue);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.now_playing_pause_btn:
                Log.i("MainActivity2","onClick ---> now_playing_pause_btn");
                if (player.mediaPlayer != null){
                    Toast.makeText(MainActivity2.this, getResources().getString(R.string.player2), Toast.LENGTH_SHORT).show();
                    if(player.mediaPlayer.isPlaying()){
                        now_playing_pause.setBackgroundResource(R.drawable.play_blue);
                        player.mediaPlayer.pause();
                    }else{
                        Toast.makeText(MainActivity2.this, getResources().getString(R.string.player1), Toast.LENGTH_SHORT).show();
                        now_playing_pause.setBackgroundResource(R.drawable.pause_blue);
                        player.mediaPlayer.start();
                    }
                }else{
                    now_playing = 0;
                    player.mediaPlayer = new MediaPlayer();
                    try {
                        //设置歌曲路径为音源
                        player.mediaPlayer.setDataSource(songs.get(now_playing).getPath());
                        player.mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.mediaPlayer.start();
                    now_playing_songname.setText(songs.get(now_playing).getName());
                    now_playing_songfront.setImageBitmap(songs.get(now_playing).getFront());
                    now_playing_pause.setBackgroundResource(R.drawable.pause_blue);
                }
                break;
            case R.id.now_playing_next_btn:
                Log.i("MainActivity2","onClick ---> now_playing_next_btn");
                now_playing = (now_playing + 1) % songs.size();
                now_playing_songname.setText(songs.get(now_playing).getName());
                now_playing_songfront.setImageBitmap(songs.get(now_playing).getFront());
                now_playing_pause.setBackgroundResource(R.drawable.pause_blue);
                player.mediaPlayer.stop();
                try {
                    player.mediaPlayer.reset();
                    player.mediaPlayer.setDataSource(songs.get(now_playing).getPath());
                    player.mediaPlayer.prepare();
                    player.mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.now_playing_bar:
                Log.i("MainActivity2","onClick ---> now_playing_bar");
                if (player.mediaPlayer != null){
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(MainActivity2.this, player.class);
                    bundle.putInt("position", now_playing);
                    bundle.putInt("now_playing", now_playing);
                    intent.putExtras(bundle);
                    //返回值为当前正在播放的歌曲位置
                    startActivityForResult(intent, 0x11);
                }
                break;
        }
    }

    protected class SongChangeReceiver extends BroadcastReceiver{

        static final String ACTION = "com.example.musicplayer.songchange";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("MainActivity2","SongChangeReceiver ---> BroadcastReceiver");
            now_playing = intent.getIntExtra("now_playing_change", -1);
            now_playing_songname.setText(songs.get(now_playing).getName());
            now_playing_songfront.setImageBitmap(songs.get(now_playing).getFront());
            if (player.mediaPlayer.isPlaying()){
                now_playing_pause.setBackgroundResource(R.drawable.pause_blue);
            }
        }
    }
}