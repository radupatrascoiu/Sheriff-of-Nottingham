package com.tema1.main;

import java.util.List;

public final class Main {
    private Main() {
        // just to trick checkstyle
    }

    public static void main(final String[] args) {
        GameInputLoader gameInputLoader = new GameInputLoader(args[0], args[1]);
        GameInput gameInput = gameInputLoader.load();
        int numberPlayers = gameInput.getPlayerNames().size();
        int numberRounds = gameInput.getRounds();
        List<String> playerStrategies = gameInput.getPlayerNames();
        List<Integer> goodsId = gameInput.getAssetIds();

        List<Player> players = Game.startGame(numberPlayers, numberRounds, playerStrategies,
                goodsId);

        Goods.transformIllegalGoodsForAllPlayers(players);
        MoneyCalculator.bonusCalculator(players);
        MoneyCalculator.standProfitCalculator(players);

        Game.endGame(players);
    }
}
