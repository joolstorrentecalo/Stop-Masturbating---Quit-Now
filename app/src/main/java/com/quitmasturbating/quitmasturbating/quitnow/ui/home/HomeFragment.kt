@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.quitnow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quitnow.*
import com.example.quitnow.R
import com.example.quitnow.data.db.user.UserEntity
import com.example.quitnow.databinding.FragmentHomeBinding
import com.example.quitnow.ui.home.goal.GoalDialogFragment
import com.example.quitnow.ui.settings.SettingsFragment.Companion.CURRENCY
import com.example.quitnow.ui.theme.AppTheme
import com.example.quitnow.ui.theme.SecondaryTextColor
import com.example.quitnow.util.DateConverters.getGoalTimestamp
import com.example.quitnow.util.DateConverters.getGoalValue
import com.example.quitnow.util.Epoch
import com.example.quitnow.util.Epoch.calcLifeLost
import com.example.quitnow.util.Epoch.calcLifeRegained
import com.example.quitnow.util.Epoch.calcMoney
import com.example.quitnow.util.Epoch.calcNotSmoked
import com.example.quitnow.util.Epoch.calcPassedTime
import com.example.quitnow.util.Epoch.calcPercentage
import com.example.quitnow.util.Epoch.calcSmoked
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    private var uiDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.user.observe(viewLifecycleOwner) { user: UserEntity? ->
            when (user) {
                null -> findNavController().navigate(HomeFragmentDirections.actionGlobalInclusiveSettingsFragment())
                else -> onUserDataChanged(user)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressCard.apply {
            onSelectGoalClick = this@HomeFragment::openDialogSheet
        }
    }

    override fun onResume() {
        super.onResume()

        val user = viewModel.getUser() ?: return
        onUserDataChanged(user)
    }

    override fun onPause() {
        super.onPause()

        uiDisposable?.dispose()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onUserDataChanged(userEntity: UserEntity) {
        uiDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread()) // poll data on a background thread
            .observeOn(AndroidSchedulers.mainThread()) // populate UI on main thread
            .subscribe({
                binding.progressCard.setProgressValue(calcPassedTime(userEntity.start))
            }, {
                Timber.e(it)
            }) // your UI code

        binding.progressCard.apply {
            setGoalPercentage(calcPercentage(userEntity.start, userEntity.goal))
            setGoalValue(getGoalValue(requireContext(), userEntity.start, userEntity.goal))
        }

        val smoked = calcSmoked(userEntity.years, userEntity.cigPerDay).toString()
        val notSmoked = calcNotSmoked(userEntity.start, userEntity.cigPerDay).toString()
        val moneySpent = "${
            calcMoney(
                days = userEntity.years * 365,
                perDay = userEntity.cigPerDay,
                inPack = userEntity.inPack,
                price = userEntity.price
            )
        } $CURRENCY"
        val moneySaved = "${
            calcMoney(
                days = Epoch.differenceBetweenTimestampsInDays(Epoch.now(), userEntity.start),
                perDay = userEntity.cigPerDay,
                inPack = userEntity.inPack,
                price = userEntity.price
            )
        } $CURRENCY"
        val lifeRegained = calcLifeRegained(notSmoked.toInt())
        val lifeLost = calcLifeLost(smoked.toInt())

        binding.composeViewStats.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    CardStats(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        savedMoney = moneySaved,
                        regainedLife = lifeRegained,
                        notSmoked = notSmoked
                    )
                }
            }
        }

        binding.composeViewHistory.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    CardHistory(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        smokedCigarettes = smoked,
                        moneySpent = moneySpent,
                        lostLife = lifeLost
                    )
                }
            }
        }
    }

    private fun openDialogSheet() {
        val selectedIndex = viewModel.getGoal()
        val dialog = GoalDialogFragment.newInstance(selectedIndex)

        dialog.onGoalSelected = ::onGoalClicked
        dialog.show(childFragmentManager, GoalDialogFragment.TAG)
    }

    private fun onGoalClicked(position: Int) {
        viewModel.setGoal(position)

        val epoch = getGoalTimestamp(position, viewModel.getStartEpoch())

        viewModel.setGoalNotification(epoch, requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()

        uiDisposable?.dispose()
    }
}

@Composable
fun RowStat(
    image: Int,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Image(painter = painterResource(id = image), contentDescription = null)
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            color = SecondaryTextColor,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = SecondaryTextColor,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardStats(
    modifier: Modifier = Modifier,
    savedMoney: String,
    regainedLife: String,
    notSmoked: String
) {
    Card(
        elevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        onClick = { Timber.d("Card clicked!") })
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.mp_stats_card_title), modifier = Modifier.fillMaxWidth()
            )
            RowStat(
                image = R.drawable.mp_ic_smoke_free_black,
                title = stringResource(id = R.string.mp_cigs_not_smoked_label),
                value = notSmoked)
            RowStat(
                image = R.drawable.mp_ic_money,
                title = stringResource(id = R.string.mp_money_saved_label),
                value = savedMoney)
        }
    }
}

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterialApi::class)
@Composable

fun CardHistory(
    modifier: Modifier = Modifier,
    smokedCigarettes: String,
    moneySpent: String,
    lostLife: String
) {
    Card(
        elevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        onClick = { Timber.d("Card clicked!") })
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.mp_history_card_title), modifier = Modifier.fillMaxWidth())
            RowStat(
                image = R.drawable.mp_ic_cigarette,
                title = stringResource(id = R.string.mp_cigs_smoked_label),
                value = smokedCigarettes)
            RowStat(
                image = R.drawable.mp_ic_attach_money_black,
                title = stringResource(id = R.string.mp_money_spent_label),
                value = moneySpent)
        }
    }
}

