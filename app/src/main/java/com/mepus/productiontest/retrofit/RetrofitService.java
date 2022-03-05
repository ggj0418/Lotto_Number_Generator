package com.mepus.productiontest.retrofit;

import com.mepus.productiontest.dto.LottoData;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    // 로또 회차별 당첨번호 조회(RxJava)
    @GET("common.do")
    Observable<LottoData> getWinningNumbersRxJava(@Query("method") String method, @Query("drwNo") int number);

    // 로또 회차별 당첨번호 조회(Call)
    @GET("common.do")
    Call<LottoData> getWinningNumbers(@Query("method") String method, @Query("drwNo") int number);
}
