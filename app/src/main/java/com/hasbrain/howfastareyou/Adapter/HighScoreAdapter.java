package com.hasbrain.howfastareyou.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hasbrain.howfastareyou.R;
import com.hasbrain.howfastareyou.utils.HighScore;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Khang on 05/12/2015.
 */
public class HighScoreAdapter extends ArrayAdapter<HighScore.Record> {
    private static final String TAG = "HighScoreAdapter";
    private final SimpleDateFormat dateformat;

    private HighScore mHighScore;

    public HighScoreAdapter(Context context, HighScore highScore) {
        super(context, 0);
        this.mHighScore = highScore;
        dateformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    }

    @Override
    public int getCount() {
        return mHighScore.getCount() == 0 ? 0 : mHighScore.getCount() + 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getCount() - 1 ?
                0 : 1;
    }

    @Override
    public HighScore.Record getItem(int position) {
        return mHighScore.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean isResetButton = position == getCount() - 1;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        //Reset Button
        if (isResetButton) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.reset_button, parent, false);
                convertView.findViewById(R.id.b_reset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(inflater.getContext())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.clear)
                                .setMessage(R.string.resethighscore)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mHighScore.clearAll();
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                });
            }
            return convertView;
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_view_item,
                    parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        //bind data
        HighScore.Record data = getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.tvScore.setText(data.score + "/" + data.time_count);
        holder.tvTime.setText(dateformat.format(new Date(data.time_end)));
        return convertView;
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'list_view_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.tv_time)
        AppCompatTextView tvTime;
        @Bind(R.id.tv_score)
        AppCompatTextView tvScore;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
