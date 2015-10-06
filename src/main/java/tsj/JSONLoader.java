package tsj;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import javax.swing.JOptionPane;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

/**
 * 
 * @author Brian Schirmacher
 * 
 */
public class JSONLoader {

	private String loadedFile, filepath;
	private long elapsed;
	private int count, tweets, deletes, users, retweets, hashtags, hashsize;
	private LinearProbingHashST<String, Integer> HTST;
	private LinearProbingHashST<Integer, String> Tweets32;
	private LinearProbingHashST<Long, String> Tweets64;
	private LinearProbingHashST<String, String> Tweets128;

	/**
	 * Loads the given JSON file if possible, and collects specified data from
	 * the files
	 * 
	 * @param filename
	 */
	public JSONLoader(String filename) {

		hashsize = (int) TwitterSample.hashSpinner.getValue();
		count = 0;
		tweets = 0;
		deletes = 0;
		retweets = 0;
		hashtags = 0;
		users = 0;

		filepath = VALS.path + filename;
		File f = new File(filepath);
		if (f.exists()) {
			loadedFile = filename;
		}

		GZIPInputStream gzip;
		JsonParser parser = new JsonParser();
		LinearProbingHashST<String, Integer> usersST = new LinearProbingHashST<String, Integer>(
				3000000);
		HTST = new LinearProbingHashST<String, Integer>(300000);
		if (hashsize == 32) {
			Tweets32 = new LinearProbingHashST<Integer, String>(3500000);
		} else if (hashsize == 64) {
			Tweets64 = new LinearProbingHashST<Long, String>(3500000);
		} else if (hashsize == 128) {
			// support for 128bit MD5 hashing is not available yet
			Tweets128 = new LinearProbingHashST<String, String>(3500000);
		}

		Simhash sim = new Simhash(new BinaryWordSeg());
		long start = System.currentTimeMillis();
		JsonObject json, user, entities, HT;
		JsonArray HTArray;
		String ID = null, ht_text, tweet = null, text;
		String simHash128Hash;
		long simhash64Hash;
		int simhash32Hash;

		try {
			gzip = new GZIPInputStream(new FileInputStream(filepath));
			InputStreamReader decoder = new InputStreamReader(gzip,
					Charset.defaultCharset());
			BufferedReader input = new BufferedReader(decoder);

			while (input.ready()) {
				json = (JsonObject) parser.parse(input.readLine());
				count++;
				if (json.has("delete")) {
					deletes++;
				} else if (json.has("created_at")) {
					tweets++;
					if (json.has("retweeted_status")) {
						retweets++;
					}

					if (json.has("user")) {
						user = json.getAsJsonObject("user");
						if (user.has("id_str")) {
							ID = user.get("id_str").toString();
							if (usersST.contains(ID)) {
								usersST.put(ID, usersST.get(ID) + 1);
							} else {
								usersST.put(ID, 1);
							}
						}
					}

					if (json.has("entities")) {
						entities = (JsonObject) json.get("entities");
						if (entities.has("hashtags")) {
							HTArray = (JsonArray) entities.get("hashtags");
							if (HTArray.size() > 0) {
								for (int i = 0; i < HTArray.size(); i++) {
									HT = (JsonObject) HTArray.get(i);
									ht_text = HT.get("text").toString();
									if (HTST.contains(ht_text)) {
										HTST.put(ht_text, HTST.get(ht_text) + 1);
									} else {
										HTST.put(ht_text, 1);
									}
								}
							}
						}
					}
					if (json.has("text")) {
						text = json.get("text").getAsString();
						tweet = text.replaceAll("\\s+", " ");
						if (hashsize == 64) {
							simhash64Hash = sim.simhash64(tweet);
							if (Tweets64.contains(simhash64Hash)) {
							} else {
								Tweets64.put(simhash64Hash, tweet);
							}
						} else if (hashsize == 32) {
							simhash32Hash = sim.simhash32(tweet);
							if (Tweets32.contains(simhash32Hash)) {
							} else {
								Tweets32.put(simhash32Hash, tweet);
							}
						} else if (hashsize == 128) {
							// TODO add 128 bit MD5 support for simhashing
							JOptionPane
									.showMessageDialog(
											null,
											"Sorry,  this application does\nnot yet support 128 bit hashing\nPlease reload the file with either\n64 or 32 bit hash",
											"128 bit hashing unsupported",
											JOptionPane.ERROR_MESSAGE, null);
							break;
						}
					}
				}
			}
			input.close();
			decoder.close();
			gzip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		hashtags = HTST.size();
		users = usersST.size();
		elapsed = System.currentTimeMillis() - start;

	}

	public int getHashSize() {
		return hashsize;
	}

	public String getLoadedFile() {
		return loadedFile;
	}

	public int getCount() {
		return count;
	}

	public int getTweetsCount() {
		return tweets;
	}

	public int getDeletesCount() {
		return deletes;
	}

	public int getRetweetsCount() {
		return retweets;
	}

	public int getHashtagCount() {
		return hashtags;
	}

	public int getUsersCount() {
		return users;
	}

	public boolean check() {
		if (tweets + deletes == count) {
			return true;
		} else {
			return false;
		}
	}

	public double deletePercentage() {
		return ((double) deletes / (double) count) * 100.0;
	}

	public double tweetsPercentage() {
		return ((double) tweets / (double) count) * 100.0;
	}

	public double retweetPercentageTotal() {
		return ((double) retweets / (double) count) * 100.0;
	}

	public double retweetPercentageOfTweets() {
		return ((double) retweets / (double) tweets) * 100.0;
	}

	public double getTime() {
		return elapsed / 1000.0;
	}

	public LinearProbingHashST<String, Integer> getHashTagTable() {
		return HTST;
	}

	public LinearProbingHashST<Long, String> getTweets64HashTable() {
		return Tweets64;
	}

	public LinearProbingHashST<Integer, String> getTweets32HashTable() {
		return Tweets32;
	}

	public LinearProbingHashST<String, String> getTweets128HashTable() {
		return Tweets128;
	}
}