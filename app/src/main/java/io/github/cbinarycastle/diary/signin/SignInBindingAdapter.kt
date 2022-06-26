package io.github.cbinarycastle.diary.signin

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.gms.common.SignInButton

@BindingAdapter("onClick")
fun setOnClickListener(signInButton: SignInButton, listener: View.OnClickListener) {
    signInButton.setOnClickListener(listener)
}