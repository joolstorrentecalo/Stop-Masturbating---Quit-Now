@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.quitnow.ui.health

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quitnow.MainViewModel
import com.example.quitnow.R
import com.example.quitnow.data.models.health.HealthAchievementItem
import com.example.quitnow.ui.theme.Accent
import com.example.quitnow.ui.theme.AppTheme
import timber.log.Timber

class HealthFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    Surface {
                        val mItems = createItems(requireContext(), viewModel.getStartEpoch())

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            items(mItems) { mItem ->
                                CardItem(
                                    title = mItem.description,
                                    progress = mItem.progress.toFloat(),
                                    finishDate = mItem.finishDate
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

private fun createItems(context: Context, startDate: Long): List<HealthAchievementItem> {
    val healthItems = mutableListOf<HealthAchievementItem>()

    for (i in 0..5) {
        val card = HealthAchievementItem()

        card.setCardData(context, i, startDate)

        healthItems.add(card)
    }

    return healthItems.toList()
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    title: String = "",
    progress: Float = 0f,
    finishDate: String = ""
) {
    Card(
        elevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth(),
        onClick = { Timber.d("Card clicked!") }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Justify
            )
            Text(
                text = "${progress.toInt()}%",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            LinearProgressIndicator(
                backgroundColor = Color(0xFFF8BBD0),
                progress = progress / 100,
                color = Accent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(top = 8.dp)
                    .clip(shape = MaterialTheme.shapes.small)
            )
            Text(
                text = if (progress == 100f) stringResource(R.string.health_status_done) else finishDate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}