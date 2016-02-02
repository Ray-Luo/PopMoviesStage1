package com.raystone.ray.popmoviesstage1;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

/**
 * Created by Ray on 2/1/2016.
 */
public class MovieDetailFragment extends Fragment{

    private View mRootView;
    private ImageView mPoster;
    private TextView mReleaseDate;
    private TextView mRateAverage;
    private TextView mDescription;
    private TextView mTitle;

    public static MovieDetailFragment newInstance() {
         Bundle args = new Bundle();
         MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        String[] detail = getActivity().getIntent().getStringArrayExtra("MovieDetail");
        final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
        String posterURL = BASE_POSTER_URL + detail[0];
        String releaseDate = detail[1];
        String rateAverage = detail[2];
        String description = detail[3];
        String title = detail[4];

        mRootView = inflater.inflate(R.layout.movie_detail,container,false);
        mPoster = (ImageView)mRootView.findViewById(R.id.detail_poster);
        Picasso.with(getActivity().getApplicationContext()).load(posterURL).into(mPoster);

        mReleaseDate = (TextView)mRootView.findViewById(R.id.release_date);
        mReleaseDate.setText(releaseDate);

        mRateAverage = (TextView)mRootView.findViewById(R.id.detail_rate);
        mRateAverage.setText(rateAverage+"/10");

        mDescription = (TextView)mRootView.findViewById(R.id.detail_description);
        mDescription.setText(description);

        mTitle = (TextView)mRootView.findViewById(R.id.detail_title);
        mTitle.setText(title);
        return mRootView;
    }
}
