package com.cinejoy.tv;

import static org.junit.Assert.*;

import com.cinejoy.tv.navigation.NavigationMode;
import com.cinejoy.tv.navigation.NavigationStateManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class NavigationModeTest {

    private NavigationStateManager stateManager;

    @Before
    public void setUp() {
        stateManager = new NavigationStateManager();
    }

    @Test
    public void testInitialStateIsBrowsing() {
        assertEquals(NavigationMode.BROWSING, stateManager.getCurrentMode());
        assertTrue(stateManager.getCurrentMode().isWebViewFocusable());
    }

    @Test
    public void testOpenMenuFromBrowsing() {
        boolean success = stateManager.openMenu();
        assertTrue(success);
        assertEquals(NavigationMode.SIDE_MENU, stateManager.getCurrentMode());
        assertFalse(stateManager.getCurrentMode().isWebViewFocusable());
    }

    @Test
    public void testCloseMenuReturnsToBrowsing() {
        stateManager.openMenu();
        boolean success = stateManager.closeMenu();
        assertTrue(success);
        assertEquals(NavigationMode.BROWSING, stateManager.getCurrentMode());
    }

    @Test
    public void testPlayerModePreventsOpeningMenu() {
        stateManager.enterPlayerMode();
        assertEquals(NavigationMode.PLAYER, stateManager.getCurrentMode());

        boolean success = stateManager.openMenu();
        assertFalse(success);
        assertEquals(NavigationMode.PLAYER, stateManager.getCurrentMode());
    }

    @Test
    public void testExitingPlayerModeRestoresBrowsingState() {
        stateManager.enterPlayerMode();
        stateManager.exitPlayerMode();
        assertEquals(NavigationMode.BROWSING, stateManager.getCurrentMode());

        boolean success = stateManager.openMenu();
        assertTrue(success);
    }
}
