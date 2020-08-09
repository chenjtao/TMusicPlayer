package com.tao.tmusicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tao.tmusicplayer.R;
import com.tao.tmusicplayer.database.entity.Music;

import java.util.List;

public class MusicAdapter extends BaseAdapter {

    private Context context;
    private List<Music> musicList;
    private onItemDeleteListener itemDeleteListener;

    public MusicAdapter(Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int i) {
        return musicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Music music = musicList.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_list_music, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_xh = view.findViewById(R.id.tv_item_music_xh);
            viewHolder.tv_name = view.findViewById(R.id.tv_item_music_name);
            viewHolder.tv_info = view.findViewById(R.id.tv_item_music_info);
            viewHolder.iv_clear = view.findViewById(R.id.iv_item_music_clear);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_xh.setText(String.valueOf(i + 1));
        viewHolder.tv_name.setText(music.getTitle());
        viewHolder.tv_info.setText(String.format("%s-%s", music.getArtist(), music.getAlbum()));
        viewHolder.iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemDeleteListener.onDeleteClick(i);
            }
        });
        return view;
    }


    /**
     * 删除按钮的监听接口
     */
    public interface onItemDeleteListener {
        void onDeleteClick(int i);
    }

    public void setOnItemDeleteClickListener(onItemDeleteListener itemDeleteListener) {
        this.itemDeleteListener = itemDeleteListener;
    }

    static class ViewHolder {
        TextView tv_xh;
        TextView tv_name;
        TextView tv_info;
        ImageView iv_clear;
    }
}
