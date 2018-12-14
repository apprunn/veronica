package com.rolandopalermo.facturacion.ec.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3Manager {

    private static S3Manager instance = null;

    private AmazonS3 s3 = null;

	private String bucketName = "sri-quipu";

    public static S3Manager getInstance() {
        if (instance == null) {
            instance = new S3Manager();
        }
        return instance;
    }

    protected S3Manager() {}

    public void initialize() {

        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider("s3_profile");
        
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        
        s3 = AmazonS3ClientBuilder.standard()
                                .withCredentials(credentialsProvider)
                                .withRegion(Regions.US_EAST_1)
                                .build();

        createBucket();
    }

    private void createBucket() {
        if (!s3.doesBucketExistV2(bucketName)) {
            try {
                s3.createBucket(bucketName);
            } catch (AmazonS3Exception e) {
                throw new RuntimeException("S3 Error create bucket");
            }
        }
    }

    public Bucket getBucket(String bucket_name) {
        Bucket named_bucket = null;
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            if (b.getName().equals(bucket_name)) {
                named_bucket = b;
            }
        }
        return named_bucket;
    }

    public String [] uploadFile(byte [] data, String format) throws AmazonServiceException, IOException {

		String nombreArchivoXML = UUID.randomUUID().toString();

        File tempFile = File.createTempFile(nombreArchivoXML, "." + format, null);
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(data);
        fos.close();

        PutObjectRequest request = new PutObjectRequest(bucketName, nombreArchivoXML, tempFile)
                                        .withCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(request);

        String urlFile = String.format("https://s3.amazonaws.com/%s/%s", bucketName, nombreArchivoXML);

        String [] result = {nombreArchivoXML, urlFile};

        return result;
    }

    public byte[] downloadFile(String name) {
        try {
			String rutaArchivoXML = UUID.randomUUID().toString();
            S3Object o = s3.getObject(bucketName, name);
            S3ObjectInputStream s3is = o.getObjectContent();
            File tempFile = File.createTempFile(rutaArchivoXML, ".xml", null);
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] readBuffer = new byte[1024];
            int read_len = 0;
            
            while ((read_len = s3is.read(readBuffer)) > 0) {
                fos.write(readBuffer, 0, read_len);
            }

            s3is.close();
            fos.close();
            
            Path path = Paths.get(tempFile.getAbsolutePath());

            return Files.readAllBytes(path);

        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}