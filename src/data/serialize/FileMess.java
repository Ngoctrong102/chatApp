package data.serialize;

import java.io.*;

public class FileMess implements Serializable {
    public byte[] fileData;
    public int from;
    public int roomID;
    public String fileName;
    public FileMess(int from, int roomID, File file) throws IOException {
        this.from = from;
        this.roomID = roomID;
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        this.fileData = new byte[(int) file.length()];
        bis.read(fileData, 0, fileData.length);
        this.fileName = file.getName();
    }
}
