package mti.p21.pokefight.utils

class ExceptionDuringSuccess(
    msg: String = "An exception occurred while the success callback was called"
) : Exception(msg)