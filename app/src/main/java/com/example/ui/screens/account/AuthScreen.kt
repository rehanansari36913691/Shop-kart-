package com.example.ui.screens.account

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartRed
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel

@Composable
fun AuthScreen(
    viewModel: ShopViewModel,
    onAuthSuccess: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Create Account

    // Login state
    var loginIdentifier by remember { mutableStateOf("rehan@example.com") }
    var loginPassword by remember { mutableStateOf("pass123") }
    var isLoginPasswordVisible by remember { mutableStateOf(false) }

    // Signup state
    var signupName by remember { mutableStateOf("") }
    var signupEmail by remember { mutableStateOf("") }
    var signupPhone by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("") }
    var isSignupPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Brand Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShopKartAmber),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingCart, null, tint = ShopKartNavyDark, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Shop", fontSize = 24.sp, fontWeight = FontWeight.Black, color = ShopKartNavyDark)
            Text("Kart", fontSize = 24.sp, fontWeight = FontWeight.Black, color = ShopKartAmber)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = ShopKartAmber
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = ""
                        },
                        text = { Text("Sign In", fontWeight = FontWeight.Bold, color = ShopKartNavyDark) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = ""
                        },
                        text = { Text("Create Account", fontWeight = FontWeight.Bold, color = ShopKartNavyDark) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = ShopKartRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (selectedTab == 0) {
                    // LOGIN FORM
                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it },
                        label = { Text("Email or Mobile Number") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_identifier_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { isLoginPasswordVisible = !isLoginPasswordVisible }) {
                                Icon(
                                    if (isLoginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (isLoginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            errorMessage = ""
                            viewModel.login(
                                identifier = loginIdentifier,
                                pass = loginPassword,
                                onSuccess = onAuthSuccess,
                                onError = { errorMessage = it }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("login_submit_btn")
                    ) {
                        Text("Sign In", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Demo Credentials helper
                    Text(
                        text = "Demo customer account pre-filled for convenience.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    // SIGNUP FORM
                    OutlinedTextField(
                        value = signupName,
                        onValueChange = { signupName = it },
                        label = { Text("First and last name") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = signupEmail,
                        onValueChange = { signupEmail = it },
                        label = { Text("Email address") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_email_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = signupPhone,
                        onValueChange = { signupPhone = it.take(10) },
                        label = { Text("Mobile number (10-digits)") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = signupPassword,
                        onValueChange = { signupPassword = it },
                        label = { Text("Create password (at least 6 characters)") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { isSignupPasswordVisible = !isSignupPasswordVisible }) {
                                Icon(
                                    if (isSignupPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    null
                                )
                            }
                        },
                        visualTransformation = if (isSignupPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_password_input")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            errorMessage = ""
                            viewModel.signup(
                                name = signupName,
                                email = signupEmail,
                                phone = signupPhone,
                                pass = signupPassword,
                                onSuccess = onAuthSuccess,
                                onError = { errorMessage = it }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("signup_submit_btn")
                    ) {
                        Text("Create your ShopKart Account", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
