package ch.itmed.radcentre.sync;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileLock {
	private static final String LOCK_EXTENSION = ".lock";

	public static boolean acquire(Path file) {
		Path lockFile = Paths.get(file.toAbsolutePath() + LOCK_EXTENSION);
		
		// If lockFile exists, we cannot acquire a new lock
		if (Files.exists(Paths.get(file.toAbsolutePath() + LOCK_EXTENSION), LinkOption.NOFOLLOW_LINKS)) {
			return false;
		}

		try {
			Files.createFile(lockFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean release(Path file) {
		Path lockFile = Paths.get(file.toAbsolutePath() + LOCK_EXTENSION);
		
		// If there is no lockFile, we do not need to unlock the file
		if (!Files.exists(Paths.get(file.toAbsolutePath() + LOCK_EXTENSION), LinkOption.NOFOLLOW_LINKS)) {
			return true;
		}

		try {
			Files.delete(lockFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean isAcquired(Path file) {
		if (Files.exists(Paths.get(file.toAbsolutePath() + LOCK_EXTENSION), LinkOption.NOFOLLOW_LINKS)) {
			return true;
		} else {
			return false;
		}
	}
}
