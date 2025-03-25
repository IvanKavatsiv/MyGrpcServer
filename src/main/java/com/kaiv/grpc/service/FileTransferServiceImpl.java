package com.kaiv.grpc.service;

import com.kaiv.grpc.FileTransfer;
import com.kaiv.grpc.FileTransferServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.io.FileOutputStream;
import java.io.IOException;

@GrpcService
public class FileTransferServiceImpl extends FileTransferServiceGrpc.FileTransferServiceImplBase {

    @Override
    public StreamObserver<FileTransfer.FileChunk> uploadFile(StreamObserver<FileTransfer.UploadStatus> responseObserver) {

        return new StreamObserver<>() {

            private final String savingFolderName = "c:/Temp/downloads/";

            private FileOutputStream outputStream;
            private String filename;

            @Override
            public void onNext(FileTransfer.FileChunk chunk) {

                try {
                    if (outputStream == null) {
                        filename = savingFolderName + chunk.getFilename();
                        outputStream = new FileOutputStream(filename);
                    }
                    outputStream.write(chunk.getData().toByteArray());
                } catch (IOException e) {
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    responseObserver.onNext(FileTransfer.UploadStatus.newBuilder().setMessage("download file successful: " + filename).build());
                    responseObserver.onCompleted();
                } catch (IOException e) {
                    responseObserver.onError(e);
                }
            }
        };
    }
}
