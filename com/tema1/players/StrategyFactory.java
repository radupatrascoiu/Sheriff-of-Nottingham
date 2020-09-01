package com.tema1.players;

public final class StrategyFactory {

    private StrategyFactory() {
    }

    public static Player setStrategy(final String strategyType, final int position) {
        // factory de Playeri
        if (strategyType == null) {
            return null;
        }
        if (strategyType.equalsIgnoreCase("basic")) {
            return new BasePlayer(Function.Trader, position, strategyType.toUpperCase());

        } else if (strategyType.equalsIgnoreCase("greedy")) {
            return new GreedyPlayer(Function.Trader, position, strategyType.toUpperCase());

        } else if (strategyType.equalsIgnoreCase("bribed")) {
            return new BribedPlayer(Function.Trader, position, strategyType.toUpperCase());
        }
        return null;
    }
}
