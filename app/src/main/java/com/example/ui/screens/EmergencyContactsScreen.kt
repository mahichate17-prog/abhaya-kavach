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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachTextSecondary
import com.example.ui.theme.KavachWarningAmber
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
        // Navigation Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = Modifier
                    .size(40.dp)
                    .background(KavachDarkSurface, CircleShape)
                    .border(1.dp, KavachDarkCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = KavachTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Emergency Contacts",
                    color = KavachTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Trusted guardians notified during anomalies & SOS",
                    color = KavachTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add Contact Primary Button
        Button(
            onClick = { showAddDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = KavachCyanPrimary,
                contentColor = KavachDarkBg
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ADD TRUSTED CONTACT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Simulate Test Alert to All Contacts
        OutlinedButton(
            onClick = { viewModel.testEmergencyAlertDispatch() },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = KavachCyanPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .border(1.dp, KavachCyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
              Text(
    "Simulate Test SMS to All Contacts",
    fontSize = 12.sp,
    fontWeight = FontWeight.Bold
)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // List of Contacts
        Text(
            text = "TRUSTED GUARDIANS (${contacts.size})",
            color = KavachTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (contacts.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = KavachDarkSurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KavachDarkCardBorder, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No emergency contacts configured yet.",
                        color = KavachTextMuted,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add at least one trusted contact to receive automated route anomaly alerts.",
                        color = KavachTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            contacts.forEach { contact ->
                ContactCard(
                    contact = contact,
                    onSetPrimary = { viewModel.setPrimaryContact(contact.id) },
                    onDelete = { viewModel.removeContact(contact.id) },
                    onCall = {
                        try {
                            val clean = contact.phone.replace(" ", "")
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            viewModel.showToast("Opening dialer for ${contact.phone}")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Official Helplines Directory
        Text(
            text = "NATIONAL EMERGENCY HELPLINES",
            color = KavachTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        HelplineCard(title = "National Emergency Response (Police / Fire / EMS)", number = "112", context = context)
        Spacer(modifier = Modifier.height(8.dp))
        HelplineCard(title = "Women in Distress Helpline (National Commission)", number = "1091", context = context)
        Spacer(modifier = Modifier.height(8.dp))
        HelplineCard(title = "Women Helpline (Domestic Abuse / Harassment)", number = "181", context = context)
    }

    // Add Contact Dialog
    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone, relationship, isPrimary ->
                viewModel.addContact(name, phone, relationship, isPrimary)
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
        colors = CardDefaults.cardColors(containerColor = KavachDarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (contact.isPrimary) KavachCyanPrimary.copy(alpha = 0.6f) else KavachDarkCardBorder,
                RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            if (contact.isPrimary) KavachCyanPrimary.copy(alpha = 0.15f) else KavachDarkSurfaceVariant,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (contact.isPrimary) KavachCyanPrimary else KavachTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    .background(KavachCyanPrimary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "PRIMARY", color = KavachCyanPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${contact.phone} • ${contact.relationship}",
                        color = KavachTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Call icon
                IconButton(
                    onClick = onCall,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = KavachSafeGreen, modifier = Modifier.size(18.dp))
                }

                // Primary toggle star
                IconButton(
                    onClick = onSetPrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (contact.isPrimary) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = "Primary",
                        tint = if (contact.isPrimary) KavachCyanPrimary else KavachTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete icon
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = KavachEmergencyRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun HelplineCard(
    title: String,
    number: String,
    context: android.content.Context
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = KavachDarkSurfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = KavachTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Toll-Free 24x7: $number", color = KavachCyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                        context.startActivity(intent)
                    } catch (e: Exception) { }
                },
                colors = ButtonDefaults.buttonColors(containerColor = KavachDarkSurface, contentColor = KavachCyanPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(36.dp)
                    .border(1.dp, KavachCyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Text("Dial $number", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String, relationship: String, isPrimary: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Family") }
    var isPrimary by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Emergency Contact",
                color = KavachTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Full Name", color = KavachTextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("e.g. Ananya Sharma", color = KavachTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = KavachDarkSurface,
                        unfocusedContainerColor = KavachDarkSurface,
                        focusedBorderColor = KavachCyanPrimary,
                        unfocusedBorderColor = KavachDarkCardBorder,
                        focusedTextColor = KavachTextPrimary,
                        unfocusedTextColor = KavachTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Phone Number", color = KavachTextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("e.g. +91 98765 43210", color = KavachTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = KavachDarkSurface,
                        unfocusedContainerColor = KavachDarkSurface,
                        focusedBorderColor = KavachCyanPrimary,
                        unfocusedBorderColor = KavachDarkCardBorder,
                        focusedTextColor = KavachTextPrimary,
                        unfocusedTextColor = KavachTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Relationship", color = KavachTextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    placeholder = { Text("e.g. Mother, Sister, Friend", color = KavachTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = KavachDarkSurface,
                        unfocusedContainerColor = KavachDarkSurface,
                        focusedBorderColor = KavachCyanPrimary,
                        unfocusedBorderColor = KavachDarkCardBorder,
                        focusedTextColor = KavachTextPrimary,
                        unfocusedTextColor = KavachTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = KavachCyanPrimary,
                            uncheckedColor = KavachDarkCardBorder
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Set as Primary Emergency Guardian", color = KavachTextPrimary, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onAdd(name, phone, relationship, isPrimary)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = KavachCyanPrimary, contentColor = KavachDarkBg)
            ) {
                Text("Save Contact", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KavachTextMuted)
            }
        },
        containerColor = KavachDarkSurfaceVariant,
        shape = RoundedCornerShape(16.dp)
    )
}
