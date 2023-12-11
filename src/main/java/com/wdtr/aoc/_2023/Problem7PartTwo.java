package com.wdtr.aoc._2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wdtr.aoc._2023.Problem7PartTwo.Card.*;

public class Problem7PartTwo {

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

        Comparator<Hand> handComparator = Hand.handTypeComparator().thenComparing(Hand.cardByCardComparisonComparator()).reversed();
        handWithBids.sort(((o1, o2) -> handComparator.compare(o1.hand, o2.hand)));

        int bidSum = 0;
        for (int i = 0; i < handWithBids.size(); i++) {
            HandWithBid handWithBid = handWithBids.get(i);
            bidSum += (i + 1) * handWithBid.bid();
        }
        System.out.println("Solution part two: " + bidSum);
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

    public record Hand(List<Card> cards, Map<Card, Integer> freqMap) {


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

        public static Comparator<Hand> handTypeComparator() {
            return (h1, h2) -> {
                HandType handTypeH1 = h1.calculateHandType();
                HandType handTypeH2 = h2.calculateHandType();
                return handTypeH1.compareTo(handTypeH2);
            };
        }

        public static Comparator<Hand> cardByCardComparisonComparator() {

            return (h1, h2) -> {
                for (int i = 0; i < h1.cards.size(); i++) {
                    Card card1 = h1.cards.get(i);
                    Card card2 = h2.cards.get(i);
                    int comparedCards = card1.compareTo(card2);
                    if(comparedCards != 0) {
                        return comparedCards;
                    }
                }
                return 0;
            };
        }

        public HandType calculateHandType() {
            /*
             The isolated checking logic in the methods isn't foolproof.
             Technically in the implemented logic a five of a kind is ALSO a pair, but since the method early returns we'll never have to worry about that.
            */
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
            boolean fourOfAKindWithoutJoker = valuesWithoutJokers().contains(4);
            boolean threeOfAKindWithoutJoker = valuesWithoutJokers().contains(3);
            boolean pairWithoutJoker = valuesWithoutJokers().contains(2);

            if(jokerCount() == 4) {
                return true;
            }
            if(jokerCount() == 3 && pairWithoutJoker) {
                return true;
            }
            if(jokerCount() == 2 && threeOfAKindWithoutJoker) {
                return true;
            }

            if(jokerCount() == 1 && fourOfAKindWithoutJoker) {
                return true;
            };
            return freqMap.containsValue(5);
        }

        public boolean isFourOfAKind() {
            boolean threeOfAKindWithoutJoker = valuesWithoutJokers().contains(3);
            boolean pairWithoutJoker = valuesWithoutJokers().contains(2);

            if(jokerCount() == 3) {
                return true;
            }
            if(jokerCount() == 2 && pairWithoutJoker) {
                return true;
            }

            if(jokerCount() == 1 && threeOfAKindWithoutJoker) {
                return true;
            }
            return freqMap.containsValue(4);
        }

        public boolean isFullHouse() {
            // KKJ55
            boolean pairWithoutJoker = valuesWithoutJokers().contains(2);
            boolean threeOfAKindWithoutJoker = valuesWithoutJokers().contains(3);
            // JJ223
            if(jokerCount() == 2 && pairWithoutJoker) {
                return true;
            }
            // J2223
            boolean twoPairWithoutJoker = valuesWithoutJokers().stream().filter(v -> v == 2).count() == 2;
            if(jokerCount() == 1 && twoPairWithoutJoker) {
                return true;
            }
            return threeOfAKindWithoutJoker && pairWithoutJoker;
        }

        public boolean isThreeOfAKind() {
            boolean pairWithoutJoker = valuesWithoutJokers().contains(2);
            boolean threeOfAKindWithoutJoker = valuesWithoutJokers().contains(3);

            // JJ456
            if(jokerCount() == 2) {
                return true;
            }
            // J4456
            if(jokerCount() == 1 && pairWithoutJoker) {
                return true;
            }
            return threeOfAKindWithoutJoker;
        }

        // if there is a joker, you will either have a pair or a better hand than two pair
        public boolean isTwoPair() {
            // 22445
            return valuesWithoutJokers().stream().filter(v -> v == 2).count() == 2;
        }

        public boolean isPair() {
            if(jokerCount() == 1) {
                return true;
            }
            return valuesWithoutJokers().contains(2);
        }

        public int jokerCount() {
            return freqMap.getOrDefault(JOKER, 0);
        }


        public Collection<Integer> valuesWithoutJokers() {
            List<Integer> valuesWithoutJoker = new ArrayList<>();
            for (Card card : freqMap.keySet()) {
                if(card.isNotJoker()) {
                    valuesWithoutJoker.add(freqMap.get(card));
                }
            }
            return valuesWithoutJoker;
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

    }

    public enum Card {
        ACE,
        KING,
        QUEEN,
        TEN,
        NINE,
        EIGHT,
        SEVEN,
        SIX,
        FIVE,
        FOUR,
        THREE,
        TWO,
        JOKER;
        public static Card from(char c) {
            return switch (c) {
                case '2' -> TWO;
                case '3' -> THREE;
                case '4' -> FOUR;
                case '5' -> FIVE;
                case '6' -> SIX;
                case '7' -> SEVEN;
                case '8' -> EIGHT;
                case '9' -> NINE;
                case 'T' -> TEN;
                case 'J' -> JOKER;
                case 'Q' -> QUEEN;
                case 'K' -> KING;
                case 'A' -> ACE;
                default -> throw new IllegalArgumentException("Wrong input");
            };
        }


        public boolean isNotJoker() {
            return !this.equals(JOKER);
        }
    }
}