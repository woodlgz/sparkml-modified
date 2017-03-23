/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.mllib.tree.impurity

import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.annotation.Since


@Since("1.0.0")
object Variance4GBDT extends Impurity {

  /**
    * :: DeveloperApi ::
    * information calculation for multiclass classification
    *
    * @param counts Array[Double] with counts for each label
    * @param totalCount sum of counts for all labels
    * @return information value, or 0 if totalCount = 0
    */
  @Since("1.1.0")
  @DeveloperApi
  override def calculate(counts: Array[Double], totalCount: Double): Double =
    throw new UnsupportedOperationException("VarianceGBDT.calculate")

  /**
    * :: DeveloperApi ::
    * variance calculation
    *
    * @param count number of instances
    * @param sum sum of labels
    * @param sumSquares summation of squares of the labels
    * @return information value, or 0 if count = 0
    */
  @Since("1.0.0")
  @DeveloperApi
  override def calculate(count: Double, sum: Double, sumSquares: Double): Double = {
    if (count == 0) {
      return 0
    }
    val squaredLoss = sumSquares - (sum * sum) / count
    squaredLoss / count
  }

  /**
    * Get this impurity instance.
    * This is useful for passing impurity parameters to a Strategy in Java.
    */
  @Since("1.0.0")
  def instance: this.type = this

}


private[spark] class Variance4GBDTAggregator()
  extends ImpurityAggregator(statsSize = 4) with Serializable {

  /**
    * Update stats for one (node, feature, bin) with the given label.
    *
    * @param allStats  Flat stats array, with stats for this (node, feature, bin) contiguous.
    * @param offset    Start index of stats for this (node, feature, bin).
    */
  def update(allStats: Array[Double], offset: Int, label: Double, instanceWeight: Double): Unit = {
    allStats(offset) += instanceWeight
    allStats(offset + 1) += instanceWeight * label
    allStats(offset + 2) += instanceWeight * label * label
    allStats(offset + 3) += instanceWeight * Math.abs(label)
  }

  /**
    * Get an [[ImpurityCalculator]] for a (node, feature, bin).
    *
    * @param allStats  Flat stats array, with stats for this (node, feature, bin) contiguous.
    * @param offset    Start index of stats for this (node, feature, bin).
    */
  def getCalculator(allStats: Array[Double], offset: Int): Variance4GBDTCalculator = {
    new Variance4GBDTCalculator(allStats.view(offset, offset + statsSize).toArray)
  }
}

private[spark] class Variance4GBDTCalculator(stats: Array[Double])
  extends ImpurityCalculator(stats) {

  require(stats.length == 4,
    s"VarianceGBDTCalculator requires sufficient statistics array stats to be of length 4," +
      s" but was given array of length ${stats.length}.")

  def copy: Variance4GBDTCalculator = new Variance4GBDTCalculator(stats.clone())

  def calculate(): Double = Variance4GBDT.calculate(stats(0), stats(1), stats(2))

  def count: Long = stats(0).toLong

  def predict: Double = if (count == 0) {
    0
  } else {
    /* if the LogLoss Function Changed,changed here */
    stats(1) / (stats(3)-stats(2))
  }

  override def predictStr: String = toString()

  override def toString: String = {
    s"Variance4GBDTAggregator(cnt = ${stats(0)}, sum = ${stats(1)}," +
      s" square_sum = ${stats(2)},abs_sum = ${stats(3)})"
  }
}