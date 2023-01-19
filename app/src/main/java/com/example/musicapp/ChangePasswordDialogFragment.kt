package com.example.musicapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangePasswordDialogFragment : DialogFragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            auth = Firebase.auth
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_custom_layout, null)
            val etNew = view.findViewById<EditText>(R.id.etDialogNewPassword)
            val etCurrent = view.findViewById<EditText>(R.id.etDialogCurrentPassword)
            val tvContinue = view.findViewById<TextView>(R.id.tvDialogContinue)
            val tvCancel = view.findViewById<TextView>(R.id.tvDialogCancel)

            builder.setView(view)

                tvContinue.setOnClickListener {
                    if (etCurrent.text.toString().length < 6) {
                        etCurrent.error = "Password Is Too Short"
                    } else if (etNew.text.toString().length < 6) {
                        etNew.error = "Password Is Too Short"
                    } else {
                        val credential = EmailAuthProvider.getCredential(
                            auth.currentUser?.email.toString(),
                            etCurrent.text.toString()
                        )
                        auth.currentUser?.reauthenticate(credential)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    auth.currentUser!!.updatePassword(etNew.text.toString())
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Password Successfully Changed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                dismiss()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Something Went Wrong, Try Again",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                dismiss()
                                            }
                                        }
                                } else {
                                    etCurrent.error = "Incorrect Password"
                                }
                            }
                    }
                }

                tvCancel.setOnClickListener { dismiss() }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    }
