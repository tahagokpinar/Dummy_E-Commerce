package com.example.afinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.afinal.configs.ApiClient
import com.example.afinal.models.LoginRequest
import com.example.afinal.models.LoginResponse
import com.example.afinal.services.IDummyService
import com.example.afinal.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {

    lateinit var iDummyService: IDummyService
    lateinit var sharedPreferences: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedPreferences = SharedPref(this)
        checkUserLoggedIn()

        setContent {
            LoginScreenContent()
        }
    }

    private fun checkUserLoggedIn() {
        if (sharedPreferences.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun LoginScreenContent() {

        var username by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var passwordVisibility by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Online Shopping",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Login",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                textStyle = TextStyle.Default.copy(fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                textStyle = TextStyle.Default.copy(fontSize = 18.sp),
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisibility) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    login(username, password, this@LoginActivity)
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF2E6EFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "Login", color = Color.White)
            }

            Text(
                text = "or",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp)
            ){
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Login",
                        modifier = Modifier.size(40.dp))
                }
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_apple),
                        contentDescription = "Apple Login",
                        modifier = Modifier.size(40.dp)
                    )
                }
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook Login",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }

    private fun login(username: String, password: String, context : Context) {
        iDummyService = ApiClient.getClient().create(IDummyService::class.java)
        val loginRequest = LoginRequest(username, password)

        iDummyService.login(loginRequest).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(p0: Call<LoginResponse>, p1: Response<LoginResponse>) {
                if (p1.isSuccessful){
                    val loginResponse = p1.body()
                    sharedPreferences.setJwtToken(loginResponse?.token.toString())
                    sharedPreferences.setLoggedIn(true)

                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    finish()
                } else{
                    Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<LoginResponse>, p1: Throwable) {
               Toast.makeText(context, "Login failed : ${p1.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
