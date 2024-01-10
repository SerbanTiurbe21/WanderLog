package com.example.wanderlog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wanderlog.activities.login.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<LoginActivity>
            = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testViewsAreDisplayed(){
        onView(withId(R.id.relativeLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_email)).check(matches(isDisplayed()))
        onView(withId(R.id.email_input_log_in)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_password)).check(matches(isDisplayed()))
        onView(withId(R.id.password_input_log_in)).check(matches(isDisplayed()))
        onView(withId(R.id.chk_terms_conditions)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_terms_conditions)).check(matches(isDisplayed()))
        onView(withId(R.id.or_layout_log_in)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_instagram)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_facebook)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_google)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_get_started_log_in)).check(matches(isDisplayed()))
    }
}