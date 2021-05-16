package com.mepus.productiontest.dto;

public class LottoData {
    String returnValue;     // 요청 결과
    String drwNoDate;       // 추첨 날짜
    long totSellamnt;       // 총 상금액
    long firstWinamnt;      // 인당 1등 수령액
    int firstPrzwnerCo;     // 1등 당첨인원
    long firstAccumamnt;    // 총 1등 수령액
    int drwNo;              // 로또회차
    int drwtNo6;            // 6번째 번호
    int drwtNo5;            // 5번째 번호
    int drwtNo4;            // 4번째 번호
    int drwtNo3;            // 3번째 번호
    int drwtNo2;            // 2번째 번호
    int drwtNo1;            // 1번째 번호
    int bnusNo;             // 보너스 번호

    public long getTotSellamnt() {
        return totSellamnt;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public String getDrwNoDate() {
        return drwNoDate;
    }

    public long getFirstWinamnt() {
        return firstWinamnt;
    }

    public int getFirstPrzwnerCo() {
        return firstPrzwnerCo;
    }

    public int getBnusNo() {
        return bnusNo;
    }

    public long getFirstAccumamnt() {
        return firstAccumamnt;
    }

    public int getDrwNo() {
        return drwNo;
    }

    public int getDrwtNo6() {
        return drwtNo6;
    }

    public int getDrwtNo5() {
        return drwtNo5;
    }

    public int getDrwtNo4() {
        return drwtNo4;
    }

    public int getDrwtNo3() {
        return drwtNo3;
    }

    public int getDrwtNo2() {
        return drwtNo2;
    }

    public int getDrwtNo1() {
        return drwtNo1;
    }
}
