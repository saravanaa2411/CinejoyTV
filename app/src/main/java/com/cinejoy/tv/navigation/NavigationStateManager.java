package com.cinejoy.tv.navigation;

public class NavigationStateManager {

    private NavigationMode currentMode = NavigationMode.BROWSING;

    public NavigationMode getCurrentMode() {
        return currentMode;
    }

    public boolean openMenu() {
        if (currentMode.canOpenMenu()) {
            currentMode = NavigationMode.SIDE_MENU;
            return true;
        }
        return false;
    }

    public boolean closeMenu() {
        if (currentMode == NavigationMode.SIDE_MENU) {
            currentMode = NavigationMode.BROWSING;
            return true;
        }
        return false;
    }

    public void enterPlayerMode() {
        currentMode = NavigationMode.PLAYER;
    }

    public void exitPlayerMode() {
        currentMode = NavigationMode.BROWSING;
    }
}
