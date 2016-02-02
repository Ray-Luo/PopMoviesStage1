package com.raystone.ray.popmoviesstage1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {return MainFragment.newInstance();}

}
