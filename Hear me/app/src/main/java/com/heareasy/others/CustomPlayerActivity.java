package com.heareasy.others;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.heareasy.R;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class CustomPlayerActivity extends AppCompatActivity {

    Toolbar toolbar;

    Timer timer;

    TextView tvCtime, tvLtime, tvTitle;

    ImageButton imgPlay, imgStop, imgNext, imgPrevious;

    private MediaPlayer mediaPlayer;

    double totaltime = 0.0;

    double curenttime = 0.0;

    SeekBar seekbar;

    boolean seektouch;

    private Handler durationHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_player);

        tvTitle = (TextView) findViewById(R.id.mytextview);

        imgPlay = (ImageButton) findViewById(R.id.btnPaly);

        imgStop = (ImageButton) findViewById(R.id.btnPause);

        imgNext = (ImageButton) findViewById(R.id.btnNext);

        seekbar = (SeekBar) findViewById(R.id.seekBar1);

        imgPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String uri = getIntent().getStringExtra("uri");
        String title = getIntent().getStringExtra("title");

        mediaPlayer = MediaPlayer.create(this, Uri.parse(uri));
        tvTitle.setText(title);

        tvCtime = (TextView) findViewById(R.id.tvTime);

        tvLtime = (TextView) findViewById(R.id.tvLastTime);

        timer = new Timer();

        totaltime = mediaPlayer.getDuration();
        curenttime = mediaPlayer.getCurrentPosition();

        imgPlay.setVisibility(View.GONE);

        imgStop.setVisibility(View.VISIBLE);

        seekbar.setMax(mediaPlayer.getDuration());

        PlaySong();


        imgPlay.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                try {


                    mediaPlayer.reset();

                    totaltime = mediaPlayer.getDuration();


                    curenttime = mediaPlayer.getCurrentPosition();

                    seekbar.setMax(mediaPlayer.getDuration());

                    PlaySong();

                } catch (Exception ex) {


                }

            }

        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override

            public void onStopTrackingTouch(SeekBar seekBar) {

                // TODO Auto-generated method stub

                seektouch = false;

            }


            @Override

            public void onStartTrackingTouch(SeekBar seekBar) {

                // TODO Auto-generated method stub

                seektouch = true;

            }


            @Override

            public void onProgressChanged(SeekBar seekBar, int progress,

                                          boolean fromUser) {

                // TODO Auto-generated method stub

                // increasing song

                if (seektouch) {

                    curenttime = progress;

                    mediaPlayer.seekTo((int) curenttime);

                }

                if (progress == mediaPlayer.getDuration()) {
                    imgStop.setVisibility(View.GONE);
                    imgPlay.setVisibility(View.VISIBLE);
                }


            }

        });

        imgPlay.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub


                curenttime = mediaPlayer.getCurrentPosition();

                imgPlay.setVisibility(View.GONE);

                imgStop.setVisibility(View.VISIBLE);

                seekbar.setMax(mediaPlayer.getDuration());


                PlaySong();

            }

        });

        imgStop.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                imgPlay.setVisibility(View.VISIBLE);

                imgStop.setVisibility(View.GONE);

                StopSong();

            }

        });
    }

    @SuppressLint("NewApi")

    public void PlaySong() {

        // start song

        mediaPlayer.start();

        if (mediaPlayer.isPlaying()) {

            imgPlay.setVisibility(View.GONE);

            imgStop.setVisibility(View.VISIBLE);



        }


        curenttime = mediaPlayer.getCurrentPosition();

        seekbar.setProgress((int) curenttime);

        // set total time of song

        tvLtime.setText(String.format(

                "%d:%d ",

                TimeUnit.MILLISECONDS.toMinutes((long) totaltime),

                TimeUnit.MILLISECONDS.toSeconds((long) totaltime)

                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS

                        .toMinutes((long) totaltime))));

        durationHandler.postDelayed(updateSeekBarTime, 100);


    }


    public void StopSong() {

        // pause song and get time before pause

        curenttime = mediaPlayer.getCurrentPosition();

        mediaPlayer.pause();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StopSong();
    }

    private Runnable updateSeekBarTime = new Runnable() {


        @SuppressLint("NewApi")

        public void run() {


            // get current position


            curenttime = mediaPlayer.getCurrentPosition();


            // set seekbar progress


            seekbar.setProgress((int) curenttime);


            // set current timing of song


            tvCtime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes((long) curenttime), TimeUnit.MILLISECONDS.toSeconds((long) curenttime)

                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS

                    .toMinutes((long) curenttime))));

            // repeat yourself that again in 100 miliseconds


            durationHandler.postDelayed(this, 100);


        }



    };
}