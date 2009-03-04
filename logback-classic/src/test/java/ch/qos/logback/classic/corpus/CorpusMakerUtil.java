/**
 * Logback: the generic, reliable, fast and flexible logging framework.
 * 
 * Copyright (C) 2000-2009, QOS.ch
 * 
 * This library is free software, you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation.
 */
package ch.qos.logback.classic.corpus;

import java.util.List;
import java.util.Random;

import ch.qos.logback.classic.Level;

public class CorpusMakerUtil {

  // level distribution is determined by the following table
  // it corresponds to TRACE 20%, DEBUG 30%, INFO 30%, WARN 10%,
  // ERROR 10%. See also getRandomLevel() method.
  static final double[] LEVEL_DISTRIBUTION = new double[] { .2, .5, .8, .9 };

  // messages will have no arguments 90% of the time, one argument in 3%, two
  // arguments in 3% and three arguments in 3% of cases
  static final double[] ARGUMENT_DISTRIBUTION = new double[] { .90, .933, 0.966 };

  static final double THROWABLE_PROPABILITY_FOR_WARNING = .1;
  static final double THROWABLE_PROPABILITY_FOR_ERRORS = .3;
  static final double NESTING_PROBABILITY = .5;

  static final int AVERAGE_LOGGER_NAME_PARTS = 6;
  static final int STD_DEV_FOR_LOGGER_NAME_PARTS = 3;

  static final int AVERAGE_MESSAGE_WORDS = 8;
  static final int STD_DEV_FOR_MESSAGE_WORDS = 4;

  final Random random;
  List<String> worldList;

  CorpusMakerUtil(long seed, List<String> worldList) {
    random = new Random(seed);
    this.worldList = worldList;

  }

  String getRandomWord() {
    int size = worldList.size();
    int randomIndex = random.nextInt(size);
    return worldList.get(randomIndex);
  }

  int[] getRandomAnchorPositions(int wordCount, int numAnchors) {
    // note that the same position may appear multiple times in
    // positionsIndex, but without serious consequences
    int[] positionsIndex = new int[numAnchors];
    for (int i = 0; i < numAnchors; i++) {
      positionsIndex[i] = random.nextInt(wordCount);
    }
    return positionsIndex;
  }

  private String[] getRandomWords(int n) {
    String[] wordArray = new String[n];
    for (int i = 0; i < n; i++) {
      wordArray[i] = getRandomWord();
    }
    return wordArray;
  }

  MessageEntry getRandomMessageEntry() {
    int numOfArguments = getNumberOfMessageArguments();
    Object[] argumentArray = null;
    if (numOfArguments > 0) {
      argumentArray = new Object[numOfArguments];
      for (int i = 0; i < numOfArguments; i++) {
        argumentArray[i] = new Long(random.nextLong());
      }
    }
    int wordCount = RandomUtil.gaussianAsPositiveInt(random,
        AVERAGE_MESSAGE_WORDS, STD_DEV_FOR_MESSAGE_WORDS);
    String[] wordArray = getRandomWords(wordCount);

    int[] anchorPositions = getRandomAnchorPositions(wordCount, numOfArguments);
    for (int anchorIndex : anchorPositions) {
      wordArray[anchorIndex] = "{}";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < wordCount; i++) {
      sb.append(getRandomWord()).append(' ');
    }
    sb.append(getRandomWord());

    return new MessageEntry(sb.toString(), argumentArray);
  }

  Throwable buildThrowable(double i) {
    return null;
  }

  Throwable getRandomThrowable(Level level) {
    double rn = random.nextDouble();
    if ((level == Level.WARN && rn < THROWABLE_PROPABILITY_FOR_WARNING)
        || (level == Level.ERROR && rn < THROWABLE_PROPABILITY_FOR_ERRORS)) {
      return ExceptionBuilder.build(random, NESTING_PROBABILITY);
    } else {
      return null;
    }
  }

  int getNumberOfMessageArguments() {
    double rn = random.nextDouble();
    if (rn < ARGUMENT_DISTRIBUTION[0]) {
      return 0;
    }
    if (rn < ARGUMENT_DISTRIBUTION[1]) {
      return 1;
    }
    if (rn < ARGUMENT_DISTRIBUTION[2]) {
      return 2;
    }
    return 3;
  }

  String getRandomLoggerName() {
    int parts = RandomUtil.gaussianAsPositiveInt(random,
        AVERAGE_LOGGER_NAME_PARTS, STD_DEV_FOR_LOGGER_NAME_PARTS);
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < parts; i++) {
      sb.append(getRandomWord()).append('.');
    }
    sb.append(getRandomWord());
    return sb.toString();
  }

  Level getRandomLevel() {
    double rn = random.nextDouble();
    if (rn < LEVEL_DISTRIBUTION[0]) {
      return Level.TRACE;
    }
    if (rn < LEVEL_DISTRIBUTION[1]) {
      return Level.DEBUG;
    }

    if (rn < LEVEL_DISTRIBUTION[2]) {
      return Level.INFO;
    }

    if (rn < LEVEL_DISTRIBUTION[3]) {
      return Level.WARN;
    }

    return Level.ERROR;
  }
}
