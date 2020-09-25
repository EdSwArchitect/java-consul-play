package com.bscllc.consulplay;

import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.health.ServiceHealth;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

public class KVPlay {
    public static void main(String... args) {
        try {

            KeyStore clientKeystore = KeyStore.getInstance("JKS");
            KeyStore trustKeystore = KeyStore.getInstance("JKS");

            FileInputStream trustIs = new FileInputStream("/Users/ebrown/certs2/consul-trust.jks");

            trustKeystore.load(trustIs, "password".toCharArray());
            trustIs.close();

            FileInputStream clientIs = new FileInputStream("/Users/ebrown/certs2/dc1-client-keystore.jks");

            clientKeystore.load(clientIs, "password".toCharArray());
            clientIs.close();

            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeystore, "password".toCharArray());

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustKeystore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

            sslContext.init(
                    keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom());

            Consul.builder().withUrl("https://127.0.0.1:8501").withSslContext(sslContext).build();

            Consul client = Consul.builder().build(); // connect on localhost

            HealthClient healthClient = client.healthClient();

            System.out.println("--- Services");

            List<ServiceHealth> nodes = healthClient.getHealthyServiceInstances("DataService").getResponse();

            nodes.forEach(node -> {
                System.out.format("Node: %s\n", node.toString());
            });

            System.out.println("---- End of service instances");

            final KeyValueClient kvClient = client.keyValueClient();

            boolean worked;

            worked = kvClient.putValue("com.my.app.foo", "foo");

            System.out.format("Did the last put work? %s\n", worked);
            worked = kvClient.putValue("com.my.app.bar", "bar");

            System.out.format("Did the last put work? %s\n", worked);
            worked = kvClient.putValue("com.your.app.foo", "hello");

            System.out.format("Did the last put work? %s\n", worked);
            worked = kvClient.putValue("com.your.app.bar", "world");

            System.out.format("Did the last put work? %s\n", worked);


            String value = kvClient.getValueAsString("com.your.app.bar").get();

            System.out.format("The value retrieved '%s'\n", value);

            List<String> list = kvClient.getValuesAsString("com");

            System.out.println("--- com get list ---");

            list.forEach(val -> {
                System.out.format("\t-- value: %s\n", val);
            });

            System.out.println("--- End of list ----");

        }
        catch(NullPointerException npe) {
            npe.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
