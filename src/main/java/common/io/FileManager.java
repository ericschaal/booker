package common.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Hashtable;
import java.util.List;


public class FileManager {

    private final Hashtable<String, Path> files = new Hashtable<>();


    public boolean openFile(String alias, String path, boolean forceCreate) throws IOException {
            Path loc = Paths.get(path);
            if (forceCreate) {
                if (Files.isRegularFile(loc)) {
                    Logger.print().warning("Overwriting filed + " + path);
                    Files.delete(loc);
                }
                files.put(alias, Files.createFile(loc));
                return true;
            } else {
                if (! Files.isRegularFile(loc)) {
                    Files.createFile(loc);
                }
                files.put(alias, loc);
                return true;
            }
    }

    public boolean write(String alias, String content) throws IOException {
        if (files.containsKey(alias)) {
            Files.write(files.get(alias), content.getBytes());
            return true;
        } else {
            Logger.print().error("File not opened.");
            return false;
        }
    }

    public boolean writeObject(String alias, Serializable obj) throws IOException {
        if (files.containsKey(alias)) {
            FileOutputStream fos = new FileOutputStream(new File(files.get(alias).toString()));
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(obj);

            oos.close();
            fos.close();

            return true;
        } else {
            Logger.print().error("File not opened.");
            return false;
        }
    }

    public Object readObject(String alias) throws IOException, ClassNotFoundException {
        if (files.containsKey(alias)) {
            FileInputStream fis = new FileInputStream(new File(files.get(alias).toString()));
            ObjectInputStream ois = new ObjectInputStream(fis);

            Object obj = ois.readObject();

            ois.close();
            fis.close();

            return obj;
        } else {
            Logger.print().error("File not opened.");
            throw new RuntimeException("No readable object in file");
        }
    }

    public List read(String alias) throws IOException {
        if (files.containsKey(alias)) {
            return Files.readAllLines(files.get(alias));
        } else {
            Logger.print().error("File not opened.");
            throw new RuntimeException("No readable file");
        }
    }

    public boolean append(String alias, String content) throws IOException {
        if (files.containsKey(alias)) {
            Files.write(files.get(alias), content.getBytes(), StandardOpenOption.APPEND);
            return true;
        } else {
            Logger.print().error("File not opened.");
            return false;
        }
    }

    public boolean appendLine(String alias, String content) throws IOException {
        return append(alias, "\n" + content + "\n");
    }

    public boolean removeFile(String alias) throws IOException {
        if (files.containsKey(alias)) {
            Files.delete(files.get(alias));
            files.remove(alias);
            return true;
        } else {
            Logger.print().error("File not opened.");
            return false;
        }
    }







}
