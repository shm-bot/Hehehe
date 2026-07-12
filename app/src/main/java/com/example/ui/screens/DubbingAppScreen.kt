package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.database.DubbedVideo
import com.example.data.database.SubtitleItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.DubbingViewModel
import com.example.ui.viewmodel.SourceType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DubbingAppScreen(viewModel: DubbingViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Logo",
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "AI Dubber",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(listOf(NeonCyan, NeonOrchid)),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "دبلجة الذكاء الاصطناعي",
                                color = ObsidianBackground,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBackground
                ),
                actions = {
                    IconButton(onClick = { viewModel.errorMessage = "تطبيق AI Dubber: جاهز دائمًا لدبلجة المحتوى الخاص بك بسرعة فائقة!" }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "معلومات",
                            tint = TextSecondary
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ObsidianBackground,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = viewModel.currentTab == AppTab.EXPLORE,
                    onClick = { viewModel.currentTab = AppTab.EXPLORE },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.currentTab == AppTab.EXPLORE) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
                            contentDescription = "دبلجة جديدة"
                        )
                    },
                    label = { Text("دبلجة جديدة", fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = SurfaceCard
                    ),
                    modifier = Modifier.testTag("explore_tab_button")
                )
                NavigationBarItem(
                    selected = viewModel.currentTab == AppTab.PLAYER,
                    onClick = { viewModel.currentTab = AppTab.PLAYER },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.currentTab == AppTab.PLAYER) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleFilled,
                            contentDescription = "المشغل التفاعلي"
                        )
                    },
                    label = { Text("المشغل الذكي", fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = SurfaceCard
                    ),
                    modifier = Modifier.testTag("player_tab_button")
                )
                NavigationBarItem(
                    selected = viewModel.currentTab == AppTab.HISTORY,
                    onClick = { viewModel.currentTab = AppTab.HISTORY },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.currentTab == AppTab.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "السجل الأرشيفي"
                        )
                    },
                    label = { Text("الأرشيف", fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = SurfaceCard
                    ),
                    modifier = Modifier.testTag("history_tab_button")
                )
            }
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.currentTab) {
                AppTab.EXPLORE -> ExploreTabScreen(viewModel)
                AppTab.PLAYER -> PlayerTabScreen(viewModel)
                AppTab.HISTORY -> HistoryTabScreen(viewModel)
            }

            // Error Toast-like banner
            viewModel.errorMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "خطأ",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.errorMessage = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Processing Dialog / Turbo parallel visualizer
            if (viewModel.isProcessing) {
                DubbingProcessingOverlay(viewModel)
            }
        }
    }
}

@Composable
fun ExploreTabScreen(viewModel: DubbingViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner Art
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner_1783853725040),
                    contentDescription = "الذكاء الاصطناعي للدبلجة",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ترجمة ودبلجة صوتية فورية بالذكاء الاصطناعي",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "بإستخدام استنساخ الصوت الطبيعي ومزامنة حركة الشفاه",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Section 1: Source Selection (URL or File)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepSurface),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. مصدر الفيديو أو الصوت",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ObsidianBackground, RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (viewModel.selectedSourceType == SourceType.URL) SurfaceCard else Color.Transparent)
                                .clickable { viewModel.selectSourceType(SourceType.URL) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = "Link",
                                    tint = if (viewModel.selectedSourceType == SourceType.URL) NeonCyan else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "لصق رابط مباشر",
                                    color = if (viewModel.selectedSourceType == SourceType.URL) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (viewModel.selectedSourceType == SourceType.FILE) SurfaceCard else Color.Transparent)
                                .clickable { viewModel.selectSourceType(SourceType.FILE) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "Upload",
                                    tint = if (viewModel.selectedSourceType == SourceType.FILE) NeonCyan else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "رفع ملف محلي",
                                    color = if (viewModel.selectedSourceType == SourceType.FILE) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (viewModel.selectedSourceType == SourceType.URL) {
                        OutlinedTextField(
                            value = viewModel.urlInput,
                            onValueChange = { viewModel.onUrlChange(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("url_text_field"),
                            label = { Text("رابط الفيديو (YouTube، TikTok، Instagram...)", fontSize = 12.sp) },
                            placeholder = { Text("https://www.youtube.com/watch?...", fontSize = 11.sp, color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Language, contentDescription = "Web", tint = NeonCyan)
                            },
                            trailingIcon = {
                                if (viewModel.urlInput.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.urlInput = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "مسح", tint = TextSecondary)
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Done
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderColor,
                                focusedLabelColor = NeonCyan,
                                unfocusedLabelColor = TextSecondary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "روابط تجريبية سريعة:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            viewModel.sampleVideoLinks.forEachIndexed { index, link ->
                                val site = when {
                                    link.contains("youtube") -> "YouTube 🎥"
                                    link.contains("tiktok") -> "TikTok 🎵"
                                    else -> "Instagram 📸"
                                }
                                Box(
                                    modifier = Modifier
                                        .background(SurfaceCard, RoundedCornerShape(16.dp))
                                        .clickable { viewModel.urlInput = link }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = site, color = NeonOrchid, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // File Selection UI
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                .background(ObsidianBackground)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = "Video",
                                tint = NeonOrchid,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = if (viewModel.uploadedFileName.isEmpty()) "اضغط لاختيار ملف صوت أو فيديو" else "الملف المختار:",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (viewModel.uploadedFileName.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .background(SurfaceCard, RoundedCornerShape(8.dp))
                                        .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Audiotrack, contentDescription = "Audio file", tint = NeonCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = viewModel.uploadedFileName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.Default.CheckCircle, contentDescription = "جاهز", tint = GlowingAqua, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))
                            Text(
                                text = "اختر من ملفات نموذجية عالية الجودة:",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                viewModel.sampleLocalFiles.forEach { file ->
                                    val isSelected = viewModel.uploadedFileName == file
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isSelected) NeonCyan.copy(alpha = 0.2f) else SurfaceCard,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) NeonCyan else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.selectUploadedFile(file) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = file,
                                            color = if (isSelected) NeonCyan else Color.White,
                                            fontSize = 9.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Target Language & Voice Settings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepSurface),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. إعدادات اللغة والصوت المستنسخ",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Language Dropdown Selector
                    Text("لغة الدبلجة المستهدفة (أكثر من 150 لغة ولهجة):", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    var langMenuExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .clickable { langMenuExpanded = true }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Translate, contentDescription = "Language", tint = NeonOrchid, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = viewModel.selectedLanguage, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = TextSecondary)
                        }
                        DropdownMenu(
                            expanded = langMenuExpanded,
                            onDismissRequest = { langMenuExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(SurfaceCard)
                                .height(280.dp)
                        ) {
                            viewModel.availableLanguages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang, color = Color.White, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.selectLanguage(lang)
                                        langMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Voice Character Selector with custom interactive Play button
                    Text("تنسيق الصوت وطبقة المتحدث:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    var voiceMenuExpanded by remember { mutableStateOf(false) }
                    var previewingVoice by remember { mutableStateOf<String?>(null) }
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .clickable { voiceMenuExpanded = true }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = "Voice", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = viewModel.selectedVoice,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = TextSecondary)
                        }
                        DropdownMenu(
                            expanded = voiceMenuExpanded,
                            onDismissRequest = { voiceMenuExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(SurfaceCard)
                        ) {
                            viewModel.availableVoices.forEach { voice ->
                                DropdownMenuItem(
                                    text = { Text(voice, color = Color.White, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.selectVoice(voice)
                                        voiceMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Interactive voice preview
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ObsidianBackground, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                val isPlaying = previewingVoice == viewModel.selectedVoice
                                IconButton(
                                    onClick = {
                                        previewingVoice = if (isPlaying) null else viewModel.selectedVoice
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(if (isPlaying) GlowingAqua else NeonCyan, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                        contentDescription = "Preview",
                                        tint = ObsidianBackground,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isPlaying) "جاري معاينة نبرة الصوت المستنسخ..." else "اضغط لمعاينة جودة الصوت وخامة المتحدث",
                                    color = if (isPlaying) GlowingAqua else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (previewingVoice == viewModel.selectedVoice) {
                                // Waveform Canvas Animation
                                val infiniteTransition = rememberInfiniteTransition(label = "wave")
                                val waveAnim by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1200, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "wave"
                                )
                                Canvas(modifier = Modifier.width(60.dp).height(24.dp)) {
                                    val count = 8
                                    val spacing = size.width / (count - 1).coerceAtLeast(1)
                                    for (i in 0 until count) {
                                        val waveMultiplier = 0.3f + 0.7f * kotlin.math.sin(i.toFloat() * 1.0f + waveAnim * 2f * Math.PI.toFloat())
                                        val h = size.height * waveMultiplier.coerceIn(0.1f, 1.0f)
                                        drawLine(
                                            color = GlowingAqua,
                                            start = Offset(i * spacing, (size.height - h) / 2),
                                            end = Offset(i * spacing, (size.height + h) / 2),
                                            strokeWidth = 3f
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: AI Engine Settings & Toggles
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepSurface),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. تقنيات الذكاء الاصطناعي المفعلة",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Turbo Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, contentDescription = "Turbo", tint = NeonCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("وضع التوربو السريع (Turbo Mode)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("دبلجة تدريجية للأجزاء ومشاهدتها فورياً خلال ثوانٍ.", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = viewModel.turboModeEnabled,
                            onCheckedChange = { viewModel.turboModeEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GlowingAqua,
                                checkedTrackColor = NeonCyan.copy(alpha = 0.5f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceCard
                            )
                        )
                    }

                    Divider(color = BorderColor, modifier = Modifier.padding(vertical = 6.dp))

                    // Lip-Sync Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Face, contentDescription = "LipSync", tint = NeonOrchid, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("مزامنة حركة الشفاه (Lip Sync AI)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("تحريك عضلات الوجه لتناسب حركات المخارج الصوتية الجديدة.", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = viewModel.lipSyncEnabled,
                            onCheckedChange = { viewModel.lipSyncEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GlowingAqua,
                                checkedTrackColor = NeonCyan.copy(alpha = 0.5f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceCard
                            )
                        )
                    }

                    Divider(color = BorderColor, modifier = Modifier.padding(vertical = 6.dp))

                    // Noise removal
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Hearing, contentDescription = "Denoise", tint = GlowingAqua, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إزالة الضوضاء وتحسين الصوت", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("تنقية الصوت تلقائياً وفصل أصوات الخلفية والحديث.", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = viewModel.noiseRemovalEnabled,
                            onCheckedChange = { viewModel.noiseRemovalEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GlowingAqua,
                                checkedTrackColor = NeonCyan.copy(alpha = 0.5f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceCard
                            )
                        )
                    }
                }
            }
        }

        // Section 4: Ultra Quality, 120Hz Smoothness, & Unrestricted Free Mode (Native App Indicator)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepSurface),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "4. خيارات الأداء والجودة الفائقة",
                            color = NeonCyan,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )
                        Box(
                            modifier = Modifier
                                .background(GlowingAqua.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(1.dp, GlowingAqua, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "مفتوح مجاناً بالكامل ✦",
                                color = GlowingAqua,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Resolution Dropdown
                    Text("دقة دبلجة وتصدير الفيديو:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    var resolutionMenuExpanded by remember { mutableStateOf(false) }
                    val resolutionOptions = listOf(
                        "4K UHD (2160p) Extreme HD 💎",
                        "2K (1440p) Ultra HD ✦",
                        "1080p Full HD ✨"
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .clickable { resolutionMenuExpanded = true }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Hd, contentDescription = "Resolution", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = viewModel.exportResolution, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = TextSecondary)
                        }
                        DropdownMenu(
                            expanded = resolutionMenuExpanded,
                            onDismissRequest = { resolutionMenuExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(SurfaceCard)
                        ) {
                            resolutionOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = Color.White, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.exportResolution = option
                                        resolutionMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Smoothness/Frame-rate Dropdown
                    Text("معدل الإطارات وسلاسة العرض (Hz):", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    var frameRateMenuExpanded by remember { mutableStateOf(false) }
                    val frameRateOptions = listOf(
                        "120 FPS Extreme Smoothness ⚡",
                        "90 FPS Super Smoothness 🌀",
                        "60 FPS High Smoothness 🎬"
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .clickable { frameRateMenuExpanded = true }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = "Framerate", tint = NeonOrchid, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = viewModel.frameRateSmoothness, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = TextSecondary)
                        }
                        DropdownMenu(
                            expanded = frameRateMenuExpanded,
                            onDismissRequest = { frameRateMenuExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(SurfaceCard)
                        ) {
                            frameRateOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = Color.White, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.frameRateSmoothness = option
                                        frameRateMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Unconstrained Free Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = "Free Mode", tint = GlowingAqua, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("الوضع المجاني بالكامل (دون قيود)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("دبلجة لا نهائية دون أي اشتراكات أو حدود يومية.", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = viewModel.isTotallyFreeUnlimited,
                            onCheckedChange = { viewModel.isTotallyFreeUnlimited = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GlowingAqua,
                                checkedTrackColor = NeonCyan.copy(alpha = 0.5f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceCard
                            )
                        )
                    }

                    Divider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))

                    // 4. Native App Guarantee / Webview-free info card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ObsidianBackground, RoundedCornerShape(12.dp))
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Android, contentDescription = "Native Android App", tint = GlowingAqua, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تطبيق أندرويد حقيقي 100% (Native APK)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "هذا التطبيق مبني بالكامل بلغة Kotlin وواجهات Jetpack Compose الحديثة وقاعدة بيانات Room المحلية. ليس مجرد موقع ويب داخل نافذة، بل تطبيق حقيقي عالي الأداء يدعم سلاسة الشاشة حتى 120Hz ومزامنة الهاردوير للـ GPU.",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 15.sp
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            Divider(color = BorderColor.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Download, contentDescription = "Download APK", tint = NeonOrchid, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "كيفية تحميل التطبيق بصيغة APK أو رفعه على GitHub:",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "1. لتحميل ملف الـ APK وتثبيته مباشرة على هاتفك: اضغط على زر الإعدادات (ترس) في الزاوية العلوية لمنصة AI Studio ثم اختر 'تصدير بصيغة APK/AAB'.\n2. لرفع الكود بالكامل إلى حسابك على GitHub لتنزيله أو التعديل عليه: اضغط على زر 'Push to GitHub' في القائمة العلوية للمنصة.",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Action Button: Start Dubbing
        item {
            Button(
                onClick = { viewModel.startDubbingProcess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_dubbing_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(listOf(NeonCyan, NeonOrchid))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.OfflineBolt, contentDescription = "Bolt", tint = ObsidianBackground)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ابدأ الدبلجة الفورية فائقة السرعة",
                            color = ObsidianBackground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DubbingProcessingOverlay(viewModel: DubbingViewModel) {
    Dialog(onDismissRequest = { }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSurface),
            border = BorderStroke(2.dp, NeonCyan),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Turbo active",
                        tint = GlowingAqua,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "جاري الدبلجة بوضع الـ Turbo",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Main Circular Progress
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                    CircularProgressIndicator(
                        progress = viewModel.processingProgress,
                        modifier = Modifier.size(80.dp),
                        color = NeonCyan,
                        strokeWidth = 6.dp,
                        trackColor = SurfaceCard
                    )
                    Text(
                        text = "${(viewModel.processingProgress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Step status text
                Text(
                    text = viewModel.processingStep,
                    color = GlowingAqua,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(color = BorderColor)

                // Segment chunks visualizer (as requested)
                Text(
                    text = "تقسيم الفيديو ومعالجة الأجزاء بالتوازي على الـ GPU:",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel.currentDubbingSegments.forEach { seg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground, RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (seg.status == "PROCESSING") NeonCyan else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (seg.status) {
                                        "READY" -> Icons.Default.CheckCircle
                                        "PROCESSING" -> Icons.Default.ChangeCircle
                                        else -> Icons.Default.HourglassEmpty
                                    },
                                    contentDescription = seg.status,
                                    tint = when (seg.status) {
                                        "READY" -> GlowingAqua
                                        "PROCESSING" -> NeonCyan
                                        else -> TextSecondary
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "جزء ${seg.id} (${seg.timeRange})",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = when (seg.status) {
                                    "READY" -> "جاهز"
                                    "PROCESSING" -> "جاري الدبلجة..."
                                    else -> "في الانتظار"
                                },
                                color = when (seg.status) {
                                    "READY" -> GlowingAqua
                                    "PROCESSING" -> NeonCyan
                                    else -> TextSecondary
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                              )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerTabScreen(viewModel: DubbingViewModel) {
    val activeVideo = viewModel.activeDubbedVideo

    if (activeVideo == null) {
        // Empty Screen State
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = "No Video",
                tint = TextSecondary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "لم تقم بدبلجة أي فيديو بعد",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "توجه إلى علامة تبويب 'دبلجة جديدة' وقم بلصق رابط أو رفع ملف فيديو للبدء فوراً في توليد صوت طبيعي مستنسخ بالكامل.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.currentTab = AppTab.EXPLORE },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("ابدأ دبلجة الآن", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        // Main Active Player interface
        var editingSubtitleItem by remember { mutableStateOf<SubtitleItem?>(null) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title
            item {
                Column {
                    Text(
                        text = activeVideo.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "لغة الدبلجة: ${activeVideo.targetLanguage}",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "خامة المتحدث: ${activeVideo.voiceName}",
                            color = NeonOrchid,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // High-fidelity Video/Audio Player Canvas
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Futuristic Graphic Equalizer acting as the background video simulation
                    val infiniteTransition = rememberInfiniteTransition(label = "viz")
                    val vizAnim by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "viz"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barCount = 15
                        val barWidth = size.width / (barCount * 1.5f)
                        val barSpacing = barWidth * 0.5f
                        val totalWidth = (barWidth + barSpacing) * barCount
                        val startX = (size.width - totalWidth) / 2

                        // Draw background ambient grid lines
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(DeepSurface.copy(alpha = 0.5f), Color.Black),
                                center = center,
                                radius = size.minDimension
                            )
                        )

                        if (viewModel.isPlaying) {
                            for (i in 0 until barCount) {
                                val randomMultiplier = 0.2f + 0.8f * kotlin.math.sin(i.toFloat() * 0.5f + vizAnim * 6f)
                                val barHeight = size.height * 0.5f * randomMultiplier.coerceIn(0.1f, 0.9f)
                                val x = startX + i * (barWidth + barSpacing)
                                drawRect(
                                    color = if (viewModel.selectedAudioTrack == "DUBBED") NeonCyan.copy(alpha = 0.4f) else NeonOrchid.copy(alpha = 0.4f),
                                    topLeft = Offset(x, (size.height - barHeight) / 2),
                                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                                )
                            }
                        }
                    }

                    // Display Current Video Subtitles in large cinematic overlay!
                    val activeSubText = if (viewModel.currentSubtitleIndex >= 0 && viewModel.currentSubtitleIndex < viewModel.activeSubtitlesList.size) {
                        viewModel.activeSubtitlesList[viewModel.currentSubtitleIndex].translatedText
                    } else ""

                    if (activeSubText.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = activeSubText,
                                color = GlowingAqua,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Playback Button Overlay when paused
                        if (!viewModel.isPlaying) {
                            IconButton(
                                onClick = { viewModel.playVideo() },
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(NeonCyan.copy(alpha = 0.8f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "تشغيل",
                                    tint = ObsidianBackground,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Player Controllers
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSurface),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = {
                                    if (viewModel.isPlaying) viewModel.pauseVideo() else viewModel.playVideo()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(SurfaceCard, CircleShape)
                                    .testTag("play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (viewModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "تشغيل/إيقاف",
                                    tint = NeonCyan
                                )
                            }

                            // Timeline slider
                            Slider(
                                value = viewModel.currentPlaybackTime,
                                onValueChange = { viewModel.seekTo(it) },
                                valueRange = 0f..25f,
                                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = GlowingAqua,
                                    activeTrackColor = NeonCyan,
                                    inactiveTrackColor = BorderColor
                                )
                            )

                            // Timer Text
                            Text(
                                text = "${viewModel.formatPlaybackTime(viewModel.currentPlaybackTime)} / 00:25",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Audio Track Dual Selector & Waveforms
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSurface),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "تبديل القنوات الصوتية الفوري (ثنائي المسار):",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground, RoundedCornerShape(8.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (viewModel.selectedAudioTrack == "DUBBED") SurfaceCard else Color.Transparent)
                                    .clickable { viewModel.selectedAudioTrack = "DUBBED" }
                                    .padding(vertical = 10.dp)
                                    .testTag("track_switch_dubbed"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "الصوت المدبلج (AI Dubbed) ✦",
                                    color = if (viewModel.selectedAudioTrack == "DUBBED") NeonCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (viewModel.selectedAudioTrack == "ORIGINAL") SurfaceCard else Color.Transparent)
                                    .clickable { viewModel.selectedAudioTrack = "ORIGINAL" }
                                    .padding(vertical = 10.dp)
                                    .testTag("track_switch_original"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "الصوت الأصلي (Original Track)",
                                    color = if (viewModel.selectedAudioTrack == "ORIGINAL") NeonOrchid else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Audio Waveform Visualization
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Waves,
                                contentDescription = "مستوى التذبذب",
                                tint = if (viewModel.selectedAudioTrack == "DUBBED") NeonCyan else NeonOrchid,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (viewModel.selectedAudioTrack == "DUBBED") "ترددات الصوت المدبلج المتزامنة مع حركة الشفاه" else "ترددات الصوت الأصلي للبيئة والخلفية",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Subtitle List Timeline & Editor
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جدول الترجمة النصية (Subtitles):",
                        color = NeonCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "اضغط على أي جزء لتعديله فوراً",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Lazy list of subtitles
            items(viewModel.activeSubtitlesList) { sub ->
                val isActive = viewModel.currentSubtitleIndex == viewModel.activeSubtitlesList.indexOf(sub)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) SurfaceCard else DeepSurface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isActive) NeonCyan else BorderColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { editingSubtitleItem = sub }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isActive) NeonCyan.copy(alpha = 0.2f) else SurfaceCard,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${sub.start} - ${sub.end}",
                                    color = if (isActive) GlowingAqua else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل",
                                tint = if (isActive) NeonCyan else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "الأصل: " + sub.originalText,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "الترجمة: " + sub.translatedText,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Subtitles Editing Dialog Overlay
        editingSubtitleItem?.let { sub ->
            var currentEditText by remember { mutableStateOf(sub.translatedText) }

            Dialog(onDismissRequest = { editingSubtitleItem = null }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSurface),
                    border = BorderStroke(1.dp, NeonCyan),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "تعديل نص الترجمة الجزء (${sub.start} - ${sub.end})",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "النص الأصلي بالإنجليزية:\n${sub.originalText}",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        OutlinedTextField(
                            value = currentEditText,
                            onValueChange = { currentEditText = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("النص المدبلج المعدل", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderColor,
                                focusedLabelColor = NeonCyan
                            ),
                            maxLines = 3
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateSubtitleItem(sub.id, currentEditText)
                                    editingSubtitleItem = null
                                },
                                modifier = Modifier.weight(1f).testTag("save_subtitle_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                            ) {
                                Text("حفظ التعديل", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { editingSubtitleItem = null },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("إلغاء")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTabScreen(viewModel: DubbingViewModel) {
    val history by viewModel.historyList.collectAsState()

    if (history.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = "Empty History",
                tint = TextSecondary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "الأرشيف فارغ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "كل مقاطع الفيديو التي دبلجتها باستخدام الذكاء الاصطناعي ستُحفظ تلقائياً هنا لتتمكن من تشغيلها وتنزيلها وتعديل ترجمتها في أي وقت لاحق.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "سجل مقاطع الفيديو المدبلجة سابقاً:",
                color = NeonCyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(history) { video ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepSurface),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = if (video.sourceType == "URL") Icons.Default.Language else Icons.Default.Audiotrack,
                                    contentDescription = video.sourceType,
                                    tint = NeonOrchid,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = video.title,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val formattedDate = remember(video.timestamp) {
                                        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                        sdf.format(Date(video.timestamp))
                                    }
                                    Text(
                                        text = formattedDate,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteVideo(video) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(SurfaceCard, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = video.targetLanguage, color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(SurfaceCard, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = video.voiceName, color = GlowingAqua, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(SurfaceCard, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "المدة: ${video.duration}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.selectActiveVideoFromHistory(video) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = ObsidianBackground)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("فتح وتشغيل في المشغل التفاعلي", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
