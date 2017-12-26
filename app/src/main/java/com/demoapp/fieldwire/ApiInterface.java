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
    @GET("/3/gallery/search/time/1/all")
    Call<SearchResult> getSearchResult(@Query("q") String query);

    /**
     * This call returns JSON response with images which are hot and viral.
     *     section	optional	hot | top | user. Defaults to hot
            sort	optional	viral | top | time | rising (only available with user section). Defaults to viral
            page	optional	integer - the data paging number
            window	optional	Change the date range of the request if the section is top.
                                Accepted values are day | week | month | year | all. Defaults to day

     https://api.imgur.com/3/gallery/{{section}}/{{sort}}/{{window}}/{{page}}?
                    showViral={{showViral}}&mature={{showMature}}&album_previews={{albumPreviews}}

     */
    @GET("/3/gallery/hot/viral/all?showViral=true")
    Call<SearchResult> getViralResults();
}
