package com.example.fitlifein30days

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(userViewModel: UserViewModel, navController: NavController,context: Context) {
    var showMenu by remember { mutableStateOf(false) }
    val progressState = remember { mutableStateListOf<Boolean>().apply { addAll(userViewModel.user.progress) } }

    LaunchedEffect(userViewModel.user.progress) {
        progressState.clear()
        progressState.addAll(userViewModel.user.progress)
    }

    val topAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome, ${userViewModel.user.name}") },
                colors = topAppBarColors,
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Restart Progress") },
                            onClick = {
                                restartAndRefreshDays(navController,userViewModel,context)
                                showMenu = false
                            })
                        DropdownMenuItem(
                            text = { Text("Delete Account") },
                            onClick = {
                                logoutAndDeleteUser(navController,userViewModel,context)
                                showMenu = false
                            })
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            UserProfileSection(userViewModel = userViewModel)
            ListDays(progressState,onDayClick = { day,completed ->
                navController.navigate("dayDetail/$day/$completed")
            })
        }
    }
}

@Composable
fun UserProfileSection(userViewModel: UserViewModel) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Name: ${userViewModel.user.name}",style = MaterialTheme.typography.titleLarge)
        Text("Surname: ${userViewModel.user.surname}",style = MaterialTheme.typography.titleLarge)
        Text("Gender: ${userViewModel.user.gender}",style = MaterialTheme.typography.titleLarge)
        Text("Age: ${userViewModel.user.age}",style = MaterialTheme.typography.titleLarge)
        Text("Height: ${userViewModel.user.height} cm",style = MaterialTheme.typography.titleLarge)
        Text("Weight: ${userViewModel.user.weight} kg",style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun ListDays(progressState: List<Boolean>,onDayClick: (Int,Boolean) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(8.dp)
    ) {
        items(30) { day ->
            Button(
                onClick = {
                    val completed = progressState[day]
                    onDayClick(day+1,completed)
                },
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp)
            ) {
                if (progressState[day]) {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Completed",
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Day ${day + 1}")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(day: Int, onBack: () -> Unit, navController: NavController, completed : Boolean) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Day $day") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("DAY $day", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            if(completed){
                Button(onClick = { navController.navigate("workoutFlow/$day")},enabled = false) {
                    Text("Completed!")
                }
            }
            else{
                Button(onClick = { navController.navigate("workoutFlow/$day")},enabled = true) {
                    Text("Start Training")
                }
            }
        }
    }
}
private fun logoutAndDeleteUser(navController: NavController, userViewModel: UserViewModel,context: Context) {
    userViewModel.deleteUser(context,userViewModel)

    navController.navigate("registerScreen")
}

private fun restartAndRefreshDays(navController: NavController,userViewModel: UserViewModel,context: Context){
    userViewModel.restartProgress(userViewModel.user)
    userViewModel.saveUser(context,userViewModel.user,"Restart the progress!")
    navController.navigate("mainScreen")
}