# java-consul-play

Playing around with the Java client to Consul

## Generate the Consul certificates

From the TLS document page: https://www.consul.io/commands/tls

Create the CA, the server, the CLI, and the client certificates

1. consul tls ca create
1. consul tls cert create -server
1. consul tls cert create -cli
1. consul tls cert create -client

## Setup Java Keystores

### Create the trust store using the CA certificate

Import the CA PEM certificate into a truststore named *consul-trust.jks*

> keytool -import -v -trustcacerts -alias consul -file consul-agent-ca.pem -keystore consul-trust.jks
>

Enter the password of your choice, and then verify the password.

### Create the keystore for the client certificate, by making a cert chain that includes the CA

Create a chain with the PEM certificates for the client and for the CA

> cat dc1-client-consul-0.pem consul-agent-ca.pem > import.pem
>

Create a PKCS12 certificate using openssl

> openssl pkcs12 -export -in import.pem -inkey dc1-client-consul-0-key.pem  > dc1-client-consul.p12
>

You will be prompted for a password. Enter a password for the PKCS12, which is named *dc1-client-consul.p12*

Now, import the PKCS12 certificate into a keystore named *dc1-client-keystore.jks*. The PKCS12 certificate will contain
the certificate chain for the client certificate in the keystore.

> keytool -importkeystore -srckeystore dc1-client-consul.p12 -destkeystore dc1-client-keystore.jks -srcstoretype pkcs12 -srcalias 1 -destalias localhost
>

Now you can use the JKS truststore and the client keystore to access Consul, as long as it was started using the generated
PEM certificates.

## Running of Consul after generating certificates

The file config-tls.json contains the Consul configuration and uses the generated certificates for TLS. The unencrypted
port is 8500. The encrypted port is 8501.

> consul agent -config-file=config-tls.json -log-level trace -dev -data-dir ~/consul-data
>

The Consul configuration file:

`
 {
 
    "verify_incoming": true,
    
    "verify_outgoing": true,
 
    "verify_server_hostname": true,
 
    "ca_file": "consul-agent-ca.pem",
 
    "cert_file": "dc1-server-consul-0.pem",
 
    "key_file": "dc1-server-consul-0-key.pem",
    
    "node_name" : "MLc195",
    
    "auto_encrypt": {
    
      "allow_tls": true
    },
    "ports" : {
      "http" : 8500,
      "https" : 8501
    }
  }
`

## MISC

Excercise accessing Consul using the Java API.

This is the API to use, at it handles watches and services.
https://github.com/rickfast/consul-client
