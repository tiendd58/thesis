package com.tiendd.uet.preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import model.Constant;
import model.User;
import model.User2;

public class PreProcess {

	public static void preProcessDataset() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(Constant.URL_FILE_RATING_ROOT));
		PrintWriter writerTrainSet = new PrintWriter(Constant.URL_FILE_RATING_TRAINSET, "UTF-8");
		PrintWriter writerTestSet = new PrintWriter(Constant.URL_FILE_RATING_TESTSET, "UTF-8");
		StringTokenizer st = null;
		String row;
		ArrayList<User> users = new ArrayList<>();
		int oldID = 1;
		while ((row = br.readLine()) != null) {
			st = new StringTokenizer(row, Constant.COMMA_DELIMITER);
			while (st.hasMoreTokens()) {
				String userID = st.nextToken();
				String itemID = st.nextToken();
				String rating = st.nextToken();
				String time = st.nextToken();
				User u = new User(userID, rating, itemID, time);
				if (Integer.parseInt(userID) > oldID) {
					Collections.sort(users, new Comparator<User>() {

						@Override
						public int compare(User o1, User o2) {
							return o1.getTime().compareTo(o2.getTime());
						}
					});
					int sizeTestSet = users.size() / 10;
					int sizeTrainSet = users.size() - sizeTestSet;

					System.out.println(sizeTrainSet);
					for (int i = 0; i < users.size(); i++) {
						if (i < sizeTrainSet) {
							writerTrainSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER
									+ users.get(i).getItemID() + Constant.COMMA_DELIMITER + users.get(i).getRating()
									+ Constant.COMMA_DELIMITER + users.get(i).getTime());
						} else {
							writerTestSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER
									+ users.get(i).getItemID() + Constant.COMMA_DELIMITER + users.get(i).getRating()
									+ Constant.COMMA_DELIMITER + users.get(i).getTime());
						}
					}
					users.clear();
					oldID = Integer.parseInt(userID);
					users.add(u);
				} else {
					users.add(u);
				}
			}

		}
		Collections.sort(users, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.getTime().compareTo(o2.getTime());
			}
		});
		int sizeTestSet = users.size() / 10;
		int sizeTrainSet = users.size() - sizeTestSet;

		System.out.println(sizeTrainSet);
		for (int i = 0; i < users.size(); i++) {
			if (i < sizeTrainSet) {
				writerTrainSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER + users.get(i).getItemID()
						+ Constant.COMMA_DELIMITER + users.get(i).getRating() + Constant.COMMA_DELIMITER
						+ users.get(i).getTime());
			} else {
				writerTestSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER + users.get(i).getItemID()
						+ Constant.COMMA_DELIMITER + users.get(i).getRating() + Constant.COMMA_DELIMITER
						+ users.get(i).getTime());
			}
		}
		writerTestSet.close();
		writerTrainSet.close();
		br.close();
	}

	public static void preProcessDataset(int category) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("dataset/ratings.txt"));
		PrintWriter writerTrainSet = new PrintWriter(Constant.URL_FILE_RATING_TRAINSET, "UTF-8");
		PrintWriter writerTestSet = new PrintWriter(Constant.URL_FILE_RATING_TESTSET, "UTF-8");
		StringTokenizer st = null;
		String row;
		ArrayList<User2> users = new ArrayList<>();
		int oldID = 1;
		while ((row = br.readLine()) != null) {
			st = new StringTokenizer(row, " ");
			while (st.hasMoreTokens()) {
				String userID = st.nextToken();
				String itemID = st.nextToken();
				// String categoryID = st.nextToken();
				String rating = st.nextToken();
				// if(Integer.parseInt(categoryID)==category){
				User2 u = new User2(userID, rating, itemID, null);
				if (Integer.parseInt(userID) > oldID) {

					int sizeTestSet = users.size() / 10;
					int sizeTrainSet = users.size() - sizeTestSet;

					System.out.println(sizeTrainSet);
					for (int i = 0; i < users.size(); i++) {
						if (i < sizeTrainSet) {
							writerTrainSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER
									+ users.get(i).getItemID() + Constant.COMMA_DELIMITER + users.get(i).getRating()
									+ Constant.COMMA_DELIMITER + "12");
						} else {
							writerTestSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER
									+ users.get(i).getItemID() + Constant.COMMA_DELIMITER + users.get(i).getRating()
									+ Constant.COMMA_DELIMITER + "12");
						}
					}
					users.clear();
					oldID = Integer.parseInt(userID);
					users.add(u);
				} else {
					users.add(u);
				}
			}
			// }

		}
		int sizeTestSet = users.size() / 10;
		int sizeTrainSet = users.size() - sizeTestSet;

		System.out.println(sizeTrainSet);
		for (int i = 0; i < users.size(); i++) {
			if (i < sizeTrainSet) {
				writerTrainSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER + users.get(i).getItemID()
						+ Constant.COMMA_DELIMITER + users.get(i).getRating() + Constant.COMMA_DELIMITER + "12");
			} else {
				writerTestSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER + users.get(i).getItemID()
						+ Constant.COMMA_DELIMITER + users.get(i).getRating() + Constant.COMMA_DELIMITER + "12");
			}
		}
		writerTestSet.close();
		writerTrainSet.close();
		br.close();
	}

	public static void preprocess(ArrayList<User> users, PrintWriter writerTrainSet, PrintWriter writerTestSet) {
		Collections.sort(users, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.getTime().compareTo(o2.getTime());
			}
		});
		int sizeTestSet = users.size() / 10;
		int sizeTrainSet = users.size() - sizeTestSet;
		for (int i = 0; i < users.size(); i++) {
			if (i < sizeTrainSet) {
				writerTrainSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER + users.get(i).getItemID()
						+ Constant.COMMA_DELIMITER + users.get(i).getRating() + Constant.COMMA_DELIMITER
						+ users.get(i).getTime());
			} else {
				writerTestSet.println(users.get(i).getId() + Constant.COMMA_DELIMITER + users.get(i).getItemID()
						+ Constant.COMMA_DELIMITER + users.get(i).getRating() + Constant.COMMA_DELIMITER
						+ users.get(i).getTime());
			}
		}
		users.clear();
	}

	public static void main(String[] args) {
		try {
			preProcessDataset(3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
