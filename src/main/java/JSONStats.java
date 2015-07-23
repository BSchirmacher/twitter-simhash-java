/**
 * @author Brian Schirmacher
 */

public class JSONStats {

	private MaxPQ<Tags> maxpq;

	/**
	 * Turn the Hashtable into a Max Priority Queue to find Most popular
	 * hashtags
	 * 
	 * @param jsonloader
	 *            - Object with Hashtable instance variable
	 */
	public JSONStats(JSONLoader jsonloader) {

		LinearProbingHashST<String, Integer> hashes = jsonloader
				.getHashTagTable();
		Tags tag;
		maxpq = new MaxPQ<Tags>();
		for (String text : hashes.keys()) {
			tag = new Tags(text, hashes.get(text));
			maxpq.insert(tag);
		}
	}

	public MaxPQ<Tags> getMaxPQ() {
		return maxpq;
	}
}