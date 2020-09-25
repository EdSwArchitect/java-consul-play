package com.bscllc.consulplay;

import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.cache.KVCache;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.model.kv.Value;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class KVWatch {

    /*
    SSLContext context = SSLContext.getInstance("TLSv1");
TrustManager[] trustManagerArray = {
    NullX509TrustManager.INSTANCE
};

context.init(null, trustManagerArray, null);

Consul.builder().withUrl("https://your-secure-host:8500").withSslContext(sslContext).build();
     */
    public static void main(String... args) {
        try {

            SSLContext context = SSLContext.getInstance("TLSv1");

            TrustManager[] tm = {
//                    X509TrustManager
            };

            SecureRandom secureRandom = new SecureRandom();

            context.init(null, tm, secureRandom);


            Consul client = Consul.builder().build(); // connect on localhost

            final KeyValueClient kvClient = client.keyValueClient();

            boolean worked;

            worked = kvClient.putValue("com.test", "Edwin Brown");

            System.out.format("Did the last put work? %s\n", worked);

            KVCache cache = KVCache.newCache(kvClient, "com.test");

            cache.addListener(newValues -> {
                Iterator<Value> newValue = newValues.values().stream().iterator();

                while (newValue.hasNext()) {
                    Value v = newValue.next();

                    System.out.format("%s: %s\n", v.getKey(), v.getValueAsString().get());
                    System.out.format("Create index:%d. Modify index: %d\n", v.getCreateIndex(), v.getModifyIndex());
                }
            });

            cache.start();

            TimeUnit.MINUTES.sleep(3L);

            cache.stop();

            cache.close();


        }
        catch(Exception exp) {
            exp.printStackTrace();
        }
    }
}
