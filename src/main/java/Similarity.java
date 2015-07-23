import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JOptionPane;
import com.google.common.base.Charsets;

/**
 * 
 * @author Brian Schirmacher
 * 
 */
public class Similarity {
	public Similarity(int percentage, JSONLoader jsonloader) {
		
		Writer writer = null;
		int maxWrites = (int)TwitterSample.mSS.getValue();
		int hashBitSize = jsonloader.getHashSize();
		int windowSize = (int) TwitterSample.lookahead.getValue();
		/** Support for 32 bit hashing **/
		if (hashBitSize == 32) {

			try {

				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("Report.txt"), Charsets.UTF_8));

				byte max_dist = (byte) (hashBitSize - (int) ((double) (percentage / 100.0) * hashBitSize));

				LinearProbingHashST<Integer, String> simHashTable = jsonloader
						.getTweets32HashTable();

				int[] hashlist = new int[simHashTable.size()];
				Simhash sim = new Simhash(new BinaryWordSeg());
				long presort = System.currentTimeMillis();
				int k = 0;
				for (int i : simHashTable.keys()) {
					hashlist[k] = i;
					k++;
				}
				assert (hashlist.length == simHashTable.size());
				assert ((k - 1) == simHashTable.size());
				HashMap<String, HashSet<String>>[] simArray = new HashMap[4];
				byte hamD;
				byte a = 0, c = 1, d = 0;
				int b = 0, sims = 0, count = 0;
				for (a = 0; a < 4; a++) {
					simArray[a] = new HashMap<String, HashSet<String>>(5000);
					QuickNew.sort(hashlist);
		
					for (b = 0; b < hashlist.length - windowSize; b++) {					
						int firstHash = hashlist[b];
						HashSet<String> simStrings = null;
						sims = 0;
						for (c = 0; c < windowSize; c++) {
							hamD = (byte) sim.hammingDistance(firstHash,
									hashlist[b + c + 1]);
							if (hamD <= max_dist) {
								if (sims == 0) {
									simStrings = new HashSet<String>(windowSize);
									simStrings.add(simHashTable.get(hashlist[b
											+ c + 1]));
								} else {
									simStrings.add(simHashTable.get(hashlist[b
											+ c + 1]));
								}
								sims++;
								count++;
							}
						}
						if(simStrings != null){
							simArray[a]
									.put(simHashTable.get(firstHash), simStrings);
						}
						
					}
					for (int i = 0; i < hashlist.length; i++) {
						hashlist[i] = Integer.rotateLeft(hashlist[i], 4);
					}
				}
				
				assert ((b + c) == hashlist.length);
				long post = System.currentTimeMillis() - presort;

				JOptionPane.showMessageDialog(null, "Matching time = " + post
						+ "\nSize = " + hashlist.length + "\nMatches = "
						+ count);
				HashMap<String, HashSet<String>> finalCompare = condense(simArray);
				int written = 0;
				long preWrite = System.currentTimeMillis();
				for(String t : finalCompare.keySet()){
					writer.write(t + '\n');	
					if(written < maxWrites){
						TwitterSample.simHashPane.append(t + "\n");
					}
					for (String comp : finalCompare.get(t)) {
						writer.write("Similar :  " + comp + "\n");
						if(written < maxWrites){
							TwitterSample.simHashPane.append("Similar :  " + comp + "\n");
						}
						
					}
					writer.write("\n\n");
					if(written < maxWrites){
						TwitterSample.simHashPane.append("\n\n");
					}
					written++;
				}
				long postWrite = System.currentTimeMillis() - preWrite;
				
				JOptionPane.showMessageDialog(null, "Writing time = " + postWrite);
				
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Support for 64 bit hashing
		} else if (hashBitSize == 64) {

			try {
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("Report.txt"), Charsets.UTF_8));

				byte max_dist = (byte) (hashBitSize - (int) ((percentage / 100.0) * hashBitSize));

				LinearProbingHashST<Long, String> simHashTable = jsonloader
						.getTweets64HashTable();

				long[] hashlist = new long[simHashTable.size()];
				Simhash sim = new Simhash(new BinaryWordSeg());
				long presort = System.currentTimeMillis();
				int k = 0;
				for (long i : simHashTable.keys()) {
					hashlist[k] = i;
					k++;
				}
				assert (hashlist.length == simHashTable.size());
				assert ((k - 1) == simHashTable.size());
				HashMap<String, HashSet<String>>[] simArray = new HashMap[4];
				byte hamD;
				byte a = 0, c = 1, d = 0;
				int b = 0, sims = 0, count = 0;

				for (a = 0; a < 4; a++) {

					simArray[a] = new HashMap<String, HashSet<String>>(5000);
					QuickNew.sort(hashlist);
					
					for (b = 0; b < hashlist.length - windowSize; b++) {
						long firstHash = hashlist[b];
						HashSet<String> simStrings = null;
						sims = 0;
						for (c = 0; c < windowSize; c++) {
							hamD = (byte) sim.hammingDistance(firstHash,
									hashlist[b + c + 1]);
							if (hamD <= max_dist) {
								if (sims == 0) {
									simStrings = new HashSet<String>(windowSize);
									simStrings.add(simHashTable.get(hashlist[b
											+ c + 1]));
								} else {
									simStrings.add(simHashTable.get(hashlist[b
											+ c + 1]));
								}
								sims++;
								count++;
							}
						}
						if(simStrings != null){
							simArray[a]
									.put(simHashTable.get(firstHash), simStrings);
						}
						
					}
					for (int i = 0; i < hashlist.length; i++) {
						hashlist[i] = Long.rotateLeft(hashlist[i], 6);
					}
				}
				assert ((b + c) == hashlist.length);
				long post = System.currentTimeMillis() - presort;
				JOptionPane.showMessageDialog(null, "Matching time = " + post
						+ "\nSize = " + hashlist.length + "\nMatches = "
						+ count);
				HashMap<String, HashSet<String>> finalCompare = condense(simArray);
				int written = 0;
				long preWrite = System.currentTimeMillis();
				for(String t : finalCompare.keySet()){
					writer.write(t + '\n');	
					if(written < maxWrites){
						TwitterSample.simHashPane.append(t + "\n");
					}
					for (String comp : finalCompare.get(t)) {
						writer.write("Similar :  " + comp + "\n");
						if(written < maxWrites){
							TwitterSample.simHashPane.append("Similar :  " + comp + "\n");
						}
						
					}
					writer.write("\n\n");
					if(written < maxWrites){
						TwitterSample.simHashPane.append("\n\n");
					}
					written++;
				}
				long postWrite = System.currentTimeMillis() - preWrite;

				JOptionPane.showMessageDialog(null, "Writing time = " + postWrite);

				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// TODO add support 128 bit hashing
			JOptionPane
					.showMessageDialog(
							null,
							"Sorry,  this application does\nnot yet support 128 bit hashing",
							"128 bit hashing unsupported",
							JOptionPane.ERROR_MESSAGE, null);
		}

	}

	private HashMap<String, HashSet<String>> condense(
			HashMap<String, HashSet<String>>[] simArray) {
		int s = 0, q;
		long startC = System.currentTimeMillis();
		for (int i = 0; i < simArray.length; i++) {
			s += simArray[i].size();
		}
		q = s;
		s = (int) (s * 0.5);
		HashMap<String, HashSet<String>> returnMap = new HashMap<String, HashSet<String>>(
				s);
		HashSet<String> fin;
		HashSet<String> temp;
		for (int i = 0; i < simArray.length; i++) {

			for (String z : simArray[i].keySet()) {
				if (!returnMap.containsKey(z)) {
					returnMap.put(z, simArray[i].get(z));
				} else {
					if (simArray[i].get(z) != null) {
						temp = simArray[i].get(z);
						fin = new HashSet<String>();
						fin.addAll(temp);
						returnMap.put(z, fin);
					}
				}
			}
		}
		int size = 0;
		for (HashSet<String> k : returnMap.values()) {
			size += k.size();
		}
		long endC = System.currentTimeMillis() - startC;
		JOptionPane.showMessageDialog(null, "Condense Time = " + endC
				+ "\nPreCondense Size = " + q + "\nPost Condense Size = "
				+ returnMap.size() + "\nMatches = " + size, "Condense Stats",
				JOptionPane.INFORMATION_MESSAGE, null);
		return returnMap;
	}

}