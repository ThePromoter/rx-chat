package com.dpinciotti.app.rxchat.utils;

import android.text.Editable;
import android.text.TextWatcher;

public interface OnTextChangedListener extends TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    default void afterTextChanged(Editable s) { }
}
