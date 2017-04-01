package com.tiendd.uet.predicting;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.tiendd.clustering.Clustering;

import model.Constant;
import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.conf.Configuration.Resource;
import net.librec.data.DataModel;
import net.librec.data.model.TextDataModel;
import net.librec.filter.GenericRecommendedFilter;
import net.librec.math.structure.SparseMatrix;
import net.librec.recommender.RecommenderContext;
import net.librec.recommender.item.RecommendedItem;
import net.librec.similarity.CosineSimilarity;
import net.librec.similarity.RecommenderSimilarity;

public class Predicting {
	public static int sizeTestMatrix;

	public static double[][] createMatrix(SparseMatrix trainMatrix) throws FileNotFoundException, IOException {
		// Initialize the matrix with -1 for all elements
		double[][] matrix = new double[Constant.SIZE_USER][Constant.SIZE_ITEM];

		for (int userIdx = 0; userIdx < trainMatrix.numRows; ++userIdx) {
			Set<Integer> itemSet = trainMatrix.getColumnsSet(userIdx);
			for (int itemIdx = 0; itemIdx < trainMatrix.numColumns; ++itemIdx) {
				if (itemSet.contains(itemIdx)) {
					matrix[userIdx][itemIdx] = trainMatrix.get(userIdx, itemIdx);
				}
			}
		}
		return matrix;
	}

	public static void predictUnrated() throws IOException, LibrecException {
		// recommender configuration
		Configuration conf = new Configuration();
		Resource resource = new Resource("timesvd-test.properties");
		conf.addResource(resource);
		// build data model
		DataModel dataModel = new TextDataModel(conf);
		dataModel.buildDataModel();
		// set recommendation context
		RecommenderContext context = new RecommenderContext(conf, dataModel);
		// RecommenderSimilarity similarity = new CosineSimilarity();
		// similarity.buildSimilarityMatrix(dataModel);
		// context.setSimilarity(similarity);

		// training
		TimeSVDRecommender recommender = new TimeSVDRecommender();
		recommender.recommend(context);
		// evaluation
		SparseMatrix matrixTrain = recommender.getMatrixTrain();
		SparseMatrix matrixTest = recommender.getMatrixTest();
		PrintWriter writerTrain = new PrintWriter(Constant.URL_FILE_RATING_TRAINSET, "UTF-8");
		for (int i = 0; i < matrixTrain.numRows; i++) {
			for (int j = 0; j < matrixTrain.numColumns; j++) {
				if (matrixTrain.get(i, j) > 0) {
					if (i < matrixTest.numColumns - 1) {
						writerTrain.println(
								i + Constant.COMMA_DELIMITER + j + Constant.COMMA_DELIMITER + matrixTrain.get(i, j));
					} else {
						writerTrain.print(
								i + Constant.COMMA_DELIMITER + j + Constant.COMMA_DELIMITER + matrixTrain.get(i, j));
					}
				}
			}
		}
		writerTrain.close();
		sizeTestMatrix = matrixTest.size();
		PrintWriter writerTest = new PrintWriter(Constant.URL_FILE_RATING_TESTSET, "UTF-8");
		for (int i = 0; i < matrixTrain.numRows; i++) {
			for (int j = 0; j < matrixTrain.numColumns; j++) {
				if (matrixTest.get(i, j) > 0) {
					if (i < matrixTest.numColumns - 1) {
						writerTest.println(
								i + Constant.COMMA_DELIMITER + j + Constant.COMMA_DELIMITER + matrixTest.get(i, j));
					} else {
						writerTest.print(
								i + Constant.COMMA_DELIMITER + j + Constant.COMMA_DELIMITER + matrixTest.get(i, j));
					}
				}
			}
		}
		writerTest.close();

		double[][] matrix = createMatrix(matrixTrain);

		// set id list of filter
		List<String> userIdList = new ArrayList<>();
		for (int i = 0; i < matrixTrain.numRows; i++) {
			userIdList.add(i + "");
		}

		// filter the recommended result
		List<RecommendedItem> recommendedItemList = recommender.getRecommendedList();
		GenericRecommendedFilter filter = new GenericRecommendedFilter();
		filter.setUserIdList(userIdList);
		recommendedItemList = filter.filter(recommendedItemList);

		// print filter result
		for (RecommendedItem recommendedItem : recommendedItemList) {
			System.out.println(recommendedItem.getUserId() + " " + recommendedItem.getItemId());
			matrix[Integer.parseInt(recommendedItem.getUserId()) - 1][Integer.parseInt(recommendedItem.getItemId())
					- 1] = recommendedItem.getValue();

		}
		PrintWriter writerUpdate = new PrintWriter(Constant.URL_FILE_RATING_UPDATED, "UTF-8");
		for (int i = 0; i < matrixTrain.numRows; i++) {
			for (int j = 0; j < matrixTrain.numColumns; j++) {
				writerUpdate.print(matrix[i][j]);
				if (j < Constant.SIZE_ITEM - 1) {
					writerUpdate.print(Constant.COMMA_DELIMITER);
				} else {
					if (i < Constant.SIZE_USER - 1) {
						writerUpdate.println(Constant.COMMA_DELIMITER);
					}
				}
			}
		}
		writerUpdate.close();
	}

}
