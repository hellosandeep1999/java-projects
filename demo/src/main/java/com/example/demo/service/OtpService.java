package com.example.demo.service;

import com.example.demo.generated.jooq.Tables;
import com.example.demo.generated.jooq.enums.VotersAuthorizedIsvoted;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
public class OtpService {


    private static final OkHttpClient client = new OkHttpClient();
    public static final String API_KEY = "bcl8y2H1UTq5BWrIouDR6igCf0apPtAh4mzFkxJjYNOXdw7GMeuv71jfQhwXt90onxeC2Pzs6S4ERGHB";

    @Autowired
    DSLContext dslContext;

    public void sendOtp(String mobileNumber) throws IOException {
        var count = dslContext.fetchCount(DSL.selectFrom(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.USERNAME.eq(mobileNumber)));
        if(count == 0){
            throw new RuntimeException("Your mobile number not verified, Please raise a signup request!");
        }

        Random rnd = new Random();
        String otp = String.format("%06d",rnd.nextInt(999999));
        String baseUrl = "https://www.fast2sms.com/dev/bulkV2";
        HttpUrl url = HttpUrl.parse(baseUrl).newBuilder()
                .addQueryParameter("authorization", API_KEY)
                .addQueryParameter("variables_values", otp)
                .addQueryParameter("route","otp")
                .addQueryParameter("numbers",mobileNumber)
                .build();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Please verify your mobile number!");
            }
            dslContext.update(Tables.VOTERS_AUTHORIZED)
                    .set(Tables.VOTERS_AUTHORIZED.OTP, otp)
                    .where(Tables.VOTERS_AUTHORIZED.USERNAME.eq(mobileNumber)).execute();
        }
    }

    public void verifyOtp(String mobileNumber, String otp) {
        var count = dslContext.fetchCount(DSL.selectFrom(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.USERNAME.eq(mobileNumber)));
        if(count == 0){
            throw new RuntimeException("Your mobile number not verified, Please check again!");
        }
        var dbOtp = dslContext.select(Tables.VOTERS_AUTHORIZED.OTP).
                from(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.USERNAME.eq(mobileNumber)).fetchInto(String.class);
        if(!dbOtp.get(0).equals(otp)){
            throw new RuntimeException("Wrong OTP, Please check again!");
        }
    }

    public void savePassword(String mobileNumber, String password) {
        var count = dslContext.fetchCount(DSL.selectFrom(Tables.VOTERS_AUTHORIZED)
                .where(Tables.VOTERS_AUTHORIZED.USERNAME.eq(mobileNumber)));
        if(count == 0){
            throw new RuntimeException("Your mobile number not verified, Please check again!");
        }
        dslContext.update(Tables.VOTERS_AUTHORIZED)
                .set(Tables.VOTERS_AUTHORIZED.PASSWORD, password)
                .where(Tables.VOTERS_AUTHORIZED.USERNAME.eq(mobileNumber)).execute();
    }

}
