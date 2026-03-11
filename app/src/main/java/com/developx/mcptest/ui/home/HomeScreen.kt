package com.developx.mcptest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.developx.mcptest.ui.theme.MCPTestTheme

private val ScreenBackground = Color(0xFF1C1431)
private val PanelBackground = Color(0xFF2A1D52)
private val ActiveTabBackground = Color(0xFF1A1135)
private val AccentColor = Color(0xFF8B5CF6)
private val AccentCardColor = Color(0xFF7C5CFC)
private val InputFillColor = Color(0xFFCCC2FE)
private val SecondaryTextColor = Color(0xFFAAA6BF)
private val TertiaryTextColor = Color(0xFF828282)
private val WeekendColor = Color(0xFFFF636C)
private val InactiveDayColor = Color(0xFF7E7E7E)
private val TimelineCardColor = Color(0x662B1E54)

enum class HomeTab(val label: String) {
    Schedule("Schedule"),
    Note("Note"),
}

private enum class EditorDestination {
    None,
    Schedule,
    NoteDetail,
    Note,
}

data class ScheduleItem(
    val id: Int,
    val title: String,
    val timeLabel: String,
    val place: String,
    val note: String,
    val dayOfMonth: Int,
)

data class NoteItem(
    val id: Int,
    val title: String,
    val body: String,
    val updatedLabel: String,
    val isPinned: Boolean = false,
)

private data class CalendarDay(
    val label: String,
    val isCurrentMonth: Boolean,
    val isWeekend: Boolean,
    val isSelected: Boolean = false,
    val hasUnderline: Boolean = false,
)

data class ScheduleFormState(
    val title: String = "",
    val isFullDay: Boolean = false,
    val startFrom: String = "Mon, 20 Sep 2021 08:00 PM",
    val finish: String = "Mon, 20 Sep 2021 10:00 PM",
    val repeat: String = "None",
    val reminder: String = "Before 15 minutes",
    val place: String = "",
    val note: String = "",
)

data class NoteFormState(
    val title: String = "",
    val body: String = "",
    val isPinned: Boolean = false,
)

@Composable
fun ScheduleNotesApp(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.Schedule) }
    var editorDestination by rememberSaveable { mutableStateOf(EditorDestination.None) }
    var editingScheduleId by rememberSaveable { mutableStateOf<Int?>(null) }
    var editingNoteId by rememberSaveable { mutableStateOf<Int?>(null) }
    var nextScheduleId by rememberSaveable { mutableIntStateOf(3) }
    var nextNoteId by rememberSaveable { mutableIntStateOf(4) }
    var noteQuery by rememberSaveable { mutableStateOf("") }

    val schedules = remember {
        mutableStateListOf(
            ScheduleItem(
                id = 1,
                title = "Meeting with Anomali Team",
                timeLabel = "07.00 am - 10.00 am",
                place = "Anomali Office",
                note = "Nothing",
                dayOfMonth = 20,
            ),
            ScheduleItem(
                id = 2,
                title = "Dinner with Anna",
                timeLabel = "08.00 pm",
                place = "Anna's House",
                note = "Don't forget flowers",
                dayOfMonth = 28,
            ),
        )
    }
    val notes = remember {
        mutableStateListOf(
            NoteItem(
                id = 1,
                title = "Meeting notes",
                body = "this morning's meeting, we have to improve the quality of office facilities and another...",
                updatedLabel = "28/10/2012",
                isPinned = true,
            ),
            NoteItem(
                id = 2,
                title = "Shopping list",
                body = "need for this month:\n-clothes\n-snack...",
                updatedLabel = "16/08/2013",
            ),
            NoteItem(
                id = 3,
                title = "Message from Liam",
                body = "Don't forget to complete assignments on Tuesday",
                updatedLabel = "15/08/2017",
            ),
        )
    }

    when (editorDestination) {
        EditorDestination.None -> HomeScreen(
            selectedTab = selectedTab,
            schedules = schedules,
            notes = notes.filter {
                noteQuery.isBlank() || it.title.contains(noteQuery, true) || it.body.contains(noteQuery, true)
            },
            noteQuery = noteQuery,
            modifier = modifier,
            onSelectTab = { selectedTab = it },
            onNoteQueryChange = { noteQuery = it },
            onAddClick = {
                when (selectedTab) {
                    HomeTab.Schedule -> {
                        editingScheduleId = null
                        editorDestination = EditorDestination.Schedule
                    }

                    HomeTab.Note -> {
                        editingNoteId = null
                        editorDestination = EditorDestination.Note
                    }
                }
            },
            onEditSchedule = { schedule ->
                editingScheduleId = schedule.id
                editorDestination = EditorDestination.Schedule
            },
            onEditNote = { note ->
                editingNoteId = note.id
                editorDestination = EditorDestination.NoteDetail
            },
        )

        EditorDestination.Schedule -> CreateScheduleScreen(
            initialValue = schedules.firstOrNull { it.id == editingScheduleId }?.toFormState(),
            onBack = {
                editorDestination = EditorDestination.None
                editingScheduleId = null
            },
            onSave = { formState ->
                val existingIndex = schedules.indexOfFirst { it.id == editingScheduleId }
                if (existingIndex >= 0) {
                    schedules[existingIndex] = schedules[existingIndex].copy(
                        title = formState.title,
                        timeLabel = if (formState.isFullDay) "Full day" else summarizeTime(formState.startFrom, formState.finish),
                        place = formState.place,
                        note = formState.note,
                    )
                } else {
                    schedules.add(
                        0,
                        ScheduleItem(
                            id = nextScheduleId++,
                            title = formState.title,
                            timeLabel = if (formState.isFullDay) "Full day" else summarizeTime(formState.startFrom, formState.finish),
                            place = formState.place.ifBlank { "No place" },
                            note = formState.note.ifBlank { "Nothing" },
                            dayOfMonth = 20,
                        ),
                    )
                }
                selectedTab = HomeTab.Schedule
                editorDestination = EditorDestination.None
                editingScheduleId = null
            },
        )

        EditorDestination.NoteDetail -> NoteDetailScreen(
            initialValue = notes.firstOrNull { it.id == editingNoteId }?.toFormState(),
            onBack = {
                editorDestination = EditorDestination.None
                editingNoteId = null
            },
            onEdit = {
                editorDestination = EditorDestination.Note
            },
        )

        EditorDestination.Note -> CreateNoteScreen(
            initialValue = notes.firstOrNull { it.id == editingNoteId }?.toFormState(),
            onBack = {
                editorDestination = if (editingNoteId == null) {
                    EditorDestination.None
                } else {
                    EditorDestination.NoteDetail
                }
            },
            onSave = { formState ->
                val existingIndex = notes.indexOfFirst { it.id == editingNoteId }
                if (existingIndex >= 0) {
                    notes[existingIndex] = notes[existingIndex].copy(
                        title = formState.title,
                        body = formState.body,
                        isPinned = formState.isPinned,
                        updatedLabel = "Updated just now",
                    )
                } else {
                    notes.add(
                        0,
                        NoteItem(
                            id = nextNoteId++,
                            title = formState.title,
                            body = formState.body,
                            updatedLabel = "Created just now",
                            isPinned = formState.isPinned,
                        ),
                    )
                }
                selectedTab = HomeTab.Note
                editorDestination = EditorDestination.None
                editingNoteId = null
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedTab: HomeTab,
    schedules: List<ScheduleItem>,
    notes: List<NoteItem>,
    noteQuery: String,
    onSelectTab: (HomeTab) -> Unit,
    onNoteQueryChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditSchedule: (ScheduleItem) -> Unit,
    onEditNote: (NoteItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        topBar = {
            HomeTopBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = AccentColor,
                contentColor = Color.White,
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            ScheduleNotesTabRow(
                selectedTab = selectedTab,
                onSelectTab = onSelectTab,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )

            when (selectedTab) {
                HomeTab.Schedule -> ScheduleTab(
                    schedules = schedules,
                    onEditSchedule = onEditSchedule,
                )

                HomeTab.Note -> NotesTab(
                    notes = notes,
                    query = noteQuery,
                    onQueryChange = onNoteQueryChange,
                    onEditNote = onEditNote,
                )
            }
        }
    }
}

@Composable
fun ScheduleTab(
    schedules: List<ScheduleItem>,
    onEditSchedule: (ScheduleItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            ScheduleCalendar()
        }
        item {
            Text(
                text = "Schedule",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
        items(schedules, key = { it.id }) { schedule ->
            ScheduleTimelineItem(
                schedule = schedule,
                onEdit = { onEditSchedule(schedule) },
            )
        }
    }
}

@Composable
fun NotesTab(
    notes: List<NoteItem>,
    query: String,
    onQueryChange: (String) -> Unit,
    onEditNote: (NoteItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SearchField(
                value = query,
                onValueChange = onQueryChange,
            )
        }
        items(notes, key = { it.id }) { note ->
            NoteCard(
                note = note,
                onClick = { onEditNote(note) },
            )
        }
        if (notes.isEmpty()) {
            item {
                EmptyTabState(
                    title = "No note found",
                    body = "Create a new note or adjust the search query.",
                )
            }
        }
    }
}

@Composable
fun CreateScheduleScreen(
    initialValue: ScheduleFormState?,
    onBack: () -> Unit,
    onSave: (ScheduleFormState) -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(initialValue?.title.orEmpty()) }
    var isFullDay by rememberSaveable { mutableStateOf(initialValue?.isFullDay ?: false) }
    var startFrom by rememberSaveable { mutableStateOf(initialValue?.startFrom ?: "Mon, 20 Sep 2021 08:00 PM") }
    var finish by rememberSaveable { mutableStateOf(initialValue?.finish ?: "Mon, 20 Sep 2021 10:00 PM") }
    var repeat by rememberSaveable { mutableStateOf(initialValue?.repeat ?: "None") }
    var reminder by rememberSaveable { mutableStateOf(initialValue?.reminder ?: "Before 15 minutes") }
    var place by rememberSaveable { mutableStateOf(initialValue?.place.orEmpty()) }
    var note by rememberSaveable { mutableStateOf(initialValue?.note.orEmpty()) }

    EditorScaffold(
        title = if (initialValue == null) "Create Schedule" else "Edit Schedule",
        onBack = onBack,
        actions = {
            IconButton(onClick = {}) {
                Text("Save", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        },
    ) {
        Text(
            text = "Schedule",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilledEditorField(
            value = title,
            onValueChange = { title = it },
            placeholder = "Dinner with Anna",
        )
        Spacer(modifier = Modifier.height(20.dp))
        SettingToggleRow(
            label = "Fullday",
            checked = isFullDay,
            onCheckedChange = { isFullDay = it },
        )
        Spacer(modifier = Modifier.height(18.dp))
        SettingValueRow(label = "Start from", value = startFrom)
        Spacer(modifier = Modifier.height(18.dp))
        SettingValueRow(label = "Finish", value = finish)
        Spacer(modifier = Modifier.height(18.dp))
        SettingValueRow(label = "Repeat", value = repeat)
        Spacer(modifier = Modifier.height(18.dp))
        SettingValueRow(label = "Reminder", value = reminder)
        Spacer(modifier = Modifier.height(24.dp))
        FilledEditorField(
            value = place,
            onValueChange = { place = it },
            placeholder = "Anna's House",
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilledEditorField(
            value = note,
            onValueChange = { note = it },
            placeholder = "Don't forget to give her a bouquet of flowers",
            minLines = 3,
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
            onClick = {
                onSave(
                    ScheduleFormState(
                        title = title.trim(),
                        isFullDay = isFullDay,
                        startFrom = startFrom,
                        finish = finish,
                        repeat = repeat,
                        reminder = reminder,
                        place = place.trim(),
                        note = note.trim(),
                    ),
                )
            },
            enabled = title.isNotBlank(),
        ) {
            Text("Save")
        }
    }
}

@Composable
fun CreateNoteScreen(
    initialValue: NoteFormState?,
    onBack: () -> Unit,
    onSave: (NoteFormState) -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(initialValue?.title.orEmpty()) }
    var body by rememberSaveable { mutableStateOf(initialValue?.body.orEmpty()) }
    var isPinned by rememberSaveable { mutableStateOf(initialValue?.isPinned ?: false) }

    EditorScaffold(
        title = "",
        onBack = onBack,
        actions = {
            IconButton(onClick = { isPinned = !isPinned }) {
                Text(
                    text = if (isPinned) "Pin" else "Pin",
                    color = if (isPinned) AccentColor else Color.White,
                )
            }
            IconButton(onClick = {
                onSave(
                    NoteFormState(
                        title = title.trim(),
                        body = body.trim(),
                        isPinned = isPinned,
                    ),
                )
            }) {
                Text("Save", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        },
    ) {
        SimpleTitleField(
            value = title,
            onValueChange = { title = it },
            label = "Title",
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 12,
            placeholder = { Text("Write your note", color = SecondaryTextColor) },
            colors = editorTextFieldColors(),
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
            onClick = {
                onSave(
                    NoteFormState(
                        title = title.trim(),
                        body = body.trim(),
                        isPinned = isPinned,
                    ),
                )
            },
            enabled = title.isNotBlank() && body.isNotBlank(),
        ) {
            Text("Save")
        }
    }
}

@Composable
fun NoteDetailScreen(
    initialValue: NoteFormState?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
) {
    EditorScaffold(
        title = "",
        onBack = onBack,
        actions = {
            IconButton(onClick = {}) {
                Text(
                    text = "Pin",
                    color = if (initialValue?.isPinned == true) AccentColor else Color.White,
                )
            }
            IconButton(onClick = onEdit) {
                Text("...", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        },
    ) {
        Text(
            text = initialValue?.title.orEmpty().ifBlank { "Title" },
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = initialValue?.body.orEmpty(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "on.time",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            IconButton(onClick = {}) {
                Text("O", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
            IconButton(onClick = {}) {
                Text("...", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = ScreenBackground,
            titleContentColor = Color.White,
        ),
    )
}

@Composable
private fun ScheduleNotesTabRow(
    selectedTab: HomeTab,
    onSelectTab: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)),
        containerColor = PanelBackground,
        divider = {},
        indicator = {},
    ) {
        HomeTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            Tab(
                selected = selected,
                onClick = { onSelectTab(tab) },
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) ActiveTabBackground else Color.Transparent),
                text = {
                    Text(
                        text = tab.label,
                        color = if (selected) Color.White else SecondaryTextColor,
                    )
                },
            )
        }
    }
}

@Composable
private fun ScheduleCalendar() {
    val calendarDays = listOf(
        CalendarDay("29", false, true),
        CalendarDay("30", false, false),
        CalendarDay("31", false, false),
        CalendarDay("1", true, false),
        CalendarDay("2", true, false),
        CalendarDay("3", true, false),
        CalendarDay("4", true, false),
        CalendarDay("5", true, true),
        CalendarDay("6", true, false),
        CalendarDay("7", true, false),
        CalendarDay("8", true, false),
        CalendarDay("9", true, false),
        CalendarDay("10", true, false),
        CalendarDay("11", true, false),
        CalendarDay("12", true, true),
        CalendarDay("13", true, false),
        CalendarDay("14", true, false),
        CalendarDay("15", true, false),
        CalendarDay("16", true, false),
        CalendarDay("17", true, false),
        CalendarDay("18", true, false),
        CalendarDay("19", true, true),
        CalendarDay("20", true, false, isSelected = true),
        CalendarDay("21", true, false),
        CalendarDay("22", true, false),
        CalendarDay("23", true, false),
        CalendarDay("24", true, false),
        CalendarDay("25", true, false),
        CalendarDay("26", true, true, hasUnderline = true),
        CalendarDay("27", true, false),
        CalendarDay("28", true, false, hasUnderline = true),
        CalendarDay("29", true, false),
        CalendarDay("30", true, false),
        CalendarDay("1", false, false),
        CalendarDay("2", false, false),
        CalendarDay("3", false, true),
        CalendarDay("4", false, false),
        CalendarDay("5", false, false),
        CalendarDay("6", false, false),
        CalendarDay("7", false, false),
        CalendarDay("8", false, false),
        CalendarDay("9", false, false),
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "SEPTEMBER 2026",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEachIndexed { index, day ->
                Text(
                    text = day,
                    color = if (index == 0) WeekendColor else Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        calendarDays.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (day.isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AccentColor),
                            )
                        }
                        Text(
                            text = day.label,
                            color = when {
                                day.isSelected -> Color.White
                                !day.isCurrentMonth -> InactiveDayColor
                                day.isWeekend -> WeekendColor
                                else -> Color.White
                            },
                        )
                        if (day.hasUnderline) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .width(28.dp)
                                    .height(2.dp)
                                    .background(Color.White, RoundedCornerShape(100.dp)),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleTimelineItem(
    schedule: ScheduleItem,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = ScreenBackground,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = schedule.dayOfMonth.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(132.dp)
                    .background(AccentColor),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TimelineCardColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        ) {
            Column(
                modifier = Modifier.padding(17.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = schedule.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFE5E7EB),
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(onClick = onEdit, modifier = Modifier.size(20.dp)) {
                        Text(
                            text = "[]",
                            color = Color(0xFFC084FC),
                        )
                    }
                }
                DetailLine(label = "Time", value = schedule.timeLabel)
                DetailLine(label = "Place", value = schedule.place)
                DetailLine(label = "Notes", value = schedule.note)
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row {
        Text(
            text = label,
            modifier = Modifier.width(48.dp),
            color = Color(0xFF9CA3AF),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = value,
            color = Color(0xFF9CA3AF),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text("Search Note", color = TertiaryTextColor) },
        leadingIcon = {
            Text("?", color = TertiaryTextColor)
        },
        shape = RoundedCornerShape(28.dp),
        colors = editorTextFieldColors(
            containerColor = InputFillColor,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = Color(0xFF4F4F4F),
            unfocusedTextColor = Color(0xFF4F4F4F),
        ),
    )
}

@Composable
private fun NoteCard(
    note: NoteItem,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AccentCardColor),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = note.body,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = note.updatedLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                )
                if (note.isPinned) {
                    Text(
                        text = "Pin",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            TopAppBar(
                title = {
                    if (title.isNotEmpty()) {
                        Text(text = title, color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = actions,
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = ScreenBackground,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 16.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun FilledEditorField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color(0xFF4F4F4F)) },
        minLines = minLines,
        colors = editorTextFieldColors(
            containerColor = InputFillColor,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = Color(0xFF4F4F4F),
            unfocusedTextColor = Color(0xFF4F4F4F),
        ),
        shape = RoundedCornerShape(4.dp),
    )
}

@Composable
private fun SimpleTitleField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Title", color = SecondaryTextColor) },
        colors = editorTextFieldColors(),
    )
}

@Composable
private fun SettingValueRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = TertiaryTextColor,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = ">",
                color = TertiaryTextColor,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun EmptyTabState(
    title: String,
    body: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PanelBackground),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(text = body, color = SecondaryTextColor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun editorTextFieldColors(
    containerColor: Color = Color.Transparent,
    focusedBorderColor: Color = Color.White.copy(alpha = 0.2f),
    unfocusedBorderColor: Color = Color.White.copy(alpha = 0.12f),
    focusedTextColor: Color = Color.White,
    unfocusedTextColor: Color = Color.White,
) = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedContainerColor = containerColor,
    unfocusedContainerColor = containerColor,
    disabledContainerColor = containerColor,
    focusedBorderColor = focusedBorderColor,
    unfocusedBorderColor = unfocusedBorderColor,
    focusedTextColor = focusedTextColor,
    unfocusedTextColor = unfocusedTextColor,
    focusedPlaceholderColor = SecondaryTextColor,
    unfocusedPlaceholderColor = SecondaryTextColor,
    focusedLabelColor = SecondaryTextColor,
    unfocusedLabelColor = SecondaryTextColor,
    cursorColor = AccentColor,
)

private fun ScheduleItem.toFormState(): ScheduleFormState {
    return ScheduleFormState(
        title = title,
        isFullDay = timeLabel == "Full day",
        place = place,
        note = note,
    )
}

private fun NoteItem.toFormState(): NoteFormState {
    return NoteFormState(
        title = title,
        body = body,
        isPinned = isPinned,
    )
}

private fun summarizeTime(startFrom: String, finish: String): String {
    return "$startFrom - $finish"
}

@Preview(showBackground = true)
@Composable
private fun ScheduleNotesPreview() {
    MCPTestTheme(darkTheme = true, dynamicColor = false) {
        ScheduleNotesApp()
    }
}
