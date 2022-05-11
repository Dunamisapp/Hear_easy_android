package com.heareasy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heareasy.others.CustomPopUpMenu;
import com.heareasy.R;
import com.heareasy.interfaces.ClickListner;
import com.heareasy.model.AudioListModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AudioRecordingAdapter extends RecyclerView.Adapter<AudioRecordingAdapter.AudioRecordingViewHolder> {
    Context context;
    ArrayList<AudioListModel> audioListModelArrayList;
    ClickListner clickListner;

  /*  public AudioRecordingAdapter(Context context, ClickListner clickListner) {
        this.context = context;
        this.clickListner = clickListner;
    }*/

    public AudioRecordingAdapter(Context context, ArrayList<AudioListModel> audioListModelArrayList, ClickListner clickListner) {
        this.context = context;
        this.audioListModelArrayList = audioListModelArrayList;
        this.clickListner = clickListner;
    }

    @NonNull
    @Override
    public AudioRecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_recording_list_item, parent, false);
        return new AudioRecordingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AudioRecordingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AudioListModel item = audioListModelArrayList.get(position);
        File file = item.getFile();
        holder.audioName.setText(item.getAudioName());
        Log.e("Shivi1",">>>>>>"+item.getAudioName());
        holder.audioDate.setText(item.getAudio_date());
        Log.e("Shivi2",">>>>>>"+item.getAudio_date());

        holder.audioDuration.setText(item.getAudio_duration());
        Log.e("Shivi3",">>>>>>"+item.getAudio_duration());

        holder.iBtn_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             clickListner.onFavouriteButtonClick(holder.itemView,position);
            }
        });
        holder.llayoutAudioName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clickListner.onItemClick(position);
            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomPopUpMenu(context, holder.menu);
                CustomPopUpMenu.powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int menuposition, PowerMenuItem item) {
                        switch (menuposition) {
                            case 0:
                                clickListner.renameItem(position);
                                break;
                            case 1:
                                clickListner.deleteItem(position);
                                break;
                                case 2:
                                clickListner.shareItem(position);
                                break;
                            default:
                                break;
                        }
                        CustomPopUpMenu.powerMenu.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return audioListModelArrayList.size();
    }



    public class AudioRecordingViewHolder extends RecyclerView.ViewHolder {
        TextView audioName, audioDate, audioDuration, menu;
        CircleImageView iv_play;
        ImageButton iBtn_favourite;
        LinearLayout llayoutAudioName;

        public AudioRecordingViewHolder(@NonNull View itemView) {
            super(itemView);
            audioName = itemView.findViewById(R.id.tv_audioName);
            audioDate = itemView.findViewById(R.id.tv_audioDate);
            audioDuration = itemView.findViewById(R.id.tv_audioDuration);
            menu = itemView.findViewById(R.id.tv_menu_audio_recording_list);
            iv_play = itemView.findViewById(R.id.iv_play);
            iBtn_favourite = itemView.findViewById(R.id.iBtn_favourite);
            llayoutAudioName = itemView.findViewById(R.id.llayoutAudioName);
        }
    }
}
