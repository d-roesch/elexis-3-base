/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.radcentre.sync;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileLock {
	private static final String LOCK_EXTENSION = ".lock";
	private static Logger logger = LoggerFactory.getLogger(FileLock.class);

	public static boolean acquire(Path file) {
		Path lockFile = Paths.get(file.toAbsolutePath() + LOCK_EXTENSION);

		// If lockFile exists, we cannot acquire a new lock
		if (Files.exists(Paths.get(file.toAbsolutePath() + LOCK_EXTENSION), LinkOption.NOFOLLOW_LINKS)) {
			return false;
		}

		try {
			Files.createFile(lockFile);
		} catch (IOException e) {
			logger.error("Failed to acquire lock for file: " + file, e);
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
			logger.error("Failed to release lock for file: " + file, e);
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
