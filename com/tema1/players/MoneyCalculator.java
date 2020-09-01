package com.tema1.players;

import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.LegalGoods;

import java.util.List;

public final class MoneyCalculator {

    private MoneyCalculator() {
    }

    //returneaza frecventa unui bun intr-o lista de bunuri stiindu-i ID-ul
    private static int getFrequency(final List<Goods> hand, final int id) {
        int freq = 0;
        for (Goods it: hand) {
            if (it.getId() == id) {
                freq++;
            }
        }
        return freq;
    }

    // se calculeaza King si Queen Bonus
    public static void bonusCalculator(final List<Player> players) {
        int king = 0;
        int queen = 0;
        Player kingPlayer = null;
        Player queenPlayer = null;
        for (int i = 0; i <= GoodsFactory.LegalGoodsIds.SUGAR; i++) {
            kingPlayer = null;
            queenPlayer = null;
            king = 0;
            queen = 0;
            for (Player player: players) {
                if (getFrequency(player.getStand(), i) > king) {
                    queenPlayer = kingPlayer;
                    queen = king;
                    kingPlayer = player;
                    king = getFrequency(player.getStand(), i);
                } else if (getFrequency(player.getStand(), i) <= king
                        && getFrequency(player.getStand(), i) > queen) {
                    queenPlayer = player;
                    queen = getFrequency(player.getStand(), i);
                }
            }
            if (kingPlayer != null) {
                kingPlayer.addProfit(((LegalGoods) GoodsFactory.getInstance().
                        getGoodsById(i)).getKingBonus());
            }


            if (queenPlayer != null) {
                queenPlayer.addProfit(((LegalGoods) GoodsFactory.getInstance().
                        getGoodsById(i)).getQueenBonus());
            }
        }
    }

    public static void standProfitCalculator(final List<Player> players) {
        for (Player player: players) {
            for (Goods good: player.getStand()) {
                player.addProfit(good.getProfit());
            }
            player.calculateMoney(); // se calculeza banii pentru fiecare jucator in parte
        }
    }
}
