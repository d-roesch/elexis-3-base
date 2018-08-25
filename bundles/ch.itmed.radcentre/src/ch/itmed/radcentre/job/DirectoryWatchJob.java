package ch.itmed.radcentre.job;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import ch.elexis.core.data.activator.CoreHub;
import ch.itmed.radcentre.sync.FileLock;
import ch.itmed.radcentre.xml.XmlParser;

import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.WatchKey;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public final class DirectoryWatchJob extends Job {

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;

	public DirectoryWatchJob(String name) throws IOException {
		super(name);

		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();

		registerDirectory();
	}

	private void registerDirectory() throws IOException {
		Path dir = FileSystems.getDefault().getPath("C:/temp");
		WatchKey key = dir.register(watcher, ENTRY_CREATE);
		keys.put(key, dir);
	}

	private void createFileLock(Path file) {
		Path lockingFile = Paths.get(file.toAbsolutePath() + ".lock");
		System.out.println(lockingFile);
		try {
			Files.createFile(lockingFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected IStatus run(IProgressMonitor pm) {

		// TODO: do not show permanent UI updates

		System.out.println("DirectoryWatchJob started");
		pm.done();

		for (;;) {
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return Status.CANCEL_STATUS;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!"); // should be logged as an error
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				@SuppressWarnings("rawtypes")
				WatchEvent.Kind kind = event.kind();

				// Context for directory entry event is the file name of entry
				@SuppressWarnings("unchecked")
				Path name = ((WatchEvent<Path>) event).context();
				Path child = dir.resolve(name);

				// print out event
				System.out.format("%s: %s\n", event.kind().name(), child);

				if (kind == ENTRY_CREATE) {

					// only process regular files, no directories
					if (Files.isRegularFile(child, LinkOption.NOFOLLOW_LINKS)) {
						// System.out.println(child.toString() + " is a regular file");
						// System.out.println("name of regular file: " + child.getFileName());

						if (!child.toFile().getAbsolutePath().endsWith("lock") && !FileLock.isAcquired(child)) {
							if (FileLock.acquire(child)) {
								// System.out.println("Lock acquired for " + child + "by Client 1");
								
								// TODO: process the file; when finished close lock and delete file
								XmlParser.run(child);
								//FileLock.release(child);
							}
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
		// return Status.OK_STATUS;

	}

}
