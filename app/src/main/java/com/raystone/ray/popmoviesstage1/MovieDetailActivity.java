package com.raystone.ray.popmoviesstage1;

import android.app.Fragment;

/**
 * Created by Ray on 2/1/2016.
 */
public class MovieDetailActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {return MovieDetailFragment.newInstance();}
}
