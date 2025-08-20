package com.workflow.expense.domain.model

data class TransactionDetail(
    val Transaction_Type: String,
    val Spent_Amount: String,
    val Received_Amount: String,
    val Recipient_Name: String,
    val Sender_Name: String,
    val Currency: String,
    val Sender_Account_Number: String,
    val Receiver_Account_Number: String?,
    val Date: String,
    val Time: String,
    val Description: String,
    val Is_Successful: Boolean,
    val Category: String,
    val Transaction_ID: Long
)