package nandini.bayes;

import java.io.FilterInputStream;
import java.io.IOException;
import java.util.Scanner;

public class UserInteraction {

	public static Scanner scanner = new Scanner(new FilterInputStream(System.in) {
		@Override
		public void close() throws IOException {
			// don't close System.in!
		}
	});

}
