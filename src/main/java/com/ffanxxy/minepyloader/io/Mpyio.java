package com.ffanxxy.minepyloader.io;

import com.ffanxxy.minepyloader.Minepyloader;
import io.netty.util.concurrent.CompleteFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Mpyio {
    /**
     * 在指定路径创建文件
     * <p>Create a file in the specified path
     * @param path
     * <p>文件目录，必须是一个文件</p>
     * <p>File Directory, must be a file</p>
     * @return
     * <p> : 1 成功创建 Successfully created
     * <p> : 0 文件已存在 File already exists
     * <p> :-1 创建目录时发生错误 An error occurred when creating the directory
     * <p> :-2 创建文件时发生错误 An error occurred when creating the file
     */
    public static int create(@NotNull Path path){
        File file = path.toFile();
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                Minepyloader.LOGGER.error("Minepyloader can't create directories: " + path.toString()
                                            + " because IOException: " + e.getMessage());
                e.fillInStackTrace();
                return -1;
            }
        }
        // 创建文件
            try {
                if(file.createNewFile()) {
                    return 1;
                } else {
                    return 0;
                }
            }catch (IOException e) {
                Minepyloader.LOGGER.error("Minepyloader can't create file: " + path.toString()
                        + " because IOException: " + e.getMessage());
                e.fillInStackTrace();
                return -2;
            }
    }

    public static int createDir(@NotNull Path path) {
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                return 1;
            } catch (IOException e) {
                e.fillInStackTrace();
                return -1;
            }
        } else {
            return 0;
        }
    }

    public static void writeAndReplace(@NotNull Path path, @NotNull List<String> lines){
        try {
            Files.writeString(
                    path,
                    String.join("\n", lines),
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    /**
     * 异步实现 {@link #create(Path)}
     * @return 异步结果，详见{@link #create(Path)}
     */
    public static @NotNull CompletableFuture<Integer> createAsync(Path path) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        result.completeAsync(() -> create(path));
        return result;
    }

    /**
     * 读取文件每一行作为列表
     * @param path 文件目录，必须存在目录
     * @return 文件内容，若发生错误或文件不存在，则返回null
     */
    public static @Nullable List<String> read(@NotNull Path path) {
        if(!Files.exists(path)) return null;

        List<String> lines = new ArrayList<>();
        try(BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.fillInStackTrace();
            return null;
        }
        return lines;
    }

    /**
     * 异步读取文件，详见{@link #read(Path)}
     */
    public static @NotNull CompletableFuture<List<String>> readAsync(Path path) {
        CompletableFuture<List<String>> result = new CompletableFuture<>();
        result.completeAsync(() -> read(path));
        return result;
    }

    public static List<String> readOrCreate(@NotNull Path path, List<String> context) {
        if(!Files.exists(path)) {
            create(path);
            writeAndReplace(path, context);
        }
        List<String> lines = read(path);
        return Objects.requireNonNullElseGet(lines, ArrayList::new);
    }

    /**
     * 读取目录下所有文件及其子文件
     * @param path 路径
     * @param files 存放处
     */
    public static void getAllFiles(Path path, List<File> files) {
        File[] fileList = path.toFile().listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                getAllFiles(file.toPath(), files);
            } else {
                files.add(file);
            }
        }
    }
}
