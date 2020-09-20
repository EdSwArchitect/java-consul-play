package com.bscllc.consulplay;

import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.health.ServiceHealth;

import java.util.List;

public class KVPlay {
    public static void main(String... args) {
        System.out.println("Hi");

        Consul client = Consul.builder().build(); // connect on localhost

        HealthClient healthClient = client.healthClient();

        System.out.println("--- Services");

// Discover only "passing" nodes
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
//
//// get single KV for key
//        Response<GetValue> keyValueResponse = client.getKVValue("com.my.app.foo");
//        System.out.println(keyValueResponse.getValue().getKey() + ": " + keyValueResponse.getValue().getDecodedValue()); // prints "com.my.app.foo: foo"
//
//// get list of KVs for key prefix (recursive)
//        Response<List<GetValue>> keyValuesResponse = client.getKVValues("com.my");
//        keyValuesResponse.getValue().forEach(value -> System.out.println(value.getKey() + ": " + value.getDecodedValue())); // prints "com.my.app.foo: foo" and "com.my.app.bar: bar"
//
////list known datacenters
//        Response<List<String>> response = client.getCatalogDatacenters();
//        System.out.println("Datacenters: " + response.getValue());

    }

}
