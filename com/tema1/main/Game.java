package com.tema1.main;

import com.tema1.goods.Goods;
import com.tema1.players.Function;
import com.tema1.players.Player;
import com.tema1.players.PlayerComparator;
import com.tema1.players.StrategyFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class Game {

    private Game() {
    }

    public static List<Player> startGame(final int numberPlayers, final int numberRounds,
                                         final List<String> playerStrategies,
                                         final List<Integer> goodsId) {

        List<Player> players = new LinkedList<Player>();

        // se creeaza primul jucator(Sheriff), deci nu are bunuri
        players.add(StrategyFactory.setStrategy(playerStrategies.get(0), 0));
        for (int i = 1; i < numberPlayers; ++i) {
            players.add(StrategyFactory.setStrategy(playerStrategies.get(i), i));

        }

        int i = 0;
        int currentRound = 1;
        int counter = 0;
        while (currentRound <= numberRounds) {
            while (i < numberPlayers) {
                // i e indexul sheriff-ului
                // numberPlayers e nr de subrunde
                players.get(i).setFunction(Function.Sheriff);
                List<Goods> hand = players.get(i).getHand();
                List<Goods> bag = players.get(i).getBag();
                hand.removeAll(hand); // daca e Sheriff nu are nici hand, nici bag
                bag.removeAll(bag);


                for (int j = 0; j < i; j++) { // mergem pana la serif
                    players.get(j).setFunction(Function.Trader);
                    players.get(j).completeHand(goodsId, j + counter * (numberPlayers - 1));
                    players.get(j).playAsTrader(goodsId, players.get(i), currentRound);

                }

                for (int j = i + 1; j < numberPlayers; ++j) { // mergem dupa serif
                    players.get(j).setFunction(Function.Trader);
                    players.get(j).completeHand(goodsId, j - 1 + counter * (numberPlayers - 1));
                    players.get(j).playAsTrader(goodsId, players.get(i), currentRound);
                }

                for (int j = 0; j < numberPlayers; j++) {
                    if (j != i) {
                        // se inspecteaza bag-urile traderilor
                        players.get(i).playAsSheriff(players.get(j).getBag(), players.get(j));
                    }
                }
                i++;
                counter++;

                for (Player player: players) {
                    player.getHand().removeAll(player.getHand());
                    player.getBag().removeAll(player.getBag());
                }
            }

            currentRound++;
            i = 0;

        }
        return players;
    }

    // se afiseaza clasamentul
    public static void endGame(final List<Player> players) {
        PlayerComparator playerComparator = new PlayerComparator();
        Collections.sort(players, playerComparator);

        for (Player player: players) {
            System.out.println(player.getInitialPosition() + " " + player.getStrategy()
                    + " " + player.getMoney());
        }
    }
}
