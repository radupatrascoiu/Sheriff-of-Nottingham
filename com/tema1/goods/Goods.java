package com.tema1.goods;

import com.tema1.players.Player;

import java.util.List;

public abstract class Goods {
    private final int id;
    private final GoodsType type;
    private final int profit;
    private final int penalty;

    public Goods(final int id, final GoodsType type, final int profit, final int penalty) {
        this.id = id;
        this.type = type;
        this.profit = profit;
        this.penalty = penalty;
    }

    public final int getId() {
        return id;
    }

    public final GoodsType getType() {
        return type;
    }

    public final int getProfit() {
        return profit;
    }

    public final int getPenalty() {
        return penalty;
    }

    // returneaza frecventa unui bun legal intr-o lista de bunuri
    public final int getFrequency(final List<Goods> hand, final Goods good) {
        int freq = 0;
        for (Goods it: hand) {
            if (it.getId() == good.getId() && good.getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                freq++;
            }
        }
        return freq;
    }

    // se tranforma fiecare lucru ilegal in echivalentul lui
    public static void tranformIllegalGoods(final Player bribedPlayer) {
        List<Goods> stand = bribedPlayer.getStand();
        for (int i = 0; i < stand.size(); ++i) {
            if ((stand.get(i).getId() == GoodsFactory.IllegalGoodsIds.SILK)) {
                bribedPlayer.addProfit(GoodsFactory.AssetsProfit.SILK_PROFIT);
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.CHEESE)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.CHEESE)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.CHEESE)));
            } else if (stand.get(i).getId() == GoodsFactory.IllegalGoodsIds.PEPPER) {
                bribedPlayer.addProfit(GoodsFactory.AssetsProfit.PEPPER_PROFIT);
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.CHICKEN)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.CHICKEN)));
            } else if (stand.get(i).getId() == GoodsFactory.IllegalGoodsIds.BARREL) {
                bribedPlayer.addProfit(GoodsFactory.AssetsProfit.BARREL_PROFIT);
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.BREAD)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.BREAD)));
            } else if (stand.get(i).getId() == GoodsFactory.IllegalGoodsIds.BEER) {
                bribedPlayer.addProfit(GoodsFactory.AssetsProfit.BEER_PROFIT);
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.WINE)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.WINE)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.WINE)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.WINE)));
            } else if (stand.get(i).getId() == GoodsFactory.IllegalGoodsIds.SEAFOOD) {
                bribedPlayer.addProfit(GoodsFactory.AssetsProfit.SEAFOOD_PROFIT);
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.TOMATO)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.TOMATO)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.POTATO)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.POTATO)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.POTATO)));
                stand.add((GoodsFactory.getInstance().
                        getGoodsById(GoodsFactory.LegalGoodsIds.CHICKEN)));
            }
        }

        for (int i = 0; i < stand.size(); ++i) {
            if (stand.get(i).getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                stand.remove(stand.get(i));
                i--;
            }
        }
    }

    public static void transformIllegalGoodsForAllPlayers(final List<Player> players) {
        // se calculeaza Illegal Bonus
        for (Player player: players) {
            Goods.tranformIllegalGoods(player);
        }
    }

    @Override
    public final String toString() {
        return "Goods{"
                + "id=" + id
                + ", type=" + type
                + ", profit=" + profit
                + ", penalty=" + penalty
                + '}';
    }
}
