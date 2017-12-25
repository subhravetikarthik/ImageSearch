package com.demoapp.fieldwire;

import com.demoapp.fieldwire.Model.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    /**
     * This call returns JSON response
     * input: q (search query)
     *        sort - time | viral | top - defaults to time (optional)
     *        window - Change the date range of the request if the sort is 'top',
     *                 day | week | month | year | all, defaults to all (optional)
     *        page - integer - the data paging number (optional)
     */
    @GET("/3/gallery/search/time/all?q=santa AND snow")
    Call<SearchResult> getSearchResult();
}
