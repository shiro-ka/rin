package com.rin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Rin {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<String> DEFAULT_IMAGE_EXTS = Arrays.asList("png", "jpg", "jpeg", "webp", "gif", "bmp");

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String targetPathStr = ".";
        int nameLength = 12;
        List<String> targetExtensions = new ArrayList<>();
        boolean allSpecified = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("all")) {
                allSpecified = true;
                continue;
            }

            // Check if it's a number (length)
            if (arg.matches("\\d+")) {
                int len = Integer.parseInt(arg);
                if (len >= 2) {
                    nameLength = len;
                    continue;
                }
            }

            // Check if it's a path
            File file = new File(arg);
            if (file.exists()) {
                targetPathStr = arg;
                continue;
            }

            // Otherwise treat as extension
            targetExtensions.add(arg.toLowerCase());
        }

        // If no extensions specified, use defaults
        if (targetExtensions.isEmpty()) {
            targetExtensions.addAll(DEFAULT_IMAGE_EXTS);
        }

        executeRename(targetPathStr, nameLength, targetExtensions);
    }

    private static void executeRename(String pathStr, int length, List<String> extensions) {
        Path path = Paths.get(pathStr);
        File target = path.toFile();

        if (!target.exists()) {
            System.err.println("Error: Path does not exist: " + pathStr);
            return;
        }

        if (target.isFile()) {
            // If it's a single file, rename it regardless of extension?
            // User said: "そのパスのディレクトリの中orそのファイルに対して"
            renameFile(target, length);
        } else if (target.isDirectory()) {
            File[] files = target.listFiles();
            if (files == null) return;

            int count = 0;
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    int lastDotIndex = fileName.lastIndexOf('.');
                    if (lastDotIndex > 0) {
                        String ext = fileName.substring(lastDotIndex + 1).toLowerCase();
                        if (extensions.contains(ext)) {
                            if (renameFile(file, length)) {
                                count++;
                            }
                        }
                    }
                }
            }
            System.out.println("Renamed " + count + " files in " + target.getAbsolutePath());
        }
    }

    private static boolean renameFile(File file, int length) {
        String originalName = file.getName();
        String ext = "";
        int lastDotIndex = originalName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            ext = originalName.substring(lastDotIndex);
        }

        String newName;
        File newFile;
        int attempts = 0;
        do {
            newName = generateRandomString(length) + ext;
            newFile = new File(file.getParent(), newName);
            attempts++;
        } while (newFile.exists() && attempts < 100);

        if (newFile.exists()) {
            System.err.println("Could not generate a unique name for: " + originalName);
            return false;
        }

        if (file.renameTo(newFile)) {
            System.out.println(originalName + " -> " + newName);
            return true;
        } else {
            System.err.println("Failed to rename: " + originalName);
            return false;
        }
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private static void printUsage() {
        System.out.println("Usage: rin [all] [path] [length] [extension...]");
        System.out.println("  all: Rename image files in current directory (12 chars)");
        System.out.println("  path: Target directory or file");
        System.out.println("  length: Length of random name (integer >= 2)");
        System.out.println("  extension: Target file extensions (e.g. png, txt)");
    }
}
