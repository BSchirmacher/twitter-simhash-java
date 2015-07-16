import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import javax.swing.*;

/**
 * 
 * @author Brian Schirmacher
 * 
 */
public class TwitterSample extends JPanel {

	private static final long serialVersionUID = 2253712637775753507L;

	private static JComboBox<String> comboBox;
	private static JButton simHashButton, loadStats;
	private static JToggleButton button;
	public static JSpinner range, hashSpinner, lookahead, mSS;
	private static JTextArea info, hash;
	public static JTextArea simHashPane;
	private static final int sidePanelWidth = (int) (VALS.size.width * 0.25);
	private static final int hashPanelWidth = (int) (VALS.size.width * 0.75);
	private static final int loadingPanelHeight = (int) (VALS.size.height * 0.20);
	private static final int textHeight = (int) (VALS.size.height * 0.95);
	private static final int loadButtonHeight = (int) (VALS.size.height * 0.05);
	static boolean loadedfile = false;
	private static JSONLoader jsonloader;
	private static boolean daily = false;

	private static void addMenu(JFrame window) {
		JMenuBar menu = new JMenuBar();
		menu.setOpaque(true);
		menu.setBackground(Color.GRAY);
		menu.setPreferredSize(new Dimension(VALS.size.width, 25));
		window.setJMenuBar(menu);
	}

	private static JPanel getFileOptionsPanel() {

		JPanel optionsLoad = new JPanel();

		int subHeight = (int) (loadingPanelHeight / 6);
		Dimension d = new Dimension(sidePanelWidth, loadingPanelHeight);
		Dimension sub = new Dimension(sidePanelWidth, subHeight);
		Dimension sub3 = new Dimension((int) (sidePanelWidth * 0.5), subHeight);

		optionsLoad.setLayout(new GridBagLayout());
		GridBagConstraints T = new GridBagConstraints();
		optionsLoad.setSize(d);
		optionsLoad.setPreferredSize(d);
		optionsLoad.setMaximumSize(d);
		optionsLoad.setMinimumSize(d);
		optionsLoad.setBackground(Color.LIGHT_GRAY);
		optionsLoad.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

		JLabel title = new JLabel("Options");
		title.setHorizontalAlignment(0);
		title.setVerticalAlignment(0);
		title.setSize(sub);
		title.setPreferredSize(sub);
		title.setMaximumSize(sub);
		title.setMinimumSize(sub);
		title.setFont(getLargestFont(title));

		button = new JToggleButton();
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (button.isSelected()) {
					daily = true;
					String[] files = getFiles();
					comboBox.setModel(new javax.swing.DefaultComboBoxModel<String>(
							files));
					button.setSelected(daily);
					button.setText("Hourly Files Showing");
				} else {
					daily = false;
					String[] files = getFiles();
					comboBox.setModel(new javax.swing.DefaultComboBoxModel<String>(
							files));
					button.setSelected(daily);
					button.setText("Daily Files Showing");
				}
			}
		});

		button.setSelected(daily);
		if (button.isSelected()) {
			button.setText("Hourly Files Showing");
		} else {
			button.setText("Daily Files Showing");
		}
		button.setSize(sub);
		button.setMaximumSize(sub);
		button.setMinimumSize(sub);
		button.setPreferredSize(sub);

		JLabel comboLabel = new JLabel("Files available to load:");
		comboLabel.setHorizontalAlignment(0);
		comboLabel.setVerticalAlignment(0);
		comboLabel.setSize(sub);
		comboLabel.setPreferredSize(sub);
		comboLabel.setMaximumSize(sub);
		comboLabel.setMinimumSize(sub);
		comboLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
		comboLabel.setToolTipText("Most recent file is listed first");

		String[] names = getFiles();

		comboBox = new JComboBox<String>();
		comboBox.setModel(new javax.swing.DefaultComboBoxModel<String>(names));
		comboBox.setSize(sub);
		comboBox.setPreferredSize(sub);
		comboBox.setMaximumSize(sub);
		comboBox.setMinimumSize(sub);
		comboBox.setEditable(false);
		comboBox.setSelectedItem(null);
		comboBox.setMaximumRowCount(25);
		comboBox.setToolTipText("Most recent file is listed first");

		JLabel spinLabel = new JLabel("Choose Hash Size =>");
		spinLabel.setSize(sub3);
		spinLabel.setPreferredSize(sub3);
		spinLabel.setMaximumSize(sub3);
		spinLabel.setMinimumSize(sub3);
		spinLabel.setFont(getLargestFont(spinLabel));

		Integer[] list = { 32, 64, 128 };
		SpinnerListModel r = new SpinnerListModel(list);
		r.setValue(list[1]);
		hashSpinner = new JSpinner(r);
		hashSpinner.setSize(sub3);
		hashSpinner.setMinimumSize(sub3);
		hashSpinner.setMaximumSize(sub3);
		hashSpinner.setPreferredSize(sub3);
		hashSpinner.setFont(getLargestFont(hashSpinner));

		JButton load = new JButton();
		load.setSize(sub);
		load.setPreferredSize(sub);
		load.setMaximumSize(sub);
		load.setMinimumSize(sub);
		load.setText("Load selected file");
		load.setToolTipText("Select file from list and press to load");
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedFile = (String) comboBox.getSelectedItem();
				int confirm = JOptionPane.showConfirmDialog(null,
						"Selected file: \n" + selectedFile
								+ "\nLoad this file?", "Confirm Load File",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null);
				if (confirm == JOptionPane.YES_OPTION && selectedFile != null) {
					loadGZIP(selectedFile);
				} else if (confirm == JOptionPane.YES_OPTION
						&& selectedFile == null) {
					JOptionPane
							.showMessageDialog(
									null,
									"No file selected to load\nAborting load operation ",
									"Aborting Load",
									JOptionPane.INFORMATION_MESSAGE, null);
				} else {
					JOptionPane.showMessageDialog(null,
							"Skipped loading\nSelect another file",
							"Do Not Load", JOptionPane.INFORMATION_MESSAGE,
							null);
				}
			}
		});
		T.fill = GridBagConstraints.BOTH;
		T.gridx = 0;
		T.gridy = 0;
		T.weightx = 0.5;
		T.weighty = 0.5;
		T.gridwidth = 2;
		optionsLoad.add(title, T);
		T.gridx = 0;
		T.gridy = 1;
		optionsLoad.add(comboLabel, T);
		T.gridx = 0;
		T.gridy = 2;
		optionsLoad.add(button, T);
		T.gridx = 0;
		T.gridy = 3;
		optionsLoad.add(comboBox, T);
		T.gridy = 4;
		T.gridx = 0;
		T.gridwidth = 1;
		optionsLoad.add(spinLabel, T);
		T.gridy = 4;
		T.gridx = 1;
		T.gridwidth = 1;
		optionsLoad.add(hashSpinner, T);
		T.gridx = 0;
		T.gridy = 5;
		T.gridwidth = 2;
		optionsLoad.add(load, T);
		return optionsLoad;
	}

	private static void loadGZIP(String selectedFile) {
		String fp;

		if (!daily) {
			fp = "Daily/" + selectedFile;
		} else {
			fp = selectedFile;
		}

		jsonloader = new JSONLoader(fp);
		Runtime.getRuntime().gc();
		String t;
		char c = '%';
		if (jsonloader.getLoadedFile() != null) {

			loadedfile = true;
			loadStats.setEnabled(true);
			simHashButton.setEnabled(true);
			range.setEnabled(true);
			hashSpinner.setEnabled(true);
			mSS.setEnabled(true);
			lookahead.setEnabled(true);

			t = String.format(
					"Success, a file is loaded! Loading time = %4.3f secs\n"
							+ "%-30s\t:%s\n" + "%-30s\t: %d\n"
							+ "%-30s\t: %d\t(%3.3f)%c\n"
							+ "%-30s\t: %d\t(%3.3f)%c\n"
							+ "%-29s\t: %d\t(%3.3f)%c\n"
							+ "%-30s\t: %d\t(%3.3f)%c\n" + "%-30s\t: %d\n"
							+ "%-30s: %d\n" + "%s + %s = %s\t: %s",
					jsonloader.getTime(), "Loaded File",
					jsonloader.getLoadedFile(), "Total Feeds",
					jsonloader.getCount(), "Total Deletes",
					jsonloader.getDeletesCount(),
					jsonloader.deletePercentage(), c, "Total Tweets",
					jsonloader.getTweetsCount(), jsonloader.tweetsPercentage(),
					c, "Total ReTweets (of Total)",
					jsonloader.getRetweetsCount(),
					jsonloader.retweetPercentageTotal(), c,
					"Retweet % of Tweets", jsonloader.getRetweetsCount(),
					jsonloader.retweetPercentageOfTweets(), c,
					"Total Different Users", jsonloader.getUsersCount(),
					"Total Different Hashtags", jsonloader.getHashtagCount(),
					"Tweets", "Deletes", "Total", jsonloader.check());
		} else {
			t = null;
			JOptionPane.showMessageDialog(null, "Could not load file",
					"Error loading file", JOptionPane.ERROR_MESSAGE, null);
		}

		String head1 = "     |                   Hashtag Text    [Count]\n";
		String lines1 = "";
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				lines1 = lines1.concat(String.format("%-4d:\n", i));
			} else {
				lines1 = lines1.concat(String.format("%-3d:\n", i));
			}

		}
		String finalS1 = head1 + lines1;
		info.setText(t);
		hash.setText(finalS1);
		simHashPane.setText("");
	}

	private static Font getLargestFont(JLabel title) {

		Font labelFont = title.getFont();
		String labelText = title.getText();
		int stringWidth = title.getFontMetrics(labelFont)
				.stringWidth(labelText);
		int componentWidth = title.getWidth();
		double widthRatio = (double) componentWidth / (double) stringWidth;
		int newFontSize = (int) (labelFont.getSize() * widthRatio);
		int componentHeight = title.getHeight();
		int fontSizeToUse = Math.min(newFontSize, componentHeight);
		return new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse);

	}

	private static Font getLargestFont(JSpinner title) {

		Font labelFont = title.getFont();
		String labelText = title.getValue().toString();
		int stringWidth = title.getFontMetrics(labelFont)
				.stringWidth(labelText);
		int componentWidth = title.getWidth();
		double widthRatio = (double) componentWidth / (double) stringWidth;
		int newFontSize = (int) (labelFont.getSize() * widthRatio * 0.8);
		int componentHeight = (int) (title.getHeight() * 0.8);
		int fontSizeToUse = Math.min(newFontSize, componentHeight);
		return new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse);
	}

	private static Component loadedFileInfo() {
		JPanel infoPanel = new JPanel();

		int subHeight = (int) (loadingPanelHeight / 8);
		Dimension d = new Dimension(sidePanelWidth, loadingPanelHeight);
		Dimension sub1 = new Dimension(sidePanelWidth, subHeight);
		Dimension sub2 = new Dimension(sidePanelWidth, 7 * subHeight);

		infoPanel.setLayout(new BorderLayout());
		infoPanel.setSize(d);
		infoPanel.setPreferredSize(d);
		infoPanel.setMaximumSize(d);
		infoPanel.setMinimumSize(d);
		infoPanel.setBackground(Color.LIGHT_GRAY);
		infoPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

		JLabel infoLabel = new JLabel("Loaded file Info");
		infoLabel.setPreferredSize(sub1);
		infoLabel.setMaximumSize(sub1);
		infoLabel.setMinimumSize(sub1);
		infoLabel.setSize(sub1);
		infoLabel.setHorizontalAlignment(0);
		infoLabel.setVerticalAlignment(0);

		info = new JTextArea();
		info.setPreferredSize(sub2);
		info.setSize(sub2);
		info.setMaximumSize(sub2);
		info.setMinimumSize(sub2);
		info.setEditable(false);
		String s = String
				.format("There is no file loaded currently\n%-30s\t:  %s\n%-30s\t:  %d\n%-30s\t:  %d\n%-30s\t:  %d\n%-30s\t:  %d\n%-30s\t:  %d\n%-30s:  %d\n",
						"Loaded File", null, "Total Feeds", 0, "Total Deletes",
						0, "Total Tweets", 0, "Total ReTweets", 0,
						"Total Different Users", 0, "Total Different Hashtags",
						0);
		info.setText(s);

		infoPanel.add(infoLabel, BorderLayout.BEFORE_FIRST_LINE);
		infoPanel.add(info);

		return infoPanel;
	}

	private static String[] getFiles() {
		String[] filesList, files;
		ArrayList<String> list = new ArrayList<String>();
		File dir;
		if (daily) {
			dir = new File("/home/user/Sampling/JSON_FILES");
			if (dir.isDirectory()) {
				files = dir.list();
				for (String file : files) {
					if (file.endsWith(".json.gz")) {
						list.add(file);
					}
				}
			}
			Collections.sort(list);
			filesList = new String[list.size()];
			int i = list.size() - 1;
			for (String string : list) {
				filesList[i] = string;
				i--;
			}
			return filesList;
		} else {
			dir = new File("/home/user/Sampling/JSON_FILES/Daily");
			if (dir.isDirectory()) {
				files = dir.list();
				for (String file : files) {
					if (file.endsWith(".json.gz")) {
						list.add(file);
					}
				}
			}
			Collections.sort(list);
			filesList = new String[list.size()];
			int i = list.size() - 1;
			for (String string : list) {
				filesList[i] = string;
				i--;
			}
			return filesList;
		}
	}

	private static Component contentCounts() {
		JPanel contents = new JPanel();
		contents.setLayout(new GridBagLayout());
		int cHeight = (int) (VALS.size.height * 0.60);
		int subHeight = (int) (cHeight / 24);
		Dimension d = new Dimension(sidePanelWidth, cHeight);
		Dimension sub1 = new Dimension(sidePanelWidth, subHeight);
		Dimension sub2 = new Dimension(sidePanelWidth, 18 * subHeight);
		Dimension sub3 = new Dimension((int) (sidePanelWidth * 0.5), subHeight);
		contents.setPreferredSize(d);
		contents.setMaximumSize(d);
		contents.setMinimumSize(d);
		contents.setSize(d);
		GridBagConstraints gbc = new GridBagConstraints();
		contents.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

		loadStats = new JButton("Load File Statistics");
		loadStats.setSize(sub1);
		loadStats.setPreferredSize(sub1);
		loadStats.setMaximumSize(sub1);
		loadStats.setMinimumSize(sub1);
		loadStats.setToolTipText("Press to load hashtags and terms");

		if (!loadedfile) {
			loadStats.setEnabled(false);
		}

		loadStats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent f) {

				JSONStats stats = new JSONStats(jsonloader);
				MaxPQ<Tags> mpq = stats.getMaxPQ();
				Tags tag;
				String updatehead1 = "     |                   Hashtag Text    [Count]\n";
				String lineupdate1 = "";
				for (int i = 1; i <= 30; i++) {
					if (!mpq.isEmpty()) {
						tag = mpq.delMax();
						if (i < 10) {
							lineupdate1 = lineupdate1.concat(String.format(
									"%-4d: #%s    [%6d]\n", i, tag.getText()
											.replaceAll("\"", ""), tag
											.getFreq()));
						} else {
							lineupdate1 = lineupdate1.concat(String.format(
									"%-3d: #%s    [%6d]\n", i, tag.getText()
											.replaceAll("\"", ""), tag
											.getFreq()));
						}
					}
				}
				String finalupdate = updatehead1 + lineupdate1;
				hash.setText(finalupdate);
			}
		});

		JLabel hL = new JLabel("30 most popular Hash-tags");
		hL.setBackground(Color.LIGHT_GRAY);
		hL.setOpaque(true);
		hL.setToolTipText("Top 30 most popular #-tags in loaded file");
		hL.setPreferredSize(sub1);
		hL.setMaximumSize(sub1);
		hL.setMinimumSize(sub1);
		hL.setSize(sub1);
		hL.setHorizontalAlignment(0);
		hL.setVerticalAlignment(0);

		hash = new JTextArea();
		hash.setPreferredSize(sub2);
		hash.setEditable(false);
		String head1 = "     |                   Hashtag Text    [Count]\n";
		String lines1 = "";
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				lines1 = lines1.concat(String.format("%-4d:\n", i));
			} else {
				lines1 = lines1.concat(String.format("%-3d:\n", i));
			}
		}
		String finalS1 = head1 + lines1;
		hash.setText(finalS1);

		JLabel ll = new JLabel("Lookahead Simhash Size");
		ll.setSize(sub3);
		ll.setMaximumSize(sub3);
		ll.setMaximumSize(sub3);
		ll.setPreferredSize(sub3);
		ll.setFont(getLargestFont(ll));
		ll.setToolTipText("Choose size to use as a lookahead parameter when\nmatching SimHashes using hamming distances");
		ll.setHorizontalAlignment(0);
		ll.setVerticalAlignment(0);
		ll.setOpaque(true);
		ll.setBorder(BorderFactory.createLineBorder(Color.orange, 2));

		JLabel wL = new JLabel("Max Window Match lines");
		wL.setSize(sub3);
		wL.setMaximumSize(sub3);
		wL.setMaximumSize(sub3);
		wL.setPreferredSize(sub3);
		wL.setFont(getLargestFont(wL));
		wL.setToolTipText("Choose number of matches to display\nin text window on right");
		wL.setHorizontalAlignment(0);
		wL.setVerticalAlignment(0);
		wL.setOpaque(true);
		wL.setBorder(BorderFactory.createLineBorder(Color.orange, 2));

		SpinnerNumberModel w = new SpinnerNumberModel();
		w.setMinimum(3);
		w.setMaximum(15);
		w.setStepSize(1);
		w.setValue(7);

		SpinnerNumberModel q = new SpinnerNumberModel();
		q.setMinimum(0);
		q.setMaximum(300);
		q.setStepSize(10);
		q.setValue(250);

		lookahead = new JSpinner(w);
		lookahead.setSize(sub3);
		lookahead.setMaximumSize(sub3);
		lookahead.setMinimumSize(sub3);
		lookahead.setPreferredSize(sub3);
		lookahead.setEnabled(false);
		lookahead
				.setToolTipText("Choose size to use as a lookahead parameter when\nmatching SimHashes using hamming distances");
		lookahead.setFont(getLargestFont(lookahead));

		mSS = new JSpinner(q);
		mSS.setSize(sub3);
		mSS.setMaximumSize(sub3);
		mSS.setMinimumSize(sub3);
		mSS.setPreferredSize(sub3);
		mSS.setEnabled(false);
		mSS.setToolTipText("Choose number of matches to display\nin text window on right");
		mSS.setFont(getLargestFont(mSS));

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 0.1;
		contents.add(loadStats, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 0.1;
		contents.add(hL, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		contents.add(hash, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.1;
		contents.add(ll, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.1;
		contents.add(lookahead, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.1;
		contents.add(wL, gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.1;
		contents.add(mSS, gbc);

		return contents;
	}

	private static Component hashPanel() {

		Dimension full = new Dimension(hashPanelWidth, VALS.size.height);
		Dimension display = new Dimension(hashPanelWidth, textHeight);
		Dimension loader = new Dimension((int) (hashPanelWidth * 0.5),
				loadButtonHeight);
		Dimension spind1 = new Dimension((int) (hashPanelWidth * 0.15),
				loadButtonHeight);
		Dimension spind2 = new Dimension((int) (hashPanelWidth * 0.35),
				loadButtonHeight);

		JPanel comparePanel = new JPanel();
		comparePanel.setLayout(new GridBagLayout());
		comparePanel.setPreferredSize(full);
		comparePanel.setMaximumSize(full);
		comparePanel.setMinimumSize(full);
		comparePanel.setSize(full);
		GridBagConstraints X = new GridBagConstraints();

		JLabel mod = new JLabel("Select Comparison Lower Limit =>");
		mod.setPreferredSize(spind2);
		mod.setMinimumSize(spind2);
		mod.setMaximumSize(spind2);
		mod.setSize(spind2);
		mod.setVerticalAlignment(0);
		mod.setHorizontalAlignment(0);
		mod.setFont(getLargestFont(mod));

		SpinnerNumberModel m = new SpinnerNumberModel();
		m.setMinimum(0);
		m.setMaximum(100);
		m.setStepSize(5);
		m.setValue(95);

		range = new JSpinner(m);
		range.setPreferredSize(spind1);
		range.setMinimumSize(spind1);
		range.setMaximumSize(spind1);
		range.setSize(spind1);
		range.setFont(getLargestFont(range));
		range.setEnabled(false);

		simHashButton = new JButton("Load SimHash Statistics");
		simHashButton.setPreferredSize(loader);
		simHashButton.setMinimumSize(loader);
		simHashButton.setMaximumSize(loader);
		simHashButton.setSize(loader);
		simHashButton.setToolTipText("Press to load SimHash Indices");
		simHashButton.setOpaque(true);
		simHashButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				simHashPane.setText("");
				Similarity similarity = new Similarity((int) range.getValue(),
						jsonloader);
			}
		});
		simHashButton.setEnabled(false);

		simHashPane = new JTextArea();
		JScrollPane scroller = new JScrollPane(simHashPane);
		scroller.setPreferredSize(display);
		scroller.setMaximumSize(display);
		scroller.setMinimumSize(display);
		scroller.setSize(display);

		X.fill = GridBagConstraints.BOTH;
		X.weightx = 1;
		X.weighty = 1;
		X.gridx = 0;
		X.gridy = 0;
		comparePanel.add(simHashButton, X);

		X.gridx = 1;
		X.gridy = 0;
		comparePanel.add(mod, X);

		X.gridx = 2;
		X.gridy = 0;
		comparePanel.add(range, X);

		X.gridx = 0;
		X.gridy = 1;
		X.gridwidth = 3;
		comparePanel.add(scroller, X);
		return comparePanel;
	}

	private static void GUI() {

		JFrame panel = new JFrame("Twitter Processing");

		panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		panel.setMaximumSize(VALS.size);
		panel.setMaximumSize(VALS.size);
		panel.setPreferredSize(VALS.size);
		panel.setSize(VALS.size);
		panel.setResizable(false);

		addMenu(panel);

		cons.fill = GridBagConstraints.VERTICAL;
		cons.gridx = 0;
		cons.gridy = 0;
		cons.weightx = 1;
		cons.weighty = 1;
		cons.anchor = GridBagConstraints.NORTHWEST;
		panel.add(getFileOptionsPanel(), cons);

		cons.fill = GridBagConstraints.VERTICAL;
		cons.gridx = 0;
		cons.gridy = 1;
		cons.weightx = 1;
		cons.weighty = 1;
		panel.add(loadedFileInfo(), cons);

		cons.fill = GridBagConstraints.VERTICAL;
		cons.gridx = 0;
		cons.gridy = 2;
		cons.weightx = 1;
		cons.weighty = 1;
		panel.add(contentCounts(), cons);

		cons.fill = GridBagConstraints.VERTICAL;
		cons.gridx = 1;
		cons.gridy = 0;
		cons.weightx = 1;
		cons.weighty = 1;
		cons.gridheight = 3;
		panel.add(hashPanel(), cons);

		panel.setLocationRelativeTo(null);
		panel.pack();
		panel.setVisible(true);
	}

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI();
			}
		});
	}
}