package common.utils;

import java.io.File;
import java.util.*;

public class FileUtils {

	public static Map<String, String> listFilesInFolder(String folderPath) {
		// TODO: List files in folder
		// 3. Calculate MD5 hash for each file
		// 4. Return map of filename to hash
		// 1. Create folder object
		Map<String, String> fileHashes = new HashMap<>();
		File file = new File(folderPath);
		if (file.exists() && file.isDirectory()) {
			// 2. Get list of files
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile())
					fileHashes.put(f.getName(), MD5Hash.HashFile(f.getPath()));
			}
		}
		return fileHashes;
	}

	public static String getSortedFileList(Map<String, String> files) {
		// 1. Check if files map is empty
		if (files.isEmpty()) return "Repository is empty.";

		// 2. Sort file names
		Map<String, String> sortedFiles = new TreeMap<>(files);

		// 3. Create formatted string with names and hashes
		StringBuilder sortedFileList = new StringBuilder();
		for (String key : sortedFiles.keySet()) {
			sortedFileList.append(key + " " + sortedFiles.get(key) + "\n");
		}
		return sortedFileList.toString();
	}

}
