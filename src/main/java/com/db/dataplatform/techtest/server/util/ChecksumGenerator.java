package com.db.dataplatform.techtest.server.util;

import com.db.dataplatform.techtest.server.exception.TechException;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumGenerator {

    private static final String ALGORITHM = "MD5";


    public static String getCheckSum(String data) {
        try {
            byte[] dataBytes;
            MessageDigest messageDigest;

            messageDigest = MessageDigest.getInstance(ALGORITHM);

            dataBytes = messageDigest.digest(data.getBytes());

            return new String(Hex.encodeHex(dataBytes));

        } catch (NoSuchAlgorithmException e) {
            throw new TechException("Error while generating checksum ");
        }


    }




}
