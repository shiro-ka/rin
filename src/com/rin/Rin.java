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
        // 引数がなかったときに、つかいかたを表示
        if (args.length == 0) {
            printUsage();
            return;
        }

        String targetPathStr = ".";
        int nameLength = 12;
        List<String> targetExtensions = new ArrayList<>();
        boolean allSpecified = false;

        // 引数チェック
        for (String arg : args) {
            if (arg.equalsIgnoreCase("all")) {
                allSpecified = true;
                continue;
            }

            if (arg.matches("\\d+")) {
                int len = Integer.parseInt(arg);
                if (len >= 2) {
                    nameLength = len;
                    continue;
                }
            }

            File file = new File(arg);
            if (file.exists()) {
                targetPathStr = arg;
                continue;
            }

            targetExtensions.add(arg.toLowerCase());
        }

        // 拡張子の指定がないときは、デフォルトの拡張子をつかう
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
        System.out.println("つかいかた");
        System.out.println(" rin all: 現在のディレクトリ内の画像ファイルをランダムな名前に変更します。12文字のランダムな名前に変更します。");
        System.out.println("引数: rin [path] [length] [extension...]");
        System.out.println(" path: 対象のディレクトリまたはファイル");
        System.out.println(" length: ランダムな名前の文字数 (整数 >= 2)");
        System.out.println(" extension: 対象のファイルの拡張子");
    }
}
