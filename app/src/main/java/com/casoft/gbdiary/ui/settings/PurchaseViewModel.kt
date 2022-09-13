package com.casoft.gbdiary.ui.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.data.billing.BillingProduct
import com.casoft.gbdiary.data.billing.BillingState
import com.casoft.gbdiary.domain.IsPremiumUserUseCase
import com.casoft.gbdiary.domain.LaunchBillingFlowUseCase
import com.casoft.gbdiary.domain.ObserveBillingStateUseCase
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.ui.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    observeBillingStateUseCase: ObserveBillingStateUseCase,
    isPremiumUserUseCase: IsPremiumUserUseCase,
    private val launchBillingFlowUseCase: LaunchBillingFlowUseCase,
) : ViewModel() {

    val isPremiumUser = isPremiumUserUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    init {
        viewModelScope.launch {
            observeBillingStateUseCase(Unit).collect { result ->
                result.data?.let { billingState ->
                    processBillingState(billingState)
                }
            }
        }
    }

    private suspend fun processBillingState(billingState: BillingState) {
        when (billingState) {
            BillingState.OK -> {}
            BillingState.BILLING_UNAVAILABLE -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[BILLING_UNAVAILABLE] 결제 서비스를 이용할 수 없습니다.\n고객센터에 문의해주세요.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.DEVELOPER_ERROR -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[DEVELOPER_ERROR] 결제 진행 중 오류가 발생했습니다.\n고객센터에 문의해주세요.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.ERROR -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[ERROR] 결제 진행 중 오류가 발생했습니다.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.FEATURE_NOT_SUPPORTED -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[FEATURE_NOT_SUPPORTED] 이 기기의 Google Play 스토어 앱에서 해당 상품 결제를 지원하지 않습니다.\n" +
                            "Google Play 스토어 앱 업데이트 후 다시 시도해주세요.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.ITEM_ALREADY_OWNED -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[ITEM_ALREADY_OWNED] 이미 구입한 상품입니다.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.ITEM_NOT_OWNED -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[ITEM_NOT_OWNED] 결제할 수 없는 상품입니다.\n고객센터에 문의해주세요.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.ITEM_UNAVAILABLE -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[ITEM_UNAVAILABLE] 결제할 수 없는 상품입니다.\n고객센터에 문의해주세요.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.SERVICE_DISCONNECTED -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[SERVICE_DISCONNECTED] Google Play 스토어 서비스에 연결되지 않았습니다.\n잠시 후 다시 시도해주세요.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.SERVICE_TIMEOUT -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[SERVICE_TIMEOUT] 결제 진행 중 오류가 발생했습니다.\n다시 시도해주세요.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.SERVICE_UNAVAILABLE -> {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "[SERVICE_UNAVAILABLE] 네트워크 연결 확인 후 다시 시도해주세요.",
                        confirmText = "확인"
                    )
                )
            }
            BillingState.USER_CANCELED -> {}
        }
    }

    fun launchBillingFlow(activity: Activity) {
        viewModelScope.launch {
            launchBillingFlowUseCase(
                LaunchBillingFlowUseCase.Params(
                    product = BillingProduct.PREMIUM,
                    activity = activity
                )
            )
        }
    }
}