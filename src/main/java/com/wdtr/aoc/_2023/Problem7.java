package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Problem7 {

    public static void main(String[] args) {
        String filePath = "src/main/resources/aoc-2023/problem7/input.txt";
        Path path = Path.of(filePath);
        List<HandWithBid> handWithBids = null;
        try (Stream<String> lines = Files.lines(path)) {
            List<String> list = lines.toList();
            handWithBids = parseHandsWithBids(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Comparator<HandWithBid> handWithBidComparator = Comparator.comparing(HandWithBid::hand).reversed();
        Collections.sort(handWithBids, handWithBidComparator);
        int bidSum = 0;
        for (int i = 0; i < handWithBids.size(); i++) {
            bidSum += (i + 1) * handWithBids.get(i).bid();
        }
        System.out.println("Solution part one: " + bidSum);



    }

    private static List<HandWithBid> parseHandsWithBids(List<String> lines) {
        return lines.stream().map(l -> new HandWithBid(parseHand(l.split(" ")[0]), Integer.parseInt(l.split(" ")[1]))).collect(Collectors.toList());
    }

    private static Hand parseHand(String handString) {
        List<Card> cards = new ArrayList<>();
        char[] chars = handString.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            cards.add(Card.from(chars[i]));
        }
        return Hand.from(cards);
    }


    public record HandWithBid(Hand hand, int bid) {

    }

    public record Hand(List<Card> cards, Map<Card, Integer> freqMap) implements Comparable<Hand>{


        public static Hand from(List<Card> cards) {
            return new Hand(cards, calculateFreqMap(cards));
        }

        private static Map<Card, Integer> calculateFreqMap(List<Card> cards) {
            Map<Card, Integer> freqMap = new HashMap<>();
            for (Card card : cards) {
                freqMap.put(card, freqMap.getOrDefault(card, 0) + 1);
            }
            return freqMap;
        }

        @Override
        public int compareTo(Hand o) {
            int comparedHandTypes = this.calculateHandType().compareTo(o.calculateHandType());
            if(comparedHandTypes == 0) {
                // Second rule: order by card strength, first to last
                List<Card> otherCards = o.cards;
                for (int i = 0; i < cards.size(); i++) {
                    int comparedCards = cards.get(i).compareTo(otherCards.get(i));
                    if(comparedCards != 0) {
                        return comparedCards;
                    }
                }
                return 0;
            }
            return comparedHandTypes;
        }


        public HandType calculateHandType() {
            if(isFiveOfAKind()) {
                return HandType.FIVE_OF_A_KIND;
            }
            if(isFourOfAKind()) {
                return HandType.FOUR_OF_A_KIND;
            }
            if(isFullHouse()) {
                return HandType.FULL_HOUSE;
            }
            if(isThreeOfAKind()) {
                return HandType.THREE_OF_A_KIND;
            }
            if(isTwoPair()) {
                return HandType.TWO_PAIR;
            }
            if(isPair()) {
                return HandType.ONE_PAIR;
            }
            return HandType.HIGH_CARD;
        }
        public boolean isFiveOfAKind() {
            return freqMap.containsValue(5);
        }

        public boolean isFourOfAKind() {
            return freqMap.containsValue(4);
        }

        public boolean isFullHouse() {
            return isThreeOfAKind() && isPair();

        }

        public boolean isThreeOfAKind() {
            return freqMap.containsValue(3);
        }

        public boolean isTwoPair() {
            int pairCount = 0;
            for (Integer value : freqMap.values()) {
                if(value == 2) {
                    pairCount++;
                }
            }
            return pairCount == 2;
        }

        public boolean isPair() {
            return freqMap.containsValue(2);
        }


    }


    public enum HandType {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD,
        ;



    }

    public enum Card {
        ACE,
        KING,
        QUEEN,
        JACK,
        TEN,
        NINE,
        EIGHT,
        SEVEN,
        SIX,
        FIVE,
        FOUR,
        THREE,
        TWO,
        ONE;

        public static Card from(char c) {
            return switch (c) {
                case '1' -> ONE;
                case '2' -> TWO;
                case '3' -> THREE;
                case '4' -> FOUR;
                case '5' -> FIVE;
                case '6' -> SIX;
                case '7' -> SEVEN;
                case '8' -> EIGHT;
                case '9' -> NINE;
                case 'T' -> TEN;
                case 'J' -> JACK;
                case 'Q' -> QUEEN;
                case 'K' -> KING;
                case 'A' -> ACE;
                default -> throw new IllegalArgumentException("Wrong input");
            };
        }


    }
}