package io.github.kgbis.remotecontrol.tray.lock;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class SingleInstanceLock {

	private FileLock lock;

	private FileChannel channel;

	public boolean tryLock(Path lockFile) {
		try {
			channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			lock = channel.tryLock();
			return lock != null;
		}
		catch (OverlappingFileLockException | IOException e) {
			return false;
		}
	}

	public void release() {
		try {
			if (lock != null) {
				lock.release();
			}
			if (channel != null) {
				channel.close();
			}
		}
		catch (IOException ignore) {
			// ignore
		}
	}

}
