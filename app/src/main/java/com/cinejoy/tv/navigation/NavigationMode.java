package com.cinejoy.tv.navigation;

public enum NavigationMode {
    BROWSING,
    SIDE_MENU,
    PLAYER;

    /**
     * Determines whether the side menu can be opened.
     */
    public boolean canOpenMenu() {
        return this != PLAYER;
    }

    /**
     * Determines whether WebView should receive D-pad events directly.
     */
    public boolean isWebViewFocusable() {
        return this == BROWSING;
    }
}
