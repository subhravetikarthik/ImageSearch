package com.demoapp.fieldwire;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demoapp.fieldwire.Model.Image;
import com.demoapp.fieldwire.Model.Result;
import com.demoapp.fieldwire.Model.SearchResult;
import com.demoapp.fieldwire.Model.Tag;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CustomItemClickListener{

    ProgressBar progressBar;
    List<SearchItem> searchList = new ArrayList<>();
    DataAdapter dataAdapter;
    RecyclerView mRecyclerView;
    private static final String BASE_URL = "https://api.imgur.com";
    TextView errorMsg;
    SearchView searchView;
    List<Photo> photos;
    Call<SearchResult> call;
    List<Tag> tagList;
    String queryString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        if(isNetworkAvailable()) {
            loadJSON();
            errorMsg.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            errorMsg.setVisibility(View.VISIBLE);
            errorMsg.setText(getResources().getString(R.string.error_text));
        }

        if (searchView != null) {
            searchView.setVersionMargins(SearchView.VersionMargins.TOOLBAR_SMALL);
            searchView.setHint(getResources().getString(R.string.search_keyword));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchList.add(new SearchItem(query));
                    searchView.close(false);
                    queryString = query;
                    new JSONAsyncTask().execute();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            SearchAdapter searchAdapter = new SearchAdapter(this, searchList);
            searchView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        }
    }

    private void initViews(){
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        errorMsg = findViewById(R.id.error_msg);
        progressBar = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_view);
    }

    private boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = null;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadJSON() {
        new JSONAsyncTask().execute();
    }

    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
        intent.putExtra("url", photos.get(position).url);
        startActivity(intent);
    }

    private class JSONAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            parseJSON();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }

    /* Method to parse JSON.
     **/
    private void parseJSON() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", Constants.IMGUR_CLIENT_ID);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        OkHttpClient client = httpClient.build();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        ApiInterface api = retrofit.create(ApiInterface.class);

        if(queryString == null) {
            call = api.getViralResults(); // To load viral images
        } else {
            call = api.getSearchResult(queryString);
        }
        call.enqueue(new retrofit2.Callback<SearchResult>() {

            @Override
            public void onResponse(@NonNull retrofit2.Call<SearchResult> call,
                                   @NonNull retrofit2.Response<SearchResult> response) {
                if (response.isSuccessful()) {
                    photos = new ArrayList<>();
                    SearchResult searchResult = response.body();
                    List<Result> resultList = searchResult != null ? searchResult.getData() : null;
                    for(int i=0;i<resultList.size();i++) {
                        if(resultList.get(i).getIsAlbum()) {
                            List<Image> imgList = resultList.get(i).getImages();
                            tagList = resultList.get(i).getTags();
                            for(int j=0;j<imgList.size();j++) {
                                Photo photo = new Photo();
                                photo.id = imgList.get(j).getId();
                                if(imgList.get(j).getTitle() != null) {
                                    photo.title = imgList.get(j).getTitle().toString();
                                } else if(imgList.get(j).getDescription() != null) {
                                    photo.title = imgList.get(j).getDescription().toString();
                                }
                                photo.url = imgList.get(j).getLink();
                                photo.tag = tagList;
                                photos.add(photo);
                            }
                        } else {
                            Photo photo = new Photo();
                            tagList = resultList.get(i).getTags();
                            photo.id = resultList.get(i).getId();
                            if(resultList.get(i).getTitle() != null) {
                                photo.title = resultList.get(i).getTitle();
                            } else if(resultList.get(i).getDescription() != null) {
                                photo.title = resultList.get(i).getDescription().toString();
                            }
                            photo.url = resultList.get(i).getLink();
                            photo.tag = tagList;
                            photos.add(photo);
                        }
                    }
                    updateDataAdapter();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<SearchResult> call,@NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    /* Method to update the data adapter with new photos
    **/
    private void updateDataAdapter() {
        dataAdapter = new DataAdapter(photos,this);
        dataAdapter.setClickListener(this);
        mRecyclerView.setAdapter(dataAdapter);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }
}
