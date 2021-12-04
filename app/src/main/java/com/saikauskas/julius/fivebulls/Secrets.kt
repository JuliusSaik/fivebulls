package com.saikauskas.julius.fivebulls

class Secrets {

    //Method calls will be added by gradle task hideSecret
    //Example : external fun getWellHiddenSecret(packageName: String): String

    companion object {
        init {
            System.loadLibrary("secrets")
        }
    }

    external fun getIbshvepG(packageName: String): String

    external fun gethMeoxORw(packageName: String): String

    external fun getuAnYVyWY(packageName: String): String
}