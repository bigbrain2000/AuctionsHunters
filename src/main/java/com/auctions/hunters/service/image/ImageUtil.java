package com.auctions.hunters.service.image;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static java.util.zip.Deflater.BEST_COMPRESSION;

@Component
public class ImageUtil {



    /**
     * Compresses the given image data using {@link Deflater}.
     * @param data The image data to be compressed.
     * @return The compressed image data.
     */
    public static byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            byte[] tmp = new byte[4 * 1024];
            while (!deflater.finished()) {
                int size = deflater.deflate(tmp);
                outputStream.write(tmp, 0, size);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error compressing image data", e);
        } finally {
            deflater.end();
        }
    }

    /**
     * Decompresses the given image data using {@link Inflater}.
     * @param data The compressed image data to be decompressed.
     * @return The decompressed image data.
     */
    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            byte[] tmp = new byte[4 * 1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }

            return outputStream.toByteArray();
        } catch (IOException | DataFormatException e) {
            throw new RuntimeException("Error decompressing image data", e);
        } finally {
            inflater.end();
        }
    }
}
