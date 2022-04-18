package dev.s0vi.shakemyhand.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class FileUtils {
    public static long getCRC32Checksum(InputStream stream, int bufferSize) throws IOException {
        CheckedInputStream checkedInputStream = new CheckedInputStream(stream, new CRC32());
        byte[] buffer = new byte[bufferSize];

        //Apparently this is the way to do it? I guess this ensures you iterate over the whole input stream??
        // https://www.baeldung.com/java-checksums
        while (checkedInputStream.read(buffer, 0, buffer.length) >= 0) {}

        return checkedInputStream.getChecksum().getValue();
    }
}
