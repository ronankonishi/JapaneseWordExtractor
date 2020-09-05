import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFile {

    private String filePath;
    private String destFolder;

    public ZipFile(String filePath) {
        this.filePath = filePath;
    }

    public ZipFile(String filePath, String destFolder) {
        this.filePath = filePath;
        this.destFolder = destFolder;
    }

    public String unzip() {
        if (filePath == null) return null;
        if (destFolder == null) destFolder = getDirPath(filePath);

        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath));
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File zipEntryFile = new File(destFolder, zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    zipEntryFile.mkdir();
                } else {
                    zipEntryFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(zipEntryFile);

                    byte buffer[] = new byte[1024];
                    int count = zis.read(buffer, 0, buffer.length);

                    while (count != -1) {
                        fos.write(buffer, 0, count);
                        count = zis.read(buffer, 0, buffer.length);
                    }
                    fos.flush();
                    fos.close();
                    zis.closeEntry();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String destFilePath = destFolder + "\\" + getFileName(filePath);

        return destFilePath;
    }

    public boolean delete() {
        return new File(filePath).delete();
    }

    private String getDirPath(String filePath) {
        String[] sections = filePath.split("\\\\");
        String dirPath = sections[0];
        for (int i = 1; i < sections.length-1; i++) {
            dirPath = dirPath + "\\" + sections[i];
        }
        return dirPath;
    }

    private String getFileName(String filePath) {
        String[] sections = filePath.split("/");
        return sections[sections.length-1];
    }

}