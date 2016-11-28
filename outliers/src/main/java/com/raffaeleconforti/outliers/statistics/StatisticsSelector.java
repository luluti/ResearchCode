package com.raffaeleconforti.outliers.statistics;

import com.raffaeleconforti.outliers.statistics.mad.LeftMedianAbsoluteDeviation;
import com.raffaeleconforti.outliers.statistics.mad.MedianAbsoluteDeviation;
import com.raffaeleconforti.outliers.statistics.mad.RightMedianAbsoluteDeviation;
import com.raffaeleconforti.outliers.statistics.mean.Mean;
import com.raffaeleconforti.outliers.statistics.median.Median;
import com.raffaeleconforti.outliers.statistics.qn.LeftQn;
import com.raffaeleconforti.outliers.statistics.qn.Qn;
import com.raffaeleconforti.outliers.statistics.qn.RightQn;
import com.raffaeleconforti.outliers.statistics.sn.LeftSn;
import com.raffaeleconforti.outliers.statistics.sn.RightSn;
import com.raffaeleconforti.outliers.statistics.sn.Sn;
import com.raffaeleconforti.outliers.statistics.standarddeviation.LeftStandardDeviation;
import com.raffaeleconforti.outliers.statistics.standarddeviation.RightStandardDeviation;
import com.raffaeleconforti.outliers.statistics.standarddeviation.StandardDeviation;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 23/11/16.
 */
public class StatisticsSelector {

    public enum StatisticsMeasures {MEAN, MEDIAN, SD, LEFT_SD, RIGHT_SD, MAD, LEFT_MAD, RIGHT_MAD, SN, LEFT_SN, RIGHT_SN, QN, LEFT_QN, RIGHT_QN}

    private final Mean mean = new Mean();
    private final Median median = new Median();

    private final StandardDeviation sd = new StandardDeviation();
    private final LeftStandardDeviation lsd = new LeftStandardDeviation();
    private final RightStandardDeviation rsd = new RightStandardDeviation();

    private final MedianAbsoluteDeviation mad = new MedianAbsoluteDeviation();
    private final LeftMedianAbsoluteDeviation lmad = new LeftMedianAbsoluteDeviation();
    private final RightMedianAbsoluteDeviation rmad = new RightMedianAbsoluteDeviation();

    private final Sn sn = new Sn();
    private final LeftSn lsn = new LeftSn();
    private final RightSn rsn = new RightSn();

    private final Qn qn = new Qn();
    private final LeftQn lqn = new LeftQn();
    private final RightQn rqn = new RightQn();

    public double evaluate(StatisticsMeasures measure, Double val, double... values) {
        StatisticsMeasure statisticsMeasure = null;
        switch (measure) {
            case MEAN       : statisticsMeasure = mean;
                            break;

            case MEDIAN     : statisticsMeasure = median;
                            break;

            case SD         : statisticsMeasure = sd;
                            break;

            case LEFT_SD    : statisticsMeasure = lsd;
                            break;

            case RIGHT_SD   : statisticsMeasure = rsd;
                            break;

            case MAD        : statisticsMeasure = mad;
                            break;

            case LEFT_MAD   : statisticsMeasure = lmad;
                            break;

            case RIGHT_MAD  : statisticsMeasure = rmad;
                            break;

            case SN         : statisticsMeasure = sn;
                            break;

            case LEFT_SN    : statisticsMeasure = lsn;
                            break;

            case RIGHT_SN   : statisticsMeasure = rsn;
                            break;

            case QN         : statisticsMeasure = qn;
                            break;

            case LEFT_QN    : statisticsMeasure = lqn;
                            break;

            case RIGHT_QN   : statisticsMeasure = rqn;
                            break;
        }

        return statisticsMeasure.evaluate(val, values);
    }
}