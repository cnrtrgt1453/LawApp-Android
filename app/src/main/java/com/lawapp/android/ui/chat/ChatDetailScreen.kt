package com.lawapp.android.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.lawapp.android.data.TokenManager
import com.lawapp.android.data.model.ChatMessageDto
import com.lawapp.android.ui.theme.LawAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    partnerName: String,
    partnerRole: String,
    leadTitle: String,
    messages: List<ChatMessageDto>,
    onSendMessage: (String) -> Unit,
    onBackClick: () -> Unit,
    myEmail: String = TokenManager.email ?: ""
) {
    var textState by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    // Mesaj geldiğinde otomatik en alta kaydır
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = partnerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = leadTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    // Küçük Rol Rozeti
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (partnerRole == "LAWYER")
                            Color(0xFF1565C0).copy(alpha = 0.15f)
                        else
                            Color(0xFF2E7D32).copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = if (partnerRole == "LAWYER") "Avukat" else "Müvekkil",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (partnerRole == "LAWYER") Color(0xFF1565C0) else Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textState,
                        onValueChange = { textState = it },
                        placeholder = { Text("Mesajınızı yazın...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (textState.isNotBlank()) {
                                onSendMessage(textState)
                                textState = ""
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Gönder",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F6F9)) // Premium soft gri zemin
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    val isMe = message.senderEmail == myEmail
                    MessageBubble(message = message, isMe = isMe)
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessageDto,
    isMe: Boolean
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.85f) // Ekranın %85'inden fazlasını kaplamasın
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 2.dp,
                    bottomEnd = if (isMe) 2.dp else 16.dp
                ),
                color = if (isMe)
                    MaterialTheme.colorScheme.primary
                else
                    Color.White,
                tonalElevation = if (isMe) 0.dp else 1.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.content,
                    color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
            
            val displayTime = message.createdAt?.let {
                if (it.contains("T")) {
                    it.substringAfter("T").substringBeforeLast(":")
                } else {
                    it.take(5)
                }
            } ?: ""

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = displayTime,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatDetailScreenPreview() {
    LawAppTheme {
        ChatDetailScreen(
            partnerName = "Av. Ahmet Yılmaz",
            partnerRole = "LAWYER",
            leadTitle = "İş Hukuku Danışmanlığı",
            messages = listOf(
                ChatMessageDto(
                    id = 1,
                    sessionId = 100,
                    senderEmail = "client@example.com",
                    senderName = "Müvekkil Can",
                    content = "Merhaba Ahmet Bey, dava süreci hakkında bilgi alabilir miyim?",
                    createdAt = "2023-10-27T10:00:00"
                ),
                ChatMessageDto(
                    id = 2,
                    sessionId = 100,
                    senderEmail = "lawyer@example.com",
                    senderName = "Av. Ahmet Yılmaz",
                    content = "Tabii ki, evrakları inceledim. Yarın adliyeye gidip son durumu kontrol edeceğim.",
                    createdAt = "2023-10-27T10:05:00"
                ),
                ChatMessageDto(
                    id = 3,
                    sessionId = 100,
                    senderEmail = "client@example.com",
                    senderName = "Müvekkil Can",
                    content = "Teşekkür ederim, bekliyorum.",
                    createdAt = "2023-10-27T10:06:00"
                )
            ),
            onSendMessage = {},
            onBackClick = {},
            myEmail = "client@example.com"
        )
    }
}
