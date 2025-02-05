import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainClass {

	public static void main(String args[]) {
		FileInputStream fIn;
		FileOutputStream fOut;
		FileChannel fIChan, fOChan;
		long fSize;
		MappedByteBuffer mBuf;

		try {
			//fIn = new FileInputStream(args[0]);
			//fOut = new FileOutputStream(args[1]);
			fIn = new FileInputStream("C:\\temp\\test.doc");
			fOut = new FileOutputStream("C:\\temp\\test222.doc");

			fIChan = fIn.getChannel();
			fOChan = fOut.getChannel();

			fSize = fIChan.size();

			mBuf = fIChan.map(FileChannel.MapMode.READ_ONLY, 0, fSize);

			fOChan.write(mBuf); // this copies the file

			fIChan.close();
			fIn.close();

			fOChan.close();
			fOut.close();
		} catch (IOException exc) {
			//System.out.println(exc);
			System.exit(1);
		} catch (ArrayIndexOutOfBoundsException exc) {
			//System.out.println("Usage: Copy from to");
			System.exit(1);
		}
	}
}
