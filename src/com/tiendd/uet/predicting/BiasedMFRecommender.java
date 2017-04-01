package com.tiendd.uet.predicting;

import net.librec.annotation.ModelData;
import net.librec.common.LibrecException;
import net.librec.math.structure.DenseMatrix;
import net.librec.math.structure.DenseVector;
import net.librec.math.structure.MatrixEntry;

/**
 * Biased Matrix Factorization Recommender
 *
 * @author GuoGuibing and Keqiang Wang
 */
@ModelData({"isRating", "biasedMF", "userFactors", "itemFactors", "userBiases", "itemBiases"})
public class BiasedMFRecommender extends MatrixFactorizationRecommender {
    /**
     * bias regularization
     */
    protected double regBias;

    /**
     * user biases
     */
    protected DenseVector userBiases;

    /**
     * user biases
     */
    protected DenseVector itemBiases;

    /*
     * (non-Javadoc)
	 *
	 * @see net.librec.recommender.AbstractRecommender#setup()
	 */
    @Override
    protected void setup() throws LibrecException {
        super.setup();
        regBias = conf.getDouble("rec.bias.regularization", 0.01);

        //initialize the userBiased and itemBiased
        userBiases = new DenseVector(numUsers);
        itemBiases = new DenseVector(numItems);

        userBiases.init(initMean, initStd);
        itemBiases.init(initMean, initStd);
    }

    @Override
    protected void trainModel() throws LibrecException {
        for (int iter = 1; iter <= numIterations; iter++) {
            loss = 0.0d;

            for (MatrixEntry matrixEntry : trainMatrix) {

                int userIdx = matrixEntry.row(); // user userIdx
                int itemIdx = matrixEntry.column(); // item itemIdx
                double realRating = matrixEntry.get(); // real rating on item itemIdx rated by user userIdx

                double predictRating = predict(userIdx, itemIdx);
                System.out.println(userIdx+" "+itemIdx+" "+predictRating);
                double error = realRating - predictRating;
                loss += error * error;

                // update user and item bias
                double userBiasValue = userBiases.get(userIdx);
                userBiases.add(userIdx, learnRate * (error - regBias * userBiasValue));
                loss += regBias * userBiasValue * userBiasValue;

                double itemBiasValue = itemBiases.get(itemIdx);
                itemBiases.add(itemIdx, learnRate * (error - regBias * itemBiasValue));
                loss += regBias * itemBiasValue * itemBiasValue;

                //update user and item factors
                for (int factorIdx = 0; factorIdx < numFactors; factorIdx++) {
                    double userFactorValue = userFactors.get(userIdx, factorIdx);
                    double itemFactorValue = itemFactors.get(itemIdx, factorIdx);

                    userFactors.add(userIdx, factorIdx, learnRate * (error * itemFactorValue - regUser * userFactorValue));
                    itemFactors.add(itemIdx, factorIdx, learnRate * (error * userFactorValue - regItem * itemFactorValue));
                    loss += regUser * userFactorValue * userFactorValue + regItem * itemFactorValue * itemFactorValue;
                }
            }

            loss *= 0.5d;
            if (isConverged(iter) && earlyStop) {
                break;
            }
            updateLRate(iter);
        }
    }

    /**
     * predict a specific rating for user userIdx on item itemIdx.
     *
     * @param userIdx user index
     * @param itemIdx item index
     * @return predictive rating for user userIdx on item itemIdx
     * @throws LibrecException if error occurs
     */
    @Override
    protected double predict(int userIdx, int itemIdx) throws LibrecException {
        return DenseMatrix.rowMult(userFactors, userIdx, itemFactors, itemIdx) + userBiases.get(userIdx) + itemBiases.get(itemIdx) + globalMean;
    }
}
