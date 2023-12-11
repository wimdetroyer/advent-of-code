package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Problem3 {

    public static void main(String[] args) {
        String filePath = "src/main/resources/aoc-2023/problem3/input.txt";
        Path path = Path.of(filePath);
        // cat input.txt | head -n1 | wc -m
        // cat input.txt | wc -l
        char[][] engineSchematic = new char[140][140];
        try (Stream<String> lines = Files.lines(path)) {
            AtomicInteger lineIdx = new AtomicInteger();
            lines.forEach(line -> {
                        char[] charArray = line.toCharArray();
                        for (int i = 0; i < charArray.length; i++) {
                            engineSchematic[lineIdx.get()][i] = charArray[i];
                        }
                        lineIdx.getAndIncrement();
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Solution part one: " + calcSumOfPartNumbers(engineSchematic));
        System.out.println("Solution part two: " + calcGearRatio(engineSchematic));
    }

    private static int calcSumOfPartNumbers(char[][] engineSchematic) {
        int sum = 0;
        for (int i = 0; i < engineSchematic.length; i++) {
            String currNumSeq = "";
            boolean isCurrSeqPartNumber = false;
            for (int j = 0; j < engineSchematic[i].length; j++) {
                char currChar = engineSchematic[i][j];
                if (isNum(currChar)) {
                    currNumSeq += currChar;
                    // Only 1 num in the seq needs to be adjacent to a symbol for it to be a match in its whole.
                    // That also means to not bother if we already found one char in the sequence to be adjacent to a symbol.
                    if (!isCurrSeqPartNumber && isAdjacentToSymbol(i, j, engineSchematic)) {
                        isCurrSeqPartNumber = true;
                    }
                } else {
                    if (isCurrSeqPartNumber) {
                        sum += Integer.parseInt(currNumSeq);
                    }
                    currNumSeq = "";
                    isCurrSeqPartNumber = false;
                }
            }
            if (isCurrSeqPartNumber) {
                sum += Integer.parseInt(currNumSeq);
            }
        }
        return sum;
    }

    private static boolean isAdjacentToSymbol(int i, int j, char[][] engineSchematic) {
        boolean jInUpperBound = j + 1 < 140;
        boolean jInLowerBound = j - 1 > 0;
        boolean iInUpperBound = i + 1 < 140;
        boolean iInLowerBound = i - 1 > 0;

        if(jInUpperBound) {
            char right = engineSchematic[i][j + 1];
            if(isSymbol(right)) {
                return true;
            }
            if(iInLowerBound) {
                char diagAboveRight = engineSchematic[i - 1][j + 1];
                if(isSymbol(diagAboveRight)) {
                    return true;
                }
            }
            if(iInUpperBound) {
                char diagUnderRight = engineSchematic[i + 1][j + 1];
                if(isSymbol(diagUnderRight)) {
                    return true;
                }
            }
        }
        if(jInLowerBound) {
            char left = engineSchematic[i][j - 1];
            if(isSymbol(left)) {
                return true;
            }
            if(iInLowerBound) {
                char diagAboveLeft = engineSchematic[i - 1][j - 1];
                if(isSymbol(diagAboveLeft)) {
                    return true;
                }
            }
            if(iInUpperBound) {
                char diagUnderLeft = engineSchematic[i + 1][j - 1];
                if(isSymbol(diagUnderLeft)) {
                    return true;
                }
            }
        }

        if(iInLowerBound) {
            char above = engineSchematic[i - 1][j];
            if(isSymbol(above)) {
                return true;
            }
        }

        if(iInUpperBound) {
            char under = engineSchematic[i + 1][j];
            if(isSymbol(under)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNum(char c) {
        return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
    }

    private static boolean isSymbol(char c) {
        return c != '.' && !isNum(c);
    }


    // Part two:

    private static int calcGearRatio(char[][] engineSchematic) {
        Map<Pair<Integer, Integer>, List<Integer>> gearsWithAdjacentNumSeqsMap = new HashMap<>();
        for (int i = 0; i < engineSchematic.length; i++) {
            String currNumSeq = "";
            Set<Pair<Integer, Integer>> gearAdjacencies = new HashSet<>();
            for (int j = 0; j < engineSchematic[i].length; j++) {
                char currChar = engineSchematic[i][j];
                if (isNum(currChar)) {
                    currNumSeq += currChar;
                    gearAdjacencies.addAll(findGearAdjacencies(i, j, engineSchematic));
                } else {
                    addAdjacentNumSeqsOfGearToMap(gearsWithAdjacentNumSeqsMap, currNumSeq, gearAdjacencies);
                    currNumSeq = "";
                    gearAdjacencies.clear();
                }
            }
            addAdjacentNumSeqsOfGearToMap(gearsWithAdjacentNumSeqsMap, currNumSeq, gearAdjacencies);
        }


        return gearsWithAdjacentNumSeqsMap.values().stream().filter(numseqs -> numseqs.size() == 2)
                .map(numseq -> numseq.get(0) * numseq.get(1))
                .mapToInt(Integer::intValue)
                .sum();
    }

    public static void addAdjacentNumSeqsOfGearToMap(Map<Pair<Integer, Integer>, List<Integer>> gearsWithAdjacentNumSeqsMap, String numSeq, Set<Pair<Integer, Integer>> gearAdjacencies) {
        for (Pair<Integer, Integer> gearAdjacency : gearAdjacencies) {
            if(!gearsWithAdjacentNumSeqsMap.containsKey(gearAdjacency)) {
                gearsWithAdjacentNumSeqsMap.put(gearAdjacency, new ArrayList<>());
            }
            gearsWithAdjacentNumSeqsMap.get(gearAdjacency).add(Integer.parseInt(numSeq));
        }
    }
    private static boolean isGear(char c) {
        return c == '*';
    }
    private static Set<Pair<Integer, Integer>> findGearAdjacencies(int i, int j, char[][] engineSchematic) {
        boolean jInUpperBound = j + 1 < 140;
        boolean jInLowerBound = j - 1 > 0;
        boolean iInUpperBound = i + 1 < 140;
        boolean iInLowerBound = i - 1 > 0;

        Set<Pair<Integer, Integer>> gearAdjs = new HashSet<>();
        if(jInUpperBound) {
            char right = engineSchematic[i][j + 1];
            if(isGear(right)) {
                gearAdjs.add(new Pair<>(i, j +1));
            }
            if(iInLowerBound) {
                char diagAboveRight = engineSchematic[i - 1][j + 1];
                if(isGear(diagAboveRight)) {
                    gearAdjs.add(new Pair<>(i - 1, j +1));
                }
            }
            if(iInUpperBound) {
                char diagUnderRight = engineSchematic[i + 1][j + 1];
                if(isGear(diagUnderRight)) {
                    gearAdjs.add(new Pair<>(i + 1, j +1));
                }
            }
        }
        if(jInLowerBound) {
            char left = engineSchematic[i][j - 1];
            if(isGear(left)) {
                gearAdjs.add(new Pair<>(i, j - 1));
            }
            if(iInLowerBound) {
                char diagAboveLeft = engineSchematic[i - 1][j - 1];
                if(isGear(diagAboveLeft)) {
                    gearAdjs.add(new Pair<>(i - 1, j - 1));
                }
            }
            if(iInUpperBound) {
                char diagUnderLeft = engineSchematic[i + 1][j - 1];
                if(isGear(diagUnderLeft)) {
                    gearAdjs.add(new Pair<>(i + 1, j - 1));
                }
            }
        }

        if(iInLowerBound) {
            char above = engineSchematic[i - 1][j];
            if(isGear(above)) {
                gearAdjs.add(new Pair<>(i - 1, j));
            }
        }

        if(iInUpperBound) {
            char under = engineSchematic[i + 1][j];
            if(isGear(under)) {
                gearAdjs.add(new Pair<>(i + 1, j));
            }
        }
        return gearAdjs;
    }

}
