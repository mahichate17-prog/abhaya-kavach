package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.model.EmergencyContact
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachDarkSurfaceVariant
import com.example.ui.theme.KavachEmergencyBg
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachLavender
import com.example.ui.theme.KavachLavenderBg
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachSafeGreenBg
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachTextSecondary
import com.example.ui.theme.KavachPowderBlueBg
import com.example.viewmodel.SafetyViewModel

@Composable
fun EmergencyContactsScreen(
    viewModel: SafetyViewModel,
    modifier: Modifier = Modifier
) {
    val contacts by viewModel.contacts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KavachDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = Modifier
                    .size(42.dp)
                    .background(KavachLavenderBg, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = KavachLavender
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Trusted Guardians",
                    color = KavachTextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "People who have your back",
                    color = KavachTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add Contact
        Button(
            onClick = { showAddDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = KavachCyanPrimary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(17.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "ADD TRUSTED CONTACT",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Simulated Test Alert
        OutlinedButton(
            onClick = { viewModel.testEmergencyAlertDispatch() },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = KavachLavender
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    1.dp,
                    KavachLavender.copy(alpha = 0.5f),
                    RoundedCornerShape(15.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "SIMULATE TEST ALERT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "YOUR GUARDIANS",
                color = KavachTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        KavachLavenderBg,
                        RoundedCornerShape(50.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${contacts.size}",
                    color = KavachLavender,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (contacts.isEmpty()) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                KavachPowderBlueBg,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = KavachCyanPrimary,
                            modifier = Modifier.size(27.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No guardians yet",
                        color = KavachTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Add someone you trust to your safety network.",
                        color = KavachTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

        } else {

            contacts.forEach { contact ->

                ContactCard(
                    contact = contact,
                    onSetPrimary = {
                        viewModel.setPrimaryContact(contact.id)
                    },
                    onDelete = {
                        viewModel.removeContact(contact.id)
                    },
                    onCall = {
                        try {
                            val clean = contact.phone.replace(" ", "")
                            val intent = Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse("tel:$clean")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            viewModel.showToast(
                                "Opening dialer for ${contact.phone}"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Helplines
        Text(
            text = "EMERGENCY HELPLINES",
            color = KavachTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        HelplineCard(
            title = "National Emergency Response",
            subtitle = "Police • Fire • Ambulance",
            number = "112",
            context = context
        )

        Spacer(modifier = Modifier.height(8.dp))

        HelplineCard(
            title = "Women in Distress",
            subtitle = "National Commission",
            number = "1091",
            context = context
        )

        Spacer(modifier = Modifier.height(8.dp))

        HelplineCard(
            title = "Women Helpline",
            subtitle = "Domestic Abuse / Harassment",
            number = "181",
            context = context
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showAddDialog) {
        AddContactDialog(
            onDismiss = {
                showAddDialog = false
            },
            onAdd = { name, phone, relationship, isPrimary ->
                viewModel.addContact(
                    name,
                    phone,
                    relationship,
                    isPrimary
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ContactCard(
    contact: EmergencyContact,
    onSetPrimary: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (contact.isPrimary)
                    KavachCyanPrimary.copy(alpha = 0.45f)
                else
                    KavachDarkCardBorder,
                RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (contact.isPrimary)
                            KavachLavenderBg
                        else
                            KavachPowderBlueBg,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = if (contact.isPrimary)
                        KavachLavender
                    else
                        KavachCyanPrimary,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = contact.name,
                        color = KavachTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (contact.isPrimary) {
                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .background(
                                    KavachLavenderBg,
                                    RoundedCornerShape(50.dp)
                                )
                                .padding(
                                    horizontal = 7.dp,
                                    vertical = 3.dp
                                )
                        ) {
                            Text(
                                text = "PRIMARY",
                                color = KavachLavender,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = contact.phone,
                    color = KavachTextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = contact.relationship,
                    color = KavachTextMuted,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        KavachSafeGreenBg,
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Call,
                    contentDescription = "Call",
                    tint = KavachSafeGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onSetPrimary,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = if (contact.isPrimary)
                        Icons.Default.Star
                    else
                        Icons.Default.StarOutline,
                    contentDescription = "Primary",
                    tint = if (contact.isPrimary)
                        KavachLavender
                    else
                        KavachTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = KavachEmergencyRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun HelplineCard(
    title: String,
    subtitle: String,
    number: String,
    context: android.content.Context
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        KavachEmergencyBg,
                        CircleShape
                    )
            ) {
                Text(
                    text = "!",
                    color = KavachEmergencyRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = KavachTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    color = KavachTextSecondary,
                    fontSize = 10.sp
                )

                Text(
                    text = number,
                    color = KavachEmergencyRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    try {
                        val intent = Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:$number")
                        )
                        context.startActivity(intent)
                    } catch (_: Exception) {
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = KavachEmergencyBg,
                    contentColor = KavachEmergencyRed
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = "CALL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (
        name: String,
        phone: String,
        relationship: String,
        isPrimary: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Family") }
    var isPrimary by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Add Trusted Guardian",
                color = KavachTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
        },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    "Full Name",
                    color = KavachTextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = {
                        Text(
                            "e.g. Mom",
                            color = KavachTextMuted
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KavachCyanPrimary,
                        unfocusedBorderColor = KavachDarkCardBorder,
                        focusedTextColor = KavachTextPrimary,
                        unfocusedTextColor = KavachTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Phone Number",
                    color = KavachTextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = {
                        Text(
                            "e.g. +91 98765 43210",
                            color = KavachTextMuted
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KavachCyanPrimary,
                        unfocusedBorderColor = KavachDarkCardBorder,
                        focusedTextColor = KavachTextPrimary,
                        unfocusedTextColor = KavachTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Relationship",
                    color = KavachTextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    placeholder = {
                        Text(
                            "Mother, Sister, Friend...",
                            color = KavachTextMuted
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KavachCyanPrimary,
                        unfocusedBorderColor = KavachDarkCardBorder,
                        focusedTextColor = KavachTextPrimary,
                        unfocusedTextColor = KavachTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = KavachCyanPrimary,
                            uncheckedColor = KavachDarkCardBorder
                        )
                    )

                    Text(
                        text = "Set as primary guardian",
                        color = KavachTextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    if (
                        name.isNotBlank() &&
                        phone.isNotBlank()
                    ) {
                        onAdd(
                            name,
                            phone,
                            relationship,
                            isPrimary
                        )
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KavachCyanPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "Save Guardian",
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "Cancel",
                    color = KavachTextMuted
                )
            }
        },

        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp)
    )
}
