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

package ch.itmed.radcentre.job;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.itmed.radcentre.preferences.PreferenceConstants;
import ch.itmed.radcentre.sync.FileLock;
import ch.itmed.radcentre.ui.MessageBoxUtil;
import ch.itmed.radcentre.xml.XmlParser;

public final class DirectoryWatcherRunnable implements Runnable {

	private WatchService watcher;
	private Map<WatchKey, Path> keys;
	private Path exportPath;
	private Path errorPath;
	private static Logger logger = LoggerFactory.getLogger(DirectoryWatcherRunnable.class);

	@Override
	public void run() {
		logger.info("RadCentre import thread started");

		try {
			this.watcher = FileSystems.getDefault().newWatchService();
			this.keys = new HashMap<WatchKey, Path>();
			registerDirectory();
			createErrorDirectory();
			processExistingFiles();
			directoryWatchLoop();

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void registerDirectory() throws IOException {
		String path = CoreHub.globalCfg.get(PreferenceConstants.RADCENTRE_EXPORT_PATH, "");
		exportPath = FileSystems.getDefault().getPath(path);
		WatchKey key = exportPath.register(watcher, ENTRY_CREATE);
		keys.put(key, exportPath);
	}

	private void createErrorDirectory() throws IOException {
		errorPath = Paths.get(exportPath.toString(), "\\error\\");
		if (!Files.exists(errorPath)) {
			Files.createDirectory(errorPath);
		}
	}

	private void processExistingFiles() {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.xml");
		List<Path> files = new ArrayList<>();

		try (Stream<Path> paths = Files.walk(exportPath, 1)) {
			paths.filter(Files::isRegularFile).filter(f -> matcher.matches(f.getFileName())).forEach(f -> files.add(f));
			files.stream().filter(f -> !FileLock.isAcquired(f)).forEach(f -> processFile(f));
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	private void directoryWatchLoop() throws InterruptedException {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.lock");

		for (;;) {
			WatchKey key = watcher.take();

			Path dir = keys.get(key);
			if (dir == null) {
				// ignore unrecognized WatchKey
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				@SuppressWarnings("rawtypes")
				WatchEvent.Kind kind = event.kind();

				// Context for directory entry event is the file name of entry
				@SuppressWarnings("unchecked")
				Path name = ((WatchEvent<Path>) event).context();
				Path child = dir.resolve(name);

				if (kind == ENTRY_CREATE) {

					// only process regular files, no directories
					if (Files.isRegularFile(child, LinkOption.NOFOLLOW_LINKS)) {

						if (!matcher.matches(child.getFileName()) && !FileLock.isAcquired(child)) {
							logger.debug("Process file " + child);
							processFile(child);
							logger.debug("Finished processing file " + child);
						}
					}
				}

				// reset key and remove from set if directory no longer accessible
				boolean valid = key.reset();
				if (!valid) {
					keys.remove(key);

					// all directories are inaccessible
					if (keys.isEmpty()) {
						break;
					}
				}
			}
		}
	}

	private void processFile(Path file) {
		if (FileLock.acquire(file)) {

			try {
				XmlParser.run(file);
				Files.delete(file);
			} catch (Exception e) {
				logger.error("Failed to process file: " + file, e);
				moveToErrDir(file);
				MessageBoxUtil.showErrorDialog("Fehler beim RadCentre Import");
			}

			FileLock.release(file);
		} else {
			logger.error("Failed to acquire lock for file: " + file);
			moveToErrDir(file);
		}
	}

	private void moveToErrDir(Path file) {
		Path moveTo = Paths.get(errorPath.toString(), file.getFileName().toString());

		try {
			Files.move(file, moveTo, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error("", e);
		}

	}
}
