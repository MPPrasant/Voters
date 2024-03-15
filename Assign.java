package org.election;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Contestant {
	String name;
	int points = 0;

	public Contestant(String name) {
		this.name = name;

	}
}

class Region {
	String name;
	List<Contestant> contestants;
	int invalidVotes = 0;
	List<String> votes;

	public Region(String name, List<Contestant> contestants) {
		this.name = name;
		this.contestants = contestants;

		this.votes = new ArrayList<>();
	}

	public void processVotes() {

		for (String vote : votes) {

			String[] parts = vote.split(" ");
			String voterId = parts[0];

			char[] preferences = parts[1].toCharArray();
			boolean validVote = false;

			if ((1 <= preferences.length && preferences.length <= 3)) {

				validVote = true;
			}

			for (char x : preferences) {

				boolean validPreference = false;
				for (Contestant contestant : contestants) {

					if (contestant.name.equals(String.valueOf(x))) {
						validPreference = true;

						break;
					}
				}
				if (validPreference) {
					validVote = true;

				}
			}

			if (validVote) {

				for (int i = 0; i < preferences.length; i++) {

					for (Contestant contestant : contestants) {
						if (contestant.name.equals(String.valueOf(preferences[i]))) {
							contestant.points += 3 - i;
							break;
						}
					}
				}
			} else {
				invalidVotes++;
			}
		}
	}
}

public class Assign {
	public static void main(String[] args) {
		List<Contestant> contestants = new ArrayList<Contestant>();
		Map<String, Integer> chiefOfficer = new HashMap<>();

		List<Region> regions = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(
				new FileReader("C:\\Users\\prasa\\OneDrive\\Desktop\\voting.dat"))) {
			String line;
			while ((line = br.readLine()) != null) {

				if (line.equals("&&")) {
					break;
				}

				else if (line.contains("/") && !(line.equals("//"))) {
					String[] parts = line.split("/");
					String regionName = parts[0];
					String contestantList = parts[1];
					List<Contestant> regionContestants = new ArrayList<>();
					for (char c : contestantList.toCharArray()) {

						Contestant contestant = new Contestant(String.valueOf(c));

						regionContestants.add(contestant);

					}

					regions.add(new Region(regionName, regionContestants));
				} else if (!line.equals("//") && !regions.isEmpty()) {

					int i = 0;
					if (line.equals("&&")) {
						break;
					}
					regions.get(i).votes.add(line);

					while (!((line = br.readLine()).equals("&&"))) {
						if (line.equals("//")) {
							i += 1;
						} else {
							regions.get(i).votes.add(line);

						}
					}

				}
			}

			for (Region region : regions) {
				region.processVotes();

			}

			int maxVotes = 0;
			String chiefOff = "";
			for (Region region : regions) {
				Contestant regionalHead = region.contestants.stream()
						.max((c1, c2) -> Integer.compare(c1.points, c2.points)).orElse(null);
				System.out.println(region.name + " - Invalid Votes: " + region.invalidVotes + ", REGIONAL HEAD: "
						+ regionalHead.name + " with " + regionalHead.points + " points");
				for (Contestant cntst : region.contestants) {
					if (chiefOfficer.containsKey(cntst.name)) {
						Integer vt = chiefOfficer.get(cntst.name);
						vt += cntst.points;
						chiefOfficer.put(cntst.name, vt);
					} else {
						chiefOfficer.put(cntst.name, cntst.points);
					}
				}
			}
			for (String officer : chiefOfficer.keySet()) {
				if (chiefOfficer.get(officer) > maxVotes) {
					chiefOff = officer;
					maxVotes = chiefOfficer.get(officer);
				}
			}
			System.out.println("Chief Officer is : " + chiefOff + " with votes: " + maxVotes);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}