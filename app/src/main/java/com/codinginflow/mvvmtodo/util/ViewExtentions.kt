package com.codinginflow.mvvmtodo.util

import androidx.appcompat.widget.SearchView

fun SearchView.onQueryTextChange(block: ((String) -> Unit)?) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            block?.invoke(newText.orEmpty())
            return true
        }
    })
}