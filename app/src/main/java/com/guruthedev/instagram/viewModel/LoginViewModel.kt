package com.guruthedev.instagram.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.guruthedev.instagram.dataClass.LoginError
import com.guruthedev.instagram.utils.LoginErrorType

class LoginViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _taskResponseLiveData = MutableLiveData<Task<AuthResult>>()
    val taskResponseLiveData: LiveData<Task<AuthResult>>
        get() = _taskResponseLiveData
    private val _errorLiveData = MutableLiveData<LoginError>()
    val errorLiveData: LiveData<LoginError>
        get() = _errorLiveData

    private fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { taskResult ->
                if (taskResult.isSuccessful) {
                    _taskResponseLiveData.postValue(taskResult)
                } else {
                    _errorLiveData.value =
                        LoginError(LoginErrorType.ERROR_API, taskResult.exception?.message)
                }
            }
    }

    fun validateCred(email: String, password: String) {
        if (email.isEmpty()) {
            _errorLiveData.value = LoginError(LoginErrorType.ERROR_EMPTY_EMAIL)
        }
        if (password.isEmpty()) {
            _errorLiveData.value = LoginError(LoginErrorType.ERROR_EMPTY_PASSWORD)
        } else {
            login(email, password)
        }
    }
}
