package com.capitek.controller;

import okhttp3.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Http2Controller {

    @GetMapping(value = "udm")
    private String udm() throws IOException {
        List<Protocol> protocolList = new ArrayList<>();
        String strTimeString = getTimeString();
        protocolList.add(Protocol.H2_PRIOR_KNOWLEDGE);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder().connectionPool(new ConnectionPool()).protocols(protocolList);
//        OkHttpClient client = builder.sslSocketFactory(createSSLSocketFactory(),new TrustAllCerts())
//                .hostnameVerifier(new TrustAllHostnameVerifier())
//                .build();
        OkHttpClient client = builder.build();
        Request.Builder httpRequestBuilder = new Request.Builder().url("http://172.18.0.16:8443/udm").get();
        httpRequestBuilder.header("Content-Type","application/json");
        httpRequestBuilder.header("VERSION","2.0");
        httpRequestBuilder.header("MSGID","capmid-"+strTimeString);
        httpRequestBuilder.header("TRANSID","captid-"+strTimeString);
        httpRequestBuilder.header("ESBID","1");
//        httpRequestBuilder.header("X-APP-ID","abc");
//        httpRequestBuilder.header("X-APP-KEY","abc");
        Request request = httpRequestBuilder.build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        return responseBody;
    }

    private String getTimeString(){
        return System.currentTimeMillis()+"";
    }

    private static class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { new TrustAllCerts() }, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }

}
