package com.example.JAVASDK.Service;


import com.yubi.oss.DCG.SDKServiceGrpc;
import com.yubi.oss.DCG.request1;
import com.yubi.oss.DCG.response1;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.io.*;
import java.util.concurrent.TimeUnit;

@GrpcService
public class SDKService extends SDKServiceGrpc.SDKServiceImplBase {

    @Override
    public void getDownloadLink(request1 request, StreamObserver<response1> responseObserver) {

        String path = request.getFolderLocation();

        try {
            System.out.println("-------------------");
            compile(path);
        } catch(Exception e) {
            e.printStackTrace();
        }

        String downloadLink = "Compiled successfully and files are located at" + path;

        responseObserver.onNext(response1.newBuilder().setDownloadLink(downloadLink).build());
        responseObserver.onCompleted();
    }


    private void compile(String path) throws Exception {

        File location1 = new File(path);
        String absolutepath = System.getProperty("user.dir");

//        runCommand(location1, "javac -cp '.:/Users/manoj.kutala/Desktop/EFS/library/javax.persistence-api-2.2.jar:/Users/manoj.kutala/Desktop/EFS/library/spring-web-5.3.22.jar:/Users/manoj.kutala/Desktop/EFS/library/lombok-1.18.24.jar:/Users/manoj.kutala/Desktop/EFS/library/spring-beans-5.3.22.jar:/Users/manoj.kutala/Desktop/EFS/library/spring-core-5.3.22.jar' DCG/*.java");
//        runCommand(location1, "javac -cp '.:/Users/manoj.kutala/Desktop/JAVA-SDK/lib/javax.persistence-api-2.2.jar:/Users/manoj.kutala/Desktop/JAVA-SDK/lib/spring-beans-5.3.22.jar:/Users/manoj.kutala/Desktop/JAVA-SDK/lib/spring-core-5.3.22.jar:/Users/manoj.kutala/Desktop/JAVA-SDK/lib/spring-web-5.3.22.jar' DCG/*.java");
//        runCommand(location1, "javac -cp '.:lib/javax.persistence-api-2.2.jar:lib/spring-beans-5.3.22.jar:lib/spring-core-5.3.22.jar:lib/spring-web-5.3.22.jar' DCG/*.java");

        runCommand(location1, "javac -cp '.:"+absolutepath+"/lib/*' DCG/*.java");
        runCommand(location1, "jar cvf DCG.jar DCG/*.class");

    }



    public static void runCommand(File whereToRun, String command) throws Exception {
        System.out.println("Running in: " + whereToRun);
        System.out.println("Command: " + command);
        String[] commands;

        commands = new String[] { "sh", "-c", command };

        Process process =  Runtime.getRuntime().exec(commands, null, whereToRun);
        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        printStream(inputStream);
        printStream(errorStream);

        boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
        outputStream.flush();
        outputStream.close();

        if(!isFinished) {
            process.destroyForcibly();
        }
        System.out.println(command + " exitValue() " + process.exitValue());
    }




    private static void printStream(InputStream inputStream) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }



}
