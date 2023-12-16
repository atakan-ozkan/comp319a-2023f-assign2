package com.example.fitlifein30days

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitlifein30days.ui.theme.FitLifeIn30DaysTheme

class MainActivity : ComponentActivity() {
    private val userModelService = UserModelService()
    private var isUserValid by mutableStateOf(true)
    private lateinit var workoutModelService: WorkoutModelService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workoutModelService = WorkoutModelService(this)
        setContent {
            FitLifeIn30DaysTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    isUserValid = userModelService.loadData(this)
                    NavigateStartApp(userModelService = userModelService, isUserValid = isUserValid)
                }
            }
        }
    }


    @Composable
    fun NavigateStartApp(userModelService: UserModelService, isUserValid: Boolean) {
        val navController = rememberNavController()
        var startDestination ="register"

        if(isUserValid){
            startDestination = "mainScreen"
        }

        NavHost(navController = navController, startDestination = startDestination) {
            composable("register") { NameSurnameScreen(navController, userModelService) }
            composable("ageGender") { AgeGenderScreen(navController, userModelService) }
            composable("height") { HeightScreen(navController, userModelService) }
            composable("weight") { WeightScreen(navController, userModelService) }
            composable("mainScreen") { MainScreen(userModelService, navController) }
            composable("dayDetail/{day}/{completed}") { backStackEntry ->
                DayDetailScreen(
                    day = backStackEntry.arguments?.getString("day")?.toInt() ?: 1,
                    onBack = { navController.navigate("mainScreen") },
                    navController = navController,
                    completed = backStackEntry.arguments?.getString("completed")?.toBoolean() ?: false
                )
                }
            composable("workoutFlow/{day}") { backStackEntry ->
                WorkoutFlowScreen(
                    day = backStackEntry.arguments?.getString("day")?.toInt() ?: 1,
                    navController
                )
            }
            composable("workoutResult/{day}") { backStackEntry ->
                WorkoutsResultScreen(
                    day = backStackEntry.arguments?.getString("day")?.toInt() ?: 1,
                    navController,
                    userModelService
                )
            }
        }

    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NameSurnameScreen(navController: NavHostController, userModelService: UserModelService) {
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
                    navController.navigate("ageGender")
                    userModelService.user.name = name
                    userModelService.user.surname = surname
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
    fun AgeGenderScreen(navController: NavHostController, userModelService: UserModelService) {
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
                        userModelService.user.age = age.toInt()
                        userModelService.user.gender = selectedGender
                        navController.navigate("height")
                    }
                }, enabled = isAgeValid && genderOptions.contains(selectedGender)) {
                    Text("Next")
                }
            }
        }
    }



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HeightScreen(navController: NavHostController, userModelService: UserModelService) {
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
                            userModelService.user.height = height.toDoubleOrNull() ?: 0.0
                            navController.navigate("weight")
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
    fun WeightScreen(navController: NavHostController, userModelService: UserModelService) {
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
                        userModelService.user.weight = weight.toDoubleOrNull() ?: 0.0
                        saveData("Profile is saved!")
                        navController.navigate("mainScreen")
                    }
                }, enabled = isWeightValid) {
                    Text("Finish")
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(userModelService: UserModelService, navController: NavController) {
        var showMenu by remember { mutableStateOf(false) }
        val progressState = remember { mutableStateListOf<Boolean>().apply { addAll(userModelService.user.progress) } }

        LaunchedEffect(userModelService.user.progress) {
            progressState.clear()
            progressState.addAll(userModelService.user.progress)
        }

        val topAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Welcome, ${userModelService.user.name}") },
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
                                text = {Text("Restart Progress")},
                                onClick = {
                                    restartAndRefreshDays(navController)
                                    showMenu = false
                            })
                            DropdownMenuItem(
                                text = {Text("Delete Account")},
                                onClick = {
                                    logoutAndDeleteUser(navController)
                                    showMenu = false
                            })
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                UserProfileSection(userModelService = userModelService)
                ListDays(progressState,onDayClick = { day,completed ->
                        navController.navigate("dayDetail/$day/$completed")
                })
            }
        }
    }

    @Composable
    fun UserProfileSection(userModelService: UserModelService) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: ${userModelService.user.name}",style = MaterialTheme.typography.titleLarge)
            Text("Surname: ${userModelService.user.surname}",style = MaterialTheme.typography.titleLarge)
            Text("Gender: ${userModelService.user.gender}",style = MaterialTheme.typography.titleLarge)
            Text("Age: ${userModelService.user.age}",style = MaterialTheme.typography.titleLarge)
            Text("Height: ${userModelService.user.height} cm",style = MaterialTheme.typography.titleLarge)
            Text("Weight: ${userModelService.user.weight} kg",style = MaterialTheme.typography.titleLarge)
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
    fun DayDetailScreen(day: Int, onBack: () -> Unit, navController: NavController,completed : Boolean) {
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

    @Composable
    fun WorkoutFlowScreen(day: Int,navController: NavController) {
        val workouts = workoutModelService.getWorkoutSchedule(day,userModelService.user.gender) ?: emptyList()

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
    fun WorkoutsResultScreen(day: Int, navController: NavController, userModelService: UserModelService) {
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
                    userModelService.completeTraining(index)
                    saveData("The training is completed!")
                    val completed = true
                    navController.navigate("dayDetail/$day/$completed")
                }
            ) {
                Text("Go back to day")
            }
        }
    }

    private fun logoutAndDeleteUser(navController: NavController) {
        userModelService.deleteUser(this,userModelService)
        isUserValid = false
        navController.navigate("register")
    }


    private fun saveData(saveMessage: String) {
        userModelService.saveUser(this,userModelService.user,saveMessage)
    }

    private fun restartAndRefreshDays(navController: NavController){
        userModelService.restartProgress(userModelService.user)
        userModelService.saveUser(this,userModelService.user,"Restart the progress!")
        navController.navigate("mainScreen")
    }

    @Preview
    @Composable
    fun Preview() {
        val userModelService = UserModelService()
        NavigateStartApp(userModelService = userModelService, isUserValid = true)
    }


}