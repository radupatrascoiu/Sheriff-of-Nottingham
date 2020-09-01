package com.tema1.players;

import java.util.Comparator;

public class PlayerComparator implements Comparator<Player> {

    @Override
    public final int compare(final Player o1, final Player o2) {
        return o2.getMoney() - o1.getMoney();
    }
}
