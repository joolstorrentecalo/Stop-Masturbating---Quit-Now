package com.example.quitnow.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.quitnow.R
import com.example.quitnow.databinding.FragmentDialogAddItemBinding
import timber.log.Timber

class AddItemDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "AddItemDialogFragment"
    }

    private lateinit var binding: FragmentDialogAddItemBinding

    var onSubmitClick: ((name: String, price: Float) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            binding = FragmentDialogAddItemBinding.inflate(LayoutInflater.from(context))

            builder.setView(binding.root)
                .setTitle(R.string.store_dialog_title)
                .setMessage(R.string.store_dialog_message)
                .setPositiveButton(R.string.common_submit) { dialog, _ ->
                    val title = binding.etItemTitle.text
                    val price = binding.etItemPrice.text
                    if (title.isNullOrEmpty() || price.isNullOrEmpty()) return@setPositiveButton

                    try {
                        onSubmitClick?.invoke(title.toString(), price.toString().toFloat())
                        dialog.dismiss()
                    } catch (ex: Exception) {
                        Timber.e(ex)
                        return@setPositiveButton
                    }
                }
                .setNegativeButton(R.string.common_cancel) { dialog, _ ->
                    dialog.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}