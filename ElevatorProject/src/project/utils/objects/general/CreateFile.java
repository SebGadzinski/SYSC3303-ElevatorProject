package project.utils.objects.general;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CreateFile {
	private File file;
	private String fileName;

	public CreateFile(String filename) {
		this.fileName = filename;
		file = new File(this.fileName);
		try {
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			} else {
				file.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
