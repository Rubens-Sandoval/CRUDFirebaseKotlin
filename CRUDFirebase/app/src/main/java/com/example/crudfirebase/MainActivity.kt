package com.example.crudfirebase

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crudfirebase.ui.theme.CRUDFirebaseTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class MainActivity : ComponentActivity() {
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CRUDFirebaseTheme {
                MyAppContent(db)
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun MyAppContent(db: FirebaseFirestore) {
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Input de e-mail
            Box(modifier = Modifier.padding(35.dp, 95.dp, 35.dp, 15.dp)) {
                Input(label = "E-mail", value = email, onValueChange = setEmail)
            }

            // Input de senha
            Box(modifier = Modifier.padding(35.dp, 15.dp)) {
                Input(label = "Senha", value = password, onValueChange = setPassword)
            }

            // Botão de Sign In
            Button(
                modifier = Modifier
                    .padding(35.dp, 25.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 60.dp)
                    .shadow(10.dp, shape = RoundedCornerShape(10.dp), spotColor = Color.Black),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    disabledContentColor = Color.Black,
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val user = hashMapOf(
                        "email" to email,
                        "password" to password
                    )

                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener {
                            // Success
                        }
                        .addOnFailureListener {
                            // Error
                        }
                }
            ) {
                Text(text = "Cadastrar", style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                ))
            }
            listUsers(db)
        }
    }
}

@Composable
fun Input(label: String, value: String, onValueChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val isFocused = remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        maxLines = 1,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocused.value = focusState.isFocused
            }
            .border(
                border = if (isFocused.value) BorderStroke(2.dp, Color.LightGray) else BorderStroke(0.dp, Color.Transparent),
                shape = RoundedCornerShape(10.dp),
            )
            .fillMaxWidth()
            .defaultMinSize(minHeight = 64.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.LightGray,
            unfocusedContainerColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun listUsers(db: FirebaseFirestore){
    Row (
        Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ){
        Column(
            Modifier
                .fillMaxWidth(0.5f)
        ) {
            Text(text = "Email:")
        }
        Column(
            Modifier
                .fillMaxWidth(0.5f)
        ) {
            Text(text = "Password:")
        }
    }

    Row (
        Modifier
            .fillMaxWidth()
            .padding(start = 24.dp)
    ){
        val users = mutableStateListOf<HashMap<String, String>>()
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    val list = hashMapOf(
                        "email" to "${document.data.get("email")}",
                        "password" to "${document.data.get("password")}"
                    )
                    users.add(list)
                }
            }
        LazyColumn (state = users, modifier = Modifier.fillMaxWidth()){
            items(users) { user ->
                Row(modifier = Modifier.fillMaxWidth()){
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        Text(text = user["email"] ?: "--")
                    }
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        Text(text = user["password"] ?: "--")
                    }
                }
            }
        }

//        Column (
//            Modifier
//                .fillMaxWidth(0.3f)
//        ){
//            val users = mutableStateListOf<HashMap<String, String>>()
//            db.collection("users")
//                .get()
//                .addOnSuccessListener { documents ->
//                    for (document in documents){
//                        val list = hashMapOf(
//                            "email" to "${document.data.get("email")}",
//                            "password" to "${document.data.get("password")}"
//                        )
//                        users.add(list)
//                    }
//                }
//            LazyColumn (modifier = Modifier.fillMaxWidth()){
//                items(users) { user ->
//                    Row(modifier = Modifier.fillMaxWidth()){
//                        Column(modifier = Modifier.fillMaxWidth(0.5f)) {
//                            Text(text = user["email"] ?: "--")
//                        }
//                        Column(modifier = Modifier.fillMaxWidth(0.5f)) {
//                            Text(text = user["password"] ?: "--")
//                        }
//                    }
//                }
//            }
        }
    }

