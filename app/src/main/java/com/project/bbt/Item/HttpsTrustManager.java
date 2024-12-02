package com.project.bbt.Item;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsTrustManager {

    public static void allowAllSSL() {
        TrustManager[] trustManagers = new TrustManager[]{

                new X509TrustManager() {

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // Empty implementation
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        if (chain == null || chain.length == 0) throw new IllegalArgumentException("Certificate is null or empty");
                        if (authType == null || authType.isEmpty()) throw new IllegalArgumentException("AuthType is null or empty");

                        // Only allow certain auth types
                        if (!authType.equalsIgnoreCase("ECDHE_RSA") &&
                                !authType.equalsIgnoreCase("ECDHE_ECDSA") &&
                                !authType.equalsIgnoreCase("RSA") &&
                                !authType.equalsIgnoreCase("GENERIC") &&
                                !authType.equalsIgnoreCase("ECDSA")) throw new CertificateException("Certificate is not trusted");

                        try {
                            chain[0].checkValidity();
                        } catch (Exception e) {
                            throw new CertificateException("Certificate is not valid or trusted", e);
                        }
                    }
                }
        };

        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("203.100.57.36");
                }
            });
        } catch (Exception e) {
            e.printStackTrace(); // Use logging instead in production
        }
    }
}
