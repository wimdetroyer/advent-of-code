package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Problem5 {

    // It'd be easy to just store everything in maps linking to eachother, but as we can see in the input data,
    // the ranges get rather high, so we will have to optimize a bit.
    public static void main(String[] args) {
        String filePath = "src/main/resources/aoc-2023/problem5/input.txt";
        Path path = Path.of(filePath);
        SeedTransformer seedTransformer = null;
        try (Stream<String> lines = Files.lines(path)) {
            seedTransformer = parseToSeedTransformer(lines.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Solution part one: " + seedTransformer.findLowestLocationNumber());
        System.out.println("running part two...");
        System.out.println("Solution part two: " + seedTransformer.findLowestLocationNumberSeedsAreRanges());
        System.out.println("done...");

        //Todo make a shortcut map from seed -> location
        // run the numbers that are mapped thru the shortcut map, the ones that are unmapped -> just take the min...
        // flatmapping from seed to location
    }

    private static SeedTransformer parseToSeedTransformer(List<String> inputLines) {
        List<Long> seeds = Arrays.stream(inputLines.get(0).split(":")[1].split(" ")).filter(chunk -> !chunk.isBlank()).map(Long::parseLong).collect(Collectors.toList());
        LinkedHashMap<AlmanacEntries, Set<AlmanacRangeMap>> almanacEntryToRangeMap = new LinkedHashMap<>();
        almanacEntryToRangeMap.put(AlmanacEntries.SEED_TO_SOIL, new HashSet<>());
        almanacEntryToRangeMap.put(AlmanacEntries.SOIL_TO_FERTILIZER, new HashSet<>());
        almanacEntryToRangeMap.put(AlmanacEntries.FERTILIZER_TO_WATER, new HashSet<>());
        almanacEntryToRangeMap.put(AlmanacEntries.WATER_TO_LIGHT, new HashSet<>());
        almanacEntryToRangeMap.put(AlmanacEntries.LIGHT_TO_TEMPERATURE, new HashSet<>());
        almanacEntryToRangeMap.put(AlmanacEntries.TEMPERATURE_TO_HUMIDITY, new HashSet<>());
        almanacEntryToRangeMap.put(AlmanacEntries.HUMIDITY_TO_LOCATION, new HashSet<>());

        AlmanacEntries currentAlmanacEntryEntries = null;
        for (String line : inputLines.subList(1, inputLines.size())) {
            if(line.isBlank()) {
                continue;
            }
            if(line.matches("[0-9].*")) {
                almanacEntryToRangeMap.get(currentAlmanacEntryEntries).add(parseToRangeMap(line));
                continue;
            }
            // not a number nor blank, switch to new almanac entry.
            Optional<AlmanacEntries> almanacEntry = AlmanacEntries.findAlmanacEntry(line);
            currentAlmanacEntryEntries = almanacEntry.get();
        }
        return new SeedTransformer(seeds, almanacEntryToRangeMap);
    }

    public record SeedTransformer(List<Long> seeds, LinkedHashMap<AlmanacEntries, Set<AlmanacRangeMap>> almanacEntryToRangeMap) {

        public Long findLowestLocationNumber() {
            long min = Long.MAX_VALUE;
            for (Long seed : seeds) {
                long currentSourceNumber = seed;
                for (Set<AlmanacRangeMap> almanacRangeMapForAlmanacEntry : almanacEntryToRangeMap.values()) {
                    for (AlmanacRangeMap almanacRangeMap : almanacRangeMapForAlmanacEntry) {
                        if (almanacRangeMap.isMapped(currentSourceNumber)) {
                            currentSourceNumber = almanacRangeMap.findDestinationNumber(currentSourceNumber);
                            break; // Match found, no need to test other rangemaps in this almanac entry
                        }
                    }
                    // if no mapping found source is presumed to be same as destination, explanation:
                    // Any source numbers that aren't mapped correspond to the same destination number. So, seed number 10 corresponds to soil number 10.
                }
                // at the end sourcenumber has become the location, compare it with min:
                min = Math.min(min, currentSourceNumber);
            }
            return min;
        }

        public Long findLowestLocationNumberSeedsAreRanges() {
            long min = Long.MAX_VALUE;
            // TODO: yeah this actually requires some thought, imma head out :-)
            return min;
        }



        public List<SeedRangeMap> seedsInterpretedAsRanges() {
            List<SeedRangeMap> seedRangeMapList = new ArrayList<>();
            for (int i = 0; i < seeds.size(); i = i + 2) {
                seedRangeMapList.add(new SeedRangeMap(seeds.get(i), seeds.get(i+1)));
            }
            return seedRangeMapList;
        }

    }
    public record SeedRangeMap(long start, long range) {

        public long getLowerBound() {
            return start;
        }

        public long getUpperBound() {
            return start + range - 1;
        }
    }

    private static AlmanacRangeMap parseToRangeMap(String line) {
        List<Long> numbers = Arrays.stream(line.split(" ")).filter(chunk -> !chunk.isBlank()).map(Long::parseLong).toList();
        return new AlmanacRangeMap(numbers.get(1), numbers.get(0), numbers.get(2));
    }
    public enum AlmanacEntries {
        SEED_TO_SOIL("seed-to-soil"),
        SOIL_TO_FERTILIZER("soil-to-fertilizer"),
        FERTILIZER_TO_WATER("fertilizer-to-water"),
        WATER_TO_LIGHT("water-to-light"),
        LIGHT_TO_TEMPERATURE("light-to-temperatur"),
        TEMPERATURE_TO_HUMIDITY("temperature-to-humidity"),
        HUMIDITY_TO_LOCATION("humidity-to-location");

        private String inputLabel;
        AlmanacEntries(String inputLabel) {
            this.inputLabel = inputLabel;
        }

        public static Optional<AlmanacEntries> findAlmanacEntry(String inputLine) {
            return Arrays.stream(AlmanacEntries.values()).filter(a -> inputLine.contains(a.inputLabel)).findFirst();
        }
    }
    public record AlmanacRangeMap(Long sourceNumberStart, Long destinationNumberStart, Long range) {


        public boolean isMapped(Long sourceNumber) {
            return sourceNumber >= sourceNumberStart && sourceNumber <= sourceNumberStart + range - 1;
        }

        public Long findDestinationNumber(Long sourceNumber) {
            Long offsetFromStart = sourceNumber - sourceNumberStart;
            return destinationNumberStart + offsetFromStart;
        }
    }

    //TODO: what about a seed-to-location map?
}
