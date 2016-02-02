package com.raystone.ray.popmoviesstage1;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 1/31/2016.
 */
public class MainFragment extends Fragment {

    private View mRootView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Movie> mAllMovies = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManger;
    private final String LOG_TAG = NetworkTask.class.getSimpleName();

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateMovies();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.settings)
        {
            Intent intent = new Intent(getActivity(),SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        new NetworkTask().execute("popularity.desc");

        mRootView = inflater.inflate(R.layout.main_fragment,container,false);

        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.recycler_view);

        mLayoutManger = new GridLayoutManager(getActivity().getApplicationContext(),3);
        mRecyclerView.setLayoutManager(mLayoutManger);

        return mRootView;
    }

    public void updateMovies()
    {
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("sort","");
        if (!sortBy.isEmpty())
            new NetworkTask().execute(sortBy);
    }

    public class MyMovieAdapter extends RecyclerView.Adapter<MyMovieAdapter.ViewHolder>
    {
        private List<Movie> allMovies;
        public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener
        {
            public ImageView mImageView;

            public ViewHolder(ImageView itemView)
            {
                super(itemView);
                mImageView = itemView;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view)
            {
                int itemPosition = mRecyclerView.getChildAdapterPosition(view);
                String[] detail = {mAllMovies.get(itemPosition).moviePoster,mAllMovies.get(itemPosition).releaseDate,mAllMovies.get(itemPosition).voteAverage,mAllMovies.get(itemPosition).plotSynopsis,mAllMovies.get(itemPosition).title};
                Intent intent = new Intent(getActivity(),MovieDetailActivity.class);
                intent.putExtra("MovieDetail",detail);
                startActivity(intent);
            }

        }

        public MyMovieAdapter(List<Movie> allMovies)
        {this.allMovies = allMovies;}

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_single_item,parent,false);
            ViewHolder holder = new ViewHolder((ImageView) v);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
            String url = BASE_POSTER_URL + allMovies.get(position).moviePoster;
            Picasso.with(getActivity().getApplicationContext()).load(url).resize(180,270).into(holder.mImageView);
        }

        @Override
        public int getItemCount()
        {
            return allMovies.size();
        }
    }



    private class NetworkTask extends AsyncTask<String,Void,List<Movie>>
    {


        @Override
        protected List<Movie> doInBackground(String... params)
        {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            String appId = BuildConfig.MOVIE_API_KEY;

            try
            {
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String APIId = "api_key";

                Uri buildUrl = Uri.parse(MOVIE_BASE_URL).buildUpon().appendQueryParameter(QUERY_PARAM,params[0]).appendQueryParameter(APIId,appId).build();
                URL url = new URL(buildUrl.toString());
                Log.v(LOG_TAG,"Built URL" + url);

                //  Create the request to MovieApi, and open connection
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //  Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null)
                    return null;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null)
                {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0)
                    return null;
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG,"Movie string" + movieJsonStr);
            }catch(IOException e)
            {
                Log.e(LOG_TAG,"Error",e);
                return null;
            }finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
                if(reader != null)
                {
                    try
                    {reader.close();}catch (final IOException e)
                    {Log.e(LOG_TAG,"Error closing stream",e);}
                }
            }

            try
            {
                mAllMovies = getMovieDataFromJson(movieJsonStr);
                return getMovieDataFromJson(movieJsonStr);
            }
            catch (JSONException e)
            {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result)
        {
            if(result != null)
            {
                mAdapter = new MyMovieAdapter(mAllMovies);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    private List<Movie> getMovieDataFromJson(String movieJsonStr)
    throws JSONException{

        //  Names of JSON objects need to be extracted
        final String RESULT = "results";
        final String TITLE = "original_title";
        final String RELEASE = "release_date";
        final String POSTER = "poster_path";
        final String VOTE = "vote_average";
        final String DESCRIPTION = "overview";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULT);

        List<Movie> movie= new ArrayList<>();
        for (int i = 0; i < movieArray.length(); i ++)
        {
            //  Info needs to be extracted
            String title;
            String releaseDate;
            String moviePoster;
            String voteAverage;
            String plotSynopsis;

            //  get info from JSON
            JSONObject movieItem = movieArray.getJSONObject(i);
            title = movieItem.getString(TITLE);
            releaseDate = movieItem.getString(RELEASE);
            moviePoster = movieItem.getString(POSTER);
            voteAverage = movieItem.getString(VOTE);
            plotSynopsis = movieItem.getString(DESCRIPTION);

            movie.add(new Movie(title,releaseDate,moviePoster,voteAverage,plotSynopsis));
        }
        Log.v(LOG_TAG,"Movie Sample" + movie.get(0));
        return movie;
    }

    public class Movie
    {
        String title;
        String releaseDate;
        String moviePoster;
        String voteAverage;
        String plotSynopsis;

        public Movie(String title,String releaseDate,String moviePoster,String voteAverage,String plotSynopsis)
        {
            this.title = title;
            this.releaseDate = releaseDate;
            this.moviePoster = moviePoster;
            this.voteAverage = voteAverage;
            this.plotSynopsis = plotSynopsis;
        }
    }
}
