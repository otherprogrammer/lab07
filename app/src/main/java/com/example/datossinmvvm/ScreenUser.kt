package com.example.datossinmvvm

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = crearDatabase(context)
    val dao = db.userDao()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                actions = {
                    IconButton(onClick = {
                        val user = User(0, firstName, lastName)
                        coroutineScope.launch {
                            AgregarUsuario(user, dao)
                        }
                        firstName = ""
                        lastName = ""
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Usuario")
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val data = getUsers(dao)
                            dataUser.value = data
                        }
                    }) {
                        Icon(Icons.Default.List, contentDescription = "Listar Usuarios")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                TextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID (solo lectura)") },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name: ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name:") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            eliminarUltimoUsuario(dao)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar Último Usuario", fontSize = 16.sp)
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = dataUser.value,
                    fontSize = 18.sp
                )
            }
        }
    )
}


@Composable
fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun getUsers(dao:UserDao): String {
    var rpta: String = ""
    //LaunchedEffect(Unit) {
    val users = dao.getAll()
    users.forEach { user ->
        val fila = user.firstName + " - " + user.lastName + "\n"
        rpta += fila
    }
    //}
    return rpta
}

suspend fun AgregarUsuario(user: User, dao:UserDao): Unit {
    //LaunchedEffect(Unit) {
    try {
        dao.insert(user)
    }
    catch (e: Exception) {
        Log.e("User","Error: insert: ${e.message}")
    }
    //}
}

suspend fun eliminarUltimoUsuario(dao: UserDao) {
    try {
        val users = dao.getAll()
        if (users.isNotEmpty()) {
            val ultimoUsuario = users.last()
            dao.delete(ultimoUsuario)
            Log.d("User", "Usuario eliminado: ${ultimoUsuario.firstName} ${ultimoUsuario.lastName}")
        }
    } catch (e: Exception) {
        Log.e("User", "Error eliminando usuario: ${e.message}")
    }
}


