package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public class Problem4 {

    public static void main(String[] args) {
        String filePath = "src/main/resources/aoc-2023/problem4/input.txt";
        Path path = Path.of(filePath);
        // cat input.txt | head -n1 | wc -m
        // cat input.txt | wc -l
        List<CardLine> cardLines = new ArrayList<>();
        try (Stream<String> lines = Files.lines(path)) {
            cardLines = lines.map(Problem4::parseToCardLine).toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Solution part one: " + cardLines.stream().map(CardLine::calculatePointsPartOne).mapToInt(Integer::intValue).sum());
        System.out.println("Solution part two: " + calculateCopiesOfScratchCards(cardLines));
    }

    private static int calculateCopiesOfScratchCards(List<CardLine> cardLines) {
        Map<Integer, Integer> cardIdxToAmountOfCopiesMap = new HashMap<>();
        int copies = 0;
        for (int i = 0; i < cardLines.size(); i++) {
            int amountOfInstances = cardIdxToAmountOfCopiesMap.getOrDefault(i, 1);
            CardLine currCardLine = cardLines.get(i);
            int pointsForCurrCard = currCardLine.calculatePointsPartTwo();
            for (int j = 1; j <= pointsForCurrCard; j++) {
                Integer valueInNextCard = cardIdxToAmountOfCopiesMap.getOrDefault(i + j, 1);
                cardIdxToAmountOfCopiesMap.put(i + j, valueInNextCard + amountOfInstances);
            }
            copies += amountOfInstances;
        }
        return copies;
    }

    private static CardLine parseToCardLine(String line) {
        String[] split = line.split(":")[1].split("\\|");
        return new CardLine(parseNumbersToSet(split[0]), parseNumbersToSet(split[1]));
    }

    private static Set<Integer> parseNumbersToSet(String numbers) {
        String[] split = numbers.split(" ");
        return Arrays.stream(split).filter(not(s -> s.equals(" ") || s.isEmpty())).map(Integer::parseInt).collect(Collectors.toSet());
    }


    record CardLine(Set<Integer> winningNumbers, Set<Integer> numbersInHand) {

        public int calculatePointsPartOne() {
            int count = 0;
            for (Integer number : numbersInHand) {
                if (winningNumbers.contains(number)) {
                    count++;
                }
            }
            if (count == 0) {
                return 0;
            }
            return (int) Math.pow(2, count - 1);
        }

        public int calculatePointsPartTwo() {
            int count = 0;
            for (Integer number : numbersInHand) {
                if (winningNumbers.contains(number)) {
                    count++;
                }
            }
            return count;
        }
    }


}
