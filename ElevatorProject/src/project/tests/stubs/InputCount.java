package project.tests.stubs;

import static project.Config.REQUEST_BATCH_FILENAME;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;

public class InputCount {

	private Scanner scanner;
	private int ya;

	public InputCount() {
		try {
			this.scanner = new Scanner(new File(Paths.get(REQUEST_BATCH_FILENAME).toAbsolutePath().toString()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ya = 0;
	}

	public int inputRead() {

		boolean isThereAnotherRequest = true;

		while (isThereAnotherRequest == this.scanner.hasNext()) {
			ya++;
			this.scanner.nextLine();
		}

		return ya;
	}

	public static void main(String[] args) {
		InputCount test = new InputCount();
		System.out.println(test.inputRead());
	}
}
