package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;

import java.util.Collections;
import java.util.List;

public final class BribedPlayer extends BasePlayer {

    private int bribe;

    public BribedPlayer(final Function function, final int position, final String strategy) {
        super(function, position, strategy);
        this.bribe = 0;
    }

    // calculeaza mita aferenta sacului
    public int getBribe(final List<Goods> bag) {
        int count = 0;

        for (Goods good: bag) {
            if (good.getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                count++;
            }
        }

        if (count == 0) {
            bribe = 0;
        } else if (count <= 2 && count > 0) {
            bribe = Constants.MINUM_BRIBE;
        } else {
            bribe = Constants.MAXIMUM_BRIBE;
        }

        return bribe;
    }

    @Override
    public void playAsTrader(final List<Integer> assetsId, final Player sheriff, final int round) {
        List<Goods> bag = completeBag(round);
        if (sheriff.getStrategy().equalsIgnoreCase("basic")) {

            if (bag.size() != 0) {
                if (bag.get(bag.size() - 1).getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                    for (Goods good: bag) {
                        this.addPenalty(good.getPenalty()); // se scade penalty-ul
                        assetsId.add(good.getId()); // se adauga la sfarsitul pachetului initial
                    }

                } else if (bag.get(bag.size() - 1).getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                    if (bag.get(0).getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {

                        for (Goods good: bag) {
                            this.addProfit(good.getPenalty());
                            List<Goods> stand = getStand();
                            stand.add(good);
                        }
                    } else {
                        for (Goods good: bag) {
                            if (good.getId() != 0) {
                                this.addPenalty(good.getPenalty()); // se scade penalty-ul
                                assetsId.add(good.getId());
                                // se adauga la sfarsitul pachetului initial
                            } else {
                                List<Goods> stand = getStand();
                                stand.add(good);
                            }
                        }
                    }
                } else {
                    List<Goods> stand = getStand();
                    stand.addAll(bag); // se adauga bunul pe stand
                    this.addProfit(bag.get(bag.size() - 1).getPenalty() * bag.size());
                }
            }
        } else if (sheriff.getStrategy().equalsIgnoreCase("greedy")) {
            if (getBribe(bag) == 0) { // cazul in care are doar lucruri legale
                for (Goods good: bag) {
                    if (good.getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                        this.addProfit(good.getPenalty());
                        List<Goods> stand = getStand();
                        stand.add(good);
                    } else {
                        this.addPenalty(good.getPenalty());
                        assetsId.add(good.getId());
                    }
                }
            } else {
                this.addPenalty(getBribe(bag));
                List<Goods> stand = getStand();
                stand.addAll(bag);
            }

        } else if (sheriff.getStrategy().equalsIgnoreCase("bribed")) {
            if (bag.size() != 0) {

                if (bag.get(0).getId() <= GoodsFactory.LegalGoodsIds.SUGAR
                        && sheriff.getMoney() >= Constants.LIMIT_OF_MONEY_SHERIFF) {
                    List<Goods> stand = getStand();
                    stand.addAll(bag);
                    this.addProfit(bag.get(0).getPenalty() * bag.size());
                } else {
                    for (Goods good: bag) {
                        if (sheriff.getMoney() < Constants.LIMIT_OF_MONEY_SHERIFF) {
                            List<Goods> stand = getStand();
                            stand.addAll(bag);
                            break;
                        }

                        if (good.getId() != 0) {
                            this.addPenalty(good.getPenalty());
                            assetsId.add(good.getId());
                        } else {
                            List<Goods> stand = getStand();
                            stand.add(good);
                        }
                    }
                }
            }
        }

        super.calculateMoney();
        this.setPenalty(0); // pentru ca se calculeaza anterior
        this.setProfit(0);
    }

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

    // compare in functie de profit
    public int compare1(final Goods o1, final Goods o2) {
        if (o1.getProfit() == o2.getProfit()) {
            return o1.getId() - o2.getId();
        } else {
            return o1.getProfit() - o2.getProfit();
        }
    }

    @Override
    public List<Goods> completeBag(final int round) {
        List<Goods> hand = getHand();
        Collections.sort(hand, this::compare1);
        Collections.reverse(hand);
        List<Goods> bag = getBag();

        if (hand.get(0).getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
            bag = super.completeBag(round);
            return bag;
        }

        // se calculeaza penalty-ul posibil pentru a sti cate bunuri ilegale
        // trebuie puse in sac
        int possiblePenalty = 0;
        for (Goods good: hand) {
            if (bag.size() < Constants.MAXIMUM_SIZE_OF_BAG) {

                if (this.getMoney() - 2 == 0) {
                    bag = super.completeBag(round);
                    return bag;
                }

                if (this.getMoney() - getBribe(bag) < 0) {
                    break;
                } else {
                    if (this.getMoney() - possiblePenalty - good.getPenalty() > 0) {
                        possiblePenalty += good.getPenalty();
                        bag.add(good);
                    } else if (this.getMoney() - good.getPenalty() == 0) {
                        bag.add(good);
                    }
                }
            }
        }
        return bag;
    }

    @Override
    public void inspectBag(final List<Goods> bag, final Player player) {

        if (player.getStrategy().equalsIgnoreCase("greedy")) {
            if (bag.size() != 0) {
                Goods lastGood = bag.get(bag.size() - 1);
                if (lastGood.getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                    if (bag.size() == 1) { //daca e runda impara are doar unul singur
                        this.addProfit(lastGood.getPenalty());
                    } else { // pentru rundele pare
                        Goods lastButOne = bag.get(bag.size() - 2);
                        if (lastButOne.getId() >= GoodsFactory.IllegalGoodsIds.SILK) {
                            this.addProfit(lastGood.getPenalty());
                            this.addProfit(lastButOne.getPenalty());
                        } else {
                            this.addProfit(lastGood.getPenalty());
                        }
                    }
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
        } else if (player.getStrategy().equalsIgnoreCase("basic")) {

            if (bag.size() != 0) {
                Goods lastGood = bag.get(bag.size() - 1);
                if (lastGood.getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                    for (Goods good: bag) {
                        this.addPenalty(good.getPenalty());
                    }
                } else {
                    this.addProfit(lastGood.getPenalty());
                }
            }
        } else if (player.getStrategy().equalsIgnoreCase("bribed")) {

            if (bag.size() != 0) {

                if (bag.get(0).getId() <= GoodsFactory.LegalGoodsIds.SUGAR) {
                    this.addPenalty(bag.get(0).getPenalty() * bag.size());
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
