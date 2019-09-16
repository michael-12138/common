package org.michael.common.utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created on 2019-09-16 11:29
 * Author : Michael.
 */
public class CompressUtil {

    public static void zipFileOrDirectoryWithoutHiddenFiles(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFileOrDirectoryWithoutHiddenFiles(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[8192];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        } finally {
            IOUtil.closeQuietely(fis);
        }
    }

    public static byte[] gzipCompress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(bytes);
            gzip.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
        }
    }

    public static byte[] gzipUncompress(byte[] bytes) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream ungzip = null;
        try {
            ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            out.flush();
            ungzip.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
        }

    }

    public static void main(String[] args) throws IOException {

        //        String json = "{\"name\",\"jack\" }";
        //
        //        String encode = URLEncoder.encode(json);
        //        System.out.println(encode);
        //
        //        byte[] buf = encode.getBytes("utf8");
        //        byte[] com = gzipCompress(buf);
        //        byte[] uncom = gzipUncompress(com);
        //
        //        System.out.println(new String(uncom));

        String sourceFile = "/web/dev/bb/t3_orc/dt=20181030";
        FileOutputStream fos = new FileOutputStream("/web/dev/bb/t3_orc/dt=20181030.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);

        zipFileOrDirectoryWithoutHiddenFiles(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();

    }
}
