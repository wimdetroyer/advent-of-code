package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class Problem1 {

    public static void main(String[] args) {
        String filePath = "src/main/resources/aoc-2023/problem1/input.txt";
        Path path = Path.of(filePath);
        try (Stream<String> lines = Files.lines(path)) {
            System.out.printf("Solution part 1: %d%n", lines.mapToInt(Problem1::cleanupCalibrationValuePartOne).sum());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Stream<String> lines = Files.lines(path)) {
            System.out.printf("Solution part 2: %d%n", lines.mapToInt(Problem1::cleanupCalibrationValuePartTwo).sum());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int cleanupCalibrationValuePartTwo(String line) {
        Map<String, Character> digitsMap = Map.of(
                "one", '1',
                "two", '2',
                "three", '3',
                "four", '4',
                "five", '5',
                "six", '6',
                "seven", '7',
                "eight", '8',
                "nine", '9'
                );
        char[] nums = new char[2];
        boolean foundFirst = false;
        String possibleDigitWord = "";
        for (char c : line.toCharArray()) {
            possibleDigitWord += c;
            Optional<Character> optionalC = isDigit(digitsMap, possibleDigitWord);
            if(optionalC.isPresent()) {
                setNumber(nums, optionalC.get(), foundFirst);
                possibleDigitWord = String.valueOf(c);
                foundFirst = true;
            }
            else if(isNum(c)) {
                setNumber(nums, c, foundFirst);
                possibleDigitWord = "";
                foundFirst = true;
            }
        }
        return Integer.parseInt(String.valueOf(nums));
    }

    private static Optional<Character> isDigit(Map<String, Character> digitsMap, String possibleDigitWord) {
        for (String digit : digitsMap.keySet()) {
            if(possibleDigitWord.contains(digit)) {
                return Optional.of(digitsMap.get(digit));
            }
        }
        return Optional.empty();
    }

    private static void setNumber(char[] nums, char numToSet, boolean foundFirst) {
        if (!foundFirst) {
            nums[0] = numToSet;
        }
        nums[1] = numToSet;
    }

    private static int cleanupCalibrationValuePartOne(String line) {
        char[] nums = new char[2];
        boolean foundFirst = false;
        for (char c : line.toCharArray()) {
            if (isNum(c)) {
                if (!foundFirst) {
                    nums[0] = c;
                    foundFirst = true;
                }
                nums[1] = c;
            }
        }
        return Integer.parseInt(String.valueOf(nums));
    }

    private static boolean isNum(char c) {
        return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
    }
}