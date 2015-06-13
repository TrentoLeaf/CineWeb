package tk.trentoleaf.cineweb.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GZIPResponseStream extends ServletOutputStream {

    private HttpServletResponse response = null;
    private ServletOutputStream outputStream = null;
    private ByteArrayOutputStream baos = null;
    private GZIPOutputStream gzipStream = null;

    // constructor
    public GZIPResponseStream(HttpServletResponse response) throws IOException {
        super();

        this.response = response;
        this.outputStream = response.getOutputStream();
        this.baos = new ByteArrayOutputStream();
        this.gzipStream = new GZIPOutputStream(baos);
    }

    @Override
    public void write(int b) throws IOException {
        gzipStream.write((byte) b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        gzipStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        gzipStream.flush();
    }

    @Override
    public void close() throws IOException {
        gzipStream.finish();

        // add new content length
        byte[] bytes = baos.toByteArray();
        response.addHeader("Content-Length", Integer.toString(bytes.length));

        // add encoding type
        response.addHeader("Content-Encoding", "gzip");

        // flush and close
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
