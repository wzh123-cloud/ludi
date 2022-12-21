package com.mk.music.ludi;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class player extends Activity implements View.OnClickListener{
    Button btn2;
    ImageButton play_btn,next_btn,  pre_btn,back_btn,modelButton,modelButton1;
    SeekBar volume_sb, duration_sb;
    TextView artist_tv, name_tv, total_tv, current_tv;
    ImageView front_iv;
    static MediaPlayer mediaPlayer;
    ContentObserver volumeObserver;
    private ObjectAnimator animator;
    private int position;
    private int tubiao;
    Thread updateSb;
    SongTools service;
    private boolean sb_pause = false;
    List<Song> songs;
    public int songNum; // 当前播放的歌曲在List中的下标
    private int Sequence = 1;//顺序播放
    private int Shuffle = 2;//随机播放
    int i = 0;
    int flag = 0;
    int j = 0;
    int flag1 = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.player_layout);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        service = new SongTools();
        back_btn = findViewById(R.id.play_go_back);
        play_btn = findViewById(R.id.play_btn);
        next_btn = findViewById(R.id.play_next);
        modelButton = findViewById(R.id.model);
        modelButton1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        pre_btn = findViewById(R.id.play_pre);
        volume_sb = findViewById(R.id.volume_seekbar);
        duration_sb = findViewById(R.id.play_seekbar);
        artist_tv = findViewById(R.id.play_song_artist);
        name_tv = findViewById(R.id.play_song_name);
        total_tv = findViewById(R.id.play_duration);
        current_tv = findViewById(R.id.play_current_time);
        front_iv = findViewById(R.id.play_song_front);
        modelButton.setOnClickListener(this);
        modelButton1.setOnClickListener(this);

        songs = service.findSongs(player.this);
        this.position = bundle.getInt("position");

        artist_tv.setText(songs.get(bundle.getInt("position")).getArtist());
        name_tv.setText(songs.get(bundle.getInt("position")).getName());

        front_iv.setImageBitmap(songs.get(bundle.getInt("position")).getFront());
        String path = songs.get(bundle.getInt("position")).getPath();

        //若后台在播放歌曲，判断是否为正在播放
       // Log.i("player","onStart ---> SharedPreferences");
        if (mediaPlayer != null) {
            if ((bundle.getInt("now_playing") == bundle.getInt("position"))) {
                if (!mediaPlayer.isPlaying()){
                    play_btn.setBackgroundResource(R.drawable.play_blue);
                }
                duration_sb.setProgress(mediaPlayer.getCurrentPosition());
                current_tv.setText(service.getCurTime(mediaPlayer.getCurrentPosition()));

            }else{
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
                try {
                    //设置歌曲路径为音源
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        }else{
            //开始播放歌曲
            mediaPlayer = new MediaPlayer();

            try {
                //设置歌曲路径为音源
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }
        this.tubiao = bundle.getInt("position");

        service = new SongTools();
        total_tv.setText(service.getCurTime(mediaPlayer.getDuration()));

        updateSb = new Thread(){
            @Override
            public void run() {
                int total = mediaPlayer.getDuration();
                int currPos = mediaPlayer.getCurrentPosition();
                duration_sb.setProgress(currPos);
                while(currPos < total){
                    try {
                        if (sb_pause){
                            sleep(500);
                        }else {
                            sleep(500);
                            currPos = mediaPlayer.getCurrentPosition();
                            duration_sb.setProgress(currPos);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        duration_sb.setMax(mediaPlayer.getDuration());
        updateSb.start();

        //进度条调整歌曲进度
        duration_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i("player","duration_sb ---> setOnSeekBarChangeListener");
                //保证播放时间显示随进度条拖动而改变
                String curTime = service.getCurTime(seekBar.getProgress());
                current_tv.setText(curTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sb_pause = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sb_pause = false;
                mediaPlayer.seekTo(seekBar.getProgress());
                //若此时为暂停状态则继续播放
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    play_btn.setBackgroundResource(R.drawable.pause_blue);
                }
                String curTime = service.getCurTime(seekBar.getProgress());
                current_tv.setText(curTime);
            }
        });

        //使textView按1s加1的速度运行
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sb_pause){
                    String curTime = service.getCurTime(mediaPlayer.getCurrentPosition());
                    current_tv.setText(curTime);
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

        //实现seekbar控制系统音量
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volume_sb.setMax(15);
        volume_sb.setProgress(audioManager.getStreamVolume((AudioManager.STREAM_MUSIC)));

        //注册同步更新广播
        myRegisterReceiver();
        volume_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, i, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        volumeObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                AudioManager audioManager1 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                volume_sb.setProgress(audioManager1.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        };

        //播放完成进入下一首
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next_btn.performClick();
                Intent bc_intent = new Intent();
                bc_intent.putExtra("now_playing_change", tubiao);
                bc_intent.setPackage("com.example.musicplayer");
                bc_intent.setAction(MainActivity2.SongChangeReceiver.ACTION);
                player.this.sendBroadcast(bc_intent);
            }
        });

        //播放按钮，暂停按钮的实现
        play_btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
        //实现切换上/下一首
        pre_btn.setOnClickListener(this);
        next_btn.setOnClickListener(this);
        //实现返回按钮
        back_btn.setOnClickListener(this);
    }

    //按钮监听事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play_btn:
                Toast.makeText(player.this, getResources().getString(R.string.player1), Toast.LENGTH_SHORT).show();
                Log.i("player","onClick ---> play_btn");
                if(mediaPlayer.isPlaying()){
                    play_btn.setBackgroundResource(R.drawable.play_blue);
                    ImageView iv_music = findViewById(R.id.play_song_front);
                    animator = ObjectAnimator.ofFloat(iv_music, "rotation", 0f, 360.0f);
                    animator.setDuration(10000);  //动画旋转一周的时间为10秒
                    animator.setInterpolator(new LinearInterpolator());
                    animator.setRepeatCount(-1);  //-1表示设置动画无限循环
                  //  animator.pause();
                    mediaPlayer.pause();

                }else{
                    Toast.makeText(player.this, getResources().getString(R.string.player2), Toast.LENGTH_SHORT).show();
                    play_btn.setBackgroundResource(R.drawable.pause_blue);
                    animator.start();
                    mediaPlayer.start();
                }
                break;
            case R.id.play_pre:
                Log.i("player","onClick ---> play_pre");
                Song pre_song = new Song();
                position = ((position - 1)<0)?(songs.size() - 1):position - 1;
                pre_song.setName(songs.get(position).getName());
                pre_song.setArtist(songs.get(position).getArtist());
                pre_song.setFront(songs.get(position).getFront());
                pre_song.setDuration(songs.get(position).getDuration());
                pre_song.setPath(songs.get(position).getPath());

                name_tv.setText(pre_song.getName());
                artist_tv.setText(pre_song.getArtist());
                front_iv.setImageBitmap(pre_song.getFront());
                total_tv.setText(service.getCurTime((int) pre_song.getDuration()));
                play_btn.setBackgroundResource(R.drawable.pause_blue);
                tubiao = position;

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(pre_song.getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                duration_sb.setMax(mediaPlayer.getDuration());
                duration_sb.setProgress(0);
                current_tv.setText("0:00");
                break;
            case R.id.play_next:
                Log.i("player","onClick ---> play_next");
                Song next_song = new Song();
                position = (position + 1) % songs.size();
                next_song.setName(songs.get(position).getName());
                next_song.setArtist(songs.get(position).getArtist());
                next_song.setFront(songs.get(position).getFront());
                next_song.setDuration(songs.get(position).getDuration());
                next_song.setPath(songs.get(position).getPath());

                name_tv.setText(next_song.getName());
                artist_tv.setText(next_song.getArtist());
                front_iv.setImageBitmap(next_song.getFront());
                total_tv.setText(service.getCurTime((int) next_song.getDuration()));
                play_btn.setBackgroundResource(R.drawable.pause_blue);
                tubiao = position;

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(next_song.getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                duration_sb.setMax(mediaPlayer.getDuration());
                duration_sb.setProgress(0);
                current_tv.setText("0:00");
                break;
            case R.id.play_go_back:
                Log.i("player","onClick ---> play_go_back");
                Intent intent = new Intent(player.this, MainActivity.class);
                intent.putExtra("now_playing", tubiao);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
            case R.id.model:
                i++;
                if(i % 2 == 1){
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.player4), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    ImageView imageCodeProject = new ImageView(getApplicationContext());
                    imageCodeProject.setImageResource(R.drawable.good);
                    toastView.addView(imageCodeProject, 0);
                    showMyToast(toast, 10*100);
                    //toast.show();
                    //产生一个随机数
                    songNum = (int)(Math.random()*songs.size());
                    System.out.println("song---" + songNum);
                    //initMediaPlayer(songNum);
                    //随机播放falg
                    flag = 1;
                    modelButton.setBackgroundResource(R.drawable.dangquxunhuang);
                }
                else{

                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.player3), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    ImageView imageCodeProject = new ImageView(getApplicationContext());
                    imageCodeProject.setImageResource(R.drawable.good);
                    toastView.addView(imageCodeProject, 0);
                    showMyToast(toast, 10*100);
                    //toast.show();
                    flag = 0;
                    modelButton.setBackgroundResource(R.drawable.xunhuangbofang);
                    break;
                }
                break;
            case R.id.btn1:

                j++;
                if(j % 2 == 1){
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.player5), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    ImageView imageCodeProject = new ImageView(getApplicationContext());
                    imageCodeProject.setImageResource(R.drawable.good);
                    toastView.addView(imageCodeProject, 0);
                    showMyToast(toast, 10*100);flag1 = 1;
                    //toast.show();
                    modelButton1.setBackgroundResource(R.drawable.lovedown);
                    //initMediaPlayer(songNum);
                    //随机播放falg
                    break;
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.player6), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    ImageView imageCodeProject = new ImageView(getApplicationContext());
                    imageCodeProject.setImageResource(R.drawable.good);
                    toastView.addView(imageCodeProject, 0);
                    showMyToast(toast, 10*100);flag1 = 1;
                    //toast.show();
                    modelButton1.setBackgroundResource(R.drawable.loveup);
                    //initMediaPlayer(songNum);
                    //随机播放falg
                    break;
                }
            case R.id.btn2:
                setDialog();
               // finish();
                break;
            case R.id.btn_choose_img:
                //选择照片按钮
                 Toast.makeText(this, getResources().getString(R.string.player7), Toast.LENGTH_SHORT).show();
                //finish();
                break;
            case R.id.btn_open_camera:
                //拍照按钮
                  Toast.makeText(this, getResources().getString(R.string.player8), Toast.LENGTH_SHORT).show();
                //finish();
                break;
            case R.id.btn_cancel:
                //取消按钮
                Toast.makeText(this, getResources().getString(R.string.player9), Toast.LENGTH_SHORT).show();
                //finish();
                break;

                //String[] name={"getResources().getString(R.string.player6)","getResources().getString(R.string.player6)","getResources().getString(R.string.player6)"};


        }

    }
    private void setDialog() {
        Dialog mCameraDialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_choose_img).setOnClickListener(this);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(this);
        root.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }









    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            Intent bc_intent = new Intent();
            bc_intent.putExtra("now_playing_change", tubiao);
            bc_intent.setPackage("com.example.musicplayer");
            bc_intent.setAction(MainActivity2.SongChangeReceiver.ACTION);
            player.this.sendBroadcast(bc_intent);

            animator.pause();
            mediaPlayer.pause();

            finish();
        }
        return false;
    }

    //实现seekbar系统音量控制
    private void myRegisterReceiver(){
        VolumeReceiver volumeReceiver = new VolumeReceiver();
        Log.i("player","myRegisterReceiver ---> myRegisterReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(volumeReceiver, filter);
    }
    public class VolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volume_sb.setProgress(curVolume);
            }
        }
    }
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }
//    @Override
//    protected void onStart(){
//       super.onStart();
//
//          animator.pause();
//       // mediaPlayer.pause();
//
//    }
}
