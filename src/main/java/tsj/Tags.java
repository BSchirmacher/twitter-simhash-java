package tsj;
/**
 * 
 * @author Brian Schirmacher
 * 
 */
public class Tags implements Comparable<Tags> {

	private String text;
	private int count;

	public Tags(String text, int count) {
		this.text = text;
		this.count = count;
	}

	@Override
	public int compareTo(Tags other) {
		if (this.count < other.count) {
			return -1;
		} else if (this.count > other.count) {
			return 1;
		} else {
			return 0;
		}
	}

	public String getText() {
		return text;
	}

	public int getFreq() {
		return count;
	}

}
