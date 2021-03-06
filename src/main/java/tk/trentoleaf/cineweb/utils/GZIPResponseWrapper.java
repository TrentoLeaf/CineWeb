package tk.trentoleaf.cineweb.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * This class wraps a HttpServletResponse and compress it using GZIP.
 *
 * @see tk.trentoleaf.cineweb.filters.all.GZIPFilter
 */
public class GZIPResponseWrapper extends HttpServletResponseWrapper {

    private ServletOutputStream stream;
    private PrintWriter writer;

    /**
     * Wraps and compress a HttpServletResponse using GZIP.
     *
     * @param response The HttpServletResponse to compress.
     * @throws IOException
     */
    public GZIPResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        stream = new GZIPResponseStream(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
        }
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        stream.flush();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return stream;
    }

    // close resources
    public void finishResponse() {
        try {
            if (writer != null) {
                writer.close();
            }
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // do nothing
        }
    }
}
