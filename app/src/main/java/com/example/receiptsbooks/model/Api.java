package com.example.receiptsbooks.model;

import com.example.receiptsbooks.model.domain.Categories;
import com.example.receiptsbooks.model.domain.ReceiptInfo;
import com.example.receiptsbooks.model.domain.SearchRecommend;
import com.example.receiptsbooks.model.domain.SearchResult;
import com.example.receiptsbooks.model.domain.StorePagerContent;
import com.example.receiptsbooks.model.domain.TicketParams;
import com.example.receiptsbooks.model.domain.TicketResult;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    @Multipart
    @POST("upload")
    Call<ReceiptInfo> getReceiptInfo(@Part MultipartBody.Part part);

    @GET("discovery/categories")
    Call<Categories> getCategories();

    @GET
    Call<StorePagerContent> getHomePagerContent(@Url String url);

    @POST("tpwd")
    Call<TicketResult> getTicket(@Body TicketParams ticketParams);

    @GET("search/recommend")
    Call<SearchRecommend> getRecommendWords();

    @GET("search")
    Call<SearchResult> doSearch(@Query("page")int page, @Query("keyword") String keyword);
}
