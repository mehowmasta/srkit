package ir.util;

import java.io.IOException;
import java.io.OutputStream;

public class ProgressOutputStream extends OutputStream {
	
	public long totalSize; 
	public OutputStream outputStream;
	public Listener listener;
	public long completed;
	
    public ProgressOutputStream(long totalSize, OutputStream outputStream, Listener listener) {
        this.outputStream = outputStream;
        this.listener = listener;
        this.completed = 0;
        this.totalSize = totalSize;
    }

    @Override
    public void write(byte[] data, int off, int len) throws IOException {
        this.outputStream.write(data, off, len);
        track(len);
    }

    @Override
    public void write(byte[] data) throws IOException {
        this.outputStream.write(data);
        track(data.length);
    }

    @Override
    public void write(int c) throws IOException {
        this.outputStream.write(c);
        track(1);
    }

    private void track(int len) {
        this.completed += len;
        this.listener.progress(this.completed, this.totalSize);
    }

    public interface Listener {
        public void progress(long completed, long totalSize);
    }
}