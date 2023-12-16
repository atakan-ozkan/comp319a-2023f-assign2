package com.example.fitlifein30days

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.gson.Gson

class UserModelService : ViewModel() {
    var user = UserModel("", "", "",0.0, 0.0, 0, MutableList(30) { false } )
        private set

    private fun updateUser(name: String, surname: String, gender: String, weight: Double, height: Double, age: Int, progress:MutableList<Boolean>) {
        user = UserModel(name, surname,gender, weight, height, age,progress)
    }

    fun completeTraining(day: Int) {
        if (day in user.progress.indices) {
            user.progress[day] = true
        }
    }

    fun restartProgress(userModel: UserModel) {
        userModel.progress = MutableList(30) { false }
    }

    fun saveUser(context: Context, userModel: UserModel, saveMessage: String) {
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userJson = gson.toJson(userModel)
        editor.putString("UserData", userJson)
        editor.apply()

        Toast.makeText(context, saveMessage, Toast.LENGTH_SHORT).show()
    }


    fun loadData(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val userJson = sharedPreferences.getString("UserData", null) ?: return false

        val userModel = gson.fromJson(userJson, UserModel::class.java)
        if (userModel != null) {
            updateUser(userModel.name,userModel.surname,userModel.gender,
                userModel.weight,userModel.height,userModel.age,userModel.progress)

            return true
        }

        return false
    }

    fun deleteUser(context: Context, userModelService: UserModelService) {
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.remove("UserData")
        editor.apply()
        sharedPreferences.edit().clear().apply()
        userModelService.user = UserModel("", "", "", 0.0, 0.0, 0, MutableList(30) { false })
    }


}
