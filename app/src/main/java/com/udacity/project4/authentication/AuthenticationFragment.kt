package com.udacity.project4.authentication

import com.udacity.project4.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.databinding.FragmentAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity


class AuthenticationFragment : Fragment() {

        private val TAG = "AuthenticationFragment";

    // Get a reference to the ViewModel scoped to this Fragment
    private val viewModel by viewModels<AuthenticationViewModel>()
    private lateinit var binding: FragmentAuthenticationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_authentication, container, false)

        binding.authButton.text = getString(R.string.btnLogin)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()

        binding.authButton.setOnClickListener { launchSignInFlow() }
    }

    fun navigateToRemindersActivity(){
        activity?.let{
            val intent = Intent (it, RemindersActivity::class.java)
            it.startActivity(intent)
            // so can't click back button to previous login screen after logging in successfully
            it.finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            val response = IdpResponse.fromResultIntent(data)
            val any = if (resultCode == Activity.RESULT_OK) {
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )

                navigateToRemindersActivity()

            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                    binding.authButton.text = getString(R.string.btnLogin)
                    binding.authTextView.text = getString(R.string.txtLogin)
                    binding.authButton.setOnClickListener {
                        AuthUI.getInstance().signOut(requireContext())
                    }
                    navigateToRemindersActivity()
                }
                else -> {
                    binding.authButton.text = getString(R.string.btnLogin)
                    binding.authTextView.text = getString(R.string.txtLogin)
                    binding.authButton.setOnClickListener {
                        launchSignInFlow()
                    }
                }
            }
        })
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
            //
        )
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).setAuthMethodPickerLayout(
                AuthMethodPickerLayout
                    .Builder(R.layout.layout_auth_providers)
                    .setGoogleButtonId(R.id.google_sign_in_button)
                    .setEmailButtonId(R.id.email_sign_in_button)
                    .build()
            ).setTheme(R.style.AppTheme).build(), 1001
        )
    }
}