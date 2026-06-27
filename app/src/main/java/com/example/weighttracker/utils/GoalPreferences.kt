package com.example.weighttracker.utils

import android.content.Context

class GoalPreferences(context: Context) {

    private val preferences =
        context.getSharedPreferences(
            "goal_pref",
            Context.MODE_PRIVATE
        )

    fun saveGoal(goal: Float) {
        preferences.edit()
            .putFloat("goal_weight", goal)
            .apply()
    }

    fun getGoal(): Float {
        return preferences.getFloat(
            "goal_weight",
            0f
        )
    }
}