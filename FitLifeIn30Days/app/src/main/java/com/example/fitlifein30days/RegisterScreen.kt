package com.example.fitlifein30days
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameSurnameScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var isNameValid by remember { mutableStateOf(false) }
    var isSurnameValid by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            Text(text = "FIT LIFE IN 30 DAYS APP", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(15.dp))
            TextField(
                value = name,
                onValueChange = {
                    name = it
                    isNameValid  = try {
                        it.isNotBlank() && it != ""
                    } catch (e: Exception) { false } },
                label = { Text("Name") }
            )
            TextField(
                value = surname,
                onValueChange = {
                    surname = it
                    isSurnameValid  = try {
                        it.isNotBlank() && it != ""
                    } catch (e: Exception) { false }
                },
                label = { Text("Surname") }
            )
            if (!isNameValid || !isSurnameValid) {
                Text("Please enter your name and surname", color = Color.Gray)
            }
            Button(onClick = {
                if(isNameValid && isSurnameValid){
                    navController.navigate("ageGenderScreen")
                    userViewModel.user.name = name
                    userViewModel.user.surname = surname
                }
            }
                ,
                enabled = (isNameValid && isSurnameValid)
            ) {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeGenderScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var age by remember { mutableStateOf("") }
    var isAgeValid by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female")
    var selectedGender by remember { mutableStateOf(genderOptions[0]) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(value = age, onValueChange = {
                age = it
                isAgeValid = try {
                    it.isNotBlank() && it.toIntOrNull() != null && it.toInt() > 0
                } catch (e: NumberFormatException) {
                    false
                }

            }, label = { Text("Age") }
            )
            if (!isAgeValid) {
                Text("Please enter a valid age", color = Color.Gray)
            }

            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedGender)
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    genderOptions.forEach { gender ->
                        DropdownMenuItem(text = { Text(gender) }, onClick = {
                            selectedGender = gender
                            expanded = false
                        })
                    }
                }
            }

            Button(onClick = {
                if (isAgeValid && genderOptions.contains(selectedGender)) {
                    userViewModel.user.age = age.toInt()
                    userViewModel.user.gender = selectedGender
                    navController.navigate("heightScreen")
                }
            }, enabled = isAgeValid && genderOptions.contains(selectedGender)) {
                Text("Next")
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeightScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var height by remember { mutableStateOf("") }
    var isHeightValid by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = height,
                onValueChange = {
                    height = it
                    isHeightValid = try {
                        it.isNotBlank() && it.toDoubleOrNull() != null && it.toDouble() >= 140.0
                                && it.toDouble() <= 230.0
                    } catch (e: NumberFormatException) {
                        false
                    }
                },
                label = { Text("Height") }
            )
            if (!isHeightValid) {
                Text(
                    "Please enter height between 140 cm and 230 cm",
                    color = Color.Gray
                )
            }
            Button(
                onClick = {
                    if (isHeightValid) {
                        userViewModel.user.height = height.toDoubleOrNull() ?: 0.0
                        navController.navigate("weightScreen")
                    }
                }, enabled = isHeightValid
            ) {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(navController: NavHostController, userViewModel: UserViewModel,context: Context) {
    var weight by remember { mutableStateOf("") }
    var isWeightValid by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = weight,
                onValueChange = {
                    weight = it
                    isWeightValid = try {
                        it.isNotBlank() && it.toDoubleOrNull() != null && it.toDouble() >= 40.0
                                && it.toDouble() <= 200.0
                    } catch (e: NumberFormatException) {
                        false
                    }
                },
                label = { Text("Weight") }
            )
            if (!isWeightValid) {
                Text("Please enter weight between 40 kg and 200 kg", color = Color.Gray)
            }
            Button(onClick = {
                if (isWeightValid) {
                    userViewModel.user.weight = weight.toDoubleOrNull() ?: 0.0
                    saveData("Profile is saved!",userViewModel, context)
                    navController.navigate("mainScreen")
                }
            }, enabled = isWeightValid) {
                Text("Finish")
            }
        }
    }
}
private fun saveData(saveMessage: String,userViewModel: UserViewModel,context: Context) {
    userViewModel.saveUser(context,userViewModel.user,saveMessage)
}