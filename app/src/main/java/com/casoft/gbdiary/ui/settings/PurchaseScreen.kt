package com.casoft.gbdiary.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.components.AlertDialogLayout
import com.casoft.gbdiary.ui.components.AlertDialogState
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.rememberAlertDialogState
import com.casoft.gbdiary.ui.theme.Dark1
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.Light1
import com.casoft.gbdiary.util.collectMessage
import com.casoft.gbdiary.util.findActivity

@Composable
fun PurchaseScreen(
    viewModel: PurchaseViewModel,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val alertDialogState = rememberAlertDialogState()

    val isPremiumUser by viewModel.isPremiumUser.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.message.collectMessage(context, alertDialogState)
    }

    PurchaseScreen(
        alertDialogState = alertDialogState,
        isPremiumUser = isPremiumUser,
        onPurchaseClick = { viewModel.launchBillingFlow(context.findActivity()) },
        onClose = onClose
    )
}

@Composable
private fun PurchaseScreen(
    alertDialogState: AlertDialogState,
    isPremiumUser: Boolean,
    onPurchaseClick: () -> Unit,
    onClose: () -> Unit,
) {
    AlertDialogLayout(state = alertDialogState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppBar(onClose = onClose)
            Column(Modifier.weight(1f)) {
                Spacer(Modifier.height(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = painterResource(R.drawable.satisfaction),
                        contentDescription = null,
                        modifier = Modifier.size(88.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    if (isPremiumUser) {
                        Text(
                            text = "Premium 구매완료",
                            fontSize = 20.sp
                        )
                    } else {
                        Text(
                            text = "Premium",
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "₩2,500",
                            fontSize = 28.sp
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
                DescriptionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "구매한 이용권은 모든 기기에서 연동됩니다",
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .alpha(0.4f)
                )
            }
            if (isPremiumUser.not()) {
                PurchaseButton(
                    onClick = onPurchaseClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 88.dp
                        )
                )
            }
        }
    }
}

@Composable
private fun AppBar(onClose: () -> Unit) {
    GBDiaryAppBar {
        Box(Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "닫기"
                )
            }
        }
    }
}

@Composable
private fun DescriptionCard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Description(
                imageResId = R.drawable.smile,
                text = "한 번 결제하면 평생 이용 가능해요"
            )
            Description(
                imageResId = R.drawable.hopeful,
                text = "광고가 나오지 않아요"
            )
            Description(
                imageResId = R.drawable.joy,
                text = "프리미엄 기능 사용이 가능해요"
            )
        }
    }
}

@Composable
private fun Description(
    @DrawableRes imageResId: Int,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(imageResId),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(text)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PurchaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (GBDiaryTheme.colors.isLight) Dark1 else Light1

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        elevation = 1.dp,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 18.dp)
        ) {
            Text(
                text = "프리미엄 이용권 구매",
                style = GBDiaryTheme.typography.subtitle1
            )
        }
    }
}