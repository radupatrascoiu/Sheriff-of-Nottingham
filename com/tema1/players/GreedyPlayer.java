package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;

import java.util.List;

public final class GreedyPlayer extends BasePlayer {

    public GreedyPlayer(final Function function, final int position, final String strategy) {
        super(function, position, strategy);
    }


    @Override
    public List<Goods> completeBag(final int round) {
        List<Goods> bag = super.completeBag(round);
        if (round % 2 == 0 && bag.size() < Constants.MAXIMUM_SIZE_OF_BAG) {
            List<Goods> hand = this.getHand();
            Goods illegalGoodToPut = getMaxByProfit(hand);
            if (illegalGoodToPut.getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                bag.add(illegalGoodToPut);
            }
        }
        return bag;
    }

    @Override
    public void playAsTrader(final List<Integer> assetsId, final Player sheriff, final int round) {
        List<Goods> bag = completeBag(round);
        List<Goods> stand = getStand();
        int verif = 0;
        if (round % 2 == 0) {
            for (Goods good: bag) {

                if (sheriff.getMoney() < Constants.LIMIT_OF_MONEY_SHERIFF) {
                    stand.addAll(bag);
                    break;
                }

                if (good.getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                    stand.add(good);
                } else {
                    this.addPenalty(good.getPenalty());
                    assetsId.add(good.getId());
                    verif = 1;
                }
            }
            if (verif == 0 && sheriff.getMoney() >= Constants.LIMIT_OF_MONEY_SHERIFF) {
                this.addProfit(bag.get(bag.size() - 1).getPenalty() * bag.size());
            }
        } else {
            if (bag.size() != 0) {
                if (bag.get(bag.size() - 1).getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                    // daca e un singur bun ilegal(mai multe nu pot fi)
                    if (sheriff.getMoney() < Constants.LIMIT_OF_MONEY_SHERIFF) {
                        stand.addAll(bag);
                    } else {
                        this.addPenalty(bag.get(bag.size() - 1).getPenalty());
                        // se scade penalty-ul
                        assetsId.add(bag.get(bag.size() - 1).getId());
                        // se adauga la sfarsitul pachetului initial
                    }
                } else {
                    stand.addAll(bag); // se adauga bunul pe stand
                    this.addProfit(bag.get(bag.size() - 1).getPenalty() * bag.size());
                }
            }
        }
        super.calculateMoney();
        this.setPenalty(0);
        this.setProfit(0);

    }

    @Override
    public void playAsSheriff(final List<Goods> bagToInspect, final Player playerToInspect) {
        this.inspectBag(bagToInspect, playerToInspect);
        super.calculateMoney();
        this.setPenalty(0); // pentru ca se calculeaza anterior
        this.setProfit(0); // pentru ca se calculeaza dupa
    }

    @Override
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
                if (((BribedPlayer) player).getBribe(bag) != 0) {
                    this.addProfit(((BribedPlayer) player).getBribe(player.getBag()));
                } else {
                    for (Goods good: bag) {
                        if (good.getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                            this.addPenalty(good.getPenalty());
                        } else {
                            this.addProfit(good.getPenalty());
                        }
                    }
                }
            }
        }
    }
}
