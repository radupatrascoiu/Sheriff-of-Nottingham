package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import java.util.List;

public abstract class Player {

    private int money = Constants.INITIAL_MONEY;
    private int profit;
    private int penalty;
    private Function function;
    private int initialPosition;
    private String strategy;
    private List<Goods> stand;
    private List<Goods> hand;
    private List<Goods> bag;

    public final List<Goods> getHand() {
        return hand;
    }

    public final List<Goods> getStand() {
        return stand;
    }

    public final int getInitialPosition() {
        return initialPosition;
    }

    public final void setInitialPosition(final int initialPosition) {
        this.initialPosition = initialPosition;
    }

    public final String getStrategy() {
        return strategy;
    }

    public Player(final Function function, final int position, final String strategy) {
        this.function = function;
        this.profit = 0;
        this.penalty = 0;
        hand = new LinkedList<Goods>();
        bag = new LinkedList<Goods>();
        stand = new LinkedList<Goods>();
        this.initialPosition = position;
        this.strategy = strategy;
    }

    public final void completeHand(final List<Integer> goodsId, final int index) {
        int startIndex = Constants.NUMBER_OF_CARDS * index;
        int finalIndex = startIndex + Constants.NUMBER_OF_CARDS;
        GoodsFactory gf = GoodsFactory.getInstance();
        for (int i = startIndex; i < finalIndex; ++i) {
            Goods good = gf.getGoodsById(goodsId.get(i));
            hand.add(good);
        }
    }
    /**
     * @param assetsId
     * @param sheriff
     * @param round
     */
    public void playAsTrader(final List<Integer> assetsId, final Player sheriff,
                                   final int round) {

        System.out.println("Trader");
    }
    /**
     * @param bagToInspect
     * @param playerToInspect
     */
    public void playAsSheriff(final List<Goods> bagToInspect, final Player playerToInspect) {
        System.out.println("Sheriff");
    }

    public final int getMoney() {
        return money;
    }

    public final void calculateMoney() {
        money = money + profit - penalty;
    }

    public final void setFunction(final Function function) {
        this.function = function;
    }

    public final Function getFunction() {
        return function;
    }

    public final List<Goods> getBag() {
        return bag;
    }

    public final void setProfit(final int profit) {
        this.profit = profit;
    }

    public final int getProfit() {
        return profit;
    }

    public final int getPenalty() {
        return penalty;
    }

    public final void addProfit(final int profitToPut) {
        this.profit += profitToPut;
    }

    public final void addPenalty(final int penaltyToPut) {
        this.penalty += penaltyToPut;
    }

    public final void setPenalty(final int penalty) {
        this.penalty = penalty;
    }

    @Override
    public final String toString() {
        return initialPosition + " " + getStrategy() + " " + money;
    }
}
