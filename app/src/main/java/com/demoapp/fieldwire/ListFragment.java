package com.demoapp.fieldwire;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demoapp.fieldwire.Model.Image;
import com.demoapp.fieldwire.Model.Result;
import com.demoapp.fieldwire.Model.SearchResult;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListFragment extends Fragment {
    private ProgressBar progressBar;
    private TextView errorMsg;
    private RecyclerView recyclerView;
    private List<Result> resultList;
    private static final String BASE_URL = "https://api.imgur.com";
    private String query;
    private List<Photo> photos = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString("params");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = getActivity().findViewById(R.id.progress_bar);
        errorMsg = getActivity().findViewById(R.id.error_msg);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        if(isNetworkAvailable()) {
            loadJSON();
            errorMsg.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            errorMsg.setVisibility(View.VISIBLE);
            errorMsg.setText(getResources().getString(R.string.error_text));
        }
    }

    private void loadJSON() {
        new JSONAsyncTask().execute();
    }

    private boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = null;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    private void parseJSON() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Client-ID e1167a9b3912ed2");

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
        retrofit2.Call<SearchResult> call = api.getSearchResult(query);
        call.enqueue(new retrofit2.Callback<SearchResult>() {

            @Override
            public void onResponse(@NonNull retrofit2.Call<SearchResult> call,
                                   @NonNull retrofit2.Response<SearchResult> response) {
                if (response.isSuccessful()) {
                    SearchResult searchResult = response.body();
                    resultList = searchResult != null ? searchResult.getData() : null;
                    for(int i=0;i<resultList.size();i++) {
                        if(resultList.get(i).getIsAlbum()) {
                            List<Image> imgList = resultList.get(i).getImages();
                            for(int j=0;j<imgList.size();j++) {
                                Photo photo = new Photo();
                                photo.id = imgList.get(j).getId();
                                if(imgList.get(j).getTitle() != null) {
                                    photo.title = imgList.get(j).getTitle().toString();
                                } else if(imgList.get(j).getDescription() != null) {
                                    photo.title = imgList.get(j).getDescription().toString();
                                }
                                photo.url = imgList.get(j).getLink();
                                photos.add(photo);
                            }
                        } else {
                            Photo photo = new Photo();
                            photo.id = resultList.get(i).getId();
                            if(resultList.get(i).getTitle() != null) {
                                photo.title = resultList.get(i).getTitle();
                            } else if(resultList.get(i).getDescription() != null) {
                                photo.title = resultList.get(i).getDescription().toString();
                            }
                            photo.url = resultList.get(i).getLink();
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

    private void updateDataAdapter() {
        DataAdapter adapter = new DataAdapter(photos, getActivity().getApplicationContext(), (v, position) -> {
            Intent intent = new Intent(getActivity(), FullscreenActivity.class);
            intent.putExtra("url", photos.get(position).url);
            getActivity().startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }
}
