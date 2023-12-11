package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Problem6 {

    public static void main(String[] args) {
        String filePath = "src/main/resources/aoc-2023/problem6/input.txt";
        Path path = Path.of(filePath);
        List<Race> racesPartOne = null;
        Race racePartTwo = null;
        try (Stream<String> lines = Files.lines(path)) {
            List<String> list = lines.toList();
            racesPartOne = parseRacesPartOne(list);
            racePartTwo = parseRacePartTwo(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Solution part one: " + racesPartOne.stream().map(Race::getNumbersOfWayToBeatRecord).reduce(1L, (a, b) -> a * b));
        System.out.println("Solution part two: " + racePartTwo.getNumbersOfWayToBeatRecord());



    }

    private static Race parseRacePartTwo(List<String> lines) {
        long time = Long.parseLong(lines.get(0).split(":")[1].replace(" ", ""));
        long distance = Long.parseLong(lines.get(1).split(":")[1].replace(" ", ""));
        return new Race(time, distance);
    }

    private static List<Race> parseRacesPartOne(List<String> lines) {
        List<Race> races = new ArrayList<>();
        List<Long> times = Arrays.stream(lines.get(0).split(":")[1].split(" ")).filter(c -> !c.isBlank()).map(Long::parseLong).toList();
        List<Long> distances = Arrays.stream(lines.get(1).split(":")[1].split(" ")).filter(c -> !c.isBlank()).map(Long::parseLong).toList();
        for (int i = 0; i < times.size(); i++) {
            races.add(new Race(times.get(i), distances.get(i)));
        }
        return races;
    }

    public record Race(long timeAllowed, long distanceRecord) {

        public long getNumbersOfWayToBeatRecord() {
            long numWays = 0;
            for(long i = 1; i < timeAllowed; i++) {
                long timeLeft = timeAllowed - i;
                if(timeLeft * i > distanceRecord) {
                    numWays++;
                }
            }
            return numWays;
        }
    }
}