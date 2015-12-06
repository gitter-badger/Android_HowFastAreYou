package com.hasbrain.howfastareyou.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hasbrain.howfastareyou.adapter.HighScoreAdapter;
import com.hasbrain.howfastareyou.R;
import com.hasbrain.howfastareyou.utils.HighScore;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 10/14/15.
 */
public class TapCountResultFragment extends Fragment {
    ListView mListView;
    HighScore mHighScore;
    HighScoreAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mListView = (ListView) inflater.inflate(R.layout.high_score_list_view, null);
        return mListView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mHighScore = HighScore.fromSharedPreferences();
        //OR
        mHighScore = HighScore.fromDatabse();

        adapter = new HighScoreAdapter(getContext(), mHighScore);
        mListView.setAdapter(adapter);
    }

    public void addRecord(int score, long time_end, int time_count) {
        mHighScore.addRecord(score, time_end, time_count);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHighScore.saveToDatabase();
        mHighScore.saveToSharedPreferences();
    }
}
