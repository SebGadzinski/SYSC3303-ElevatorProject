package project.utils.objects.general;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CreateFile {

    private final String fileName;

    public CreateFile(String filename) {
        this.fileName = filename;
        File file = new File(this.fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String message) {
        try {
            FileWriter writer = new FileWriter(this.fileName, true);
            BufferedWriter bufWriter = new BufferedWriter(writer);
            PrintWriter pw = new PrintWriter(bufWriter);
            pw.println(message);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
