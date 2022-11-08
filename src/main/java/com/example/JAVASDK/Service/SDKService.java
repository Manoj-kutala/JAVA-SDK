package com.example.JAVASDK.Service;

import com.example.sample.request;
import com.example.sample.response;
import com.example.sample.sample1Grpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.io.*;
import java.util.concurrent.TimeUnit;

@GrpcService
public class SDKService extends sample1Grpc.sample1ImplBase{

    @Override
    public void func1(request request, StreamObserver<response> responseObserver) {

        String path = request.getReq();

        try {
            System.out.println("-------------------");
            compile(path);
        } catch(Exception e) {
            e.printStackTrace();
        }

        String s = "Compiled successfully and files are located at" + path;

        responseObserver.onNext(response.newBuilder().setRes(s).build());
        responseObserver.onCompleted();
    }


    private void compile(String path) throws Exception {

        File location1 = new File(path);

        runCommand(location1, "javac -cp '.:/Users/manoj.kutala/Desktop/EFS/library/javax.persistence-api-2.2.jar:/Users/manoj.kutala/Desktop/EFS/library/spring-web-5.3.22.jar:/Users/manoj.kutala/Desktop/EFS/library/lombok-1.18.24.jar:/Users/manoj.kutala/Desktop/EFS/library/spring-beans-5.3.22.jar:/Users/manoj.kutala/Desktop/EFS/library/spring-core-5.3.22.jar:/Users/manoj.kutala/Desktop/EFS/library/commons-collections4-4.2.jar' DCG/*.java");
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
