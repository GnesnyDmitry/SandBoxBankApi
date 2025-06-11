package org.example

import org.example.models.Card

fun generateCard(userId: Long): Card {
    return Card(
        id = (2200_0000_0000_0000..2299_9999_9999_9999).random().toLong(),
        cvv = (100..999).random(),
        endDate = "2028-02-02",
        owner = "Ivan Ivanov",
        type = "debit",
        percent = 0.0,
        balance = 0,
    )
}