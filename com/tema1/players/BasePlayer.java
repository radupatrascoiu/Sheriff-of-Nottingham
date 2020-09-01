package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class BasePlayer extends Player implements Comparator<Goods> {

    public BasePlayer(final Function function, final int position, final String strategy) {
        super(function, position, strategy);
    }
    /**
     * @param assetsId
     * @param sheriff
     * @param round
     */
    @Override
    public void playAsTrader(final List<Integer> assetsId, final Player sheriff, final int round) {
        List<Goods> bag = completeBag(round);
        if (bag.size() != 0) {

            if (sheriff.getMoney() < Constants.LIMIT_OF_MONEY_SHERIFF) {
                List<Goods> stand = getStand();
                stand.addAll(bag);
            } else {
                if (bag.get(bag.size() - 1).getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                    // daca e un singur bun ilegal(mai multe nu pot fi)
                    this.addPenalty(bag.get(bag.size() - 1).getPenalty()); // se scade penalty-ul
                    assetsId.add(bag.get(bag.size() - 1).getId());
                    // se adauga la sfarsitul pachetului initial
                } else {
                    List<Goods> stand = getStand();
                    stand.addAll(bag); // se adauga bunul pe stand
                    this.addProfit(bag.get(bag.size() - 1).getPenalty() * bag.size());
                }
            }
        }

        super.calculateMoney();
        this.setPenalty(0); // pentru ca se calculeaza anterior
        this.setProfit(0);
    }
    /**
     * @param bagToInspect
     * @param playerToInspect
     */
    @Override
    public void playAsSheriff(final List<Goods> bagToInspect, final Player playerToInspect) {

        if (this.getMoney() >= Constants.LIMIT_OF_MONEY_SHERIFF) {
            // doar daca ii permite bugetul inspecteaza traderii
            this.inspectBag(bagToInspect, playerToInspect);
        }

        super.calculateMoney();
        this.setPenalty(0); // pentru ca se calculeaza anterior
        this.setProfit(0);
    }
    /**
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public final int compare(final Goods o1, final Goods o2) { // comparator dupa cele 3 creterii
        if (o1.getFrequency(getHand(), o1) == o2.getFrequency(getHand(), o2)) {
            if (o1.getProfit() == o2.getProfit()) {
                return o1.getId() - o2.getId();
            } else {
                return o1.getProfit() - o2.getProfit();
            }
        } else {
            return (o1.getFrequency(getHand(), o1) - o2.getFrequency(getHand(), o2));
        }
    }

    public final void sortHand(final List<Goods> hand) {
        Collections.sort(hand, this::compare);
    }
    /**
     * @param round
     * @return
     */
    public List<Goods> completeBag(final int round) {
        List<Goods> hand = getHand();

        sortHand(hand);

        List<Goods> bag = getBag();
        Goods goodToPut = hand.get(hand.size() - 1);
        // daca nu are bunuri legale
        if (goodToPut.getId() >= GoodsFactory.IllegalGoodsIds.SILK
                && this.getMoney() > Constants.LIMIT_OF_MONEY_TRADER) {
            // adauga doar daca isi permite penalty-ul
            goodToPut = getMaxByProfit(hand);
            bag.add(goodToPut); // cel mai valoros bun ilegal
            hand.remove(goodToPut);
        }
        // daca are cel putin un bun legal
        if (bag.size() == 0) {
            int frequencyOfGood = goodToPut.getFrequency(hand, goodToPut);
            for (int i = 1; i <= frequencyOfGood; i++) {
                bag.add(goodToPut);
            }
        }
        return bag;
    }

    // returneaza bunul cu cel mai mare profit
    public final Goods getMaxByProfit(final List<Goods> goods) {
        int maxProfit = 0;
        int index = 0;

        for (int i = 0; i < goods.size(); ++i) {
            if (goods.get(i).getProfit() > maxProfit) {
                maxProfit = goods.get(i).getProfit();
                index = i;
            }
        }

        return goods.get(index);
    }
    /**
     * @param bag
     * @param player
     */
    public void inspectBag(final List<Goods> bag, final Player player) {

        if (player.getStrategy().equalsIgnoreCase("greedy")) {
            if (bag.size() != 0) {
                Goods lastGood = bag.get(bag.size() - 1);
                if (lastGood.getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                    if (bag.size() == 1) { //daca e runda impara are doar unul singur
                        this.addProfit(lastGood.getPenalty());
                    } else {
                        Goods lastButOne = bag.get(bag.size() - 2);
                        if (lastButOne.getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                            this.addProfit(lastGood.getPenalty());
                            this.addProfit(lastButOne.getPenalty());
                        } else {
                            this.addProfit(lastGood.getPenalty());
                        }
                    }
                } else {
                    this.addPenalty(lastGood.getPenalty() * bag.size());
                }

            }
        } else if (player.getStrategy().equalsIgnoreCase("basic")) {
            if (bag.size() != 0) {
                Goods good = bag.get(bag.size() - 1);
                if (good.getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                    this.addPenalty(good.getPenalty() * bag.size());
                } else {
                    this.addProfit(good.getPenalty());
                }
            }
        } else if (player.getStrategy().equalsIgnoreCase("bribed")) {
            if (bag.size() != 0) {

                if (bag.get(0).getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                    for (Goods good: bag) {
                        if (good.getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                            this.addPenalty(good.getPenalty());
                        } else {
                            this.addProfit(good.getPenalty());
                        }
                    }
                } else {
                    for (Goods good: bag) {
                        if (good.getId() != 0) {
                            this.addProfit(good.getPenalty());
                        }
                    }
                }
            }
        }
    }
}
