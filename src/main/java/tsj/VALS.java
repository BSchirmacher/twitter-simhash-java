package tsj;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * 
 * @author Brian Schirmacher
 * 
 */
public class VALS {
	private final static Dimension screenSize = Toolkit.getDefaultToolkit()
			.getScreenSize();
	private static final double sW = screenSize.getWidth();
	private static final double sH = screenSize.getHeight();
	public static final Dimension size = new Dimension((int) (sW * 0.9),
			(int) (sH * 0.9));
	public static final String path = "/home/user/Sampling/JSON_FILES/";
	public static final String reportpath = "/home/user/Sampling/Report/";
}
