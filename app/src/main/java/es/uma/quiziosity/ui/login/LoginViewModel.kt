package es.uma.quiziosity.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uma.quiziosity.R
import es.uma.quiziosity.data.LoginRepository
import es.uma.quiziosity.data.Result
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {


    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // Run the login function in a background thread (using coroutine)
        viewModelScope.launch {
            val result = loginRepository.login(username, password)

            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun register(username: String, password: String) {
        // Run the register function in a background thread (using coroutine)
        viewModelScope.launch {
            val result = loginRepository.register(username, password)

            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
            } else {
                _loginResult.value = LoginResult(error = R.string.invalid_username)
            }
        }
    }

    fun loginDataChanged(password: String) {
        if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}