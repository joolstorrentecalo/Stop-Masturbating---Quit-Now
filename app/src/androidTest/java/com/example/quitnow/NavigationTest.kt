package com.example.quitnow

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.quitnow.ui.home.HomeFragment
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class NavigationTest {

    @Test
    fun toolbarMenu_clickOnSettingsItem_OpensSettingsScreen() {
        val appContext = getInstrumentation().targetContext
        val activityScenario = launch(MainActivity::class.java)
        openActionBarOverflowOrOptionsMenu(appContext)
        onView(withText(R.string.menu_settings_title)).perform(click())
        activityScenario.close()
    }

    @Test
    fun testFirstRun() {
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        runOnUiThread {
            mockNavController.setGraph(R.navigation.nav_graph)
        }

        val scenario = launchFragmentInContainer {
            HomeFragment().also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(fragment.requireView(), mockNavController)
                    }
                }
            }
        }

        scenario.onFragment {
            assertThat(mockNavController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
        }
    }
}