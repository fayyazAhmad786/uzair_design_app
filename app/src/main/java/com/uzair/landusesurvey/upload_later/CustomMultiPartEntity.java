package com.uzair.landusesurvey.upload_later;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CustomMultiPartEntity extends MultipartEntity {

    private ProgressListener listener;

//    public CustomMultiPartEntity(final uu.android.DataSender.ProgressListener progressListener)
//    {
//            super();
//            this.listener = progressListener;
//    }
//
//    public CustomMultiPartEntity(final HttpMultipartMode mode, final uu.android.DataSender.ProgressListener listener)
//    {
//            super(mode);
//            this.listener = listener;
//    }
//
//    public CustomMultiPartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final uu.android.DataSender.ProgressListener listener)
//    {
//            super(mode, boundary, charset);
//            this.listener = listener;
//    }

    public CustomMultiPartEntity(ProgressListener progressListener) {
    	listener = progressListener;
	}
	@Override
    public void writeTo(final OutputStream outstream) throws IOException
    {
            super.writeTo(new CountingOutputStream(outstream, this.listener));
    }
    public static interface ProgressListener
    {
            void transferred(long num);
    }
    public static class CountingOutputStream extends FilterOutputStream
    {
            private final ProgressListener listener;
            private long transferred;

            public CountingOutputStream(final OutputStream out, final ProgressListener listener2)
            {
                    super(out);
                    this.listener = listener2;
                    this.transferred = 0;
            }
            public void write(byte[] b, int off, int len) throws IOException
            {
                    out.write(b, off, len);
                    this.transferred += len;
                    this.listener.transferred(this.transferred);
            }
            public void write(int b) throws IOException
            {
                    out.write(b);
                    this.transferred++;
                    this.listener.transferred(this.transferred);
            }
    }
}