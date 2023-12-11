package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.function.Predicate.not;

public class Problem9 {

    public static void main(String[] args) throws IOException {
        String filePath = "src/main/resources/aoc-2023/problem9/input.txt";
        Path path = Path.of(filePath);

        List<String> lines = Files.lines(path).toList();
        List<Extrapolation> extrapolations = lines.stream().map(Problem9::calculateExtrapolation).toList();

        System.out.println("Solution part one: " + extrapolations.stream().map(Extrapolation::calculateHistoryValueByExtrapolatingForwards).mapToLong(Long::longValue).sum());
        System.out.println("Solution part two: " + extrapolations.stream().map(Extrapolation::calculateHistoryValueByExtrapolatingBackwards).mapToLong(Long::longValue).sum());

    }

    private static Extrapolation calculateExtrapolation(String line) {
        List<ExtrapolationLine> extrapolationLines = new ArrayList<>();
        var currentExtrapolationLine = new ExtrapolationLine(Arrays.stream(line.split(" ")).filter(not(String::isBlank)).map(Long::parseLong).toList());
        extrapolationLines.add(currentExtrapolationLine);
        while(!currentExtrapolationLine.isAllZeroExtrapolationLine()) {
            currentExtrapolationLine = currentExtrapolationLine.calculateNextExtrapolationLine();
            extrapolationLines.add(currentExtrapolationLine);
        }
        return new Extrapolation(extrapolationLines);
    }

    public record Extrapolation(List<ExtrapolationLine> extrapolationLines) {


        public long calculateHistoryValueByExtrapolatingBackwards() {
            long previousCalculatedFirstDigit = 0;
            for (int i = extrapolationLines.size() - 2; i >= 0; i--) {
                long currentFirstDigit = extrapolationLines.get(i).getFirstDigit();
                previousCalculatedFirstDigit = currentFirstDigit - previousCalculatedFirstDigit;
            }
            return previousCalculatedFirstDigit;
        }


        public long calculateHistoryValueByExtrapolatingForwards() {
            long previousCalculatedLastDigit = 0;
            for (int i = extrapolationLines.size() - 2; i >= 0; i--) {
                long currentLastDigit = extrapolationLines.get(i).getLastDigit();
                previousCalculatedLastDigit = currentLastDigit + previousCalculatedLastDigit;
            }
            return previousCalculatedLastDigit;
        }


    }

    private record ExtrapolationLine(List<Long> digits) {


        public long getFirstDigit() {
            return digits.getFirst();
        }
        public long getLastDigit() {
            return digits.getLast();
        }

        public ExtrapolationLine calculateNextExtrapolationLine() {
            List<Long> nextDigits = new ArrayList<>();
            for (int i = 0; i < digits.size() - 1; i++) {
                long first = digits.get(i);
                long second = digits.get(i + 1);
                nextDigits.add(second - first);
            }
            return new ExtrapolationLine(nextDigits);
        }

        public boolean isAllZeroExtrapolationLine() {
            return digits.stream().allMatch(d -> d == 0);
        }
    }
}