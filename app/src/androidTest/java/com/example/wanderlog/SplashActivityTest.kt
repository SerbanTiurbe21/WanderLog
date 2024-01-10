package com.example.wanderlog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wanderlog.activities.splash.SplashActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.wanderlog.activities.signup.SignUpActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashActivityTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<SplashActivity>
            = ActivityScenarioRule(SplashActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testButtonStartsSignUpActivity() {
        onView(withId(R.id.btn_get_started)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(SignUpActivity::class.java.name))
    }
}