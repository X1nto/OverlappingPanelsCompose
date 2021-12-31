package com.xinto.overlappingpanelscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xinto.overlappingpanels.OverlappingPanels
import com.xinto.overlappingpanels.rememberOverlappingPanelsState
import com.xinto.overlappingpanelscompose.component.PanelColumn
import com.xinto.overlappingpanelscompose.component.PanelHeaderText
import com.xinto.overlappingpanelscompose.component.PanelSurface
import com.xinto.overlappingpanelscompose.component.ScrollItem
import com.xinto.overlappingpanelscompose.ui.theme.OverlappingPanelsTheme
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OverlappingPanelsTheme {
                val panelState = rememberOverlappingPanelsState()
                val coroutineScope = rememberCoroutineScope()

                var gesturesEnabled by remember { mutableStateOf(true) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            backgroundColor = MaterialTheme.colors.secondary
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    OverlappingPanels(
                        modifier = Modifier.fillMaxSize(),
                        panelsState = panelState,
                        gesturesEnabled = gesturesEnabled,
                        panelStart = {
                            PanelSurface {
                                PanelColumn {
                                    PanelHeaderText(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        text = stringResource(R.string.start_panel_name)
                                    )
                                    Button(onClick = {
                                        coroutineScope.launch { panelState.closePanels() }
                                    }) {
                                        Text(text = stringResource(R.string.close_panel_button_text))
                                    }
                                }
                            }
                        },
                        panelCenter = {
                            PanelSurface {
                                PanelColumn {
                                    PanelHeaderText(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        text = stringResource(R.string.center_panel_name)
                                    )
                                    Text(text = stringResource(R.string.swipe_gesture_instructions))
                                    Spacer(Modifier.weight(1f))
                                    Text(text = stringResource(R.string.open_panel_programmatically_instructions))
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                panelState.openStartPanel()
                                            }
                                        }
                                    ) {
                                        Text(text = stringResource(R.string.open_start_panel_button_text))
                                    }
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                panelState.openEndPanel()
                                            }
                                        }
                                    ) {
                                        Text(text = stringResource(R.string.open_end_panel_button_text))
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Text(text = stringResource(R.string.gestures_toggle_instructions))
                                    Button(
                                        onClick = {
                                            gesturesEnabled = !gesturesEnabled
                                        }
                                    ) {
                                        Text(text = stringResource(R.string.gestures_status, gesturesEnabled.toString()))
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Text(text = stringResource(R.string.child_gesture_region_instructions))
                                    LazyRow {
                                        items(10) { index ->
                                            ScrollItem(
                                                modifier = Modifier.padding(4.dp),
                                                itemNumber = index + 1
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(48.dp))
                                }
                            }
                        },
                        panelEnd = {
                            PanelSurface {
                                PanelColumn {
                                    PanelHeaderText(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        text = stringResource(R.string.end_panel_name)
                                    )
                                    Button(onClick = {
                                        coroutineScope.launch { panelState.closePanels() }
                                    }) {
                                        Text(text = stringResource(R.string.close_panel_button_text))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
