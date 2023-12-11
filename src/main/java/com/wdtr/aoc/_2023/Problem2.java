package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.Map;
import java.util.stream.Stream;

public class Problem2 {


    public static void main(String[] args) {
        String filePath = "src/main/resources/aoc-2023/problem2/input.txt";
        Path path = Path.of(filePath);
        try (Stream<String> lines = Files.lines(path)) {
            System.out.printf("Solution part 1: " +  lines.mapToInt(Problem2::isValidLine).sum());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("");
        try (Stream<String> lines = Files.lines(path)) {
            System.out.printf("Solution part 2: " +  lines.mapToInt(Problem2::maxColorPerLineMultipliedByEachother).sum());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int isValidLine(String line) {
        Map<String, Integer> maxColorMap = Map.of("red", 12,
                "green", 13,
                "blue", 14
        );
        String[] split = line.split(":");
        String game = split[0];
        String input = split[1];
        String[] sets = input.split(";");
        for (String set : sets) {
            String[] numbersWithColors = set.split(",");
            for (String numberWithColor : numbersWithColors) {
                String[] numberWithColorSplit = numberWithColor.trim().split(" ");
                int number = Integer.parseInt(numberWithColorSplit[0]);
                String color = numberWithColorSplit[1];
                if(number > maxColorMap.get(color)) {
                    return 0;
                }
            }
        }
        return Integer.parseInt(game.split(" ")[1].replace(":",""));
    }

    private static int maxColorPerLineMultipliedByEachother(String line) {
        String[] split = line.split(":");
        String game = split[0];
        String input = split[1];
        String[] sets = input.split(";");
        int maxRed = 0;
        int maxGreen = 0;
        int maxBlue = 0;
        for (String set : sets) {
            String[] numbersWithColors = set.split(",");
            for (String numberWithColor : numbersWithColors) {
                String[] numberWithColorSplit = numberWithColor.trim().split(" ");
                int number = Integer.parseInt(numberWithColorSplit[0]);
                String color = numberWithColorSplit[1];
                if(color.equals("red")) {
                    maxRed = Math.max(maxRed, number);
                } else if (color.equals("green")) {
                    maxGreen = Math.max(maxGreen, number);
                } else {
                    maxBlue = Math.max(maxBlue, number);
                }
            }
        }
        return maxRed * maxGreen * maxBlue;
    }
}
