package ir.util;

import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends InputStream {

	public long totalSize;
	public InputStream InputStream;
	public Listener listener;
	public long completed;

	public ProgressInputStream(long totalSize, InputStream InputStream, Listener listener) {
		this.InputStream = InputStream;
		this.listener = listener;
		this.completed = 0;
		this.totalSize = totalSize;
	}

	@Override
	public int read() throws IOException {
		int bytesRead = this.InputStream.read();
		track(bytesRead);
		return bytesRead;
	}

	@Override
	public int read(byte[] data, int off, int len) throws IOException {
		int bytesRead = this.InputStream.read(data, off, len);
		track(bytesRead);
		return bytesRead;
	}

	@Override
	public int read(byte[] data) throws IOException {
		int bytesRead = this.InputStream.read(data);
		track(bytesRead);
		return bytesRead;
	}

	private void track(int len) {
		if(len!=-1)
		{
			this.completed += len;
		}
		this.listener.progress(this.completed, this.totalSize);
	}

	public interface Listener {
		public void progress(long completed, long totalSize);
	}

}