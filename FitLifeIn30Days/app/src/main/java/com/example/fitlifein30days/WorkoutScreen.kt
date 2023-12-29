package com.example.fitlifein30days

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun WorkoutFlowScreen(day: Int,navController: NavController,userViewModel: UserViewModel,workoutViewModel: WorkoutViewModel) {
    val workouts = workoutViewModel.getWorkoutSchedule(day,userViewModel.user.gender) ?: emptyList()

    var currentWorkoutIndex by remember { mutableStateOf(0) }
    val progress = (currentWorkoutIndex + 1).toFloat() / workouts.size

    Column {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Text(text = workouts[currentWorkoutIndex].title ,style = MaterialTheme.typography.labelLarge)
        Image(
            painter = painterResource(id = workouts[currentWorkoutIndex].imageResourceId),
            contentDescription = "Current Workout",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (currentWorkoutIndex > 0) currentWorkoutIndex-- },
                enabled = currentWorkoutIndex > 0
            ) {
                Text("Previous")
            }

            if (currentWorkoutIndex < workouts.lastIndex) {
                Button(
                    onClick = { currentWorkoutIndex++ }
                ) {
                    Text("Next Workout")
                }
            } else {
                Button(
                    onClick = {
                        navController.navigate("workoutResult/$day")
                    }
                ) {
                    Text("Finish Workout")
                }
            }
        }
    }
}

@Composable
fun WorkoutsResultScreen(day: Int, navController: NavController, userViewModel: UserViewModel,context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Congratulations, you've finished the training!", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(25.dp))
        Button(
            onClick = {
                var index= day-1
                if(index < 0){
                    index = 0
                }
                userViewModel.completeTraining(index)
                saveData("The training is completed!",userViewModel,context)
                val completed = true
                navController.navigate("dayDetail/$day/$completed")
            }
        ) {
            Text("Go back to day")
        }
    }
}

private fun saveData(saveMessage: String,userViewModel: UserViewModel,context: Context) {
    userViewModel.saveUser(context,userViewModel.user,saveMessage)
}