package mti.p21.pokefight.utils

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Extension method for Call<T> objects.
 * [callback] A variable containing a lambda which is an extension method on CallBackKt.
 * This callback take no args and return nothing
 */
fun <T : Any> Call<T>.call(callback: CallBackKt<T>.() -> Unit) {
    val callBackKt = CallBackKt<T>()
    callback.invoke(callBackKt)
    this.enqueue(callBackKt)
}

class CallBackKt<T : Any> : Callback<T> {

    var showDefaultErrors = true

    var onSuccess: ((Response<T>) -> Unit)? = null
    var onUserError: ((Response<T>) -> Unit)? = null
    fun defaultOnUserError(response : Response<T>) {
        Log.w(
            "CallbackUserError", "There is a user error not catched! >>\n" +
                    "Message = ${response.message()}" +
                    "Code = ${response.code()}\n" +
                    "Body = ${response.body().toString()}\n" +
                    "ErrorBody = ${response.errorBody()?.string()}"
        )
    }


    var onServerError: ((Response<T>) -> Unit)? = null
    fun defaultOnServerError(response: Response<T>) {
        Log.w(
            "CallbackServerError", "There is a server error not catched! >>\n" +
                    "Message = ${response.message()}" +
                    "Code = ${response.code()}\n" +
                    "Body = ${response.body().toString()}\n" +
                    "ErrorBody = ${response.errorBody()?.string()}"
        )
    }

    var onFailure: ((Throwable?) -> Unit)? = null
    fun defaultOnFailure(throwable: Throwable?) {
        Log.w(
            "CallbackFailure", "The call failed, there is no response! >>\n" +
                    "Message = ${throwable?.message}\n" +
                    "Cause = ${throwable?.cause}"
        )
        throwable?.printStackTrace()
    }

    var onUnknownError: ((Response<T>) -> Unit)? = null
    fun defaultOnUnknownError(): Nothing =
        throw IllegalStateException("The server should never return an other code!")

    var onAnyError: ((Response<T>?, (Throwable?)) -> Unit)? = null
    var onAnyErrorNoArg: (() -> Unit)? = null


    fun errorAsCompactString(r: Response<T>?, t: Throwable?): String {
        return when {
            t != null -> t.message ?: "Failure (no response)"
            r != null -> "Error code : ${r.code()}\n${r.message()}"
            else -> "You should not be here!"
        }
    }
    fun errorDetailedString(r: Response<T>?, t: Throwable?): String {
        return when {
            t != null -> failureDetailed(t)
            r != null -> errorDetailed(r)
            else -> "You should not be here!"
        }
    }

    private fun failureDetailed(t: Throwable) : String {
        val sb = StringBuilder()
        sb.append("Failure :\n")
        sb.append("Message = ${t.message}\n")
        sb.append("Cause = ${t.cause}\n")
        t.stackTrace.forEach {
            sb.append("${it}\n")
        }
        return sb.toString()
    }

    private fun errorDetailed(r: Response<T>): String {
        return "Response but error:\n" +
                "Message = ${r.message()}" +
                "Code = ${r.code()}\n" +
                "Body = ${r.body().toString()}\n" +
                "ErrorBody = ${r.errorBody()?.string()}"
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        if (showDefaultErrors)
            defaultOnFailure(t)
        onFailure?.invoke(t)
        onAnyError?.invoke(null, t)
        onAnyErrorNoArg?.invoke()
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        var e: Exception? = null
        if (response.isSuccessful) {
            try {
                onSuccess?.invoke(response)
                return
            } catch (ex: Exception) {
                e = ex
                Log.e("CallbackSuccessError", "$e.message")
                e.printStackTrace()
            }
        } else {
            when (response.code()) {
                in 400..499 -> {
                    if (showDefaultErrors)
                        defaultOnUserError(response)
                    onUserError?.invoke(response)
                }
                in 500..599 -> {
                    if (showDefaultErrors)
                        defaultOnServerError(response)
                    onServerError?.invoke(response)
                }
                else -> {
                    if (showDefaultErrors)
                        defaultOnUnknownError()
                    onUnknownError?.invoke(response)
                }
            }
        }
        onAnyError?.invoke(response, e)
        onAnyErrorNoArg?.invoke()
        Log.w("ServiceCallError", errorDetailedString(response, e))
    }
}