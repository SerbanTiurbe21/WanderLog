package com.example.wanderlog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wanderlog.activities.forgetpassword.ForgetPasswordActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForgetPasswordActivityTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<ForgetPasswordActivity>
            = ActivityScenarioRule(ForgetPasswordActivity::class.java)

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
        onView(withId(R.id.tv_forget_password)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_email_forget_password)).check(matches(isDisplayed()))
        onView(withId(R.id.email_input_forget_password)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_password_forget_password)).check(matches(isDisplayed()))
        onView(withId(R.id.password_input_forget_password)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_rePassword_forget_password)).check(matches(isDisplayed()))
        onView(withId(R.id.rePassword_input_forget_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_change_password)).check(matches(isDisplayed()))
    }
}