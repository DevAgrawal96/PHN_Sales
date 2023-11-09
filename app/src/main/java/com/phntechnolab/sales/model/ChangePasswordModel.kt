package com.phntechnolab.sales.model

class ChangePasswordModel {
    var confirm_password: String = ""
        get() = field
        set(value) {
            field = value
        }
    var new_password: String = ""
        get() = field
        set(value) {
            field = value
        }
    var old_password: String = ""
        get() = field
        set(value) {
            field = value
        }
}