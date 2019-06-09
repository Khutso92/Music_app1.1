package com.example.khutsomatlala.music_app11;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static com.example.khutsomatlala.music_app11.R.id.play_pause;


public class MainActivity extends Activity {

    int stop_again = 0, next_position,arraySize;
    TextView song_name;
    ViewGroup start_seekBar_end, forward_section;

    //Responsible for displaying the songs on the screen
    ListView listView;

    //Responsible storing the songs
    List<String> list;

    //Responsible for setting the list of songs
    ListAdapter adapter;

    //Responsible for media
    MediaPlayer mediaPlayer;

    //Responsible for testing the pause/ play songs
    boolean playTester = true, after_stopped = false;
    View v;

    private Button btn_play_pause,btn_back;


    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();

    private ProgressBar seekbar;
    private TextView fwind, bwind;

    public static int oneTimeOnly = 0;


    //Handles audio focus when playing a sound file
    private AudioManager mAudioManager;

    //handles the Audio Focus happening within the phone
    AudioManager.OnAudioFocusChangeListener mOnAudiFocusChangeListerner =
            new AudioManager.OnAudioFocusChangeListener() {

                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || //means if we lost the audio for a short time
                            focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) { //means the app is allowed to continue playing sound but lower volume

                        // Pause playback and reset player to start of the file.That way,
                        //we can play  the word from the beginning when we resume playback
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);//start from the beginning after being pause
                        // Is better for the user to hear from the beginning instead
                        // of hearing the last reminding word

                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) { //means we have regained focus and an resume playback
                        mediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) { //means we have lost audio focus and

                    }
                }
            };


    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(MainActivity.this, "done playing song", Toast.LENGTH_SHORT).show();

            mediaPlayer.start();
            mediaPlayer.pause();
            btn_play_pause.setText("Play");
            playTester = true;

            startTime = 0;
            finalTime = 0;

            song_name.setVisibility(View.GONE);
            start_seekBar_end.setVisibility(View.GONE);
            forward_section.setVisibility(View.GONE);

            song_name.setVisibility(View.VISIBLE);
            song_name.setText("Tab a song to play ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Create and setup the audioManager to request audio focus
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        song_name = findViewById(R.id.song_name);
        listView = findViewById(R.id.listView);
        start_seekBar_end = findViewById(R.id.start_seekBar_end);
        forward_section = findViewById(R.id.forward_section);

        btn_play_pause = findViewById(play_pause);

        //fast forward
        fwind = findViewById(R.id.bwind);

        //backward (rewinfing the song)
        bwind = findViewById(R.id.fwind);

        //Refercening the Seeker Bar
        seekbar = findViewById(R.id.seekBar);

        //Disabling the seekBar  and the pause button
        seekbar.setClickable(true);
        //  b2.setEnabled(false);

        start_seekBar_end.setVisibility(View.GONE);
        forward_section.setVisibility(View.GONE);
        btn_back =findViewById(R.id.btn_back);

        //  song_name.setVisibility(View.VISIBLE);
        song_name.setText("Tab a song to play ");

        list = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();

        for (int i = 0; i < fields.length; i++) {
            list.add(fields[i].getName());


        }

        //removing the first two elements,the $change item
        list.remove(0);
        list.remove(10);
         arraySize = fields.length;

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                btn_back.setEnabled(true);
                //Request audio focus for playback
                /***
                 * requestAudioFocus has 3 inputs
                 * 1.Listener
                 * 2.Music stream Type
                 * 3.How long will I request the  (AUDIOFOCUS_GAIN - for songs)
                 */

                int result = mAudioManager.requestAudioFocus(mOnAudiFocusChangeListerner,
                        //use the music stream
                        AudioManager.STREAM_MUSIC,
                        mAudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                //eliminate play many songs
                releaseMediaPlayer();

                int resID = getResources().getIdentifier(list.get(position), "raw", getPackageName());

                next_position = position;

                // mediaPlayer = MediaPlayer.create(MainActivity.this, resID[position]);

                mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                song_name.setText(song_name(position));
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    //Play the next song
                    playTester = false;

                    if (mediaPlayer.getCurrentPosition() == 0) {
                        playPause(v);
                    } else {
                        Toast.makeText(MainActivity.this, "Song already playing", Toast.LENGTH_SHORT).show();
                    }

                }
                song_name.setVisibility(View.VISIBLE);
                start_seekBar_end.setVisibility(View.VISIBLE);
                forward_section.setVisibility(View.VISIBLE);
            }
        });

    }

    //return the song name and artist according to the position stored in the array
    @SuppressLint("NewApi")
    public String song_name(int position) {

        switch (position) {

            case 0:
                listView.setBackground(getDrawable(R.drawable.changes_tupac));
                return " Changes.mp3 - Tupac";

            case 1:

            listView.setBackground(getDrawable(R.drawable.aston_martin_rick_ross));
            return " Aston Martin.mp3 - Rick Ross";
            case 2:
                listView.setBackground(getDrawable(R.drawable.changes_tupac));
                return " Dear mama.mp3 - Tupac";

            case 3:
                listView.setBackground(getDrawable(R.drawable.destiny_cassper));
                return " Destiny.mp3 - Cassper  Nyovest ft. Goapele";

            case 4:
                listView.setBackground(getDrawable(R.drawable.hey_mama_kanye_west));
                return " Hey mama.mp3 - Kanye West";

            case 5:
                listView.setBackground(getDrawable(R.drawable.house_music));
                return " Masingati.mp3 - unknown artist ";

            case 6:
                listView.setBackground(getDrawable(R.drawable.understood_common));
                return " Understood.mp3 - Common ";

            case 7:
                listView.setBackground(getDrawable(R.drawable.hugh));
                return " Nomali.mp3 - Bo Mapefane";

            case 8:
                listView.setBackground(getDrawable(R.drawable.nothing_last4ever_nas));
                return " Nothing Last4ever.mp3 - Nas ";

            case 9:
                listView.setBackground(getDrawable(R.drawable.president_carter_weezy));
                return "President Carter.mp3 - Weezy ";

            case 10:
                listView.setBackground(getDrawable(R.drawable.take_care_drake_rihanna));
                return "Take Care.mp3 - Drake ft. Rihanna";

            case 11:
                listView.setBackground(getDrawable(R.drawable.we_not_afraid_big));
                return "We Are Not Afraid.mp3 - B.I.G";


            case 12:
                listView.setBackground(getDrawable(R.drawable.when_am_gone_eminem));
                return "When am_gone.mp3 - Eminem";

            case 13:
                listView.setBackground(getDrawable(R.drawable.trim_lamar));
                return "Growing Apart.mp3 -  Kendrick Lamar";

        }
        return "";
    }

    //A Theard for a delay with one second
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            fwind.setText(String.format("%d:%d ",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );

            myHandler.postDelayed(this, 100);
            if (startTime < finalTime) {
                seekbar.setProgress((int) startTime);
            }
        }
    };

    //for playing and pausing the song
    public void playPause(View view) {

        if (!playTester) {

            playTester = true;
            btn_play_pause.setText("||");

            //When the song is playing it should this message
            Toast.makeText(getApplicationContext(), "Playing ", Toast.LENGTH_SHORT).show();


            mediaPlayer.start();

            finalTime = mediaPlayer.getDuration();  //getting total song time
            startTime = mediaPlayer.getCurrentPosition(); //getting current position of the song

            //Validating the seekbar to be only to be updated per song
            if (oneTimeOnly == 0) {
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }


            bwind.setText(String.format("%d:%d ",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    finalTime)))
            );

            fwind.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    startTime))));

            //Updating the current progress on the Seekbar with a delay of one second
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(UpdateSongTime, 100);

            //Responsible for letting user know the song is done playing
            mediaPlayer.setOnCompletionListener(mCompletionListener);
            stop_again = 0;

        } else {

            stop_again = 0;
            if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
                btn_play_pause.setText(">");

                playTester = false;
                Toast.makeText(this, "Pausing ", Toast.LENGTH_SHORT).show();
            }
        }


        if (after_stopped) {
            mediaPlayer.start();
            after_stopped = false;
            btn_play_pause.setText("||");
        }

    }

    //To stop the song
    public void stop(View view) {


        after_stopped = true;

        if (stop_again == 1) {
            stop_again = 0;
            Toast.makeText(this, "song already stopped", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "song stopped", Toast.LENGTH_SHORT).show();
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            btn_play_pause.setText(">");
        }

        stop_again = 1;
    }

/*
    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playTester = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_play_pause.setText(">");
    }*/


    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public void Fliper(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }


    public void next(View view) {

        btn_back.setEnabled(true);

        if ( next_position <13) {
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            int resID = getResources().getIdentifier(list.get(++next_position), "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(MainActivity.this, resID);

            playTester = false;
            playPause(view);
            song_name.setText(song_name(next_position));
        } else {

            Toast.makeText(this, "Last song on the playlist", Toast.LENGTH_SHORT).show();
            btn_back.setEnabled(false);
        }



    }

    public void back(View view) {

        if (next_position >0  ) {
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            int resID = getResources().getIdentifier(list.get(--next_position), "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(MainActivity.this, resID);

            playTester = false;
            playPause(view);
            song_name.setText(song_name(next_position));
        }
        else {

            Toast.makeText(this, "first song on the playlist", Toast.LENGTH_SHORT).show();
            btn_back.setEnabled(false);
        }



    }





}











