package com.bscllc.consulplay;

import com.orbitz.consul.Consul;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;


public class SslTest {

    public static void main(String... args) {
        try {
//            KeyStore clientKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
//            KeyStore trustKeystore = KeyStore.getInstance(KeyStore.getDefaultType());

            KeyStore clientKeystore = KeyStore.getInstance("JKS");
            KeyStore trustKeystore = KeyStore.getInstance("JKS");

            FileInputStream trustIs = new FileInputStream("/Users/ebrown/certs2/consul-trust.jks");

            trustKeystore.load(trustIs, "password".toCharArray());
            trustIs.close();

            FileInputStream clientIs = new FileInputStream("/Users/ebrown/certs2/dc1-client-keystore.jks");

            clientKeystore.load(clientIs, "password".toCharArray());
            clientIs.close();

            System.out.println("******** Default algorithm: " + KeyManagerFactory.getDefaultAlgorithm());

            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeystore, "password".toCharArray());

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustKeystore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
//            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(
                    keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom());

//            MLc195

            Consul.builder().withUrl("https://127.0.0.1:8501").withSslContext(sslContext).build();

            System.out.println("Success");
        }
        catch(Exception exp) {
            exp.printStackTrace();
        }
    }
}
