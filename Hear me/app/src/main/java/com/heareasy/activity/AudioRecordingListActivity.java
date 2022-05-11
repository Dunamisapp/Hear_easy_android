package com.heareasy.activity;

import static java.lang.Integer.parseInt;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.heareasy.BuildConfig;
import com.heareasy.others.CustomPlayerActivity;
import com.heareasy.R;
import com.heareasy.others.TimeAgo;
import com.heareasy.adapter.AudioRecordingAdapter;
import com.heareasy.common_classes.MyToast;
import com.heareasy.interfaces.ClickListner;
import com.heareasy.model.AudioListModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import nl.changer.audiowife.AudioWife;


public class AudioRecordingListActivity extends AppCompatActivity implements ClickListner {
    private Toolbar toolbar_recordingList;
    private RecyclerView rv_audio_recording_list;
    private TextView tv_empty_audioList;
    private AudioRecordingAdapter audioRecordingAdapter;
    public static ArrayList<AudioListModel> audioListModelArrayList;
    // private ClickListner clickListner;
    boolean isFavourite;
    private MediaPlayer mPlayer;
    private FrameLayout mPlayerContainer;

    File[] files;
    private File directory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording_list);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        isFavourite = readState();
        init();
        toolbar_recordingList.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar_recordingList);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar_recordingList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        directory = ContextCompat.getExternalFilesDirs(this, Environment.DIRECTORY_MUSIC)[0];

         getFlieListByDate();


        AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int valuess = 13;//range(0-15)
        mgr.setStreamVolume(AudioManager.STREAM_MUSIC, valuess, 0);
    }


    // Get files from local storage
    private void getFlieListByName() {
        audioListModelArrayList = new ArrayList<>();
//        String path = this.getExternalFilesDir("/").getAbsolutePath();
//        Log.e("Files", "Path: " + path);
//        directory = new File(path);
//        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
//        directory = getApplicationContext().getExternalFilesDir("");
        if (directory.listFiles() != null) {
            files = directory.listFiles();
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            }*/

            // Sort files by Name
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });


            if (files.length >= 0) {
                Log.e("Files", "Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String name = files[i].getName();
                    Log.e("Files", "FileName:" + name);
                    //String duration = getDuration(files[i]);
                    String duration = getDuration(files[i]);

                    Log.e("TAG", "duration:>>>>>>>>> " + duration);

//                Date lastModDate = new Date(files[i].lastModified());
//                String date1 = DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastModDate);
//                    Log.e("TAG", "date:>>>>>>>>>> " + date1);

                    String date = new TimeAgo().getTimeAgo(files[i].lastModified());
                    Log.e("TAG", "date:>>>>>>>>>> " + date);

                    if (name.equalsIgnoreCase("download")) {
                        audioListModelArrayList.remove(file);
                    } else if (name.equalsIgnoreCase(".thumbnails")) {
                        audioListModelArrayList.remove(file);
                    } else if (name.contains(".mp3")) {
                        audioListModelArrayList.add(new AudioListModel(file, name, duration, date, isFavourite));
//                        Collections.reverse(audioListModelArrayList);
                        audioRecordingAdapter = new AudioRecordingAdapter(AudioRecordingListActivity.this, audioListModelArrayList, this);
                        tv_empty_audioList.setVisibility(View.GONE);
                        rv_audio_recording_list.setVisibility(View.VISIBLE);
                        rv_audio_recording_list.setAdapter(audioRecordingAdapter);
                    }

                }
            }
        }
    }

    private void getFlieListByDate() {
        audioListModelArrayList = new ArrayList<>();
//        String path = this.getExternalFilesDir("/").getAbsolutePath();
//        Log.e("Files", "Path: " + path);
//        directory = new File(path);
//        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
//        directory = getApplicationContext().getExternalFilesDir("");
        if (directory.listFiles() != null) {
            files = directory.listFiles();
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            }*/

            // Sort files by Date
            Collections.sort(Arrays.asList(files), new Comparator<File>() {
                public int compare(File o1, File o2) {
                    long lastModifiedO1 = o1.lastModified();
                    long lastModifiedO2 = o2.lastModified();

                    return (lastModifiedO2 < lastModifiedO1) ? -1 : ((lastModifiedO1 > lastModifiedO2) ? 1 : 0);
                }
            });


            if (files.length >= 0) {
                Log.e("Files", "Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String name = files[i].getName();
                    Log.e("Files", "FileNameSivani:" + name);
                    //  String duration = getDuration(files[i]);
                    String duration = getDuration(files[i]);
                    Log.e("TAG", "durationShivani:>>>>>>>>> " + duration);
//                Date lastModDate = new Date(files[i].lastModified());
//                String date1 = DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastModDate);
//                    Log.e("TAG", "date:>>>>>>>>>> " + date1);

                    String date = new TimeAgo().getTimeAgo(files[i].lastModified());
                    Log.e("TAG", "dateShivani:>>>>>>>>>> " + date);

                    if (name.equalsIgnoreCase("download")) {
                        audioListModelArrayList.remove(file);
                    } else if (name.equalsIgnoreCase(".thumbnails")) {
                        audioListModelArrayList.remove(file);
                    } else if (name.contains(".mp3")) {
                        audioListModelArrayList.add(new AudioListModel(file, name, duration, date, isFavourite));
//                        Collections.reverse(audioListModelArrayList);
                        audioRecordingAdapter = new AudioRecordingAdapter(AudioRecordingListActivity.this, audioListModelArrayList, this);
                        tv_empty_audioList.setVisibility(View.GONE);
                        rv_audio_recording_list.setVisibility(View.VISIBLE);
                        rv_audio_recording_list.setAdapter(audioRecordingAdapter);
                    }

                }
            }
        }
    }

    private void getFlieListBySize() {
        audioListModelArrayList = new ArrayList<>();
        String path = this.getExternalFilesDir("/").getAbsolutePath();
        Log.e("Files", "Path: " + path);
//        directory = new File(path);
//        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
//        directory = getApplicationContext().getExternalFilesDir("");
        if (directory.listFiles() != null) {
            files = directory.listFiles();
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            }*/

            // Sort files by Size
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
//                    return Long.compare(f1.length(), f2.length());
                    // For descending
                    return -Long.compare(f1.length(), f2.length());
                }
            });


            if (files.length >= 0) {
                Log.e("Files", "Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String name = files[i].getName();
                    Log.e("Files", "FileName:" + name);
                    String duration = getDuration(files[i]);
                    Log.e("TAG", "duration:>>>>>>>>> " + duration);
//                Date lastModDate = new Date(files[i].lastModified());
//                String date1 = DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastModDate);
//                    Log.e("TAG", "date:>>>>>>>>>> " + date1);

                    String date = new TimeAgo().getTimeAgo(files[i].lastModified());
                    Log.e("TAG", "date:>>>>>>>>>> " + date);

                    if (name.equalsIgnoreCase("download")) {
                        audioListModelArrayList.remove(file);
                    } else if (name.equalsIgnoreCase(".thumbnails")) {
                        audioListModelArrayList.remove(file);
                    } else if (name.contains(".mp3")) {
                        audioListModelArrayList.add(new AudioListModel(file, name, duration, date, isFavourite));
//                        Collections.reverse(audioListModelArrayList);
                        audioRecordingAdapter = new AudioRecordingAdapter(AudioRecordingListActivity.this, audioListModelArrayList, this);
                        tv_empty_audioList.setVisibility(View.GONE);
                        rv_audio_recording_list.setVisibility(View.VISIBLE);
                        rv_audio_recording_list.setAdapter(audioRecordingAdapter);
                    }

                }
            }
        }
    }

    private void init() {
        toolbar_recordingList = findViewById(R.id.toolbar_recordingList);
        rv_audio_recording_list = findViewById(R.id.rv_audio_recording_list);
        tv_empty_audioList = findViewById(R.id.tv_empty_audioList);

    }

    public static String getDuration(File file) {
        int millSecond = 0;

        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
            String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            millSecond = Integer.parseInt(durationStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millSecond),
                TimeUnit.MILLISECONDS.toMinutes(millSecond) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millSecond)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millSecond) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millSecond)));

        return hms;

    }

    @Override
    public void onItemClick(int position) {
        AudioListModel item = audioListModelArrayList.get(position);
    /*    Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(item.getFile()), "audio/*");
        startActivity(intent);*/



        startActivity(new Intent(AudioRecordingListActivity.this, CustomPlayerActivity.class)
                .putExtra("uri", item.getFile() + "")
                .putExtra("title", item.getAudioName()));

        // mPlayerContainer = Parent view to add default player UI to.
        /*mPlayerContainer = findViewById(R.id.container);
        mPlayerContainer.setVisibility(View.VISIBLE);
        AudioWife.getInstance().init(AudioRecordingListActivity.this, Uri.fromFile(item.getFile()))
                .useDefaultUi(mPlayerContainer, getLayoutInflater());
        AudioWife.getInstance().play();*/

      /*  Uri myUri1 = Uri.fromFile(item.getFile());
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(getApplicationContext(), myUri1);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        }
        mPlayer.start();*/


    }

    @Override
    protected void onResume() {
        super.onResume();

        AudioWife.getInstance().addOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getBaseContext(), "Completed", Toast.LENGTH_SHORT)
                        .show();
                // do you stuff
                AudioWife.getInstance().release();
                mPlayerContainer.setVisibility(View.GONE);
            }
        });

        AudioWife.getInstance().addOnPlayClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Play", Toast.LENGTH_SHORT)
                        .show();
                // Lights-Camera-Action. Lets dance.
                AudioWife.getInstance().play();
            }
        });

        AudioWife.getInstance().addOnPauseClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Pause", Toast.LENGTH_SHORT)
                        .show();
                // Your on audio pause stuff.
                AudioWife.getInstance().pause();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioWife.getInstance().release();
    }

    @Override
    public void shareItem(int position) {
        AudioListModel item = audioListModelArrayList.get(position);
        if (item.getFile() != null) {
              //shareFile(item.getFile().getAbsolutePath());
            shareFile(item.getFile());
           // shareAudioFile(item.getFile());
        }
    }

    private void shareAudioFile(File file) {

        String authorities = BuildConfig.APPLICATION_ID + ".fileprovider";
        Uri path = FileProvider.getUriForFile(this, authorities, file);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, path);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("audio/mp3");//Replace with audio/* to choose other extensions
        startActivity(Intent.createChooser(intent, "Share Audio"));

    }

    private void shareFile(File file) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType("audio/mp3");
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://" + file.getAbsolutePath()));
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivity(Intent.createChooser(intentShareFile, "Share File"));

    }

    public void shareFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            MediaScannerConnection.scanFile(this, new String[]{filePath}, null,
                    (path, uri) -> {
                        Log.i("", "onScanCompleted: path :- " + path + " uri :- " + uri);
                        if (uri != null) {
                            try {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND); // create action send
                                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                // create mime type of file from file uri
                                String mimeType;
                                if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                                    ContentResolver cr = getContentResolver();
                                    mimeType = cr.getType(uri);
                                } else {
                                    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                                            .toString());
                                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                            fileExtension.toLowerCase());
                                }

                                // shareIntent.setPackage(""); // you can specify app package name here to share file that app only
                                shareIntent.setDataAndType(uri, mimeType);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                startActivity(Intent.createChooser(shareIntent, "Share File"));
                            } catch (Throwable throwable) {
                                Log.i("", "shareSong: error :- " + throwable.getMessage());
                            }
                        }
                    });
        } else {
            Log.i("", "shareSong: currentMusic NULL");
        }
    }

    @Override
    public void deleteItem(int position) {
        AudioListModel item = audioListModelArrayList.get(position);
        if (item.getFile() != null) {
            item.getFile().delete();
            audioListModelArrayList.remove(position);
            audioRecordingAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void renameItem(int position) {
        AlertDialog.Builder fileDialog = new AlertDialog.Builder(AudioRecordingListActivity.this);
        fileDialog.setTitle("Rename file");

        final EditText input = new EditText(AudioRecordingListActivity.this);
        AudioListModel item = audioListModelArrayList.get(position);
        AudioListModel model = new AudioListModel();
        input.setText(item.getAudioName());
        fileDialog.setView(input);
        fileDialog.setCancelable(false);
        fileDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String fileReName = input.getText().toString();
                        if (fileReName.contains(".mp3")) {
                            model.setAudioName(fileReName);
                            item.getFile().renameTo(new File(directory, fileReName));
                            audioListModelArrayList.set(position, model);
                            audioRecordingAdapter.notifyItemChanged(position);
                            getFlieListByName();
                            MyToast.display(AudioRecordingListActivity.this, "File rename successfully");
                            finish();
                            startActivity(getIntent());
                        } else {
                            MyToast.display(AudioRecordingListActivity.this, "File extension should be .mp3");
                        }
                    }
                });

        fileDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
        fileDialog.create();
        fileDialog.show();
    }

    @Override
    public void onFavouriteButtonClick(View view, int position) {
        ImageButton imageView = view.findViewById(R.id.iBtn_favourite);
        if (isFavourite) {
            imageView.setImageResource(R.drawable.ic_favorite);
            isFavourite = false;
            saveState(isFavourite);

        } else {
            imageView.setImageResource(R.drawable.ic_favorite_24);
            isFavourite = true;
            saveState(isFavourite);

        }
    }

    private void saveState(boolean isFavourite) {
        SharedPreferences aSharedPreferences = AudioRecordingListActivity.this.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        SharedPreferences.Editor aSharedPreferencesEdit = aSharedPreferences
                .edit();
        aSharedPreferencesEdit.putBoolean("State", isFavourite);
        aSharedPreferencesEdit.commit();
    }

    private boolean readState() {
        SharedPreferences aSharedPreferences = AudioRecordingListActivity.this.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        return aSharedPreferences.getBoolean("State", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_tab_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_name:
                getFlieListByName();
                return true;

            case R.id.menu_date:
                getFlieListByDate();
                return true;

            case R.id.menu_size:

                getFlieListBySize();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }


    }

}